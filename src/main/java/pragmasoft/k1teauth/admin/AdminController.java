package pragmasoft.k1teauth.admin;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.ModelAndView;
import io.micronaut.views.View;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.violation.MessageSource;
import pragmasoft.k1teauth.user.User.Role;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.user.exception.EmailAlreadyTakenException;
import pragmasoft.k1teauth.user.form.EditUserForm;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller("/admin")
@Secured("ADMIN")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @View("admin/usersList")
    @Get(uri = "/users-list", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> usersList() {
        return HttpResponse.ok(Map.of("users", userService.listAll()));
    }

    @View("admin/editUser")
    @Get(uri = "/edit-user", produces = MediaType.TEXT_HTML)
    public HttpResponse<?> editUserTemplate(@QueryValue UUID id) {
        return HttpResponse.ok(Map.of("user", userService.getById(id), "roles", Role.values()));
    }

    @Post(uri = "/edit-user", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> editUser(@Valid @Body EditUserForm form) {
        try {
            userService.edit(form);
            return HttpResponse.seeOther(URI.create("/admin/users-list"));
        } catch (EmailAlreadyTakenException e) {
            return HttpResponse.badRequest(new ModelAndView<>(
                    "admin/editUser",
                    Map.of("user", userService.getById(form.getId()), "roles", Role.values(),
                            "errors", List.of("Email address is already registered"))
            ));
        }
    }

    @Post(uri = "/delete-user", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> deleteUser(UUID id) {
        userService.delete(id);
        return HttpResponse.seeOther(URI.create("/admin/users-list"));
    }

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<?> handleError(HttpRequest<EditUserForm> request, ConstraintViolationException ex) {
        Optional<UUID> userIdOptional = request.getBody().map(EditUserForm::getId);
        return userIdOptional.isPresent()
                ? HttpResponse.badRequest(new ModelAndView<>(
                        "admin/editUser",
                        Map.of("user", userService.getById(userIdOptional.get()),
                                "roles", Role.values(),
                                "errors", MessageSource.violationsMessages(ex.getConstraintViolations()))
                ))
                : HttpResponse.seeOther(URI.create("/admin/users-list"));
    }
}
