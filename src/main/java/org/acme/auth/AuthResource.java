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
import org.acme.auth.request.ForgotPasswordRequest;
import org.acme.auth.request.LoginRequest;
import org.acme.auth.request.RegisterRequest;
import org.acme.email.EmailSender;
import org.acme.mapper.UserMapper;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.verification.VerificationTokenStorage;
import org.jboss.resteasy.reactive.RestForm;

import java.net.URI;
import java.util.UUID;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class AuthResource {

    @CheckedTemplate
    static class Templates {
        public static native TemplateInstance login();
        public static native TemplateInstance register();

        public static native TemplateInstance registrationConfirmation(UUID userId);
        public static native TemplateInstance resetPasswordConfirmation(UUID userId);

        public static native TemplateInstance forgotPassword();
        public static native TemplateInstance resetPassword(UUID userId);
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
            sendRegistrationEmail(user);
            return Response.seeOther(URI.create("/auth/registration-confirmation/" + user.getId())).build();
        }
        return Response.ok().build();
    }

    @GET
    @Path("/register")
    public TemplateInstance register() {
        return Templates.register();
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest request) {
        User user = userMapper.mapToUser(request);
        userService.create(user);
        sendRegistrationEmail(user);
        return Response.ok().entity(user.getId()).build();
    }

    @GET
    @Path("/registration-confirmation/{userId}")
    public TemplateInstance registrationConfirmation(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        if (user.isVerified()) {
            return Templates.login();
        }
        return Templates.registrationConfirmation(userId);
    }

    @GET
    @Path("/forgot-password")
    public TemplateInstance forgotPassword() {
        return Templates.forgotPassword();
    }

    @POST
    @Path("/forgot-password")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response forgotPassword(ForgotPasswordRequest request) {
        User user = userService.getByEmail(request.email());
        sendResetPasswordEmail(user);
        return Response.ok().entity(user.getId()).build();
    }

    @GET
    @Path("/reset-password-confirmation/{userId}")
    public TemplateInstance resetPasswordConfirmation(@PathParam("userId") UUID userId) {
        return Templates.resetPasswordConfirmation(userId);
    }

    @GET
    @Path("/reset-password/{userId}")
    public TemplateInstance resetPassword(@PathParam("userId") UUID userId) {
        return Templates.resetPassword(userId);
    }

    @POST
    @Path("/reset-password")
    public Response resetPassword(@RestForm UUID userId, @RestForm String password) {
        User user = userService.getById(userId);
        user.changePassword(password);
        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @POST
    @Path("/resend-registration-email/{userId}")
    public void resendRegistrationEmail(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        sendRegistrationEmail(user);
    }

    @POST
    @Path("/resend-reset-password-email/{userId}")
    public void resendResetPasswordEmail(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        sendResetPasswordEmail(user);
    }

    private void sendRegistrationEmail(User user) {
        String token = UUID.randomUUID().toString();
        verificationTokenStorage.create(token, user);

        String url = uriInfo.getBaseUri().toString() + "verify/registration?token=" + token;
        emailSender.send(user.getEmail(), url);
    }

    private void sendResetPasswordEmail(User user) {
        String token = UUID.randomUUID().toString();
        verificationTokenStorage.create(token, user);

        String url = uriInfo.getBaseUri().toString() + "verify/reset-password?token=" + token;
        emailSender.send(user.getEmail(), url);
    }
}
