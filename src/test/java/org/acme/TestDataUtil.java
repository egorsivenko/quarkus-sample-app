package org.acme;

import org.acme.user.User;

public final class TestDataUtil {

    private TestDataUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CLIENT_IP = "192.168.1.1";

    public static User buildTestUser() {
        return new User("John Doe", "john.doe@example.com", "Password123");
    }
}
