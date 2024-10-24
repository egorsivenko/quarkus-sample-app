package org.acme.email;

import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import org.acme.jwt.TokenService;
import org.acme.user.User;

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

    private final TokenService tokenService;

    public SimpleEmailSender(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void sendRegistrationEmail(User recipient) {
        String link = formatLink(recipient, "registration");

        Templates.registrationEmail(link)
                .to(recipient.getEmail())
                .subject(REGISTRATION_SUBJECT)
                .send().await().indefinitely();
    }

    @Override
    public void sendResetPasswordEmail(User recipient) {
        String link = formatLink(recipient, "reset-password");

        Templates.resetPasswordEmail(link)
                .to(recipient.getEmail())
                .subject(RESET_PASSWORD_SUBJECT)
                .send().await().indefinitely();
    }

    private String formatLink(User recipient, String linkPart) {
        String token = tokenService.generate(recipient);
        String path = uriInfo.getBaseUri().toString() + "verify/" + linkPart;

        return UriBuilder.fromPath(path)
                .queryParam("token", token)
                .build()
                .toString();
    }
}
