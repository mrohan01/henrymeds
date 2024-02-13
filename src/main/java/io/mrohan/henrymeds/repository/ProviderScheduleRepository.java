package io.mrohan.henrymeds.repository;

import io.mrohan.henrymeds.domain.model.Provider;
import io.mrohan.henrymeds.domain.model.ProviderScheduleTimeslot;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ProviderRecord;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ProviderScheduleTimeslotRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static io.mrohan.henrymeds.repository.model.public_.tables.Provider.PROVIDER;
import static io.mrohan.henrymeds.repository.model.public_.tables.ProviderScheduleTimeslot.PROVIDER_SCHEDULE_TIMESLOT;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProviderScheduleRepository {

    private final DSLContext dslContext;

    static ProviderScheduleTimeslot toDomainObject(
        final ProviderRecord providerRecord,
        final ProviderScheduleTimeslotRecord providerScheduleTimeslotRecord
    ) {
        return ProviderScheduleTimeslot.builder()
            .id(providerScheduleTimeslotRecord.getId())
            .provider(Provider.builder()
                .id(providerRecord.getId())
                .name(providerRecord.getName())
                .build())
            .booked(providerScheduleTimeslotRecord.getBooked() == 1)
            .timeslotStart(providerScheduleTimeslotRecord.getTimeslotStart())
            .build();
    }

    public List<ProviderScheduleTimeslot> getProviderSchedules(final String providerId) {
        return dslContext
            .select()
            .from(PROVIDER_SCHEDULE_TIMESLOT.join(PROVIDER).on(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(PROVIDER.ID)))
            .where(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(providerId))
            .orderBy(PROVIDER_SCHEDULE_TIMESLOT.TIMESLOT_START, PROVIDER.NAME)
            .fetch()
            .stream()
            .map(record -> toDomainObject(record.into(PROVIDER), record.into(PROVIDER_SCHEDULE_TIMESLOT)))
            .collect(Collectors.toList());
    }

    public ProviderScheduleTimeslot getProviderScheduleTimeslot(final String providerId, final OffsetDateTime timeSlot) {
        org.jooq.Record record = dslContext
            .select()
            .from(PROVIDER_SCHEDULE_TIMESLOT.join(PROVIDER).on(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(PROVIDER.ID)))
            .where(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(providerId)).and(PROVIDER_SCHEDULE_TIMESLOT.TIMESLOT_START.eq(timeSlot))
            .fetchOne();
        return record == null ? null : toDomainObject(record.into(PROVIDER), record.into(PROVIDER_SCHEDULE_TIMESLOT));
    }

    public void updateProviderSchedule(final Provider provider, final List<ProviderScheduleTimeslot> providerSchedules) {
        // Delete existing records except for slots which have been booked
        dslContext.delete(PROVIDER_SCHEDULE_TIMESLOT)
            .where(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(provider.getId()))
            .and(PROVIDER_SCHEDULE_TIMESLOT.BOOKED.eq(0))
            .execute();
        // Insert new records
        // Note this will fail if a booked record exists for a new time slot; this should be a rare occurrence, since
        // it's unlikely multiple users would be updating a single provider's schedule, but can be fixed by using an
        // upsert
        dslContext.batchInsert(
            providerSchedules.stream()
                .map(this::toRecord)
                .collect(Collectors.toList())
        ).execute();
    }

    private ProviderScheduleTimeslotRecord toRecord(ProviderScheduleTimeslot schedule) {
        ProviderScheduleTimeslotRecord record = PROVIDER_SCHEDULE_TIMESLOT.newRecord();
        record.setId(schedule.getId());
        record.setProviderId(schedule.getProvider().getId());
        record.setBooked(schedule.isBooked() ? 1 : 0);
        record.setTimeslotStart(schedule.getTimeslotStart());
        return record;
    }

    public List<ProviderScheduleTimeslot> getAvailableProviderSchedules(final OffsetDateTime startDateTime, final OffsetDateTime endDateTime) {
        return dslContext
            .select()
            .from(PROVIDER_SCHEDULE_TIMESLOT.join(PROVIDER).on(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(PROVIDER.ID)))
            .where(PROVIDER_SCHEDULE_TIMESLOT.TIMESLOT_START.ge(startDateTime).and(PROVIDER_SCHEDULE_TIMESLOT.TIMESLOT_START.lt(endDateTime)))
            .and(PROVIDER_SCHEDULE_TIMESLOT.BOOKED.eq(0))
            .fetch()
            .stream()
            .map(record -> toDomainObject(record.into(PROVIDER), record.into(PROVIDER_SCHEDULE_TIMESLOT)))
            .collect(Collectors.toList());
    }
}
