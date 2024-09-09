package org.acme.admin;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.user.UserService;

@Path("/admin")
@Produces(MediaType.TEXT_HTML)
@RolesAllowed("admin")
public class AdminResource {

    @Inject
    Template usersList;

    @Inject
    UserService userService;

    @GET
    @Path("/users")
    public TemplateInstance usersList() {
        return usersList.data("users", userService.listAll());
    }
}
