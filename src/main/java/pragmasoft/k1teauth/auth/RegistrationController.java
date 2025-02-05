package pragmasoft.k1teauth.auth;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.auth.form.RegistrationForm;
import pragmasoft.k1teauth.common.violation.MessageSource;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.turnstile.TurnstileClient;
import pragmasoft.k1teauth.turnstile.TurnstileRequest;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class RegistrationController {

    @Property(name = "turnstile.siteKey")
    private String siteKey;

    @Property(name = "turnstile.secretKey")
    private String secretKey;

    private final TurnstileClient turnstileClient;
    private final UserService userService;
    private final EmailService emailService;

    public RegistrationController(TurnstileClient turnstileClient,
                                  UserService userService,
                                  EmailService emailService) {
        this.turnstileClient = turnstileClient;
        this.userService = userService;
        this.emailService = emailService;
    }

    @View("auth/registration")
    @Get(uri = "/registration", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> registrationTemplate() {
        return HttpResponse.ok(Map.of("siteKey", siteKey));
    }

    @View("auth/registration")
    @Post(uri = "/registration", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<?> registration(@Valid @Body RegistrationForm form) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, form.getCfTurnstileResponse());
        if (!turnstileClient.verifyToken(turnstileRequest).success()) {
            return HttpResponse.badRequest(Map.of("siteKey", siteKey, "errors", List.of("Turnstile verification failed")));
        }
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            return HttpResponse.badRequest(Map.of("siteKey", siteKey, "errors", List.of("Passwords don't match")));
        }
        if (userService.existsByEmail(form.getEmail())) {
            return HttpResponse.badRequest(Map.of("siteKey", siteKey, "errors", List.of("Email address is already registered")));
        }
        User user = form.mapToUser();
        userService.create(user);
        emailService.sendRegistrationEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/registration-confirmation?userId=" + user.getId()));
    }

    @Get(uri = "/registration-confirmation", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> registrationConfirmation(@QueryValue UUID userId) {
        User user = userService.getById(userId);
        if (user.isVerified()) {
            return HttpResponse.seeOther(URI.create("/auth/login"));
        }
        return HttpResponse.ok(new ModelAndView<>("auth/registrationConfirmation", Map.of("userId", userId)));
    }

    @Post(uri = "/resend-registration-email", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> resendRegistrationEmail(UUID userId) {
        User user = userService.getById(userId);
        emailService.sendRegistrationEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/registration-confirmation?userId=" + userId));
    }

    @View("auth/registration")
    @Error(exception = ConstraintViolationException.class)
    public Map<String, Object> handleError(ConstraintViolationException ex) {
        return Map.of("siteKey", siteKey, "errors", MessageSource.violationsMessages(ex.getConstraintViolations()));
    }
}
