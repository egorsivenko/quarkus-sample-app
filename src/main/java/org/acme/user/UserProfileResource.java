package org.acme.user;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
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

    private final UserService userService;

    public UserProfileResource(UserService userService) {
        this.userService = userService;
    }

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
    public void changePassword(@Context SecurityContext securityContext,
                               ChangePasswordRequest request) {
        String email = securityContext.getUserPrincipal().getName();
        userService.changePassword(email, request);
    }
}
