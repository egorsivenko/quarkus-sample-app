package org.acme.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
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

        public static native TemplateInstance registrationEmail(String link);
        public static native TemplateInstance registrationEmailPlain(String link);

        public static native TemplateInstance resetPasswordEmail(String link);
        public static native TemplateInstance resetPasswordEmailPlain(String link);
    }

    @Context
    UriInfo uriInfo;

    private final TokenService tokenService;
    private final Mailer mailer;

    public SimpleEmailSender(TokenService tokenService, Mailer mailer) {
        this.tokenService = tokenService;
        this.mailer = mailer;
    }

    @Override
    public void sendRegistrationEmail(User recipient) {
        String link = formatLink(recipient, "registration");

        String htmlContent = Templates.registrationEmail(link).render();
        String plainTextContent = Templates.registrationEmailPlain(link).render();

        sendMultipartEmail(recipient.getEmail(), "Confirm your email", htmlContent, plainTextContent);
    }

    @Override
    public void sendResetPasswordEmail(User recipient) {
        String link = formatLink(recipient, "reset-password");

        String htmlContent = Templates.resetPasswordEmail(link).render();
        String plainTextContent = Templates.resetPasswordEmailPlain(link).render();

        sendMultipartEmail(recipient.getEmail(), "Reset password", htmlContent, plainTextContent);
    }

    private String formatLink(User recipient, String linkPart) {
        String token = tokenService.generate(recipient);
        String path = uriInfo.getBaseUri().toString() + "verify/" + linkPart;

        return UriBuilder.fromPath(path)
                .queryParam("token", token)
                .build()
                .toString();
    }

    private void sendMultipartEmail(String to, String subject,
                                    String htmlContent, String plainTextContent) {
        mailer.send(
                Mail.withHtml(to, subject, htmlContent)
                        .setText(plainTextContent)
        );
    }
}
