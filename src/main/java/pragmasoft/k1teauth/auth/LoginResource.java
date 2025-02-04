package pragmasoft.k1teauth.auth;

import io.github.bucket4j.Bucket;
import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import pragmasoft.k1teauth.ratelimit.RateLimitService;
import pragmasoft.k1teauth.util.RequestDetails;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.resteasy.reactive.RestQuery;

import static pragmasoft.k1teauth.util.FlashScopeConstants.RATE_LIMITED;
import static pragmasoft.k1teauth.util.FlashScopeConstants.RATE_LIMITED_MESSAGE;

@Path("/auth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class LoginResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance login(String siteKey, boolean error);
    }

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    private final RequestDetails requestDetails;
    private final RateLimitService rateLimitService;

    public LoginResource(RequestDetails requestDetails,
                         RateLimitService rateLimitService) {
        this.requestDetails = requestDetails;
        this.rateLimitService = rateLimitService;
    }

    @GET
    @Path("/login")
    public TemplateInstance login(@RestQuery boolean error) {
        String clientIp = requestDetails.getClientIpAddress();
        Bucket bucket = rateLimitService.resolveBucket(clientIp);

        if ((error && !bucket.tryConsume(1)) || (bucket.getAvailableTokens() == 0)) {
            validation.addError(RATE_LIMITED, RATE_LIMITED_MESSAGE);
        }
        return Templates.login(siteKey, error);
    }
}
