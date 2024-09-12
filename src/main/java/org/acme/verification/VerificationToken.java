package org.acme.verification;

import org.acme.user.User;

import java.time.LocalDateTime;

public record VerificationToken(
        String token,
        User user,
        LocalDateTime expiryDate
) {
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}
