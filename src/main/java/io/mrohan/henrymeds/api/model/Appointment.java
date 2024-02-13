package io.mrohan.henrymeds.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class Appointment {
    private String id;
    private String providerId;
    private String providerName;
    private String timeslotStartTimestamp;
    private String scheduledAtTimestamp;
    private boolean confirmed;
    private String confirmedAtTimestamp;
}
