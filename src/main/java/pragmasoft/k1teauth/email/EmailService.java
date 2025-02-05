package pragmasoft.k1teauth.email;

import io.micronaut.email.BodyType;
import io.micronaut.email.Email;
import io.micronaut.email.EmailSender;
import io.micronaut.email.MultipartBody;
import io.micronaut.email.template.TemplateBody;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.views.ModelAndView;
import jakarta.inject.Singleton;
import pragmasoft.k1teauth.common.ServerInfo;
import pragmasoft.k1teauth.security.jwt.JwtService;
import pragmasoft.k1teauth.user.User;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Singleton
public class EmailService {

    private static final String REGISTRATION_SUBJECT = "Confirm your email";
    private static final String RESET_PASSWORD_SUBJECT = "Confirm password reset";

    private static final Duration TOKEN_EXP_TIME = Duration.ofMinutes(30);

    private final EmailSender<?, ?> emailSender;
    private final JwtService jwtService;
    private final ServerInfo serverInfo;

    public EmailService(EmailSender<?, ?> emailSender,
                        JwtService jwtService,
                        ServerInfo serverInfo) {
        this.emailSender = emailSender;
        this.jwtService = jwtService;
        this.serverInfo = serverInfo;
    }

    public void sendRegistrationEmail(User recipient) {
        String link = formatLink(recipient.getId(), "/registration");
        Map<String, String> model = Map.of("link", link, "action", "registration");

        emailSender.send(Email.builder()
                .to(recipient.getEmail())
                .subject(REGISTRATION_SUBJECT)
                .body(new MultipartBody(
                        new TemplateBody<>(BodyType.HTML, new ModelAndView<>("email/htmlEmail", model)),
                        new TemplateBody<>(BodyType.TEXT, new ModelAndView<>("email/textEmail", model))
                ))
        );
    }

    public void sendResetPasswordEmail(User recipient) {
        String link = formatLink(recipient.getId(), "/reset-password");
        Map<String, String> model = Map.of("link", link, "action", "password reset");

        emailSender.send(Email.builder()
                .to(recipient.getEmail())
                .subject(RESET_PASSWORD_SUBJECT)
                .body(new MultipartBody(
                        new TemplateBody<>(BodyType.HTML, new ModelAndView<>("email/htmlEmail", model)),
                        new TemplateBody<>(BodyType.TEXT, new ModelAndView<>("email/textEmail", model))
                ))
        );
    }

    private String formatLink(UUID userId, String endpoint) {
        String path = serverInfo.getBaseUrl() + "/verify" + endpoint;
        String token = jwtService.generate(userId.toString(), List.of(path), TOKEN_EXP_TIME);

        return UriBuilder.of(path)
                .queryParam("token", token)
                .build()
                .toString();
    }
}
