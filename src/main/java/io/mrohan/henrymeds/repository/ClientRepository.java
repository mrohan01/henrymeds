package io.mrohan.henrymeds.repository;

import io.mrohan.henrymeds.domain.model.Client;
import io.mrohan.henrymeds.repository.model.public_.tables.records.ClientRecord;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static io.mrohan.henrymeds.repository.model.public_.tables.Client.CLIENT;

@Component
@RequiredArgsConstructor(onConstructor = @__({@Autowired}))
public class ClientRepository {
    private final DSLContext dslContext;

    static Client toDomainObject(ClientRecord clientRecord) {
        return Client.builder()
            .id(clientRecord.getId())
            .name(clientRecord.getName())
            .build();
    }

    public Client getClient(final String clientId) {
        return toDomainObject(dslContext.select()
            .from(CLIENT)
            .where(CLIENT.ID.eq(clientId))
            .fetchSingleInto(CLIENT));
    }
}
