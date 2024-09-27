package org.acme.email;

import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Context;
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

    @Context
    UriInfo uriInfo;

    private final TokenService tokenService;

    public SimpleEmailSender(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public void sendRegistrationEmail(User recipient) {
        String link = formatLink(recipient, "verify/registration?token=");

        Templates.registrationEmail(link)
                .to(recipient.getEmail())
                .subject("Confirm your email")
                .send()
                .await()
                .indefinitely();
    }

    @Override
    public void sendResetPasswordEmail(User recipient) {
        String link = formatLink(recipient, "verify/reset-password?token=");

        Templates.resetPasswordEmail(link)
                .to(recipient.getEmail())
                .subject("Reset password")
                .send()
                .await()
                .indefinitely();
    }

    private String formatLink(User recipient, String linkPart) {
        String token = tokenService.generate(recipient);
        return uriInfo.getBaseUri().toString() + linkPart + token;
    }
}
