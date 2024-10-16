package org.acme.user;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import org.acme.TestDataUtil;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class UserProfileResourceTest {

    @ConfigProperty(name = "admin.email")
    String adminEmail;

    @ConfigProperty(name = "admin.password")
    String adminPassword;

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;

    @Inject
    UserService userService;

    @AfterEach
    void resetPassword() {
        User admin = userService.getByEmail(adminEmail);
        if (!admin.verifyPassword(adminPassword)) {
            admin.changePassword(adminPassword);
        }
    }

    @Test
    void testSuccessfulPasswordChange() {
        String authCookie = TestDataUtil.extractAuthCookieFromLogin(cookieName, adminEmail, adminPassword);
        String newPassword = "NewPassword789";

        given()
                .formParam("currentPassword", adminPassword)
                .formParam("newPassword", newPassword)
                .formParam("confirmPassword", newPassword)
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .when().post("/profile/change-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Profile"));

        assertTrue(userService.getByEmail(adminEmail).verifyPassword(newPassword));
    }

    @Test
    void testPasswordChangeWithPasswordMismatch() {
        String authCookie = TestDataUtil.extractAuthCookieFromLogin(cookieName, adminEmail, adminPassword);

        given()
                .formParam("currentPassword", adminPassword)
                .formParam("newPassword", "NewPassword789")
                .formParam("confirmPassword", "MismatchPassword")
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .when().post("/profile/change-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Change password"));
    }

    @Test
    void testPasswordChangeWithIncorrectCurrentPassword() {
        String authCookie = TestDataUtil.extractAuthCookieFromLogin(cookieName, adminEmail, adminPassword);
        String newPassword = "NewPassword789";

        given()
                .formParam("currentPassword", "WrongPassword123")
                .formParam("newPassword", newPassword)
                .formParam("confirmPassword", newPassword)
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .when().post("/profile/change-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Change password"));
    }
}
