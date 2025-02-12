package pragmasoft.k1teauth.oauth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Consumes;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Header;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.RequestBean;
import io.micronaut.http.server.exceptions.NotFoundException;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.views.ModelAndView;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import pragmasoft.k1teauth.common.ServerInfo;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.client.OAuthClientRepository;
import pragmasoft.k1teauth.oauth.code.AuthCode;
import pragmasoft.k1teauth.oauth.code.AuthCodeRepository;
import pragmasoft.k1teauth.oauth.consent.Consent;
import pragmasoft.k1teauth.oauth.consent.ConsentRepository;
import pragmasoft.k1teauth.oauth.dto.AuthRequest;
import pragmasoft.k1teauth.oauth.dto.ConsentForm;
import pragmasoft.k1teauth.oauth.dto.UserInfoResponse;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.oauth.scope.ScopeRepository;
import pragmasoft.k1teauth.oauth.util.CodeChallengeUtil;
import pragmasoft.k1teauth.oauth.util.OAuthConstants;
import pragmasoft.k1teauth.oauth.util.ResponseBuilder;
import pragmasoft.k1teauth.security.generator.CodeGenerator;
import pragmasoft.k1teauth.security.hash.HashUtil;
import pragmasoft.k1teauth.security.jwt.JwtService;
import pragmasoft.k1teauth.user.User;
import pragmasoft.k1teauth.user.UserService;

import java.security.Principal;
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
public class OAuthController {

    private static final String BEARER_PREFIX = "Bearer ";

    private final UserService userService;
    private final JwtService jwtService;
    private final ServerInfo serverInfo;
    private final TokenRequestHandler tokenRequestHandler;
    private final OAuthClientRepository clientRepository;
    private final ScopeRepository scopeRepository;
    private final ConsentRepository consentRepository;
    private final AuthCodeRepository authCodeRepository;

    public OAuthController(UserService userService,
                           JwtService jwtService,
                           ServerInfo serverInfo,
                           TokenRequestHandler tokenRequestHandler,
                           OAuthClientRepository clientRepository,
                           ScopeRepository scopeRepository,
                           ConsentRepository consentRepository,
                           AuthCodeRepository authCodeRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.serverInfo = serverInfo;
        this.tokenRequestHandler = tokenRequestHandler;
        this.clientRepository = clientRepository;
        this.scopeRepository = scopeRepository;
        this.consentRepository = consentRepository;
        this.authCodeRepository = authCodeRepository;
    }

    @Get(uri = "/auth")
    public HttpResponse<?> authorization(@RequestBean AuthRequest request, Principal principal) {
        Optional<OAuthClient> clientOptional = clientRepository.findById(request.getClientId());
        if (clientOptional.isEmpty()) {
            return ResponseBuilder.buildErrorResponse("Unknown Client ID");
        }
        OAuthClient client = clientOptional.get();
        if (!client.getCallbackUrls().contains(request.getRedirectUri())) {
            return ResponseBuilder.buildErrorResponse("Invalid redirect URI");
        }
        if (!"code".equals(request.getResponseType())) {
            return ResponseBuilder.buildErrorResponse("Unsupported response type");
        }
        String codeChallenge = request.getCodeChallenge();
        String codeChallengeMethod = Optional.ofNullable(request.getCodeChallengeMethod()).orElse("plain");

        if (!StringUtils.hasText(codeChallenge)) {
            return ResponseBuilder.buildErrorResponse("Code challenge is required");
        }
        if (!CodeChallengeUtil.getAvailableCodeChallengeMethods().contains(codeChallengeMethod)) {
            return ResponseBuilder.buildErrorResponse("Unsupported code challenge method");
        }
        Set<Scope> requestedScopes = mapScopeStringToSet(request.getScope());
        if (requestedScopes.isEmpty()) {
            return ResponseBuilder.buildErrorResponse("Not a single existing scope has been provided");
        }
        if (!client.getScopes().containsAll(requestedScopes)) {
            return ResponseBuilder.buildErrorResponse("Unsupported scope has been provided");
        }
        User user = userService.getByEmail(principal.getName());
        Optional<Consent> consentOptional = consentRepository.findByResourceOwnerAndClient(user, client);

        if (consentOptional.isPresent()) {
            HttpResponse<?> response = handleExistingConsent(consentOptional.get(), requestedScopes,
                    request.getRedirectUri(), request.getState(), codeChallenge, codeChallengeMethod, request.getNonce());
            if (response != null) {
                return response;
            }
        }
        return HttpResponse.ok(new ModelAndView<>("oauth/consent",
                Map.of("form", new ConsentForm(
                        client.getName(),
                        request.getRedirectUri(),
                        request.getState(),
                        user.getId(),
                        mapScopeSetToScopeNames(requestedScopes),
                        mapScopeSetToScopeDescriptions(requestedScopes),
                        codeChallenge,
                        codeChallengeMethod,
                        request.getNonce()
                ))
        ));
    }

    @Post(uri = "/consent")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public HttpResponse<?> consent(@Valid @Body ConsentForm form) {
        UriBuilder uriBuilder = UriBuilder.of(form.getCallbackUrl());

        if (form.userGaveConsent()) {
            Set<Scope> allowedScopes = mapScopeStringToSet(form.getScopeNames());
            User user = userService.getById(form.getUserId());
            OAuthClient client = clientRepository.findByName(form.getClientName()).orElseThrow(NotFoundException::new);
            Optional<Consent> consentOptional = consentRepository.findByResourceOwnerAndClient(user, client);

            Consent consent;
            if (consentOptional.isPresent()) {
                consent = consentOptional.get();
                consent.getScopes().addAll(allowedScopes);
            } else {
                consent = new Consent(user, client, allowedScopes);
            }
            return buildAuthCodeAndRedirect(uriBuilder, consent, form.getState(),
                    form.getCodeChallenge(), form.getCodeChallengeMethod(), form.getNonce());
        } else {
            uriBuilder
                    .queryParam("error", "access_denied")
                    .queryParam("error_description", "The resource owner declined to provide the necessary consent")
                    .queryParam("state", form.getState());
        }
        return ResponseBuilder.buildRedirectResponse(uriBuilder.build());
    }

    @Post(uri = "/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public HttpResponse<?> token(@Body("grant_type") String grantType,
                                 @Nullable @Body("code") String code,
                                 @Nullable @Body("code_verifier") String codeVerifier,
                                 @Nullable @Body("refresh_token") String refreshToken,
                                 Principal principal) throws BadJWTException {
        Optional<OAuthClient> clientOptional = clientRepository.findById(principal.getName());
        if (clientOptional.isEmpty()) {
            return ResponseBuilder.buildErrorResponse("Unknown Client ID");
        }
        return tokenRequestHandler.handleTokenRequest(grantType, code, codeVerifier, refreshToken, clientOptional.get());
    }

    @Get(uri = "/userinfo", produces = MediaType.APPLICATION_JSON)
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> userInfoGet(@Header(HttpHeaders.AUTHORIZATION) String authorization) {
        return processUserInfoRequest(authorization);
    }

    @Post(uri = "/userinfo", produces = MediaType.APPLICATION_JSON)
    @Secured(SecurityRule.IS_ANONYMOUS)
    public HttpResponse<?> userInfoPost(@Header(HttpHeaders.AUTHORIZATION) String authorization) {
        return processUserInfoRequest(authorization);
    }

    private HttpResponse<?> handleExistingConsent(Consent consent, Set<Scope> requestedScopes,
                                                  String callbackUrl, String state,
                                                  String codeChallenge, String codeChallengeMethod, String nonce) {
        UriBuilder uriBuilder = UriBuilder.of(callbackUrl);

        if (authCodeRepository.findByConsent(consent).isPresent()) {
            uriBuilder
                    .queryParam("error", "access_denied")
                    .queryParam("error_description", "An existing auth code belonging to this user hasn't yet been used")
                    .queryParam("state", state);

            return ResponseBuilder.buildRedirectResponse(uriBuilder.build());
        }
        requestedScopes.removeAll(consent.getScopes());

        if (requestedScopes.isEmpty()) {
            return buildAuthCodeAndRedirect(uriBuilder, consent, state, codeChallenge, codeChallengeMethod, nonce);
        }
        return null;
    }

    private HttpResponse<?> buildAuthCodeAndRedirect(UriBuilder uriBuilder, Consent consent, String state,
                                                     String codeChallenge, String codeChallengeMethod, String nonce) {
        AuthCode authCode = new AuthCode();
        String code = CodeGenerator.generate(40);
        authCode.setCode(HashUtil.hashWithSHA256(code));
        authCode.setCodeChallenge(codeChallenge);
        authCode.setCodeChallengeMethod(codeChallengeMethod);
        authCode.setNonce(nonce);
        authCode.setExpiresAt(LocalDateTime.now().plus(OAuthConstants.AUTH_CODE_EXP_TIME));
        authCode.setConsent(consent);
        authCodeRepository.save(authCode);

        uriBuilder
                .queryParam("code", code)
                .queryParam("state", state)
                .queryParam("iss", serverInfo.getBaseUrl());

        return ResponseBuilder.buildRedirectResponse(uriBuilder.build());
    }

    private HttpResponse<?> processUserInfoRequest(String authHeader) {
        String accessToken = extractJwtToken(authHeader);
        try {
            JWTClaimsSet jwtClaimsSet = jwtService.extractClaimsSet(accessToken);
            List<String> scopes = jwtClaimsSet.getStringListClaim("scopes");
            if (!jwtClaimsSet.getAudience().contains(serverInfo.getBaseUrl())
                    || scopes == null
                    || !scopes.contains("openid")) {
                return HttpResponse.status(HttpStatus.FORBIDDEN);
            }
            User user = userService.getById(UUID.fromString(jwtClaimsSet.getSubject()));
            return HttpResponse.ok(UserInfoResponse.fromUser(user));

        } catch (Exception e) {
            return ResponseBuilder.buildErrorResponse(e.getMessage());
        }
    }

    private String extractJwtToken(String authHeader) {
        return StringUtils.hasText(authHeader) && authHeader.startsWith(BEARER_PREFIX)
                ? authHeader.substring(BEARER_PREFIX.length())
                : null;
    }

    private Set<Scope> mapScopeStringToSet(String scopes) {
        return Arrays.stream(scopes.split(" "))
                .map(scope -> scopeRepository.findById(scope.strip()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private String mapScopeSetToScopeNames(Set<Scope> scopes) {
        return scopes.stream().map(Scope::getName).collect(Collectors.joining(" "));
    }

    private String mapScopeSetToScopeDescriptions(Set<Scope> scopes) {
        return scopes.stream().map(Scope::getDescription).collect(Collectors.joining("\n"));
    }
}
