package org.acme.admin;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.admin.request.EditUserRequest;
import org.acme.user.User;
import org.acme.user.UserRole;
import org.acme.user.UserService;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Path("/admin")
@Produces(MediaType.TEXT_HTML)
@RolesAllowed("admin")
public class AdminResource {

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance usersList(List<User> users);
        public static native TemplateInstance editUser(User user, UserRole[] roles);
    }

    private final UserService userService;

    public AdminResource(UserService userService) {
        this.userService = userService;
    }

    @GET
    @Path("/users")
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
    @Consumes(MediaType.APPLICATION_JSON)
    public void editUser(EditUserRequest request) {
        userService.edit(request);
    }

    @POST
    @Path("/delete-user")
    public Response deleteUser(@RestForm UUID id) {
        userService.delete(id);
        return Response.seeOther(URI.create("/admin/users")).build();
    }
}
