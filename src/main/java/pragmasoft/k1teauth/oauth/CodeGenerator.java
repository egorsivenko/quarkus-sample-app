package pragmasoft.k1teauth.oauth;

import jakarta.enterprise.context.ApplicationScoped;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Base64.Encoder;

@ApplicationScoped
public class CodeGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public String generate(int byteLength) {
        byte[] bytes = new byte[byteLength];
        random.nextBytes(bytes);
        return encoder.encodeToString(bytes);
    }
}
