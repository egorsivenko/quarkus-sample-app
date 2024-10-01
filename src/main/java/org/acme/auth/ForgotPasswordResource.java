package org.acme.auth;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.auth.request.ForgotPasswordRequest;
import org.acme.email.EmailSender;
import org.acme.turnstile.TurnstileRequest;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class ForgotPasswordResource {

    @CheckedTemplate
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance forgotPassword(String siteKey);
    }

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    @ConfigProperty(name = "turnstile.secret.key")
    String secretKey;

    @RestClient
    TurnstileService turnstileService;

    private final UserService userService;
    private final EmailSender emailSender;

    public ForgotPasswordResource(UserService userService,
                                  EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/forgot-password")
    public TemplateInstance forgotPassword() {
        return Templates.forgotPassword(siteKey);
    }

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(ForgotPasswordRequest request) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, request.token());
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User user = userService.getByEmail(request.email());
        emailSender.sendResetPasswordEmail(user);
        return Response.ok().entity(user.getId()).build();
    }
}
