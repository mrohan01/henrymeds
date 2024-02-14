package io.mrohan.henrymeds.api;

import io.mrohan.henrymeds.api.model.AppointmentSearch;
import io.mrohan.henrymeds.api.model.AppointmentSelection;
import io.mrohan.henrymeds.api.model.AppointmentTimeslot;
import io.mrohan.henrymeds.domain.controller.ClientController;
import io.mrohan.henrymeds.domain.model.Appointment;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping(value = "/client")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ClientApi {

    private final ClientController clientController;

    // TODO: Add ACL
    @RequestMapping(value = "/{clientId}/appointments/available", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public AppointmentTimeslot[] findAvailableAppointments(@RequestBody final AppointmentSearch search) {
        Assert.notNull(search, "Appointment search is required");
        Assert.hasText(search.getStartTimestamp(), "Start timestamp is required");
        Assert.hasText(search.getEndTimestamp(), "End timestamp is required");
        OffsetDateTime startTimestamp = OffsetDateTime.parse(search.getStartTimestamp());
        OffsetDateTime endTimestamp = OffsetDateTime.parse(search.getEndTimestamp());
        return clientController.findAvailableAppointments(startTimestamp, endTimestamp).stream()
            .map(this::toAppointmentTimeslot)
            .toArray(AppointmentTimeslot[]::new);
    }

    private AppointmentTimeslot toAppointmentTimeslot(ProviderScheduleTimeslot timeslot) {
        AppointmentTimeslot availableTimeslot = new AppointmentTimeslot();
        availableTimeslot.setId(timeslot.getId());
        availableTimeslot.setProviderId(timeslot.getProvider().getId());
        availableTimeslot.setProviderName(timeslot.getProvider().getName());
        availableTimeslot.setTimeslotStartTimestamp(timeslot.getTimeslotStart().toString());
        return availableTimeslot;
    }

    // TODO: Add ACL
    @RequestMapping(value = "/{clientId}/appointments/schedule", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public String scheduleAppointment(@PathVariable("clientId") String clientId, @RequestBody AppointmentSelection appointmentSelection) {
        Assert.hasText(clientId, "Client ID is required");
        Assert.notNull(appointmentSelection, "Appointment selection is required");
        Assert.hasText(appointmentSelection.getProviderId(), "Appointment selection provider ID is required");
        Assert.hasText(appointmentSelection.getStartTimestamp(), "Appointment selection start timestamp is required");
        return clientController.scheduleAppointment(
            clientId,
            appointmentSelection.getProviderId(),
            OffsetDateTime.parse(appointmentSelection.getStartTimestamp()));
    }

    // TODO: Add ACL
    @RequestMapping(value = "/{clientId}/appointments/{appointmentId}/confirm", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void confirmAppointment(@PathVariable("clientId") String clientId, @PathVariable("appointmentId") String appointmentId) {
        Assert.hasText(clientId, "Client ID is required");
        Assert.notNull(appointmentId, "Appointment ID is required");
        clientController.confirmAppointment(clientId, appointmentId);
    }

    // TODO: Add ACL
    @RequestMapping(value = "/{clientId}/appointments/{appointmentId}", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    @ResponseStatus(HttpStatus.OK)
    public io.mrohan.henrymeds.api.model.Appointment getAppointment(@PathVariable("clientId") String clientId, @PathVariable("appointmentId") String appointmentId) {
        Assert.hasText(clientId, "Client ID is required");
        Assert.notNull(appointmentId, "Appointment ID is required");
        return toApiObject(clientController.getAppointment(clientId, appointmentId));
    }

    private io.mrohan.henrymeds.api.model.Appointment toApiObject(Appointment appointment) {
        io.mrohan.henrymeds.api.model.Appointment apiObject = new io.mrohan.henrymeds.api.model.Appointment();
        apiObject.setId(appointment.getId());
        apiObject.setProviderId(appointment.getProviderScheduleTimeSlot().getProvider().getId());
        apiObject.setProviderName(appointment.getProviderScheduleTimeSlot().getProvider().getName());
        apiObject.setTimeslotStartTimestamp(appointment.getProviderScheduleTimeSlot().getTimeslotStart().toString());
        apiObject.setScheduledAtTimestamp(appointment.getScheduledAt().toString());
        apiObject.setConfirmed(appointment.isConfirmed());
        apiObject.setConfirmedAtTimestamp(appointment.getConfirmedAt().toString());
        return apiObject;
    }
}
