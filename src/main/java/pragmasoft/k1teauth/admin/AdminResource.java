package pragmasoft.k1teauth.admin;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import pragmasoft.k1teauth.admin.form.EditUserForm;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserRole;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.user.exception.EmailAlreadyTakenException;
import pragmasoft.k1teauth.util.CsrfTokenValidator;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;
import java.util.UUID;

import static pragmasoft.k1teauth.util.FlashScopeConstants.EMAIL_ALREADY_REGISTERED;
import static pragmasoft.k1teauth.util.FlashScopeConstants.ERROR;

@Path("/admin")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@RolesAllowed("admin")
public class AdminResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance usersList(List<User> users);

        public static native TemplateInstance editUser(User user, UserRole[] roles);
    }

    private final UserService userService;

    public AdminResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/users-list")
    public TemplateInstance usersList() {
        return Templates.usersList(userService.listAll());
    }

    @GET
    @Path("/edit-user")
    public TemplateInstance editUserTemplate(@RestQuery UUID id) {
        return Templates.editUser(userService.getById(id), UserRole.values());
    }

    @POST
    @Path("/edit-user")
    public void editUser(@BeanParam @Valid EditUserForm form,
                         @CookieParam("csrf-token") Cookie csrfTokenCookie,
                         @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);
        try {
            if (validationFailed()) {
                editUserTemplate(form.getId());
            }
            userService.edit(form);
            usersList();
        } catch (EmailAlreadyTakenException e) {
            flash(ERROR, EMAIL_ALREADY_REGISTERED);
            editUserTemplate(form.getId());
        }
    }

    @POST
    @Path("/delete-user")
    public void deleteUser(@RestForm UUID id,
                           @CookieParam("csrf-token") Cookie csrfTokenCookie,
                           @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        userService.delete(id);
        usersList();
    }
}
