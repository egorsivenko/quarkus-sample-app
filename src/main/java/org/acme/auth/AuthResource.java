package org.acme.auth;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class AuthResource {

    @Inject
    Template login;

    @Inject
    Template register;

    @GET
    @Path("/login")
    public TemplateInstance login() {
        return login.instance();
    }

    @GET
    @Path("/register")
    public TemplateInstance register() {
        return register.instance();
    }
}
