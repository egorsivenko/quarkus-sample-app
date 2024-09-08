package org.acme.user.request;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {}
