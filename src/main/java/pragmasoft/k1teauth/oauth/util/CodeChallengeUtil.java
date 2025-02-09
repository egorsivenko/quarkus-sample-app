package pragmasoft.k1teauth.oauth.util;

import pragmasoft.k1teauth.security.hash.HashUtil;

import java.util.Arrays;
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
                case S256 -> codeChallenge.equals(HashUtil.hashWithSHA256(codeVerifier));
            };
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
