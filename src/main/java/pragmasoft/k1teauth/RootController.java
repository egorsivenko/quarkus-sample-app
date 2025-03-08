package pragmasoft.k1teauth;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Hidden;

import java.net.URI;

@Hidden
@Controller
@Secured(SecurityRule.IS_ANONYMOUS)
public class RootController {

    @Get
    public HttpResponse<Void> redirect() {
        return HttpResponse.redirect(URI.create("/auth/login"));
    }
}
