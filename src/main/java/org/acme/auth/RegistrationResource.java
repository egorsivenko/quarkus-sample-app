package org.acme.auth;

import io.github.bucket4j.Bucket;
import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.auth.form.RegistrationForm;
import org.acme.email.EmailSender;
import org.acme.ratelimit.RateLimitService;
import org.acme.turnstile.TurnstileRequest;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.util.RequestDetails;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.UUID;

import static org.acme.util.FlashScopeConstants.EMAIL_ALREADY_REGISTERED;
import static org.acme.util.FlashScopeConstants.ERROR;
import static org.acme.util.FlashScopeConstants.PASSWORDS_MATCH;
import static org.acme.util.FlashScopeConstants.PASSWORDS_MATCH_MESSAGE;
import static org.acme.util.FlashScopeConstants.RATE_LIMITED_MESSAGE;
import static org.acme.util.FlashScopeConstants.TURNSTILE_MESSAGE;

@Path("/auth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class RegistrationResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance registration(String siteKey);

        public static native TemplateInstance registrationConfirmation(UUID userId);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(RegistrationResource.class);

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

    public RegistrationResource(UserService userService,
                                EmailSender emailSender,
                                RequestDetails requestDetails,
                                RateLimitService rateLimitService) {
        this.userService = userService;
        this.emailSender = emailSender;
        this.requestDetails = requestDetails;
        this.rateLimitService = rateLimitService;
    }

    @GET
    @Path("/registration")
    public TemplateInstance registrationTemplate() {
        return Templates.registration(siteKey);
    }

    @POST
    @Path("/registration")
    public void registration(
            @BeanParam @Valid RegistrationForm form,
            @RestForm("cf-turnstile-response") String token
    ) {
        LOGGER.info("Registration attempt with email `{}`", form.getEmail());

        String clientIp = requestDetails.getClientIpAddress();
        Bucket bucket = rateLimitService.resolveBucket(clientIp);

        if (!bucket.tryConsume(1)) {
            flash(ERROR, RATE_LIMITED_MESSAGE);
            registrationTemplate();
        }
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, token);
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            flash(ERROR, TURNSTILE_MESSAGE);
            registrationTemplate();
        }
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            validation.addError(PASSWORDS_MATCH, PASSWORDS_MATCH_MESSAGE);
        }
        if (validationFailed()) {
            registrationTemplate();
        }
        if (userService.existsByEmail(form.getEmail())) {
            flash(ERROR, EMAIL_ALREADY_REGISTERED);
            registrationTemplate();
        }
        User user = form.mapToUser();
        userService.create(user);
        emailSender.sendRegistrationEmail(user);

        LOGGER.info("Registration confirmation for email `{}`", form.getEmail());
        registrationConfirmation(user.getId());
    }

    @GET
    @Path("/registration-confirmation")
    public TemplateInstance registrationConfirmation(@RestQuery UUID userId) {
        User user = userService.getById(userId);
        if (user.isVerified()) {
            redirect(LoginResource.class).login(false);
        }
        return Templates.registrationConfirmation(user.getId());
    }

    @POST
    @Path("/resend-registration-email")
    public void resendRegistrationEmail(@RestForm UUID userId) {
        User user = userService.getById(userId);
        emailSender.sendRegistrationEmail(user);
        registrationConfirmation(user.getId());
    }
}
