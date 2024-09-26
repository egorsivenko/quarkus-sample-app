package org.acme.verification;

import io.smallrye.jwt.auth.principal.JWTParser;
import io.smallrye.jwt.auth.principal.ParseException;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.BadRequestException;
import org.acme.user.User;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

@ApplicationScoped
public class TokenService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenService.class);

    private final JWTParser jwtParser;

    public TokenService(JWTParser jwtParser) {
        this.jwtParser = jwtParser;
    }

    public String generate(User user) {
        return Jwt.subject(user.getId().toString())
                .upn(user.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .sign();
    }

    public String extractSubject(String token) {
        try {
            JsonWebToken jwt = jwtParser.parse(token);

            if (jwt.getExpirationTime() < Instant.now().getEpochSecond()) {
                throw new BadRequestException("Token is expired");
            }
            return jwt.getSubject();

        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            throw new BadRequestException("Failed to parse the token");
        }
    }
}
