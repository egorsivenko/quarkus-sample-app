package org.acme.auth;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.auth.request.RegisterRequest;
import org.acme.email.EmailSender;
import org.acme.turnstile.TurnstileRequest;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.net.URI;
import java.util.UUID;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class RegistrationResource {

    @CheckedTemplate
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance registration(String siteKey);

        public static native TemplateInstance registrationConfirmation(UUID userId);
    }

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    @ConfigProperty(name = "turnstile.secret.key")
    String secretKey;

    @RestClient
    TurnstileService turnstileService;

    private final UserService userService;
    private final EmailSender emailSender;

    public RegistrationResource(UserService userService,
                                EmailSender emailSender) {
        this.userService = userService;
        this.emailSender = emailSender;
    }

    @GET
    @Path("/registration")
    public TemplateInstance registration() {
        return Templates.registration(siteKey);
    }

    @POST
    @Path("/registration")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registration(RegisterRequest request) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, request.token());
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        User user = request.mapToUser();
        userService.create(user);
        emailSender.sendRegistrationEmail(user);
        return Response.ok().entity(user.getId()).build();
    }

    @GET
    @Path("/registration-confirmation/{userId}")
    public Response registrationConfirmation(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        if (user.isVerified()) {
            return Response.seeOther(URI.create("/auth/login")).build();
        }
        return Response.ok(Templates.registrationConfirmation(userId)).build();
    }

    @POST
    @Path("/resend-registration-email/{userId}")
    public void resendRegistrationEmail(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        emailSender.sendRegistrationEmail(user);
    }
}
