package io.mrohan.henrymeds.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@AllArgsConstructor
@Builder
@Data
public class Appointment {
    private final String id;
    private final Client client;
    private final ProviderScheduleTimeslot providerScheduleTimeSlot;
    private final OffsetDateTime scheduledAt;
    private final OffsetDateTime confirmedAt;
    private int version;
    private boolean confirmed;
}
