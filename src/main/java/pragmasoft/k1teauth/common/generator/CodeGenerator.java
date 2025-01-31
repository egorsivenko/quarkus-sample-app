package pragmasoft.k1teauth.common.generator;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;

public final class CodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    private CodeGenerator() {}

    public static String generate(int byteLength) {
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
