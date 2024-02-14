package io.mrohan.henrymeds;

import io.mrohan.henrymeds.api.model.ProviderAvailability;
import io.mrohan.henrymeds.domain.IdGenerator;
import io.mrohan.henrymeds.domain.model.Provider;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;

import java.time.OffsetDateTime;

public final class TestData {

    public static final String CLIENT_ID_COLONEL_MUSTARD = "2cIG1YVDjOSjQVYotCRB0eKz6rB";
    public static final String PROVIDER_ID_DR_FREUD = "2cIG1dASclNWixEOeHm24oKOwMt";
    public static final String PROVIDER_ID_DR_SPOCK = "2cIG1WRZ87LW1mvlg76L0xHhSpz";

    public static final Provider PROVIDER_DR_FREUD = Provider.builder()
        .id(PROVIDER_ID_DR_FREUD)
        .name("Dr. Freud")
        .build();

    public static final Provider PROVIDER_DR_SPOCK = Provider.builder()
        .id(PROVIDER_ID_DR_SPOCK)
        .name("Dr. Spock")
        .build();

    private TestData() {
        // private to prevent instantiation
    }

    public static ProviderAvailability buildProviderAvailability(String startTimestamp, String endTimestamp) {
        return new ProviderAvailability(startTimestamp, endTimestamp);
    }

    public static ProviderScheduleTimeslot buildProviderSchedule(Provider provider, String timeslot) {
        return ProviderScheduleTimeslot.builder()
            .id(IdGenerator.generate())
            .provider(provider)
            .timeslotStart(OffsetDateTime.parse(timeslot))
            .booked(Boolean.FALSE)
            .build();
    }
}
