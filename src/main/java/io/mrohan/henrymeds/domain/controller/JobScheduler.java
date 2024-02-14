package io.mrohan.henrymeds.domain.controller;

import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class JobScheduler {

    /**
     * Schedules a job which will expire the appointment if it's not confirmed
     */
    public void scheduleAppointmentExpiration(String appointmentId, OffsetDateTime expiresAt) {
        // TODO
    }

    /**
     * Cancels the job which handles expiration of an appointment.
     */
    public void cancelAppointmentExpiration(String appointmentId) {
        // TODO
    }
}
