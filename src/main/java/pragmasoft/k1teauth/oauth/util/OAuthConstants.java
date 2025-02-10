package pragmasoft.k1teauth.oauth.util;

import java.time.Duration;

public final class OAuthConstants {

    private OAuthConstants() {}

    public static final Duration AUTH_CODE_EXP_TIME = Duration.ofMinutes(10);
    public static final Duration ACCESS_TOKEN_EXP_TIME = Duration.ofHours(1);
    public static final Duration REFRESH_TOKEN_EXP_TIME = Duration.ofDays(14);
}
