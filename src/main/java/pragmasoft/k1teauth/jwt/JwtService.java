package pragmasoft.k1teauth.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;

import java.text.ParseException;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@ApplicationScoped
public class JwtService {

    private RSAKey rsaJWK;
    private RSAKey rsaPublicJWK;

    @PostConstruct
    public void init() throws JOSEException {
        rsaJWK = new RSAKeyGenerator(2048)
                .keyID(UUID.randomUUID().toString())
                .generate();

        rsaPublicJWK = rsaJWK.toPublicJWK();
    }

    public String generate(String subject, JwtClaim... claims) {
        try {
            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder()
                    .subject(subject)
                    .issueTime(new Date())
                    .expirationTime(new Date(System.currentTimeMillis() + 60 * 60 * 1000));

            Stream.of(claims)
                    .forEach(claim -> claimsBuilder.claim(claim.name(), claim.value()));

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
                    claimsBuilder.build());

            signedJWT.sign(new RSASSASigner(rsaJWK));

            return signedJWT.serialize();

        } catch (JOSEException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }

    public String extractSubject(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(new RSASSAVerifier(rsaPublicJWK))) {
                throw new BadRequestException("Invalid token");
            }
            if (new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime())) {
                throw new BadRequestException("Token is expired");
            }
            return signedJWT.getJWTClaimsSet().getSubject();

        } catch (ParseException | JOSEException e) {
            throw new BadRequestException(e.getMessage(), e);
        }
    }
}
