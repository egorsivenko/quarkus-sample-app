package pragmasoft.k1teauth.turnstile;

import io.micronaut.core.annotation.Blocking;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.client.annotation.Client;

@Client("https://challenges.cloudflare.com/turnstile/v0/siteverify")
public interface TurnstileClient {

    @Blocking
    @Post(consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    TurnstileResponse verifyToken(@Body TurnstileRequest request);
}
