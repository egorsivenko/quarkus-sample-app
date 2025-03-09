package pragmasoft.k1teauth.admin;

import io.micronaut.data.model.Pageable;
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
import io.micronaut.security.annotation.Secured;
import io.micronaut.views.fields.FormGenerator;
import io.micronaut.views.fields.messages.Message;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.admin.form.EditUserForm;
import pragmasoft.k1teauth.common.jte.JteTemplateRenderer;
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

    private static final String USERS_LIST_PATH = "/admin/users-list";
    private static final String EDIT_USER_PATH = "/admin/edit-user";
    private static final String USERS_LIST_TEMPLATE = "admin/usersList.jte";
    private static final String EDIT_USER_TEMPLATE = "admin/editUser.jte";

    private final UserService userService;
    private final FormGenerator formGenerator;
    private final JteTemplateRenderer jteTemplateRenderer;

    public AdminController(UserService userService,
                           FormGenerator formGenerator,
                           JteTemplateRenderer jteTemplateRenderer) {
        this.userService = userService;
        this.formGenerator = formGenerator;
        this.jteTemplateRenderer = jteTemplateRenderer;
    }

    @Get(uri = "/users-list", produces = MediaType.TEXT_HTML)
    public String usersList(Pageable pageable) {
        return jteTemplateRenderer.render(USERS_LIST_TEMPLATE,
                Map.of("page", userService.listAll(pageable), "formGenerator", formGenerator));
    }

    @Get(uri = "/edit-user", produces = MediaType.TEXT_HTML)
    public String editUserTemplate(@QueryValue UUID id) {
        return jteTemplateRenderer.render(EDIT_USER_TEMPLATE,
                Map.of("form", formGenerator.generate(EDIT_USER_PATH, EditUserForm.from(userService.getById(id)))));
    }

    @Post(uri = "/edit-user", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<String> editUser(@Valid @Body EditUserForm form) {
        try {
            userService.edit(form);
            return HttpResponse.seeOther(URI.create(USERS_LIST_PATH));
        } catch (EmailAlreadyTakenException e) {
            return HttpResponse.badRequest(jteTemplateRenderer.render(EDIT_USER_TEMPLATE,
                    Map.of("form", formGenerator.generate(EDIT_USER_PATH, form),
                            "errors", List.of(Message.of("Email address is already registered")))));
        }
    }

    @Post(uri = "/delete-user", consumes = MediaType.APPLICATION_FORM_URLENCODED, produces = MediaType.TEXT_HTML)
    public HttpResponse<Void> deleteUser(UUID id) {
        userService.delete(id);
        return HttpResponse.seeOther(URI.create(USERS_LIST_PATH));
    }

    @Error(exception = ConstraintViolationException.class)
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse<String> handleError(HttpRequest<EditUserForm> request, ConstraintViolationException ex) {
        Optional<EditUserForm> formOptional = request.getBody();
        return formOptional.isPresent()
                ? HttpResponse.unprocessableEntity().body(jteTemplateRenderer.render(EDIT_USER_TEMPLATE,
                        Map.of("form", formGenerator.generate(EDIT_USER_PATH, formOptional.get(), ex))))
                : HttpResponse.serverError();
    }
}
