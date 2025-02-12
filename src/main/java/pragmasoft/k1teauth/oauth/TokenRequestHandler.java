package pragmasoft.k1teauth.oauth;

import com.nimbusds.jwt.proc.BadJWTException;
import io.micronaut.http.HttpResponse;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.common.ServerInfo;
import pragmasoft.k1teauth.oauth.client.OAuthClient;
import pragmasoft.k1teauth.oauth.code.AuthCode;
import pragmasoft.k1teauth.oauth.code.AuthCodeRepository;
import pragmasoft.k1teauth.oauth.consent.Consent;
import pragmasoft.k1teauth.oauth.consent.ConsentRepository;
import pragmasoft.k1teauth.oauth.scope.Scope;
import pragmasoft.k1teauth.oauth.scope.ScopeRepository;
import pragmasoft.k1teauth.oauth.util.CodeChallengeUtil;
import pragmasoft.k1teauth.oauth.util.OAuthConstants;
import pragmasoft.k1teauth.oauth.util.ResponseBuilder;
import pragmasoft.k1teauth.security.hash.HashUtil;
import pragmasoft.k1teauth.security.jwt.JwtClaim;
import pragmasoft.k1teauth.security.jwt.JwtService;
import pragmasoft.k1teauth.user.User;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Singleton
public class TokenRequestHandler {

    private final JwtService jwtService;
    private final ServerInfo serverInfo;
    private final ConsentRepository consentRepository;
    private final AuthCodeRepository authCodeRepository;
    private final ScopeRepository scopeRepository;

    public TokenRequestHandler(JwtService jwtService,
                               ServerInfo serverInfo,
                               ConsentRepository consentRepository,
                               AuthCodeRepository authCodeRepository,
                               ScopeRepository scopeRepository) {
        this.jwtService = jwtService;
        this.serverInfo = serverInfo;
        this.consentRepository = consentRepository;
        this.authCodeRepository = authCodeRepository;
        this.scopeRepository = scopeRepository;
    }

    public enum GrantType {
        AUTHORIZATION_CODE,
        REFRESH_TOKEN,
        CLIENT_CREDENTIALS;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    public static Set<String> getAvailableAuthorizationGrantTypes() {
        return Arrays.stream(GrantType.values())
                .map(GrantType::toString)
                .collect(Collectors.toSet());
    }

    public HttpResponse<?> handleTokenRequest(String grantType, String code, String codeVerifier,
                                              String refreshToken, OAuthClient client) throws BadJWTException {
        try {
            return switch (GrantType.valueOf(grantType.toUpperCase())) {
                case AUTHORIZATION_CODE -> handleAuthorizationCodeGrant(code, codeVerifier, client);
                case REFRESH_TOKEN -> handleRefreshTokenGrant(refreshToken, client);
                case CLIENT_CREDENTIALS -> handleClientCredentialsGrant(client);
            };
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("Unsupported grant type");
        }
    }

    private HttpResponse<?> handleAuthorizationCodeGrant(String code, String codeVerifier, OAuthClient client) {
        Optional<AuthCode> authCodeOptional = authCodeRepository.findById(HashUtil.hashWithSHA256(code));
        if (authCodeOptional.isEmpty()) {
            return ResponseBuilder.buildErrorResponse("Auth code is invalid or has already been used");
        }
        AuthCode authCode = authCodeOptional.get();
        Consent consent = authCode.getConsent();

        if (authCode.isExpired()) {
            authCodeRepository.delete(authCode);
            return ResponseBuilder.buildErrorResponse("Auth code has expired");
        }
        if (!consent.getClient().getClientId().equals(client.getClientId())) {
            consentRepository.delete(consent);
            return ResponseBuilder.buildErrorResponse("Authorization code was issued to a different client");
        }
        if (codeVerifier == null || codeVerifier.isEmpty()) {
            return ResponseBuilder.buildErrorResponse("Code verifier is required");
        }
        if (!CodeChallengeUtil.verifyCodeChallenge(authCode.getCodeChallenge(), codeVerifier, authCode.getCodeChallengeMethod())) {
            return ResponseBuilder.buildErrorResponse("Invalid code verifier");
        }
        authCodeRepository.delete(authCode);
        User resourceOwner = consent.getResourceOwner();

        String accessToken = generateAccessToken(resourceOwner.getId().toString(), consent.getScopes());
        String refreshToken = generateRefreshToken(consent.getId().toString());

        if (consent.getScopes().contains(scopeRepository.findById("openid").orElseThrow())) {
            String idToken = generateIdToken(resourceOwner, client.getClientId());
            return ResponseBuilder.buildTokenResponse(accessToken, refreshToken, idToken);
        }
        return ResponseBuilder.buildTokenResponse(accessToken, refreshToken, null);
    }

    private HttpResponse<?> handleRefreshTokenGrant(String refreshToken, OAuthClient client) throws BadJWTException {
        if (!jwtService.extractClaimsSet(refreshToken).getAudience().contains(serverInfo.getBaseUrl())) {
            return ResponseBuilder.buildErrorResponse("Audience mismatch in the refresh token");
        }
        Optional<Consent> consentOptional;
        try {
            consentOptional = consentRepository.findById(UUID.fromString(jwtService.extractClaimsSet(refreshToken).getSubject()));
        } catch (IllegalArgumentException e) {
            return ResponseBuilder.buildErrorResponse("Invalid refresh token subject");
        }
        if (consentOptional.isEmpty()) {
            return ResponseBuilder.buildErrorResponse("Refresh token no longer valid: consent revoked");
        }
        Consent consent = consentOptional.get();

        if (!consent.getClient().getClientId().equals(client.getClientId())) {
            consentRepository.delete(consent);
            return ResponseBuilder.buildErrorResponse("Refresh Token was issued to a different client");
        }
        String accessToken = generateAccessToken(consent.getResourceOwner().getId().toString(), consent.getScopes());
        String newRefreshToken = generateRefreshToken(consent.getId().toString());

        return ResponseBuilder.buildTokenResponse(accessToken, newRefreshToken, null);
    }

    private HttpResponse<?> handleClientCredentialsGrant(OAuthClient client) {
        String accessToken = generateAccessToken(client.getClientId(), client.getScopes());
        return ResponseBuilder.buildTokenResponse(accessToken, null, null);
    }

    private String generateAccessToken(String subject, Set<Scope> scopes) {
        return jwtService.generate(subject,
                scopes.stream().map(Scope::getAudience).toList(),
                OAuthConstants.ACCESS_TOKEN_EXP_TIME,
                new JwtClaim("scopes", scopes.stream().map(Scope::getName).toList()));
    }

    private String generateIdToken(User resourceOwner, String clientId) {
        return jwtService.generate(resourceOwner.getId().toString(),
                List.of(clientId),
                OAuthConstants.ID_TOKEN_EXP_TIME,
                new JwtClaim("name", resourceOwner.getFullName()),
                new JwtClaim("email", resourceOwner.getEmail()),
                new JwtClaim("email_verified", resourceOwner.isVerified()),
                new JwtClaim("created_at", Date.from(resourceOwner.getCreatedAt().toInstant(ZoneOffset.UTC))));
    }

    private String generateRefreshToken(String subject) {
        return jwtService.generate(subject,
                List.of(serverInfo.getBaseUrl()),
                OAuthConstants.REFRESH_TOKEN_EXP_TIME);
    }
}
