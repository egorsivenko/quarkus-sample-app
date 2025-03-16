package pragmasoft.k1teauth.security.encryption;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.Security;
import java.util.Optional;

public final class KeyPairProvider {

    private static final Logger logger = LoggerFactory.getLogger(KeyPairProvider.class);

    private KeyPairProvider() {}

    public static Optional<KeyPair> keyPair(String pemPath) {
        Security.addProvider(new BouncyCastleProvider());

        PEMParser pemParser;
        try {
            pemParser = new PEMParser(new InputStreamReader(Files.newInputStream(Paths.get(pemPath))));
            PEMKeyPair pemKeyPair = (PEMKeyPair) pemParser.readObject();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            KeyPair keyPair = converter.getKeyPair(pemKeyPair);
            pemParser.close();

            return Optional.of(keyPair);

        } catch (FileNotFoundException e) {
            logger.warn("File not found: {}", pemPath);

        } catch (PEMException e) {
            logger.warn("PEMException {}", e.getMessage());

        } catch (IOException e) {
            logger.warn("IOException {}", e.getMessage());
        }
        return Optional.empty();
    }
}
