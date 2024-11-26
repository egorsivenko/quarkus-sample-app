package pragmasoft.k1teauth.verification;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import pragmasoft.k1teauth.jwt.JwtService;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import org.jboss.resteasy.reactive.RestQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.UUID;

@Path("/verify")
public class VerificationResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerificationResource.class);

    private final UserService userService;
    private final JwtService jwtService;

    public VerificationResource(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GET
    @Path("/registration")
    public Response verifyRegistration(@RestQuery String token) {
        String subj = jwtService.extractSubject(token);
        User user = userService.verifyUser(UUID.fromString(subj));

        LOGGER.info("Successful email verification after registration: `{}`", user.getEmail());
        return Response.seeOther(URI.create("/auth/login")).build();
    }

    @GET
    @Path("/reset-password")
    public Response verifyResetPassword(@RestQuery String token) {
        String subj = jwtService.extractSubject(token);
        User user = userService.verifyUser(UUID.fromString(subj));

        LOGGER.info("Successful email verification for password reset: `{}`", user.getEmail());
        return Response.seeOther(URI.create("/auth/reset-password/" + user.getId())).build();
    }
}
