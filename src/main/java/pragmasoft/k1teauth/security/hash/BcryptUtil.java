package pragmasoft.k1teauth.security.hash;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class BcryptUtil {

    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private BcryptUtil() {}

    public static String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}
