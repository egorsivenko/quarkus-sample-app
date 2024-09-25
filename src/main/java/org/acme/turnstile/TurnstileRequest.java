package org.acme.turnstile;

public record TurnstileRequest(
        String secret,
        String response
) {}
