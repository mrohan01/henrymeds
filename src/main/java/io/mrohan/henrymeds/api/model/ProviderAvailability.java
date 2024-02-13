package io.mrohan.henrymeds.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Encapsulates a provider's selected availability on a specific date.
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class ProviderAvailability {
    // Timestamp in ISO 8601 offset date time in format 'yyyy-MM-ddTHH:mmZZZ'
    private String startTimestamp;
    // Timestamp in ISO 8601 offset date time in format 'yyyy-MM-ddTHH:mmZZZ'
    private String endTimestamp;
}
