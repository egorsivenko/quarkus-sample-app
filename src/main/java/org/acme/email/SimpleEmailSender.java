package org.acme.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimpleEmailSender implements EmailSender {

    private final Mailer mailer;

    public SimpleEmailSender(Mailer mailer) {
        this.mailer = mailer;
    }

    public void send(String to, String link) {
        String subject = "Email Confirmation";

        String plainTextContent = "Please verify your email by visiting the following link: " + link;
        String htmlContent = "Please verify your email by clicking the following link: <a href=\"" + link + "\">Verify Email</a>";

        mailer.send(
                Mail.withHtml(to, subject, htmlContent)
                        .setText(plainTextContent)
        );
    }
}
