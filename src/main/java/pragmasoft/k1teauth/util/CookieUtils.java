package pragmasoft.k1teauth.util;

import jakarta.ws.rs.core.NewCookie;

import java.time.Instant;
import java.util.Date;

public final class CookieUtils {

    private CookieUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static NewCookie buildRemoveCookie(String cookieName) {
        return new NewCookie.Builder(cookieName)
                .maxAge(0)
                .expiry(Date.from(Instant.EPOCH))
                .path("/")
                .build();
    }
}
