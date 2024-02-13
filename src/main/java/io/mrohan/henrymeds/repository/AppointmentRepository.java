package io.mrohan.henrymeds.repository;

import io.mrohan.henrymeds.domain.model.Appointment;
import io.mrohan.henrymeds.repository.model.public_.tables.records.AppointmentRecord;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ClientRecord;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ProviderRecord;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ProviderScheduleTimeslotRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

import static io.mrohan.henrymeds.repository.model.public_.tables.Appointment.APPOINTMENT;
import static io.mrohan.henrymeds.repository.model.public_.tables.Client.CLIENT;
import static io.mrohan.henrymeds.repository.model.public_.tables.Provider.PROVIDER;
import static io.mrohan.henrymeds.repository.model.public_.tables.ProviderScheduleTimeslot.PROVIDER_SCHEDULE_TIMESLOT;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class AppointmentRepository {
    private final DSLContext dslContext;

    public Appointment getAppointment(String appointmentId) {
        Result<org.jooq.Record> result = dslContext.select()
            .from(APPOINTMENT)
            .join(PROVIDER_SCHEDULE_TIMESLOT).on(APPOINTMENT.PROVIDER_SCHEDULE_ID.eq(PROVIDER_SCHEDULE_TIMESLOT.ID))
            .join(PROVIDER).on(PROVIDER_SCHEDULE_TIMESLOT.PROVIDER_ID.eq(PROVIDER.ID))
            .join(CLIENT).on(APPOINTMENT.CLIENT_ID.eq(CLIENT.ID))
            .where(APPOINTMENT.ID.eq(appointmentId))
            .fetch();
        return result.stream().findFirst()
            .map(record -> toDomainObject(
                record.into(APPOINTMENT),
                record.into(PROVIDER),
                record.into(CLIENT),
                record.into(PROVIDER_SCHEDULE_TIMESLOT)))
            .orElse(null);
    }

    private Appointment toDomainObject(
        AppointmentRecord appointmentRecord,
        ProviderRecord providerRecord,
        ClientRecord clientRecord,
        ProviderScheduleTimeslotRecord providerScheduleTimeslotRecord
    ) {
        return Appointment.builder()
            .id(appointmentRecord.getId())
            .client(ClientRepository.toDomainObject(clientRecord))
            .scheduledAt(appointmentRecord.getScheduledAt())
            .confirmed(appointmentRecord.getConfirmed() == 1)
            .confirmedAt(appointmentRecord.getConfirmedAt())
            .providerScheduleTimeSlot(ProviderScheduleRepository.toDomainObject(providerRecord, providerScheduleTimeslotRecord))
            .build();
    }

    public void scheduleAppointment(final Appointment appointment) {
        dslContext.insertInto(APPOINTMENT)
            .set(APPOINTMENT.ID, appointment.getId())
            .set(APPOINTMENT.CLIENT_ID, appointment.getClient().getId())
            .set(APPOINTMENT.PROVIDER_SCHEDULE_ID, appointment.getProviderScheduleTimeSlot().getId())
            .set(APPOINTMENT.SCHEDULED_AT, appointment.getScheduledAt())
            .execute();
    }

    public void confirmAppointment(final Appointment appointment) {
        dslContext.update(APPOINTMENT)
            .set(APPOINTMENT.CONFIRMED, 1)
            .set(APPOINTMENT.CONFIRMED_AT, OffsetDateTime.now())
            .where(APPOINTMENT.ID.eq(appointment.getId()))
            .execute();
    }

}
