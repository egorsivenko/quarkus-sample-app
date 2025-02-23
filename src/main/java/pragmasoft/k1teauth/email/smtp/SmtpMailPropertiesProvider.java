package pragmasoft.k1teauth.email.smtp;

import io.micronaut.context.annotation.Primary;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.email.javamail.sender.MailPropertiesProvider;
import jakarta.inject.Singleton;

import java.util.Properties;

@Primary
@Singleton
public class SmtpMailPropertiesProvider implements MailPropertiesProvider {

    private final SmtpConfig smtpConfig;

    public SmtpMailPropertiesProvider(SmtpConfig smtpConfig) {
        this.smtpConfig = smtpConfig;
    }

    @Override
    public @NonNull Properties mailProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpConfig.isAuth());
        props.put("mail.smtp.starttls.enable", smtpConfig.getStarttls().isEnable());
        props.put("mail.smtp.host", smtpConfig.getHost());
        props.put("mail.smtp.port", smtpConfig.getPort());
        return props;
    }
}
