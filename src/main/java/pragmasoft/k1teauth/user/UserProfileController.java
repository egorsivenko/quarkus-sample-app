package pragmasoft.k1teauth.user;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.user.form.ChangePasswordForm;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Hidden
@Controller("/profile")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UserProfileController {

    private static final String USER_PROFILE_PATH = "/profile";
    private static final String CHANGE_PASSWORD_PATH = "/profile/change-password";
    private static final String USER_PROFILE_TEMPLATE = "user/profile.jte";
    private static final String CHANGE_PASSWORD_TEMPLATE = "user/changePassword.jte";

    private final UserService userService;
    private final EmailService emailService;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public UserProfileController(UserService userService,
                                 EmailService emailService,
                                 FormGenerator formGenerator,
                                 JteTemplateRenderer jteTemplateRenderer) {
        this.userService = userService;
        this.emailService = emailService;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(produces = MediaType.TEXT_HTML)
    public HttpResponse<String> profile(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (!user.isVerified()) {
            emailService.sendRegistrationEmail(user);
            return HttpResponse.seeOther(URI.create("/auth/registration-confirmation?userId=" + user.getId()));
        }
        return HttpResponse.ok(jteTemplateRenderer.render(USER_PROFILE_TEMPLATE, Map.of("user", user)));
    }

    @Get(uri = "/change-password", produces = MediaType.TEXT_HTML)
    public String changePasswordTemplate() {
        return jteTemplateRenderer.render(CHANGE_PASSWORD_TEMPLATE,
                Map.of("form", formGenerator.generate(CHANGE_PASSWORD_PATH, ChangePasswordForm.class)));
    }

    @Post(uri = "/change-password", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> changePassword(@Valid @Body ChangePasswordForm form, Principal principal) {
        if (!Objects.equals(form.newPassword(), form.confirmPassword())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(CHANGE_PASSWORD_TEMPLATE,
                    Map.of("form", formGenerator.generate(CHANGE_PASSWORD_PATH, ChangePasswordForm.class),
                            "errors", List.of(Message.of("Passwords don't match")))));
        }
        User user = userService.getByEmail(principal.getName());
        if (!user.verifyPassword(form.currentPassword())) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(CHANGE_PASSWORD_TEMPLATE,
                    Map.of("form", formGenerator.generate(CHANGE_PASSWORD_PATH, ChangePasswordForm.class),
                            "errors", List.of(Message.of("Incorrect current password")))));
        }
        userService.changePassword(user, form.newPassword());
        return HttpResponse.seeOther(URI.create(USER_PROFILE_PATH));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<ChangePasswordForm> request, ConstraintViolationException ex) {
        Optional<ChangePasswordForm> formOptional = request.getBody();
        return formOptional.isPresent()
                ? HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(CHANGE_PASSWORD_TEMPLATE,
                        Map.of("form", formGenerator.generate(CHANGE_PASSWORD_PATH, formOptional.get(), ex))))
                : HttpResponse.serverError();
    }
}
