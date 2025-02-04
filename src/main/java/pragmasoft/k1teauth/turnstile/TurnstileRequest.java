package pragmasoft.k1teauth.turnstile;

public record TurnstileRequest(
        String secret,
        String response
) {}
