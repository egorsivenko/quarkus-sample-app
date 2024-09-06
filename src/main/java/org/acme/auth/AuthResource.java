package org.acme.auth;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.auth.exception.EmailAlreadyTakenException;
import org.acme.auth.request.RegisterRequest;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

@Path("/auth")
@Produces(MediaType.TEXT_HTML)
public class AuthResource {

    @Inject
    Template login;

    @Inject
    Template register;

    @Inject
    AuthService authService;

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

    @POST
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    public void register(RegisterRequest request) {
        authService.registerUser(request);
    }

    @ServerExceptionMapper
    public RestResponse<String> mapException(EmailAlreadyTakenException ex) {
        return RestResponse.status(Response.Status.BAD_REQUEST, ex.getMessage());
    }
}
