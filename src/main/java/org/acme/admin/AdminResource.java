package org.acme.admin;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.admin.form.EditUserForm;
import org.acme.user.User;
import org.acme.user.UserRole;
import org.acme.user.UserService;
import org.acme.user.exception.EmailAlreadyTakenException;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.List;
import java.util.UUID;

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
    public TemplateInstance editUser(@RestQuery UUID id) {
        return Templates.editUser(userService.getById(id), UserRole.values());
    }

    @POST
    @Path("/edit-user")
    public void editUser(@BeanParam @Valid EditUserForm form) {
        try {
            if (validationFailed()) {
                editUser(form.getId());
            }
            userService.edit(form);
            usersList();
        } catch (EmailAlreadyTakenException e) {
            flash("error", "Email is already taken.");
            editUser(form.getId());
        }
    }

    @POST
    @Path("/delete-user")
    public void deleteUser(@RestForm UUID id) {
        userService.delete(id);
        usersList();
    }
}
