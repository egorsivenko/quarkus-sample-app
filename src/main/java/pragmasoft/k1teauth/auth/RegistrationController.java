package pragmasoft.k1teauth.auth;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.auth.form.RegistrationForm;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.turnstile.TurnstileClient;
import pragmasoft.k1teauth.turnstile.TurnstileRequest;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Hidden
@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class RegistrationController {

    private static final String REGISTRATION_PATH = "/auth/registration";
    private static final String REGISTRATION_TEMPLATE = "auth/registration.jte";
    private static final String SITE_KEY = "siteKey";
    private static final String ERRORS = "errors";

    @Property(name = "turnstile.siteKey")
    private String siteKey;

    @Property(name = "turnstile.secretKey")
    private String secretKey;

    private final TurnstileClient turnstileClient;
    private final UserService userService;
    private final EmailService emailService;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public RegistrationController(TurnstileClient turnstileClient,
                                  UserService userService,
                                  EmailService emailService,
                                  FormGenerator formGenerator,
                                  JteTemplateRenderer jteTemplateRenderer) {
        this.turnstileClient = turnstileClient;
        this.userService = userService;
        this.emailService = emailService;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(uri = "/registration", produces = MediaType.TEXT_HTML)
    public String registrationTemplate() {
        return jteTemplateRenderer.render(REGISTRATION_TEMPLATE,
                Map.of("form", formGenerator.generate(REGISTRATION_PATH, RegistrationForm.class, Message.of("Register")),
                        SITE_KEY, siteKey));
    }

    @Post(uri = "/registration", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<String> registration(@Valid @Body RegistrationForm form) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, form.cfTurnstileResponse());
        if (!turnstileClient.verifyToken(turnstileRequest).success()) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(REGISTRATION_TEMPLATE,
                    Map.of("form", formGenerator.generate(REGISTRATION_PATH, form),
                            SITE_KEY, siteKey, ERRORS, List.of(Message.of("Turnstile verification failed")))));
        }
        if (!Objects.equals(form.password(), form.confirmPassword())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(REGISTRATION_TEMPLATE,
                    Map.of("form", formGenerator.generate(REGISTRATION_PATH, form),
                            SITE_KEY, siteKey, ERRORS, List.of(Message.of("Passwords don't match")))));
        }
        if (userService.existsByEmail(form.email())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(REGISTRATION_TEMPLATE,
                    Map.of("form", formGenerator.generate(REGISTRATION_PATH, form),
                            SITE_KEY, siteKey, ERRORS, List.of(Message.of("Email address is already registered")))));
        }
        User user = form.mapToUser();
        userService.create(user);
        emailService.sendRegistrationEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/registration-confirmation?userId=" + user.getId()));
    }

    @Get(uri = "/registration-confirmation", produces = MediaType.TEXT_HTML)
    public HttpResponse<String> registrationConfirmation(@QueryValue UUID userId) {
        if (userService.getById(userId).isVerified()) {
            return HttpResponse.seeOther(URI.create("/auth/login"));
        }
        return HttpResponse.ok(jteTemplateRenderer.render("auth/registrationConfirmation.jte", Map.of("userId", userId)));
    }

    @Post(uri = "/resend-registration-email", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<Void> resendRegistrationEmail(UUID userId) {
        emailService.sendRegistrationEmail(userService.getById(userId));
        return HttpResponse.seeOther(URI.create("/auth/registration-confirmation?userId=" + userId));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<RegistrationForm> request, ConstraintViolationException ex) {
        Optional<RegistrationForm> formOptional = request.getBody();
        return formOptional.isPresent()
                ? HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(REGISTRATION_TEMPLATE,
                        Map.of("form", formGenerator.generate(REGISTRATION_PATH, formOptional.get(), ex), SITE_KEY, siteKey)))
                : HttpResponse.serverError();
    }
}
