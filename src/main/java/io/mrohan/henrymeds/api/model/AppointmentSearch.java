package io.mrohan.henrymeds.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AppointmentSearch {
    // Timestamp in ISO 8601 offset date time in format 'yyyy-MM-ddTHH:mmZZZ'
    private String startTimestamp;
    // Timestamp in ISO 8601 offset date time in format 'yyyy-MM-ddTHH:mmZZZ'
    private String endTimestamp;
}
