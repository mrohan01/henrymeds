package io.mrohan.henrymeds.domain;

public class ProviderNotFoundException extends RuntimeException {
    public ProviderNotFoundException(String providerId) {
        super("No provider with ID " + providerId + " was found");
    }
}
