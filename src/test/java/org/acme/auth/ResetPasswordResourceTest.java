package org.acme.auth;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.TestDataUtil;
import org.acme.user.User;
import org.acme.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class ResetPasswordResourceTest {

    @InjectMock
    UserService userService;

    @Test
    void testSuccessfulPasswordReset() {
        User testUser = TestDataUtil.buildTestUser();
        Mockito.when(userService.getById(Mockito.any()))
                .thenReturn(testUser);

        String newPassword = "NewPassword789";

        given()
                .formParam("userId", UUID.randomUUID())
                .formParam("password", newPassword)
                .formParam("confirmPassword", newPassword)
                .contentType(ContentType.URLENC)
                .when().post("/auth/reset-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Login"));

        assertTrue(testUser.verifyPassword(newPassword));
    }

    @Test
    void testPasswordResetWithPasswordMismatch() {
        given()
                .formParam("userId", UUID.randomUUID())
                .formParam("password", "NewPassword789")
                .formParam("confirmPassword", "MismatchPassword")
                .contentType(ContentType.URLENC)
                .when().post("/auth/reset-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Reset password"));
    }
}
