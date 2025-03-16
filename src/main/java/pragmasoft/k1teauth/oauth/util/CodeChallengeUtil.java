package pragmasoft.k1teauth.oauth.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Set;
import java.util.stream.Collectors;

public final class CodeChallengeUtil {

    private CodeChallengeUtil() {}

    private enum CodeChallengeMethod {
        PLAIN("plain"),
        S256("S256");

        private final String methodName;

        CodeChallengeMethod(String methodName) {
            this.methodName = methodName;
        }

        public String getMethodName() {
            return methodName;
        }
    }

    public static Set<String> getAvailableCodeChallengeMethods() {
        return Arrays.stream(CodeChallengeMethod.values())
                .map(CodeChallengeMethod::getMethodName)
                .collect(Collectors.toSet());
    }

    public static boolean verifyCodeChallenge(String codeChallenge, String codeVerifier, String method) {
        try {
            return switch (CodeChallengeMethod.valueOf(method.toUpperCase())) {
                case PLAIN -> codeChallenge.equals(codeVerifier);
                case S256 -> codeChallenge.equals(transformToCodeChallenge(codeVerifier));
            };
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static String transformToCodeChallenge(String codeVerifier) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            byte[] digest = messageDigest.digest();
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
