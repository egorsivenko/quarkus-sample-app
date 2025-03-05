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
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.auth.form.ForgotPasswordForm;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.turnstile.TurnstileClient;
import pragmasoft.k1teauth.turnstile.TurnstileRequest;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Hidden
@Controller("/auth")
@Secured(SecurityRule.IS_ANONYMOUS)
public class ForgotPasswordController {

    private static final String FORGOT_PASSWORD_PATH = "/auth/forgot-password";
    private static final String FORGOT_PASSWORD_TEMPLATE = "auth/forgotPassword.jte";
    private static final String SITE_KEY = "siteKey";
    private static final String ERRORS = "errors";
    private static final Message SUBMIT_MESSAGE = Message.of("Proceed");

    @Property(name = "turnstile.siteKey")
    private String siteKey;

    @Property(name = "turnstile.secretKey")
    private String secretKey;

    private final TurnstileClient turnstileClient;
    private final UserService userService;
    private final EmailService emailService;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public ForgotPasswordController(TurnstileClient turnstileClient,
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

    @Get(uri = "/forgot-password", produces = MediaType.TEXT_HTML)
    public String forgotPasswordTemplate() {
        return jteTemplateRenderer.render(FORGOT_PASSWORD_TEMPLATE,
                Map.of("form", formGenerator.generate(FORGOT_PASSWORD_PATH, ForgotPasswordForm.class, SUBMIT_MESSAGE),
                        SITE_KEY, siteKey));
    }

    @Post(uri = "/forgot-password", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    @ExecuteOn(TaskExecutors.BLOCKING)
    public HttpResponse<String> forgotPassword(@Valid @Body ForgotPasswordForm form) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, form.cfTurnstileResponse());
        if (!turnstileClient.verifyToken(turnstileRequest).success()) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(FORGOT_PASSWORD_TEMPLATE,
                    Map.of("form", formGenerator.generate(FORGOT_PASSWORD_PATH, form, SUBMIT_MESSAGE),
                            SITE_KEY, siteKey, ERRORS, List.of(Message.of("Turnstile verification failed")))));
        }
        if (!userService.existsByEmail(form.email())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(FORGOT_PASSWORD_TEMPLATE,
                    Map.of("form", formGenerator.generate(FORGOT_PASSWORD_PATH, form, SUBMIT_MESSAGE),
                            SITE_KEY, siteKey, ERRORS, List.of(Message.of("Account with this email is not registered")))));
        }
        User user = userService.getByEmail(form.email());
        emailService.sendResetPasswordEmail(user);
        return HttpResponse.seeOther(URI.create("/auth/reset-password-confirmation?userId=" + user.getId()));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<ForgotPasswordForm> request, ConstraintViolationException ex) {
        Optional<ForgotPasswordForm> formOptional = request.getBody();
        return formOptional.isPresent()
                ? HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(FORGOT_PASSWORD_TEMPLATE,
                        Map.of("form", formGenerator.generate(FORGOT_PASSWORD_PATH, formOptional.get(), ex, SUBMIT_MESSAGE),
                                SITE_KEY, siteKey)))
                : HttpResponse.serverError();
    }
}
