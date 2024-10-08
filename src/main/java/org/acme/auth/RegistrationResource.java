package org.acme.auth;

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
import org.acme.turnstile.TurnstileRequest;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.UUID;

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

    private static final Logger LOGGER = LogManager.getLogger(RegistrationResource.class);

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
            @BeanParam @Valid RegistrationForm form,
            @RestForm("cf-turnstile-response") String token
    ) {
        LOGGER.info("Registration attempt with email `{}`", form.getEmail());

        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, token);
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            flash("error", "Turnstile verification failed.");
            registration();
        }
        validation.equals("passwordMatch", form.getPassword(), form.getConfirmPassword());
        if (validationFailed()) {
            registration();
        }
        if (userService.existsByEmail(form.getEmail())) {
            flash("error", "Email address is already registered.");
            registration();
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
