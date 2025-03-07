package pragmasoft.k1teauth.oauth.consent;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.fields.FormGenerator;
import io.swagger.v3.oas.annotations.Hidden;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
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

    private static final String CONSENTS_PATH = "/granted-consents";
    private static final String CONSENTS_TEMPLATE = "oauth/grantedConsents.jte";

    private final UserService userService;
    private final ConsentRepository consentRepository;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public ConsentController(UserService userService,
                             ConsentRepository consentRepository,
                             FormGenerator formGenerator,
                             JteTemplateRenderer jteTemplateRenderer) {
        this.userService = userService;
        this.consentRepository = consentRepository;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(produces = MediaType.TEXT_HTML)
    public String grantedConsents(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        return jteTemplateRenderer.render(CONSENTS_TEMPLATE,
                Map.of("consents", consentRepository.findAllByResourceOwner(user), "formGenerator", formGenerator));
    }

    @Post(uri = "/revoke", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<Void> revokeConsent(UUID id) {
        consentRepository.deleteById(id);
        return HttpResponse.seeOther(URI.create(CONSENTS_PATH));
    }
}
