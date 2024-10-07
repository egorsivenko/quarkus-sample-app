package org.acme.user;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriBuilder;
import org.acme.email.EmailSender;
import org.acme.user.form.ChangePasswordForm;
import org.acme.util.CookieUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

@Path("/profile")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
@Authenticated
public class UserProfileResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
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

    public UserProfileResource(UserService userService, EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/")
    public Response profile(@Context SecurityContext securityContext) {
        String email = securityContext.getUserPrincipal().getName();
        User user = userService.getByEmail(email);

        if (!user.isVerified()) {
            emailSender.sendRegistrationEmail(user);

            URI uri = UriBuilder.fromPath("/auth/registration-confirmation")
                    .queryParam("userId", user.getId())
                    .build();

            return Response.seeOther(uri)
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
    public Response changePassword(
            @Context SecurityContext securityContext,
            @BeanParam @Valid ChangePasswordForm form
    ) {
        validation.equals("passwordMatch", form.getNewPassword(), form.getConfirmPassword());
        if (validationFailed()) {
            changePassword();
        }
        String email = securityContext.getUserPrincipal().getName();
        User user = userService.getByEmail(email);

        if (!user.verifyPassword(form.getCurrentPassword())) {
            flash("error", "Incorrect current password.");
            changePassword();
        }
        user.changePassword(form.getNewPassword());
        return Response.seeOther(URI.create("/profile")).build();
    }
}
