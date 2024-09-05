package org.acme.user;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/profile")
@Produces(MediaType.TEXT_HTML)
public class UserProfileResource {

    @Inject
    Template profile;

    @GET
    @Authenticated
    public TemplateInstance profile() {
        return profile.instance();
    }
}
