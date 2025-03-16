package pragmasoft.k1teauth.turnstile;

import io.micronaut.serde.annotation.Serdeable;

@Serdeable
public record TurnstileRequest(
        String secret,
        String response
) {}
