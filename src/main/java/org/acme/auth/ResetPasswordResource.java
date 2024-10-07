package org.acme.auth;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.email.EmailSender;
import org.acme.user.User;
import org.acme.user.UserService;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.net.URI;
import java.util.UUID;

import static org.acme.util.ValidationConstraints.PASSWORD_SIZE_MESSAGE;

@Path("/auth")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.TEXT_HTML)
public class ResetPasswordResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance resetPassword(UUID userId);

        public static native TemplateInstance resetPasswordConfirmation(UUID userId);
    }

    private final UserService userService;
    private final EmailSender emailSender;

    public ResetPasswordResource(UserService userService,
                                 EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/reset-password/{userId}")
    public TemplateInstance resetPassword(@PathParam("userId") UUID userId) {
        return Templates.resetPassword(userId);
    }

    @POST
    @Path("/reset-password")
    public Response resetPassword(
            @RestForm UUID userId,
            @RestForm @NotBlank @Size(min = 6, max = 50, message = PASSWORD_SIZE_MESSAGE) String password
    ) {
        if (validationFailed()) {
            resetPassword(userId);
        }
        User user = userService.getById(userId);
        user.changePassword(password);
        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @GET
    @Path("/reset-password-confirmation")
    public TemplateInstance resetPasswordConfirmation(@RestQuery UUID userId) {
        return Templates.resetPasswordConfirmation(userId);
    }

    @POST
    @Path("/resend-reset-password-email")
    public void resendResetPasswordEmail(@RestForm UUID userId) {
        User user = userService.getById(userId);
        emailSender.sendResetPasswordEmail(user);
        resetPasswordConfirmation(userId);
    }
}
