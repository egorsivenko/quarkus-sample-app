package pragmasoft.k1teauth.email.smtp;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.email.javamail.sender.MailPropertiesProvider;
import io.micronaut.email.javamail.sender.SessionProvider;
import jakarta.inject.Singleton;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;

@Singleton
public class SmtpSessionProvider implements SessionProvider {

    private final SmtpConfig smtpConfig;
    private final MailPropertiesProvider propsProvider;

    public SmtpSessionProvider(SmtpConfig smtpConfig,
                               MailPropertiesProvider propsProvider) {
        this.smtpConfig = smtpConfig;
        this.propsProvider = propsProvider;
    }

    @Override
    public @NonNull Session session() {
        return Session.getInstance(propsProvider.mailProperties(), new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpConfig.getSession().getUsername(), smtpConfig.getSession().getPassword());
            }
        });
    }
}
