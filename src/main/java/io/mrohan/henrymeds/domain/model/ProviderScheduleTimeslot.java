package io.mrohan.henrymeds.domain.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.OffsetDateTime;

@Builder
@Data
@EqualsAndHashCode(exclude = {"booked"})
public class ProviderScheduleTimeslot {
    private final String id;
    private final Provider provider;
    private final OffsetDateTime timeslotStart;
    private boolean booked;
}
