package pragmasoft.k1teauth.auth;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;

import java.util.Map;

@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class LoginController {

    @Property(name = "turnstile.siteKey")
    private String siteKey;

    @View("auth/login")
    @Get(uri = "/login", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> login(@QueryValue(defaultValue = "false") boolean failed) {
        return HttpResponse.ok(Map.of("siteKey", siteKey, "failed", failed));
    }
}
