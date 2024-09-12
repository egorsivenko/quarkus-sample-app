package org.acme.verification;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.acme.user.User;
import org.jboss.resteasy.reactive.RestQuery;

import java.net.URI;

@Path("/verify")
public class VerificationResource {

    @Inject
    VerificationTokenStorage verificationTokenStorage;

    @GET
    public Response verifyEmail(@RestQuery String token) {
        VerificationToken verificationToken = verificationTokenStorage.get(token);

        if (verificationToken == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if (verificationToken.isExpired()) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        User user = verificationToken.user();
        user.setVerified(true);
        verificationTokenStorage.remove(token);

        return Response.seeOther(URI.create("/auth/login")).build();
    }
}
