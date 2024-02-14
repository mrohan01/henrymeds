package io.mrohan.henrymeds.repository;

import io.mrohan.henrymeds.TestData;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static io.mrohan.henrymeds.TestData.buildProviderSchedule;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProviderScheduleTimeslotRepositoryTest {

    @BeforeEach
    public void setup(@Autowired ProviderScheduleRepository providerScheduleRepository) {
        providerScheduleRepository.updateProviderSchedule(TestData.PROVIDER_DR_SPOCK,
            List.of(
                buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:00:00Z"),
                buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:15:00Z"),
                buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:30:00Z"),
                buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:45:00Z")
            )
        );
    }

    @Test
    public void testProviderScheduleManagement(
        @Autowired ProviderScheduleRepository providerScheduleRepository
    ) {
        List<ProviderScheduleTimeslot> timeslots = List.of(
            buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:00:00Z"),
            buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:15:00Z"),
            buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:30:00Z"),
            buildProviderSchedule(TestData.PROVIDER_DR_SPOCK, "2024-02-01T10:45:00Z")
        );
        // Update provider schedule
        providerScheduleRepository.updateProviderSchedule(TestData.PROVIDER_DR_SPOCK, timeslots);

        assertEquals(timeslots, providerScheduleRepository.getProviderSchedules(TestData.PROVIDER_DR_SPOCK.getId()));
    }
}
