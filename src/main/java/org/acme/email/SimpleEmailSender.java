package org.acme.email;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Properties;

@ApplicationScoped
public class SimpleEmailSender implements EmailSender {

    @ConfigProperty(name = "sender.email")
    String email;

    @ConfigProperty(name = "sender.username")
    String username;

    @ConfigProperty(name = "sender.password")
    String password;

    @ConfigProperty(name = "sender.host")
    String host;

    @ConfigProperty(name = "sender.port")
    String port;

    public void send(String to, String link) {
        String subject = "Email Confirmation";

        String plainTextContent = "Please verify your email by visiting the following link: " + link;
        String htmlContent = "Please verify your email by clicking the following link: <a href=\"" + link + "\">Verify Email</a>";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);

            MimeBodyPart plainTextPart = new MimeBodyPart();
            plainTextPart.setContent(plainTextContent, "text/plain; charset=utf-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(plainTextPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
