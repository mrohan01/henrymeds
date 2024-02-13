package io.mrohan.henrymeds.domain;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String clientId) {
        super("No client with ID " + clientId + " was found");
    }
}
