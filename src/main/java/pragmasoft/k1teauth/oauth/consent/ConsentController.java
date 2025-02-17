package pragmasoft.k1teauth.oauth.consent;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.transaction.Transactional;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.net.URI;
import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Hidden
@Controller("/granted-consents")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class ConsentController {

    private final UserService userService;
    private final ConsentRepository consentRepository;

    public ConsentController(UserService userService,
                             ConsentRepository consentRepository) {
        this.userService = userService;
        this.consentRepository = consentRepository;
    }

    @View("oauth/grantedConsents")
    @Get(produces = MediaType.TEXT_HTML)
    public HttpResponse<?> grantedConsents(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        return HttpResponse.ok(Map.of("consents", consentRepository.findAllByResourceOwner(user)));
    }

    @Post(uri = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public HttpResponse<?> revokeConsent(UUID id) {
        consentRepository.deleteById(id);
        return HttpResponse.redirect(URI.create("/granted-consents"));
    }
}
