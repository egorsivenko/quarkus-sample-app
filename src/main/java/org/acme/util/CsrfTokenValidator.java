package org.acme.util;

import io.quarkus.csrf.reactive.runtime.CsrfTokenUtils;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Cookie;
import org.eclipse.microprofile.config.ConfigProvider;

public final class CsrfTokenValidator {

    private CsrfTokenValidator() {
        throw new IllegalStateException("Utility class");
    }

    public static void validate(Cookie csrfCookie, String csrfFormParam) {
        String signedToken = CsrfTokenUtils.signCsrfToken(csrfFormParam,
                ConfigProvider.getConfig().getValue("quarkus.rest-csrf.token-signature-key",
                        String.class));

        if (!csrfCookie.getValue().equals(signedToken)) {
            throw new BadRequestException();
        }
    }
}
