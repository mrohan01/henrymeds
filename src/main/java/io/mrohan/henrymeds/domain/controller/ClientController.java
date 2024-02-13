package io.mrohan.henrymeds.domain.controller;

import io.mrohan.henrymeds.domain.ClientNotFoundException;
import io.mrohan.henrymeds.domain.IdGenerator;
import io.mrohan.henrymeds.domain.model.Appointment;
import io.mrohan.henrymeds.domain.model.Client;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;
import io.mrohan.henrymeds.repository.AppointmentRepository;
import io.mrohan.henrymeds.repository.ClientRepository;
import io.mrohan.henrymeds.repository.ProviderScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ClientController {

    private static final int EXPIRES_AFTER_MINUTES = 30;

    private final ClientRepository clientRepository;
    private final ProviderScheduleRepository providerScheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final JobScheduler jobScheduler;

    public List<ProviderScheduleTimeslot> findAvailableAppointments(final OffsetDateTime startDateTime, final OffsetDateTime endDateTime) {
        // Ensure the start date is at least 24 hours in the future
        OffsetDateTime earliestAvailableTime = OffsetDateTime.now().plusHours(24);
        if (startDateTime.isBefore(earliestAvailableTime)) {
            throw new IllegalArgumentException("Appointments must be made at least 24 hours in advance");
        }
        return providerScheduleRepository.getAvailableProviderSchedules(startDateTime, endDateTime);
    }

    public String scheduleAppointment(final String clientId, final String providerId, final OffsetDateTime appointmentStartTimestamp) {
        final Client client = clientRepository.getClient(clientId);
        if (client == null) {
            throw new ClientNotFoundException(clientId);
        }
        final ProviderScheduleTimeslot timeslot = providerScheduleRepository.getProviderScheduleTimeslot(providerId, appointmentStartTimestamp);
        if (timeslot == null) {
            throw new IllegalArgumentException("Appointment time " + appointmentStartTimestamp + " not found for provider " + providerId);
        }
        final OffsetDateTime scheduleAt = OffsetDateTime.now();
        final Appointment appointment = Appointment.builder()
            .id(IdGenerator.generate())
            .client(client)
            .providerScheduleTimeSlot(timeslot)
            .scheduledAt(scheduleAt)
            .build();
        appointmentRepository.scheduleAppointment(appointment);
        jobScheduler.scheduleAppointmentExpiration(appointment.getId(), calculateExpiration(scheduleAt));
        return appointment.getId();
    }

    private OffsetDateTime calculateExpiration(OffsetDateTime scheduleAt) {
        return scheduleAt.plusMinutes(EXPIRES_AFTER_MINUTES);
    }

    public void confirmAppointment(final String clientId, final String appointmentId) {
        Appointment appointment = getAppointment(clientId, appointmentId);
        appointmentRepository.confirmAppointment(appointment);
        jobScheduler.cancelAppointmentExpiration(appointmentId);
    }

    public Appointment getAppointment(final String clientId, final String appointmentId) {
        final Client client = clientRepository.getClient(clientId);
        if (client == null) {
            throw new ClientNotFoundException(clientId);
        }
        final Appointment appointment = appointmentRepository.getAppointment(appointmentId);
        if (appointment == null) {
            throw new IllegalArgumentException("Appointment " + appointmentId + " not found");
        }
        if (!client.equals(appointment.getClient())) {
            throw new IllegalArgumentException("Attempt to confirm an appointment for a different client");
        }
        return appointment;
    }
}
