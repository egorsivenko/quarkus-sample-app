package org.acme.auth;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import org.acme.auth.request.ForgotPasswordRequest;
import org.acme.auth.request.LoginRequest;
import org.acme.auth.request.RegisterRequest;
import org.acme.email.EmailSender;
import org.acme.turnstile.TurnstileRequest;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.jwt.TokenService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestForm;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class AuthResource {

    @CheckedTemplate
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance login(String siteKey);
        public static native TemplateInstance register(String siteKey);

        public static native TemplateInstance registrationConfirmation(UUID userId);
        public static native TemplateInstance resetPasswordConfirmation(UUID userId);

        public static native TemplateInstance forgotPassword(String siteKey);
        public static native TemplateInstance resetPassword(UUID userId);
    }

    private static final String TURNSTILE_ERROR = "Turnstile verification failed.";

    @ConfigProperty(name = "turnstile.site.key")
    String siteKey;

    @ConfigProperty(name = "turnstile.secret.key")
    String secretKey;

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;

    @Context
    UriInfo uriInfo;

    @RestClient
    TurnstileService turnstileService;

    private final CurrentIdentityAssociation identity;
    private final UserService userService;
    private final EmailSender emailSender;
    private final TokenService tokenService;

    public AuthResource(CurrentIdentityAssociation identity,
                        UserService userService,
                        EmailSender emailSender,
                        TokenService tokenService) {
        this.identity = identity;
        this.userService = userService;
        this.emailSender = emailSender;
        this.tokenService = tokenService;
    }

    @GET
    @Path("/login")
    public TemplateInstance login() {
        return Templates.login(siteKey);
    }

    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginRequest request) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, request.token());
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            return Response.status(Response.Status.FORBIDDEN).entity(TURNSTILE_ERROR).build();
        }

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

    @POST
    @Path("/logout")
    public Response logout() {
        if (identity.getIdentity().isAnonymous()) {
            throw new UnauthorizedException("Not authenticated");
        }
        final NewCookie removeCookie = new NewCookie.Builder(cookieName)
                .maxAge(0)
                .expiry(Date.from(Instant.EPOCH))
                .path("/")
                .build();
        return Response.seeOther(URI.create("/")).cookie(removeCookie).build();
    }

    @GET
    @Path("/register")
    public TemplateInstance register() {
        return Templates.register(siteKey);
    }

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response register(RegisterRequest request) {
        TurnstileRequest turnstileRequest = new TurnstileRequest(secretKey, request.token());
        if (!turnstileService.verifyToken(turnstileRequest).success()) {
            return Response.status(Response.Status.FORBIDDEN).entity(TURNSTILE_ERROR).build();
        }

        User user = request.mapToUser();
        userService.create(user);
        sendRegistrationEmail(user);
        return Response.ok().entity(user.getId()).build();
    }

    @GET
    @Path("/registration-confirmation/{userId}")
    public TemplateInstance registrationConfirmation(@PathParam("userId") UUID userId) {
        User user = userService.getById(userId);
        if (user.isVerified()) {
            return Templates.login(siteKey);
        }
        return Templates.registrationConfirmation(userId);
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
            return Response.status(Response.Status.FORBIDDEN).entity(TURNSTILE_ERROR).build();
        }

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
        String token = tokenService.generate(user);

        String url = uriInfo.getBaseUri().toString() + "verify/registration?token=" + token;
        emailSender.send(user.getEmail(), url);
    }

    private void sendResetPasswordEmail(User user) {
        String token = tokenService.generate(user);

        String url = uriInfo.getBaseUri().toString() + "verify/reset-password?token=" + token;
        emailSender.send(user.getEmail(), url);
    }
}
