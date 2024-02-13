package io.mrohan.henrymeds.domain.controller;

import io.mrohan.henrymeds.domain.ProviderNotFoundException;
import io.mrohan.henrymeds.domain.model.Provider;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;
import io.mrohan.henrymeds.repository.ProviderRepository;
import io.mrohan.henrymeds.repository.ProviderScheduleRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProviderController {
    private final ProviderRepository providerRepository;
    private final ProviderScheduleRepository providerScheduleRepository;

    public Provider getProvider(@NonNull final String providerId) {
        final Provider provider = providerRepository.getProvider(providerId);
        if (provider == null) {
            throw new ProviderNotFoundException(providerId);
        }
        return provider;
    }

    /**
     * This implementation relies on ALL provider timeslots to be submitted, and doesn't allow for modification of a
     * subset. This can be improved on.
     *
     * @param provider         The provider whose schedule is being updated
     * @param updatedTimeslots The updated schedule timeslots
     */
    public void updateSchedule(@NonNull final Provider provider, @NonNull final List<ProviderScheduleTimeslot> updatedTimeslots) {
        List<ProviderScheduleTimeslot> existingSchedules = providerScheduleRepository.getProviderSchedules(provider.getId());
        // We want to ensure we aren't deleting any existing schedules which have been booked
        if (existingSchedules.stream()
            .anyMatch(existingTimeslot -> !updatedTimeslots.contains(existingTimeslot) && existingTimeslot.isBooked())) {
            // TODO: Optionally, we could cancel the appointment and notify the customer
            throw new IllegalStateException("Unable to remove availability from booked timeslot");
        }
        providerScheduleRepository.updateProviderSchedule(provider, updatedTimeslots);
    }
}
