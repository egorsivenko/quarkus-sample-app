package org.acme.auth.request;

public record RegisterRequest(
        String fullName,
        String email,
        String password
) {}
