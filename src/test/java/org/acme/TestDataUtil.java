package org.acme;

import io.restassured.http.ContentType;
import org.acme.user.User;

import static io.restassured.RestAssured.given;

public final class TestDataUtil {

    private TestDataUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static final String CLIENT_IP = "192.168.1.1";

    public static User buildTestUser() {
        return new User("John Doe", "john.doe@example.com", "Password123");
    }

    public static String extractAuthCookieFromLogin(String cookieName, String email, String password) {
        return given()
                .formParam("j_email", email)
                .formParam("j_password", password)
                .contentType(ContentType.URLENC)
                .when().post("/j_security_check")
                .then()
                .extract()
                .cookie(cookieName);
    }
}
