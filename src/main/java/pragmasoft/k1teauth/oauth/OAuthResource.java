package pragmasoft.k1teauth.oauth;

import io.quarkiverse.renarde.Controller;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.identity.CurrentIdentityAssociation;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriBuilder;
import org.jboss.resteasy.reactive.RestQuery;
import pragmasoft.k1teauth.jwt.JwtClaim;
import pragmasoft.k1teauth.jwt.JwtService;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.code.AuthCode;
import pragmasoft.k1teauth.oauth.code.CodeGenerator;
import pragmasoft.k1teauth.oauth.consent.Consent;
import pragmasoft.k1teauth.oauth.dto.AuthRequest;
import pragmasoft.k1teauth.oauth.dto.ErrorResponse;
import pragmasoft.k1teauth.oauth.dto.TokenRequest;
import pragmasoft.k1teauth.oauth.dto.TokenResponse;
import pragmasoft.k1teauth.oauth.form.ConsentForm;
import pragmasoft.k1teauth.oauth.form.SignInForm;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;
import pragmasoft.k1teauth.user.exception.UserNotFoundException;
import pragmasoft.k1teauth.util.CsrfTokenValidator;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Path("/oauth2")
public class OAuthResource extends Controller {

    @CheckedTemplate(requireTypeSafeExpressions = false)
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native TemplateInstance signIn(String clientId, String clientName, String callbackUrl,
                                                     String state, boolean error, String scopes);

        public static native TemplateInstance consent(String clientId, String clientName, String callbackUrl,
                                                      String state, UUID userId, Set<Scope> scopes);
    }

    private final CodeGenerator codeGenerator;
    private final UserService userService;
    private final JwtService jwtService;
    private final CurrentIdentityAssociation identity;

    public OAuthResource(CodeGenerator codeGenerator,
                         UserService userService,
                         JwtService jwtService,
                         CurrentIdentityAssociation identity) {
        this.codeGenerator = codeGenerator;
        this.userService = userService;
        this.jwtService = jwtService;
        this.identity = identity;
    }

    @GET
    @Path("/auth")
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public Response authorize(@BeanParam AuthRequest request) {
        Optional<OAuthClient> clientOptional = OAuthClient.findByClientIdOptional(request.getClientId());

        if (clientOptional.isEmpty()) {
            return buildResponse(Status.NOT_FOUND, "Client ID not found");
        }
        OAuthClient client = clientOptional.get();

        if (!client.callbackUrls.contains(request.getRedirectUri())) {
            return buildResponse(Status.BAD_REQUEST, "Invalid redirect URI");
        }
        if (!"code".equals(request.getResponseType())) {
            return buildResponse(Status.BAD_REQUEST, "Unsupported response type");
        }
        Set<Scope> scopeSet = mapScopeStringToSet(request.getScope());

        if (scopeSet.isEmpty()) {
            return buildResponse(Status.BAD_REQUEST, "Not a single existing scope has been provided");
        }
        if (!client.scopes.containsAll(scopeSet)) {
            return buildResponse(Status.BAD_REQUEST, "Unsupported scope has been provided");
        }
        if (!identity.getIdentity().isAnonymous()) {
            String email = identity.getIdentity().getPrincipal().getName();
            User user = userService.getByEmail(email);

            Optional<Consent> consentOptional = Consent.findByResourceOwnerAndClient(user, client);

            if (consentOptional.isPresent()) {
                Response response = handleExistingConsent(request.getRedirectUri(), consentOptional.get(), scopeSet, request.getState());
                if (response != null) {
                    return response;
                }
            }
            return Response.ok(consentTemplate(request.getClientId(), client.name, request.getRedirectUri(),
                    request.getState(), user.getId(), mapScopeSetToString(scopeSet))).build();
        }
        return Response.ok(signInTemplate(request.getClientId(), client.name, request.getRedirectUri(),
                request.getState(), false, mapScopeSetToString(scopeSet))).build();
    }

    @GET
    @Path("/sign-in")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance signInTemplate(@RestQuery String clientId,
                                           @RestQuery String clientName,
                                           @RestQuery String callbackUrl,
                                           @RestQuery String state,
                                           @RestQuery boolean error,
                                           @RestQuery String scopes) {
        return Templates.signIn(clientId, clientName, callbackUrl, state, error, scopes);
    }

    @POST
    @Path("/sign-in")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public Response signIn(@BeanParam @Valid SignInForm form,
                           @CookieParam("csrf-token") Cookie csrfTokenCookie,
                           @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        Supplier<TemplateInstance> failureResponse = () -> signInTemplate(
                form.getClientId(), form.getClientName(), form.getCallbackUrl(), form.getState(), true, form.getScopes());

        if (validationFailed()) {
            return Response.ok(failureResponse.get()).build();
        }
        try {
            User user = userService.getByEmail(form.getEmail());

            if (!user.verifyPassword(form.getPassword())) {
                return Response.ok(failureResponse.get()).build();
            }
            Set<Scope> scopeSet = mapScopeStringToSet(form.getScopes());
            OAuthClient client = OAuthClient.findByClientIdOptional(form.getClientId()).orElseThrow();

            Optional<Consent> consentOptional = Consent.findByResourceOwnerAndClient(user, client);

            if (consentOptional.isPresent()) {
                Response response = handleExistingConsent(form.getCallbackUrl(), consentOptional.get(), scopeSet, form.getState());
                if (response != null) {
                    return response;
                }
            }
            return Response.ok(consentTemplate(form.getClientId(), form.getClientName(), form.getCallbackUrl(),
                    form.getState(), user.getId(), mapScopeSetToString(scopeSet))).build();

        } catch (UserNotFoundException e) {
            return Response.ok(failureResponse.get()).build();
        }
    }

    @GET
    @Path("/consent")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance consentTemplate(@RestQuery String clientId,
                                            @RestQuery String clientName,
                                            @RestQuery String callbackUrl,
                                            @RestQuery String state,
                                            @RestQuery UUID userId,
                                            @RestQuery String scopes) {
        return Templates.consent(clientId, clientName, callbackUrl, state, userId, mapScopeStringToSet(scopes));
    }

    @POST
    @Path("/consent")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Transactional
    public Response consent(@BeanParam @Valid ConsentForm form,
                            @CookieParam("csrf-token") Cookie csrfTokenCookie,
                            @FormParam("csrf-token") String csrfTokenForm) {
        CsrfTokenValidator.validate(csrfTokenCookie, csrfTokenForm);

        UriBuilder uriBuilder = UriBuilder.fromPath(form.getCallbackUrl());

        if (form.userGaveConsent()) {
            User user = userService.getById(form.getUserId());
            OAuthClient client = OAuthClient.findByClientIdOptional(form.getClientId()).orElseThrow();
            Optional<Consent> consentOptional = Consent.findByResourceOwnerAndClient(user, client);

            Consent consent;
            if (consentOptional.isPresent()) {
                consent = consentOptional.get();
                consent.scopes.addAll(mapScopeStringToSet(form.getScopes()));
            } else {
                consent = new Consent();
                consent.resourceOwner = userService.getById(form.getUserId());
                consent.client = OAuthClient.findByClientIdOptional(form.getClientId()).orElseThrow();
                consent.scopes = mapScopeStringToSet(form.getScopes());
            }
            return buildAuthCodeAndRedirect(uriBuilder, consent, form.getState());
        } else {
            uriBuilder
                    .queryParam("error", "access_denied")
                    .queryParam("error_message", "The resource owner declined to provide the necessary consent");
        }
        return Response.seeOther(uriBuilder.build()).build();
    }

    @POST
    @Path("/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response token(@BeanParam TokenRequest request) {
        return switch (request.getGrantType()) {
            case "authorization_code" -> {
                Optional<AuthCode> authCodeOptional = AuthCode.findByCodeOptional(request.getCode());

                if (authCodeOptional.isEmpty()) {
                    yield buildResponse(Status.NOT_FOUND, "Auth code does not exist or has already been used");
                }
                AuthCode authCode = authCodeOptional.get();

                if (authCode.isExpired()) {
                    AuthCode.deleteByCode(authCode.code);
                    yield buildResponse(Status.BAD_REQUEST, "Auth code has expired");
                }

                if (!authCode.consent.client.clientId.equals(request.getClientId())
                        || !authCode.consent.client.clientSecret.equals(request.getClientSecret())) {
                    yield buildResponse(Status.BAD_REQUEST, "Invalid client ID or secret");
                }
                User resourceOwner = authCode.consent.resourceOwner;
                AuthCode.deleteByCode(request.getCode());

                yield buildTokenResponse(resourceOwner.getId().toString(),
                        new JwtClaim("email", resourceOwner.getEmail()),
                        new JwtClaim("full_name", resourceOwner.getFullName()));
            }
            case "client_credentials" -> {
                Optional<OAuthClient> clientOptional = OAuthClient.findByClientIdOptional(request.getClientId());

                if (clientOptional.isEmpty()) {
                    yield buildResponse(Status.NOT_FOUND, "Client ID not found");
                }
                OAuthClient client = clientOptional.get();

                if (!client.clientSecret.equals(request.getClientSecret())) {
                    yield buildResponse(Status.BAD_REQUEST, "Invalid client secret");
                }
                yield buildTokenResponse(client.clientId,
                        new JwtClaim("client_name", client.name),
                        new JwtClaim("callback_urls", client.callbackUrls));
            }
            default -> buildResponse(Status.BAD_REQUEST, "Unsupported grant type");
        };
    }

    private Response handleExistingConsent(String callbackUrl, Consent consent, Set<Scope> scopeSet, String state) {
        UriBuilder uriBuilder = UriBuilder.fromPath(callbackUrl);

        if (AuthCode.findByConsentOptional(consent).isPresent()) {
            uriBuilder
                    .queryParam("error", "bad_request")
                    .queryParam("error_message", "An existing auth code belonging to this user hasn't yet been used");

            return Response.seeOther(uriBuilder.build()).build();
        }
        scopeSet.removeAll(consent.scopes);

        if (scopeSet.isEmpty()) {
            return buildAuthCodeAndRedirect(uriBuilder, consent, state);
        }
        return null;
    }

    private Response buildAuthCodeAndRedirect(UriBuilder uriBuilder, Consent consent, String state) {
        AuthCode authCode = new AuthCode();
        String code = codeGenerator.generate(20);
        authCode.code = code;
        authCode.expiresAt = LocalDateTime.now().plusMinutes(5);
        authCode.consent = consent;
        authCode.persist();

        uriBuilder
                .queryParam("code", code)
                .queryParam("state", state);

        return Response.seeOther(uriBuilder.build()).build();
    }

    private Set<Scope> mapScopeStringToSet(String scopes) {
        return Arrays.stream(scopes.split(" "))
                .map(scope -> Scope.findByName(scope.strip()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String mapScopeSetToString(Set<Scope> scopes) {
        return scopes.stream()
                .map(scope -> scope.name)
                .collect(Collectors.joining(" "));
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
