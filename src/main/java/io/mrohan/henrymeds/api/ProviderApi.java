package io.mrohan.henrymeds.api;

import io.mrohan.henrymeds.api.model.ProviderAvailability;
import io.mrohan.henrymeds.domain.IdGenerator;
import io.mrohan.henrymeds.domain.ProviderNotFoundException;
import io.mrohan.henrymeds.domain.controller.ProviderController;
import io.mrohan.henrymeds.domain.model.Provider;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides the Provider API endpoint mappings, as well as mapping from request to domain types, and from domain types
 * to response types.
 * <p>
 * Future Enhancements:
 * - provide OpenAPI annotations to generate a document service with test forms
 */
@RestController
@RequestMapping(value = "/provider")
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProviderApi {

    private final ProviderController providerController;

    /**
     * @param providerId the ID of the provider whose schedule should be retrieved
     */
    // TODO: Add ACL
    @RequestMapping(value = "/{providerId}/schedule", method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public void updateSchedule(
        @PathVariable final String providerId,
        @RequestBody final ProviderAvailability[] availability) {
        Assert.hasText(providerId, "Provider ID is required");
        Assert.notEmpty(availability, "One or more availability selections are required");
        final Provider provider = providerController.getProvider(providerId);
        if (provider == null) {
            throw new ProviderNotFoundException(providerId);
        }
        providerController.updateSchedule(
            provider,
            toProviderSchedules(provider, availability));
    }

    private List<ProviderScheduleTimeslot> toProviderSchedules(final Provider provider, final ProviderAvailability[] availability) {
        return Arrays.stream(availability)
            .flatMap(a -> toProviderScheduleTimeslots(provider, a).stream())
            .collect(Collectors.toList());
    }

    private List<ProviderScheduleTimeslot> toProviderScheduleTimeslots(final Provider provider, final ProviderAvailability availability) {
        OffsetDateTime startDateTime = OffsetDateTime.parse(availability.getStartTimestamp());
        // Round up to nearest quarter-hour
        startDateTime = startDateTime.truncatedTo(ChronoUnit.HOURS)
            .plusMinutes(startDateTime.getMinute() / 15 + (startDateTime.getMinute() % 15 > 0 ? 15 : 0));

        OffsetDateTime endDateTime = OffsetDateTime.parse(availability.getEndTimestamp());
        // Round down to nearest quarter-hour
        endDateTime = endDateTime.truncatedTo(ChronoUnit.HOURS)
            .plusMinutes(endDateTime.getMinute() / 15);

        List<ProviderScheduleTimeslot> timeslots = new ArrayList<>();
        OffsetDateTime lastTimeslotStartTime = startDateTime;
        do {
            timeslots.add(ProviderScheduleTimeslot.builder()
                .id(IdGenerator.generate())
                .provider(provider)
                .timeslotStart(lastTimeslotStartTime)
                .build());
            lastTimeslotStartTime = lastTimeslotStartTime.plusMinutes(15);
        } while (lastTimeslotStartTime.isBefore(endDateTime));
        return timeslots;
    }
}
