package org.acme.auth;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.email.EmailSender;
import org.acme.turnstile.TurnstileRequest;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.UUID;

import static org.acme.util.ValidationConstraints.EMAIL_SIZE_MESSAGE;
import static org.acme.util.ValidationConstraints.FULL_NAME_SIZE_MESSAGE;
import static org.acme.util.ValidationConstraints.PASSWORD_SIZE_MESSAGE;

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

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    @ConfigProperty(name = "turnstile.secret.key")
    String secretKey;

    @RestClient
    TurnstileService turnstileService;

    private final UserService userService;
    private final EmailSender emailSender;

    public RegistrationResource(UserService userService,
                                EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/registration")
    public TemplateInstance registration() {
        return Templates.registration(siteKey);
    }

    @POST
    @Path("/registration")
    public void registration(
            @RestForm @NotBlank @Size(min = 4, max = 50, message = FULL_NAME_SIZE_MESSAGE) String fullName,
            @RestForm @NotBlank @Size(min = 6, max = 50, message = EMAIL_SIZE_MESSAGE) String email,
            @RestForm @NotBlank @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE) String password,
            @RestForm("cf-turnstile-response") String token
    ) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, token);
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            flash("error", "Turnstile verification failed.");
            registration();
        }
        if (validationFailed()) {
            registration();
        }
        if (userService.existsByEmail(email)) {
            flash("error", "Email address is already registered.");
            registration();
        }
        User user = new User(email, password, fullName);
        userService.create(user);
        emailSender.sendRegistrationEmail(user);

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
