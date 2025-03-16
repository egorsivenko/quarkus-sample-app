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
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.auth.form.ResetPasswordForm;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
import pragmasoft.k1teauth.email.EmailService;
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
public class ResetPasswordController {

    private static final String RESET_PASSWORD_PATH = "/auth/reset-password";
    private static final String RESET_PASSWORD_TEMPLATE = "auth/resetPassword.jte";

    private final UserService userService;
    private final EmailService emailService;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public ResetPasswordController(UserService userService,
                                   EmailService emailService,
                                   FormGenerator formGenerator,
                                   JteTemplateRenderer jteTemplateRenderer) {
        this.userService = userService;
        this.emailService = emailService;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(uri = "/reset-password/{userId}", produces = MediaType.TEXT_HTML)
    public String resetPasswordTemplate(@PathVariable UUID userId) {
        return jteTemplateRenderer.render(RESET_PASSWORD_TEMPLATE,
                Map.of("form", formGenerator.generate(RESET_PASSWORD_PATH, new ResetPasswordForm(userId))));
    }

    @Post(uri = "/reset-password", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> resetPassword(@Valid @Body ResetPasswordForm form) {
        if (!Objects.equals(form.newPassword(), form.confirmPassword())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(RESET_PASSWORD_TEMPLATE,
                    Map.of("form", formGenerator.generate(RESET_PASSWORD_PATH, form),
                            "errors", List.of(Message.of("Passwords don't match")))));
        }
        userService.changePassword(form.userId(), form.newPassword());
        return HttpResponse.seeOther(URI.create("/auth/login"));
    }

    @Get(uri = "/reset-password-confirmation", produces = MediaType.TEXT_HTML)
    public String resetPasswordConfirmation(@QueryValue UUID userId) {
        return jteTemplateRenderer.render("auth/resetPasswordConfirmation.jte", Map.of("userId", userId));
    }

    @Post(uri = "/resend-reset-password-email", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<Void> resendRegistrationEmail(UUID userId) {
        User user = userService.getById(userId);
        emailService.sendResetPasswordEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/reset-password-confirmation?userId=" + userId));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<ResetPasswordForm> request, ConstraintViolationException ex) {
        Optional<ResetPasswordForm> formOptional = request.getBody();
        return formOptional.isPresent()
                ? HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(RESET_PASSWORD_TEMPLATE,
                        Map.of("form", formGenerator.generate(RESET_PASSWORD_PATH, formOptional.get(), ex))))
                : HttpResponse.serverError();
    }
}
