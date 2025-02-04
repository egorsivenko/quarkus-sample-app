package pragmasoft.k1teauth.security.encryption;

import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JWEAlgorithm;
import io.micronaut.context.annotation.Value;
import io.micronaut.security.token.jwt.encryption.ec.ECEncryptionConfiguration;
import jakarta.inject.Named;
import jakarta.inject.Singleton;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Optional;

@Named("generator")
@Singleton
public class ECDHEncryptionConfiguration implements ECEncryptionConfiguration {

    private ECPublicKey publicKey;
    private ECPrivateKey privateKey;
    private final JWEAlgorithm jweAlgorithm = JWEAlgorithm.ECDH_ES_A256KW;
    private final EncryptionMethod encryptionMethod = EncryptionMethod.A256GCM;

    public ECDHEncryptionConfiguration(@Value("${pem.path}") String pemPath) {
        Optional<KeyPair> keyPair = KeyPairProvider.keyPair(pemPath);
        if (keyPair.isPresent()) {
            this.publicKey = (ECPublicKey) keyPair.get().getPublic();
            this.privateKey = (ECPrivateKey) keyPair.get().getPrivate();
        }
    }

    @Override
    public ECPublicKey getPublicKey() {
        return publicKey;
    }

    @Override
    public ECPrivateKey getPrivateKey() {
        return privateKey;
    }

    @Override
    public JWEAlgorithm getJweAlgorithm() {
        return jweAlgorithm;
    }

    @Override
    public EncryptionMethod getEncryptionMethod() {
        return encryptionMethod;
    }
}
