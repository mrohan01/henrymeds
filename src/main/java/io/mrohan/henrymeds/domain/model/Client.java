package io.mrohan.henrymeds.domain.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Client {
    private final String id;
    private String name;
}
