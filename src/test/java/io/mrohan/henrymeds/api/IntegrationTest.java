package io.mrohan.henrymeds.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mrohan.henrymeds.TestData;
import io.mrohan.henrymeds.api.model.*;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.*;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import static io.mrohan.henrymeds.TestData.buildProviderAvailability;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class IntegrationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void happyPath(@Autowired MockMvc mockMvc) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        LocalDate dayAfterTomorrow = tomorrow.plusDays(1);
        // Setup provider availability
        updateProviderEligibility(mockMvc, TestData.PROVIDER_ID_DR_FREUD, new ProviderAvailability[]{
            buildProviderAvailability(buildTimestamp(today, "14:00:00"), buildTimestamp(today, "15:05:00")),
            buildProviderAvailability(buildTimestamp(tomorrow, "09:14:00"), buildTimestamp(tomorrow, "12:30:00")),
            buildProviderAvailability(buildTimestamp(dayAfterTomorrow, "11:17:00"), buildTimestamp(dayAfterTomorrow, "16:35:00"))
        });

        updateProviderEligibility(mockMvc, TestData.PROVIDER_ID_DR_SPOCK, new ProviderAvailability[]{
            buildProviderAvailability(buildTimestamp(today, "10:00:00"), buildTimestamp(today, "17:00:00")),
            buildProviderAvailability(buildTimestamp(tomorrow, "10:05:00"), buildTimestamp(tomorrow, "15:01:00")),
            buildProviderAvailability(buildTimestamp(dayAfterTomorrow, "11:17:00"), buildTimestamp(dayAfterTomorrow, "16:35:00"))
        });

        // Search for availableAppointments
        // Today (will fail due to 24 hour limit)
        ServletException exception = assertThrows(ServletException.class, () -> {
            AppointmentSearch appointmentSearch = new AppointmentSearch(buildTimestamp(today, "10:30:00"), buildTimestamp(today, "16:13:00"));
            mockMvc.perform(
                MockMvcRequestBuilders.get("/client/{clientId}/appointments/available", TestData.CLIENT_ID_COLONEL_MUSTARD)
                    .content(objectMapper.writeValueAsString(appointmentSearch))
                    .contentType(MediaType.APPLICATION_JSON_VALUE));
        });
        assertTrue(exception.getRootCause() instanceof IllegalArgumentException);
        assertEquals("Appointments must be made at least 24 hours in advance", exception.getRootCause().getMessage());

        // Tomorrow before current time (will fail due to 24 hour limit)
        exception = assertThrows(ServletException.class, () -> {
            AppointmentSearch appointmentSearch = new AppointmentSearch(
                buildTimestamp(tomorrow, LocalTime.now().minusMinutes(5).toString()),
                buildTimestamp(tomorrow, LocalTime.now().plusHours(1).toString()));
            mockMvc.perform(
                MockMvcRequestBuilders.get("/client/{clientId}/appointments/available", TestData.CLIENT_ID_COLONEL_MUSTARD)
                    .content(objectMapper.writeValueAsString(appointmentSearch))
                    .contentType(MediaType.APPLICATION_JSON_VALUE));
        });
        assertTrue(exception.getRootCause() instanceof IllegalArgumentException);
        assertEquals("Appointments must be made at least 24 hours in advance", exception.getRootCause().getMessage());

        // Tomorrow, after current time
        AppointmentSearch appointmentSearch = new AppointmentSearch(
            buildTimestamp(tomorrow, LocalTime.now().plusMinutes(5).toString()),
            buildTimestamp(dayAfterTomorrow, LocalTime.now().toString()));
        byte[] availableAppointmentsJson = mockMvc.perform(
                MockMvcRequestBuilders.get("/client/{clientId}/appointments/available", TestData.CLIENT_ID_COLONEL_MUSTARD)
                    .content(objectMapper.writeValueAsString(appointmentSearch))
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
            ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        AppointmentTimeslot[] availableAppointments = objectMapper.readValue(availableAppointmentsJson, AppointmentTimeslot[].class);
        assertNotNull(availableAppointments);
        System.out.println("Available Appointments\n" + Arrays.stream(availableAppointments).map(timeslot -> String.format("\t%s\t%s\n", timeslot.getProviderName(), timeslot.getTimeslotStartTimestamp())).collect(Collectors.joining()));

        // Schedule appointment
        AppointmentTimeslot selectedAppointmentTimeslot = availableAppointments[0];
        AppointmentSelection appointmentSelection = new AppointmentSelection(selectedAppointmentTimeslot.getProviderId(), selectedAppointmentTimeslot.getTimeslotStartTimestamp());
        String appointmentId = mockMvc.perform(
            MockMvcRequestBuilders.post("/client/{clientId}/appointments/schedule", TestData.CLIENT_ID_COLONEL_MUSTARD)
                .content(objectMapper.writeValueAsString(appointmentSelection))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
        ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        assertNotNull(appointmentId);

        // Confirm appointment
        mockMvc.perform(
                MockMvcRequestBuilders.post("/client/{clientId}/appointments/{appointmentId}/confirm", TestData.CLIENT_ID_COLONEL_MUSTARD, appointmentId)
            ).andExpect(MockMvcResultMatchers.status().isOk());

        // Validate appointment is confirmed
        byte[] appointmentJson = mockMvc.perform(
            MockMvcRequestBuilders.get("/client/{clientId}/appointments/{appointmentId}", TestData.CLIENT_ID_COLONEL_MUSTARD, appointmentId)
        ).andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

        Appointment appointment = objectMapper.readValue(appointmentJson, Appointment.class);
        assertNotNull(appointment);
        assertTrue(appointment.isConfirmed());
        assertNotNull(appointment.getScheduledAtTimestamp());
        assertNotNull(appointment.getConfirmedAtTimestamp());
    }

    private String buildTimestamp(LocalDate date, String s) {
        return OffsetDateTime.parse(date.toString() + "T" + s + "-06:00").toString();
    }

    private void updateProviderEligibility(MockMvc mockMvc, String providerId, ProviderAvailability[] providerAvailabilities) throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.post("/provider/{providerId}/schedule", providerId)
                    .content(objectMapper.writeValueAsString(providerAvailabilities))
                    .contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
