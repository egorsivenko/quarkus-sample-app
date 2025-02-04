package pragmasoft.k1teauth.auth;

import io.github.bucket4j.Bucket;
import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import pragmasoft.k1teauth.auth.form.ForgotPasswordForm;
import pragmasoft.k1teauth.email.EmailSender;
import pragmasoft.k1teauth.ratelimit.RateLimitService;
import pragmasoft.k1teauth.turnstile.TurnstileRequest;
import pragmasoft.k1teauth.turnstile.TurnstileService;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.util.CsrfTokenValidator;
import pragmasoft.k1teauth.util.RequestDetails;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static pragmasoft.k1teauth.util.FlashScopeConstants.EMAIL_NOT_REGISTERED_MESSAGE;
import static pragmasoft.k1teauth.util.FlashScopeConstants.ERROR;
import static pragmasoft.k1teauth.util.FlashScopeConstants.RATE_LIMITED_MESSAGE;
import static pragmasoft.k1teauth.util.FlashScopeConstants.TURNSTILE_MESSAGE;

@Path("/auth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class ForgotPasswordResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance forgotPassword(String siteKey);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotPasswordResource.class);

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    @ConfigProperty(name = "turnstile.secret.key")
    String secretKey;

    @RestClient
    TurnstileService turnstileService;

    private final UserService userService;
    private final EmailSender emailSender;
    private final RequestDetails requestDetails;
    private final RateLimitService rateLimitService;

    public ForgotPasswordResource(UserService userService,
                                  EmailSender emailSender,
                                  RequestDetails requestDetails,
                                  RateLimitService rateLimitService) {
        this.userService = userService;
        this.emailSender = emailSender;
        this.requestDetails = requestDetails;
        this.rateLimitService = rateLimitService;
    }

    @GET
    @Path("/forgot-password")
    public TemplateInstance forgotPasswordTemplate() {
        return Templates.forgotPassword(siteKey);
    }

    @POST
    @Path("/forgot-password")
    public void forgotPassword(@BeanParam @Valid ForgotPasswordForm form,
                               @RestForm("cf-turnstile-response") String token,
                               @CookieParam("csrf-token") Cookie csrfTokenCookie,
                               @FormParam("csrf-token") String csrfTokenForm) {
        LOGGER.info("Forgot password attempt via email `{}`", form.getEmail());
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        String clientIp = requestDetails.getClientIpAddress();
        Bucket bucket = rateLimitService.resolveBucket(clientIp);

        if (!bucket.tryConsume(1)) {
            flash(ERROR, RATE_LIMITED_MESSAGE);
            forgotPasswordTemplate();
        }
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, token);
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            flash(ERROR, TURNSTILE_MESSAGE);
            forgotPasswordTemplate();
        }
        if (validationFailed()) {
            forgotPasswordTemplate();
        }
        if (!userService.existsByEmail(form.getEmail())) {
            flash(ERROR, EMAIL_NOT_REGISTERED_MESSAGE);
            forgotPasswordTemplate();
        }
        User user = userService.getByEmail(form.getEmail());
        emailSender.sendResetPasswordEmail(user);

        LOGGER.info("Password reset confirmation for email `{}`", form.getEmail());
        redirect(ResetPasswordResource.class).resetPasswordConfirmation(user.getId());
    }
}
