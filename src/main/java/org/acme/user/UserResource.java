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
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.email.EmailSender;
import org.acme.user.request.ChangePasswordRequest;
import org.acme.util.CookieUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

@Path("/profile")
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class UserResource {

    @CheckedTemplate
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance profile(User user);
        public static native TemplateInstance changePassword();
    }

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;

    private final UserService userService;
    private final EmailSender emailSender;

    public UserResource(UserService userService, EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    public Response profile(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userService.getByEmail(email);

        if (!user.isVerified()) {
            emailSender.sendRegistrationEmail(user);
            return Response.seeOther(URI.create("/auth/registration-confirmation/" + user.getId()))
                    .cookie(CookieUtils.buildRemoveCookie(cookieName))
                    .build();
        }
        return Response.ok(Templates.profile(user)).build();
    }

    @GET
    @Path("/change-password")
    public TemplateInstance changePassword() {
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