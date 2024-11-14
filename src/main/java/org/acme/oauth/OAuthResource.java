package org.acme.oauth;

import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;
import org.acme.jwt.JwtClaim;
import org.acme.jwt.TokenService;
import org.acme.oauth.client.OAuthClient;
import org.acme.oauth.dto.ErrorResponse;
import org.acme.oauth.dto.TokenRequest;
import org.acme.oauth.dto.TokenResponse;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.user.exception.UserNotFoundException;
import org.jboss.resteasy.reactive.RestForm;

import java.util.Optional;

@Path("/oauth2")
public class OAuthResource {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance signIn(String clientId, String clientName, String state, boolean error);

        public static native TemplateInstance consent(OAuthClient client, String userEmail, String state);
    }

    private final CodeGenerator codeGenerator;
    private final UserService userService;
    private final TokenService tokenService;

    public OAuthResource(CodeGenerator codeGenerator,
                         UserService userService,
                         TokenService tokenService) {
        this.codeGenerator = codeGenerator;
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @GET
    @Path("/auth")
    @Produces(MediaType.TEXT_HTML)
    public Response authorize(@QueryParam("client_id") String clientId,
                              @QueryParam("redirect_uri") String redirectUri,
                              @QueryParam("response_type") String responseType,
                              @QueryParam("scope") String scope,
                              @QueryParam("state") String state) {

        Optional<OAuthClient> clientOptional = OAuthClient.findByClientIdOptional(clientId);

        if (clientOptional.isEmpty()) {
            return buildResponse(Status.NOT_FOUND, "Client ID not found");
        }

        OAuthClient client = clientOptional.get();

        if (!client.callbackUrl.equals(redirectUri)) {
            return buildResponse(Status.BAD_REQUEST, "Invalid redirect URI");
        }

        if (!"code".equals(responseType)) {
            return buildResponse(Status.BAD_REQUEST, "Unsupported response type");
        }

        if (!"openid".equals(scope)) {
            return buildResponse(Status.BAD_REQUEST, "Unsupported scope provided");
        }
        return Response.ok(Templates.signIn(clientId, client.name, state, false)).build();
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public TemplateInstance signIn(@RestForm String email,
                                   @RestForm String password,
                                   @RestForm String clientId,
                                   @RestForm String clientName,
                                   @RestForm String state) {
        try {
            User user = userService.getByEmail(email);

            if (!user.verifyPassword(password)) {
                return Templates.signIn(clientId, clientName, state, true);
            }
            OAuthClient client = OAuthClient.findByClientIdOptional(clientId).orElseThrow();
            return Templates.consent(client, user.getEmail(), state);

        } catch (UserNotFoundException e) {
            return Templates.signIn(clientId, clientName, state, true);
        }
    }

    @POST
    @Path("/consent/{consent}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response consent(@PathParam("consent") boolean userGaveConsent,
                            @RestForm String clientId,
                            @RestForm String redirectUri,
                            @RestForm String userEmail,
                            @RestForm String state) {

        UriBuilder uriBuilder = UriBuilder.fromPath(redirectUri);

        if (userGaveConsent) {
            AuthCode authCode = new AuthCode();
            authCode.code = codeGenerator.generate(20);
            authCode.client = OAuthClient.findByClientIdOptional(clientId).orElseThrow();
            authCode.resourceOwner = userService.getByEmail(userEmail);

            authCode.persist();

            uriBuilder
                    .queryParam("code", authCode.code)
                    .queryParam("state", state);
        } else {
            uriBuilder
                    .queryParam("error", "access_denied")
                    .queryParam("error_message", "The resource owner declined to provide the necessary consent");
        }
        return Response.seeOther(uriBuilder.build()).build();
    }

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response token(TokenRequest request) {

        if (!"authorization_code".equals(request.grantType())) {
            return buildResponse(Status.BAD_REQUEST, "Unsupported grant type");
        }

        Optional<AuthCode> authCodeOptional = AuthCode.findByCodeOptional(request.code());

        if (authCodeOptional.isEmpty()) {
            return buildResponse(Status.NOT_FOUND, "Auth code does not exist or has already been used");
        }

        AuthCode authCode = authCodeOptional.get();

        if (!authCode.client.clientId.equals(request.clientId())
                || !authCode.client.clientSecret.equals(request.clientSecret())) {
            return buildResponse(Status.BAD_REQUEST, "Invalid client ID or secret");
        }

        User resourceOwner = authCode.resourceOwner;
        AuthCode.deleteByCode(authCode.code);

        return buildResponse(Status.OK, new TokenResponse(
                tokenService.generate(
                        resourceOwner,
                        new JwtClaim("email", resourceOwner.getEmail()),
                        new JwtClaim("full_name", resourceOwner.getFullName())),
                3600,
                "Bearer"
        ));
    }

    private Response buildResponse(Status status, Object entity) {
        return Response.status(status)
                .entity(entity instanceof String error ? new ErrorResponse(error) : entity)
                .build();
    }
}
