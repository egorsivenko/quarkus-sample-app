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
import io.micronaut.views.fields.Form;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.admin.form.EditUserForm;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.user.exception.EmailAlreadyTakenException;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Hidden
@Controller("/admin")
@Secured("ADMIN")
public class AdminController {

    private final UserService userService;
    private final FormGenerator formGenerator;

    public AdminController(UserService userService,
                           FormGenerator formGenerator) {
        this.userService = userService;
        this.formGenerator = formGenerator;
    }

    @View("admin/usersList")
    @Get(uri = "/users-list", produces = MediaType.TEXT_HTML)
    public Map<String, List<User>> usersList() {
        return Map.of("users", userService.listAll());
    }

    @View("admin/editUser")
    @Get(uri = "/edit-user", produces = MediaType.TEXT_HTML)
    public Map<String, Form> editUserTemplate(@QueryValue UUID id) {
        User user = userService.getById(id);
        return Map.of("form", formGenerator.generate("/admin/edit-user", EditUserForm.from(user)));
    }

    @Post(uri = "/edit-user", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> editUser(@Valid @Body EditUserForm form) {
        try {
            userService.edit(form);
            return HttpResponse.seeOther(URI.create("/admin/users-list"));
        } catch (EmailAlreadyTakenException e) {
            return HttpResponse.badRequest(new ModelAndView<>("admin/editUser",
                    Map.of("form", formGenerator.generate("/admin/edit-user", form),
                            "errors", List.of(Message.of("Email address is already registered")))
            ));
        }
    }

    @Post(uri = "/delete-user", consumes = MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<Void> deleteUser(UUID id) {
        userService.delete(id);
        return HttpResponse.seeOther(URI.create("/admin/users-list"));
    }

    @Error(exception = ConstraintViolationException.class)
    public HttpResponse<?> handleError(HttpRequest<EditUserForm> request, ConstraintViolationException ex) {
        Optional<EditUserForm> formOptional = request.getBody();
        return formOptional.isPresent()
                ? HttpResponse.unprocessableEntity().body(new ModelAndView<>("admin/editUser",
                        Map.of("form", formGenerator.generate("/admin/edit-user", formOptional.get(), ex))))
                : HttpResponse.serverError();
    }
}
