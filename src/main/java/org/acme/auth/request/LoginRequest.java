package org.acme.auth.request;

public record LoginRequest(
        String email,
        String password,
        String token
) {}
