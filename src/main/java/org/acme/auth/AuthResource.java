package org.acme.auth;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.acme.auth.request.LoginRequest;
import org.acme.auth.request.RegisterRequest;
import org.acme.email.EmailSender;
import org.acme.mapper.UserMapper;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.verification.VerificationTokenStorage;

import java.net.URI;
import java.util.UUID;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class AuthResource {

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance login();
        public static native TemplateInstance register();
        public static native TemplateInstance confirmation(UUID userId);
    }

    @Inject
    UserService userService;

    @Inject
    UserMapper userMapper;

    @Inject
    EmailSender emailSender;

    @Inject
    VerificationTokenStorage verificationTokenStorage;

    @Context
    UriInfo uriInfo;

    @GET
    @Path("/login")
    public TemplateInstance login() {
        return Templates.login();
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        User user = userService.getByEmail(request.email());
        if (!user.verifyPassword(request.password())) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        if (!user.isVerified()) {
            sendConfirmationEmail(user);
            return Response.seeOther(URI.create("/auth/confirmation/" + user.getId())).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/register")
    public TemplateInstance register() {
        return Templates.register();
    }

    @GET
    @Path("/confirmation/{userId}")
    public TemplateInstance confirmation(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        if (user.isVerified()) {
            return Templates.login();
        }
        return Templates.confirmation(userId);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest request) {
        User user = userMapper.mapToUser(request);
        userService.create(user);
        sendConfirmationEmail(user);
        return Response.ok().entity(user.getId()).build();
    }

    @POST
    @Path("/resend-email/{userId}")
    public void resendEmail(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        sendConfirmationEmail(user);
    }

    private void sendConfirmationEmail(User user) {
        String token = UUID.randomUUID().toString();
        verificationTokenStorage.create(token, user);

        String url = uriInfo.getBaseUri().toString() + "/verify?token=" + token;
        emailSender.send(user.getEmail(), url);
    }
}
