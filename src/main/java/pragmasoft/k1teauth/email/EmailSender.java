package pragmasoft.k1teauth.email;

import pragmasoft.k1teauth.user.User;

public interface EmailSender {

    void sendRegistrationEmail(User recipient);

    void sendResetPasswordEmail(User recipient);
}
