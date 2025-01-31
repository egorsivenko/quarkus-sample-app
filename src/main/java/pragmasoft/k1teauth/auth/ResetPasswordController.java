package pragmasoft.k1teauth.auth;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.auth.form.ResetPasswordForm;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.common.violation.MessageSource;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class ResetPasswordController {

    private final UserService userService;
    private final EmailService emailService;

    public ResetPasswordController(UserService userService,
                                   EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @View("auth/resetPassword")
    @Get(uri = "/reset-password/{userId}", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> resetPasswordTemplate(@PathVariable UUID userId) {
        return HttpResponse.ok(Map.of("userId", userId));
    }

    @Post(uri = "/reset-password", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> resetPassword(@Valid @Body ResetPasswordForm form) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            return HttpResponse.badRequest(new ModelAndView<>("auth/resetPassword",
                    Map.of("userId", form.getUserId(), "errors", List.of("Passwords don't match"))));
        }
        userService.changePassword(form.getUserId(), form.getPassword());
        return HttpResponse.seeOther(URI.create("/auth/login"));
    }

    @View("auth/resetPasswordConfirmation")
    @Get(uri = "/reset-password-confirmation", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> resetPasswordConfirmation(@QueryValue UUID userId) {
        return HttpResponse.ok(Map.of("userId", userId));
    }

    @Post(uri = "/resend-reset-password-email", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> resendRegistrationEmail(UUID userId) {
        User user = userService.getById(userId);
        emailService.sendResetPasswordEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/reset-password-confirmation?userId=" + userId));
    }

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<?> handleError(HttpRequest<ResetPasswordForm> request, ConstraintViolationException ex) {
        Optional<UUID> userIdOptional = request.getBody().map(ResetPasswordForm::getUserId);
        return userIdOptional.isPresent()
                ? HttpResponse.badRequest(new ModelAndView<>(
                        "auth/resetPassword",
                        Map.of("userId", userIdOptional.get(),
                                "errors", MessageSource.violationsMessages(ex.getConstraintViolations()))
                ))
                : HttpResponse.seeOther(URI.create("/auth/forgot-password"));
    }
}
