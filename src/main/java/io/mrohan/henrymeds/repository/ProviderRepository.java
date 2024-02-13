package io.mrohan.henrymeds.repository;

import io.mrohan.henrymeds.domain.model.Provider;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ProviderRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.mrohan.henrymeds.repository.model.public_.tables.Provider.PROVIDER;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ProviderRepository {
    private final DSLContext dslContext;

    public Provider getProvider(final String providerId) {
        return toDomainObject(dslContext.select()
            .from(PROVIDER)
            .where(PROVIDER.ID.eq(providerId))
            .fetchSingleInto(PROVIDER));
    }

    private Provider toDomainObject(ProviderRecord providerRecord) {
        return Provider.builder()
            .id(providerRecord.getId())
            .name(providerRecord.getName())
            .build();
    }
}
