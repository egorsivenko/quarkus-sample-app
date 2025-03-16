package pragmasoft.k1teauth.oauth;

import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpMethod;
import io.micronaut.http.MutableHttpRequest;
import io.micronaut.http.annotation.RequestFilter;
import io.micronaut.http.annotation.ServerFilter;
import io.micronaut.http.server.annotation.PreMatching;
import io.micronaut.security.token.cookie.AccessTokenCookieConfiguration;

@ServerFilter
public class OAuthFilters {

    private final AccessTokenCookieConfiguration tokenCookieConfig;

    public OAuthFilters(AccessTokenCookieConfiguration tokenCookieConfig) {
        this.tokenCookieConfig = tokenCookieConfig;
    }

    @PreMatching
    @RequestFilter(patterns = "/oauth2/token", methods = HttpMethod.POST)
    public void excludeJWTCookie(MutableHttpRequest<?> request) {
        request.getHeaders()
                .findFirst(HttpHeaders.COOKIE)
                .ifPresent(cookieHeader -> {
                    String[] cookies = cookieHeader.split(";");
                    StringBuilder newCookieHeader = new StringBuilder();

                    for (String cookie : cookies) {
                        if (!cookie.trim().startsWith(tokenCookieConfig.getCookieName())) {
                            if (!newCookieHeader.isEmpty()) {
                                newCookieHeader.append(";");
                            }
                            newCookieHeader.append(cookie.trim());
                        }
                    }

                    if (!newCookieHeader.isEmpty()) {
                        request.getHeaders().set(HttpHeaders.COOKIE, newCookieHeader.toString());
                    } else {
                        request.getHeaders().remove(HttpHeaders.COOKIE);
                    }
                });
    }
}
