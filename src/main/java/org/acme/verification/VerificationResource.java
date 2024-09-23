package org.acme.verification;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.acme.user.User;
import org.jboss.resteasy.reactive.RestQuery;

import java.net.URI;

@Path("/verify")
public class VerificationResource {

    private final VerificationTokenStorage verificationTokenStorage;

    public VerificationResource(VerificationTokenStorage verificationTokenStorage) {
        this.verificationTokenStorage = verificationTokenStorage;
    }

    @GET
    @Path("/registration")
    public Response verifyRegistration(@RestQuery String token) {
        VerificationToken verificationToken = validateAndGetToken(token);

        User user = verificationToken.user();
        user.setVerified(true);
        verificationTokenStorage.remove(token);

        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @GET
    @Path("/reset-password")
    public Response verifyResetPassword(@RestQuery String token) {
        VerificationToken verificationToken = validateAndGetToken(token);

        User user = verificationToken.user();
        if (!user.isVerified()) {
            user.setVerified(true);
        }
        verificationTokenStorage.remove(token);

        return Response.seeOther(URI.create("/auth/reset-password/" + user.getId())).build();
    }

    private VerificationToken validateAndGetToken(String token) {
        VerificationToken verificationToken = verificationTokenStorage.get(token);

        if (verificationToken == null) {
            throw new NotFoundException();
        }
        if (verificationToken.isExpired()) {
            throw new BadRequestException();
        }
        return verificationToken;
    }
}
