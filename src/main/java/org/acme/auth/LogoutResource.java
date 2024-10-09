package org.acme.auth;

import io.quarkus.security.UnauthorizedException;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.util.CookieUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.URI;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class LogoutResource {

    private static final Logger LOGGER = LogManager.getLogger(LogoutResource.class);

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;

    private final CurrentIdentityAssociation identity;

    public LogoutResource(CurrentIdentityAssociation identity) {
        this.identity = identity;
    }

    @POST
    @Path("/logout")
    public Response logout() {
        if (identity.getIdentity().isAnonymous()) {
            throw new UnauthorizedException("Not authenticated");
        }
        LOGGER.info("Logout via email `{}`", identity.getIdentity().getPrincipal().getName());
        var removeCookie = CookieUtils.buildRemoveCookie(cookieName);
        return Response.seeOther(URI.create("/")).cookie(removeCookie).build();
    }
}
