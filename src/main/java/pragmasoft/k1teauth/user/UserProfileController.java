package pragmasoft.k1teauth.user;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.violation.MessageSource;
import pragmasoft.k1teauth.email.EmailService;
import pragmasoft.k1teauth.user.form.ChangePasswordForm;

import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Hidden
@Controller("/profile")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UserProfileController {

    private final UserService userService;
    private final EmailService emailService;

    public UserProfileController(UserService userService,
                                 EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @Get(produces = MediaType.TEXT_HTML)
    public HttpResponse<?> profile(Principal principal) {
        User user = userService.getByEmail(principal.getName());
        if (!user.isVerified()) {
            emailService.sendRegistrationEmail(user);
            return HttpResponse.seeOther(URI.create("/auth/registration-confirmation?userId=" + user.getId()));
        }
        return HttpResponse.ok(new ModelAndView<>("user/profile", Map.of("user", user)));
    }

    @View("user/changePassword")
    @Get(uri = "/change-password", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> changePasswordTemplate() {
        return HttpResponse.ok(Collections.emptyMap());
    }

    @View("user/changePassword")
    @Post(uri = "/change-password", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> changePassword(@Valid @Body ChangePasswordForm form, Principal principal) {
        if (!Objects.equals(form.getNewPassword(), form.getConfirmPassword())) {
            return HttpResponse.badRequest(Map.of("errors", List.of("Passwords don't match")));
        }
        User user = userService.getByEmail(principal.getName());
        if (!user.verifyPassword(form.getCurrentPassword())) {
            return HttpResponse.badRequest(Map.of("errors", List.of("Incorrect current password")));
        }
        userService.changePassword(user, form.getNewPassword());
        return HttpResponse.seeOther(URI.create("/profile"));
    }

    @View("user/changePassword")
    @Error(exception = ConstraintViolationException.class)
    public Map<String, Object> handleError(ConstraintViolationException ex) {
        return Map.of("errors", MessageSource.violationsMessages(ex.getConstraintViolations()));
    }
}
