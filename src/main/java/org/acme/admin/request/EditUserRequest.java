package org.acme.admin.request;

import java.util.UUID;

public record EditUserRequest(
        UUID id,
        String fullName,
        String email,
        String role
) {}
