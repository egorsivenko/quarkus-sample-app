package org.acme.email;

import org.acme.user.User;

public interface EmailSender {

    void sendRegistrationEmail(User recipient);

    void sendResetPasswordEmail(User recipient);
}
