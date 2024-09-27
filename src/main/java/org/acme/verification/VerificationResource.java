package org.acme.verification;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.acme.jwt.TokenService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.jboss.resteasy.reactive.RestQuery;

import java.net.URI;
import java.util.UUID;

@Path("/verify")
public class VerificationResource {

    private final UserService userService;
    private final TokenService tokenService;

    public VerificationResource(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GET
    @Path("/registration")
    public Response verifyRegistration(@RestQuery String token) {
        String subj = tokenService.extractSubject(token);
        User user = userService.getById(UUID.fromString(subj));
        user.setVerified(true);
        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @GET
    @Path("/reset-password")
    public Response verifyResetPassword(@RestQuery String token) {
        String subj = tokenService.extractSubject(token);
        User user = userService.getById(UUID.fromString(subj));
        if (!user.isVerified()) {
            user.setVerified(true);
        }
        return Response.seeOther(URI.create("/auth/reset-password/" + user.getId())).build();
    }
}
