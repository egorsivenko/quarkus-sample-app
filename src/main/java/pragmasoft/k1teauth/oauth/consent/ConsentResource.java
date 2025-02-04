package pragmasoft.k1teauth.oauth.consent;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestForm;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.util.CsrfTokenValidator;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Path("/consents")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class ConsentResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance grantedConsents(List<Consent> consents, Locale locale);
    }

    private final UserService userService;
    private final CurrentIdentityAssociation identityAssociation;

    public ConsentResource(UserService userService,
                           CurrentIdentityAssociation identityAssociation) {
        this.userService = userService;
        this.identityAssociation = identityAssociation;
    }

    @GET
    @Path("/")
    public TemplateInstance grantedConsents() {
        String email = identityAssociation.getIdentity().getPrincipal().getName();
        User user = userService.getByEmail(email);
        return Templates.grantedConsents(Consent.listByResourceOwner(user), Locale.ENGLISH);
    }

    @POST
    @Path("/revoke")
    @Transactional
    public void revokeConsent(@RestForm UUID id,
                              @CookieParam("csrf-token") Cookie csrfTokenCookie,
                              @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        Consent.deleteById(id);
        grantedConsents();
    }
}
