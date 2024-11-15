package org.acme.email;

import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.acme.jwt.JwtService;
import org.acme.user.User;

import java.util.UUID;

@ApplicationScoped
public class SimpleEmailSender implements EmailSender {

    @CheckedTemplate
    static class Templates {

        private Templates() {
            throw new IllegalStateException("Utility class");
        }

        public static native MailTemplateInstance registrationEmail(String link);

        public static native MailTemplateInstance resetPasswordEmail(String link);
    }

    private static final String REGISTRATION_SUBJECT = "Confirm your email";
    private static final String RESET_PASSWORD_SUBJECT = "Confirm password reset";

    @Context
    UriInfo uriInfo;

    private final JwtService jwtService;

    public SimpleEmailSender(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void sendRegistrationEmail(User recipient) {
        String link = formatLink(recipient.getId(), "registration");

        Templates.registrationEmail(link)
                .to(recipient.getEmail())
                .subject(REGISTRATION_SUBJECT)
                .send().await().indefinitely();
    }

    @Override
    public void sendResetPasswordEmail(User recipient) {
        String link = formatLink(recipient.getId(), "reset-password");

        Templates.resetPasswordEmail(link)
                .to(recipient.getEmail())
                .subject(RESET_PASSWORD_SUBJECT)
                .send().await().indefinitely();
    }

    private String formatLink(UUID userId, String linkPart) {
        String token = jwtService.generate(userId.toString());
        String path = uriInfo.getBaseUri().toString() + "verify/" + linkPart;

        return UriBuilder.fromPath(path)
                .queryParam("token", token)
                .build()
                .toString();
    }
}
