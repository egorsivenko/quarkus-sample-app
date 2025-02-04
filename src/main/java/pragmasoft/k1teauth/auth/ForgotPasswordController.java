package pragmasoft.k1teauth.auth;

import io.micronaut.context.annotation.Property;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.View;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.auth.form.ForgotPasswordForm;
import pragmasoft.k1teauth.common.violation.MessageSource;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.turnstile.TurnstileClient;
import pragmasoft.k1teauth.turnstile.TurnstileRequest;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class ForgotPasswordController {

    @Property(name = "turnstile.siteKey")
    private String siteKey;

    @Property(name = "turnstile.secretKey")
    private String secretKey;

    private final TurnstileClient turnstileClient;
    private final UserService userService;
    private final EmailService emailService;

    public ForgotPasswordController(TurnstileClient turnstileClient,
                                    UserService userService,
                                    EmailService emailService) {
        this.turnstileClient = turnstileClient;
        this.userService = userService;
        this.emailService = emailService;
    }

    @View("auth/forgotPassword")
    @Get(uri = "/forgot-password", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> forgotPasswordTemplate() {
        return HttpResponse.ok(Map.of("siteKey", siteKey));
    }

    @View("auth/forgotPassword")
    @Post(uri = "/forgot-password", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<?> forgotPassword(@Valid @Body ForgotPasswordForm form,
                                          @Body("cf-turnstile-response") String token) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, token);
        if (!turnstileClient.verifyToken(turnstileRequest).success()) {
            return HttpResponse.badRequest(Map.of("siteKey", siteKey, "errors", List.of("Turnstile verification failed")));
        }
        if (!userService.existsByEmail(form.getEmail())) {
            return HttpResponse.badRequest(Map.of("siteKey", siteKey, "errors", List.of("Account with this email is not registered")));
        }
        User user = userService.getByEmail(form.getEmail());
        emailService.sendResetPasswordEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/reset-password-confirmation?userId=" + user.getId()));
    }

    @View("auth/forgotPassword")
    @Error(exception = ConstraintViolationException.class)
    public Map<String, Object> handleError(ConstraintViolationException ex) {
        return Map.of("siteKey", siteKey, "errors", MessageSource.violationsMessages(ex.getConstraintViolations()));
    }
}
