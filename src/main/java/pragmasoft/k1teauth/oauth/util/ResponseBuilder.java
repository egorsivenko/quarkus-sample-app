package pragmasoft.k1teauth.oauth.util;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import pragmasoft.k1teauth.common.dto.ErrorResponse;
import pragmasoft.k1teauth.oauth.dto.TokenResponse;

import java.net.URI;

public final class ResponseBuilder {

    private ResponseBuilder() {}

    public static HttpResponse<?> buildTokenResponse(String accessToken, String refreshToken, String idToken) {
        return HttpResponse
                .ok(new TokenResponse(accessToken, refreshToken, idToken,
                        OAuthConstants.ACCESS_TOKEN_EXP_TIME.toSeconds(), "Bearer"))
                .header(HttpHeaders.CACHE_CONTROL, "no-store");
    }

    public static HttpResponse<?> buildRedirectResponse(URI location) {
        return HttpResponse.status(HttpStatus.FOUND)
                .headers(headers ->
                        headers.location(location).add(HttpHeaders.CACHE_CONTROL, "no-store")
                );
    }

    public static HttpResponse<?> buildErrorResponse(String error) {
        return HttpResponse.badRequest(new ErrorResponse(error))
                .header(HttpHeaders.CACHE_CONTROL, "no-store");
    }
}
