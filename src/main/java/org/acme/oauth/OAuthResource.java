package org.acme.oauth;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;
import org.acme.jwt.JwtClaim;
import org.acme.jwt.JwtService;
import org.acme.oauth.client.OAuthClient;
import org.acme.oauth.dto.ErrorResponse;
import org.acme.oauth.dto.TokenRequest;
import org.acme.oauth.dto.TokenResponse;
import org.acme.user.User;
import org.acme.user.UserService;
import org.acme.user.exception.UserNotFoundException;
import org.acme.util.CsrfTokenValidator;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.RestQuery;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/oauth2")
public class OAuthResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance signIn(String clientId, String clientName, String callbackUrl,
                                                     String state, boolean error);

        public static native TemplateInstance consent(String clientId, String clientName, String callbackUrl,
                                                      String state, UUID userId);
    }

    private final CodeGenerator codeGenerator;
    private final UserService userService;
    private final JwtService jwtService;

    public OAuthResource(CodeGenerator codeGenerator,
                         UserService userService,
                         JwtService jwtService) {
        this.codeGenerator = codeGenerator;
        this.userService = userService;
        this.jwtService = jwtService;
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

        Set<String> scopeSet = Arrays.stream(scope.split(" "))
                .map(String::strip)
                .collect(Collectors.toSet());

        if (!client.scopes.containsAll(scopeSet)) {
            return buildResponse(Status.BAD_REQUEST, "Unsupported scope provided");
        }
        return Response.ok(Templates.signIn(clientId, client.name, redirectUri, state, false)).build();
    }

    @POST
    @Path("/auth")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance signIn(@RestForm String email,
                                   @RestForm String password,
                                   @RestForm String clientId,
                                   @RestForm String clientName,
                                   @RestForm String callbackUrl,
                                   @RestForm String state,
                                   @CookieParam("csrf-token") Cookie csrfTokenCookie,
                                   @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);
        try {
            User user = userService.getByEmail(email);

            if (!user.verifyPassword(password)) {
                return Templates.signIn(clientId, clientName, callbackUrl, state, true);
            }
            return consentTemplate(clientId, clientName, callbackUrl, state, user.getId());

        } catch (UserNotFoundException e) {
            return Templates.signIn(clientId, clientName, callbackUrl, state, true);
        }
    }

    @GET
    @Path("/consent")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance consentTemplate(@RestQuery String clientId,
                                            @RestQuery String clientName,
                                            @RestQuery String callbackUrl,
                                            @RestQuery String state,
                                            @RestQuery UUID userId) {
        return Templates.consent(clientId, clientName, callbackUrl, state, userId);
    }

    @POST
    @Path("/consent")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response consent(@RestForm boolean userGaveConsent,
                            @RestForm String clientId,
                            @RestForm String callbackUrl,
                            @RestForm UUID userId,
                            @RestForm String state,
                            @CookieParam("csrf-token") Cookie csrfTokenCookie,
                            @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        UriBuilder uriBuilder = UriBuilder.fromPath(callbackUrl);

        if (userGaveConsent) {
            AuthCode authCode = new AuthCode();
            authCode.code = codeGenerator.generate(20);
            authCode.client = OAuthClient.findByClientIdOptional(clientId).orElseThrow();
            authCode.resourceOwner = userService.getById(userId);

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

        return switch (request.grantType()) {
            case "authorization_code" -> {
                Optional<AuthCode> authCodeOptional = AuthCode.findByCodeOptional(request.code());

                if (authCodeOptional.isEmpty()) {
                    yield buildResponse(Status.NOT_FOUND, "Auth code does not exist or has already been used");
                }

                AuthCode authCode = authCodeOptional.get();

                if (!authCode.client.clientId.equals(request.clientId())
                        || !authCode.client.clientSecret.equals(request.clientSecret())) {
                    yield buildResponse(Status.BAD_REQUEST, "Invalid client ID or secret");
                }

                User resourceOwner = authCode.resourceOwner;
                AuthCode.deleteByCode(authCode.code);

                yield buildTokenResponse(resourceOwner.getId().toString(),
                        new JwtClaim("email", resourceOwner.getEmail()),
                        new JwtClaim("full_name", resourceOwner.getFullName()));
            }
            case "client_credentials" -> {
                Optional<OAuthClient> clientOptional = OAuthClient.findByClientIdOptional(request.clientId());

                if (clientOptional.isEmpty()) {
                    yield buildResponse(Status.NOT_FOUND, "Client ID not found");
                }

                OAuthClient client = clientOptional.get();

                if (!client.clientSecret.equals(request.clientSecret())) {
                    yield buildResponse(Status.BAD_REQUEST, "Invalid client secret");
                }

                yield buildTokenResponse(client.id.toString(),
                        new JwtClaim("client_name", client.name),
                        new JwtClaim("homepage_url", client.homepageUrl),
                        new JwtClaim("callback_url", client.callbackUrl));
            }
            default -> buildResponse(Status.BAD_REQUEST, "Unsupported grant type");
        };
    }

    private Response buildTokenResponse(String subject, JwtClaim... claims) {
        String token = jwtService.generate(subject, claims);
        TokenResponse tokenResponse = new TokenResponse(token, 3600, "Bearer");

        return Response.ok(tokenResponse).build();
    }

    private Response buildResponse(Status status, Object entity) {
        return Response.status(status)
                .entity(entity instanceof String error ? new ErrorResponse(error) : entity)
                .build();
    }
}
