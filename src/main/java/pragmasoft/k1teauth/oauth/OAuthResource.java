package pragmasoft.k1teauth.oauth;

import com.nimbusds.jwt.proc.BadJWTException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.RequestBean;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.dto.ErrorResponse;
import pragmasoft.k1teauth.common.generator.CodeGenerator;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.client.OAuthClientRepository;
import pragmasoft.k1teauth.oauth.code.AuthCode;
import pragmasoft.k1teauth.oauth.code.AuthCodeRepository;
import pragmasoft.k1teauth.oauth.consent.Consent;
import pragmasoft.k1teauth.oauth.consent.ConsentRepository;
import pragmasoft.k1teauth.oauth.dto.AuthRequest;
import pragmasoft.k1teauth.oauth.dto.ConsentForm;
import pragmasoft.k1teauth.oauth.dto.TokenResponse;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.oauth.scope.ScopeRepository;
import pragmasoft.k1teauth.security.hash.HashUtil;
import pragmasoft.k1teauth.security.jwt.JwtClaim;
import pragmasoft.k1teauth.security.jwt.JwtService;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller("/oauth2")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Transactional
public class OAuthResource {

    private static final Duration AUTH_CODE_EXP_TIME = Duration.ofMinutes(10);
    private static final Duration ACCESS_TOKEN_EXP_TIME = Duration.ofHours(1);
    private static final Duration REFRESH_TOKEN_EXP_TIME = Duration.ofDays(14);

    private final UserService userService;
    private final JwtService jwtService;
    private final EmbeddedServer embeddedServer;
    private final OAuthClientRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final ConsentRepository consentRepository;
    private final AuthCodeRepository authCodeRepository;

    public OAuthResource(UserService userService,
                         JwtService jwtService,
                         EmbeddedServer embeddedServer,
                         OAuthClientRepository clientRepository,
                         ScopeRepository scopeRepository,
                         ConsentRepository consentRepository,
                         AuthCodeRepository authCodeRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.embeddedServer = embeddedServer;
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.consentRepository = consentRepository;
        this.authCodeRepository = authCodeRepository;
    }

    @Get(uri = "/auth")
    public HttpResponse<?> authorize(@RequestBean AuthRequest request, Principal principal) {
        Optional<OAuthClient> clientOptional = clientRepository.findById(request.getClientId());
        if (clientOptional.isEmpty()) {
            return buildResponse(HttpStatus.NOT_FOUND, "Client ID not found");
        }
        OAuthClient client = clientOptional.get();
        if (!client.getCallbackUrls().contains(request.getRedirectUri())) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Invalid redirect URI");
        }
        if (!"code".equals(request.getResponseType())) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Unsupported response type");
        }
        String codeChallenge = request.getCodeChallenge();
        String codeChallengeMethod = Optional.ofNullable(request.getCodeChallengeMethod()).orElse("plain");

        if (codeChallenge.isEmpty()) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Code challenge is required");
        }
        if (!List.of("plain", "S256").contains(codeChallengeMethod)) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Unsupported code challenge method");
        }
        Set<Scope> requestedScopes = mapScopeStringToSet(request.getScope());
        if (requestedScopes.isEmpty()) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Not a single existing scope has been provided");
        }
        if (!client.getScopes().containsAll(requestedScopes)) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Unsupported scope has been provided");
        }
        User user = userService.getByEmail(principal.getName());
        Optional<Consent> consentOptional = consentRepository.findByResourceOwnerAndClient(user, client);

        if (consentOptional.isPresent()) {
            HttpResponse<?> response = handleExistingConsent(request.getRedirectUri(), consentOptional.get(),
                    requestedScopes, request.getState(), codeChallenge, codeChallengeMethod);
            if (response != null) {
                return response;
            }
        }
        return HttpResponse.ok(new ModelAndView<>(
                "oauth/consent",
                Map.of("clientId", client.getClientId(),
                        "clientName", client.getName(),
                        "callbackUrl", request.getRedirectUri(),
                        "state", request.getState(),
                        "userId", user.getId(),
                        "scopes", requestedScopes,
                        "codeChallenge", codeChallenge,
                        "codeChallengeMethod", codeChallengeMethod)
        ));
    }

    @Post(uri = "/consent")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> consent(@Valid @Body ConsentForm form) {
        UriBuilder uriBuilder = UriBuilder.of(form.getCallbackUrl());

        if (form.userGaveConsent()) {
            Set<Scope> allowedScopes = mapScopeStringToSet(form.getScopes());
            User user = userService.getById(form.getUserId());
            OAuthClient client = clientRepository.findById(form.getClientId()).orElseThrow(NotFoundException::new);
            Optional<Consent> consentOptional = consentRepository.findByResourceOwnerAndClient(user, client);

            Consent consent;
            if (consentOptional.isPresent()) {
                consent = consentOptional.get();
                consent.getScopes().addAll(allowedScopes);
            } else {
                consent = new Consent(user, client, allowedScopes);
            }
            return buildAuthCodeAndRedirect(uriBuilder, consent, form.getState(), form.getCodeChallenge(), form.getCodeChallengeMethod());
        } else {
            uriBuilder
                    .queryParam("error", "access_denied")
                    .queryParam("error_message", "The resource owner declined to provide the necessary consent");
        }
        return HttpResponse.seeOther(uriBuilder.build());
    }

    @Post(uri = "/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> token(@Body("grant_type") String grantType,
                                 @Nullable @Body("code") String code,
                                 @Nullable @Body("code_verifier") String codeVerifier,
                                 @Nullable @Body("refresh_token") String refreshToken,
                                 Principal principal) throws BadJWTException {
        OAuthClient client = clientRepository.findById(principal.getName()).orElseThrow(NotFoundException::new);

        return switch (grantType) {
            case "authorization_code" -> handleAuthorizationCodeGrant(code, codeVerifier, client);
            case "refresh_token" -> handleRefreshTokenGrant(refreshToken, client);
            case "client_credentials" -> handleClientCredentialsGrant(client);
            default -> buildResponse(HttpStatus.BAD_REQUEST, "Unsupported grant type");
        };
    }

    private HttpResponse<?> handleAuthorizationCodeGrant(String code, String codeVerifier, OAuthClient client) {
        Optional<AuthCode> authCodeOptional = authCodeRepository.findById(HashUtil.hashWithSHA256(code));

        if (authCodeOptional.isEmpty()) {
            return buildResponse(HttpStatus.NOT_FOUND, "Auth code does not exist or has already been used");
        }
        AuthCode authCode = authCodeOptional.get();
        Consent consent = authCode.getConsent();

        if (authCode.isExpired()) {
            authCodeRepository.deleteById(HashUtil.hashWithSHA256(code));
            return buildResponse(HttpStatus.BAD_REQUEST, "Auth code has expired");
        }
        if (!consent.getClient().getClientId().equals(client.getClientId())) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Authorization code was issued to a different client");
        }
        if (codeVerifier == null || codeVerifier.isEmpty()) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Code verifier is required");
        }
        if (!verifyCodeChallenge(authCode.getCodeChallenge(), codeVerifier, authCode.getCodeChallengeMethod())) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Invalid code verifier");
        }
        User resourceOwner = consent.getResourceOwner();
        authCodeRepository.deleteById(HashUtil.hashWithSHA256(code));

        String accessToken = generateAccessToken(resourceOwner.getId().toString(), consent.getScopes());
        String refreshToken = generateRefreshToken(consent.getId().toString());

        return buildTokenResponse(accessToken, refreshToken);
    }

    private HttpResponse<?> handleRefreshTokenGrant(String refreshToken, OAuthClient client) throws BadJWTException {
        if (!jwtService.extractClaimsSet(refreshToken).getAudience().contains(embeddedServer.getContextURI().toString())) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Audience mismatch in the refresh token");
        }
        Optional<Consent> consentOptional;
        try {
            consentOptional = consentRepository.findById(UUID.fromString(jwtService.extractClaimsSet(refreshToken).getSubject()));
        } catch (IllegalArgumentException e) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Invalid refresh token subject");
        }
        if (consentOptional.isEmpty()) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Refresh token no longer valid: consent revoked");
        }
        Consent consent = consentOptional.get();

        if (!consent.getClient().getClientId().equals(client.getClientId())) {
            return buildResponse(HttpStatus.BAD_REQUEST, "Refresh Token was issued to a different client");
        }
        String accessToken = generateAccessToken(consent.getResourceOwner().getId().toString(), consent.getScopes());
        String newRefreshToken = generateRefreshToken(consent.getId().toString());

        return buildTokenResponse(accessToken, newRefreshToken);
    }

    private HttpResponse<?> handleClientCredentialsGrant(OAuthClient client) {
        String accessToken = generateAccessToken(client.getClientId(), client.getScopes());
        return buildTokenResponse(accessToken, null);
    }

    private HttpResponse<?> handleExistingConsent(String callbackUrl, Consent consent, Set<Scope> requestedScopes, String state,
                                                  String codeChallenge, String codeChallengeMethod) {
        UriBuilder uriBuilder = UriBuilder.of(callbackUrl);

        if (authCodeRepository.findByConsent(consent).isPresent()) {
            uriBuilder
                    .queryParam("error", "bad_request")
                    .queryParam("error_message", "An existing auth code belonging to this user hasn't yet been used");

            return HttpResponse.seeOther(uriBuilder.build());
        }
        requestedScopes.removeAll(consent.getScopes());

        if (requestedScopes.isEmpty()) {
            return buildAuthCodeAndRedirect(uriBuilder, consent, state, codeChallenge, codeChallengeMethod);
        }
        return null;
    }

    private HttpResponse<?> buildAuthCodeAndRedirect(UriBuilder uriBuilder, Consent consent, String state,
                                                     String codeChallenge, String codeChallengeMethod) {
        AuthCode authCode = new AuthCode();
        String code = CodeGenerator.generate(20);
        authCode.setCode(HashUtil.hashWithSHA256(code));
        authCode.setCodeChallenge(codeChallenge);
        authCode.setCodeChallengeMethod(codeChallengeMethod);
        authCode.setExpiresAt(LocalDateTime.now().plus(AUTH_CODE_EXP_TIME));
        authCode.setConsent(consent);
        authCodeRepository.save(authCode);

        uriBuilder
                .queryParam("code", code)
                .queryParam("state", state);

        return HttpResponse.seeOther(uriBuilder.build());
    }

    private boolean verifyCodeChallenge(String codeChallenge, String codeVerifier, String method) {
        return switch (method) {
            case "plain" -> codeChallenge.equals(codeVerifier);
            case "S256" -> codeChallenge.equals(HashUtil.hashWithSHA256(codeVerifier));
            default -> false;
        };
    }

    private Set<Scope> mapScopeStringToSet(String scopes) {
        return Arrays.stream(scopes.split(" "))
                .map(scope -> scopeRepository.findById(scope.strip()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String generateAccessToken(String subject, Set<Scope> scopes) {
        return jwtService.generate(subject,
                scopes.stream().map(Scope::getAudience).toList(),
                ACCESS_TOKEN_EXP_TIME,
                new JwtClaim("scopes", scopes.stream().map(Scope::getName).toList()));
    }

    private String generateRefreshToken(String subject) {
        return jwtService.generate(subject, List.of(embeddedServer.getContextURI().toString()), REFRESH_TOKEN_EXP_TIME);
    }

    private HttpResponse<?> buildTokenResponse(String accessToken, String refreshToken) {
        return HttpResponse.ok(new TokenResponse(accessToken, refreshToken, ACCESS_TOKEN_EXP_TIME.toSeconds(), "Bearer"));
    }

    private HttpResponse<?> buildResponse(HttpStatus status, Object body) {
        return HttpResponse.status(status)
                .body(body instanceof String error ? new ErrorResponse(error) : body);
    }
}
