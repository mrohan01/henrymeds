package io.mrohan.henrymeds.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AppointmentSelection {
    private String providerId;
    // Timestamp in ISO 8601 offset date time in format 'yyyy-MM-ddTHH:mmZZZ'
    private String startTimestamp;
}
