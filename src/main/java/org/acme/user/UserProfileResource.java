package org.acme.user;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.user.request.ChangePasswordRequest;

@Path("/profile")
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class UserProfileResource {

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance profile(User user);
        public static native TemplateInstance changePassword();
    }

    @Inject
    UserService userService;

    @GET
    public TemplateInstance profileTemplate(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userService.getByEmail(email);
        return Templates.profile(user);
    }

    @GET
    @Path("/change-password")
    public TemplateInstance changePasswordTemplate() {
        return Templates.changePassword();
    }

    @POST
    @Path("/change-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@Context SecurityContext securityContext,
                                   ChangePasswordRequest request) {
        String email = securityContext.getUserPrincipal().getName();

        if (!userService.verifyPassword(email, request.currentPassword())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        userService.changePassword(email, request.newPassword());
        return Response.ok().build();
    }
}
