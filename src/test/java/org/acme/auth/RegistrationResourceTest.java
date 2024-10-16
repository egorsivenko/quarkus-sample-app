package org.acme.auth;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.TestDataUtil;
import org.acme.email.EmailSender;
import org.acme.turnstile.TurnstileResponse;
import org.acme.turnstile.TurnstileService;
import org.acme.user.User;
import org.acme.user.UserService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class RegistrationResourceTest {

    @InjectMock
    @RestClient
    TurnstileService turnstileService;

    @InjectMock
    UserService userService;

    @InjectMock
    EmailSender emailSender;

    @Test
    void testSuccessfulRegistration() {
        Mockito.when(turnstileService.verifyToken(Mockito.any()))
                .thenReturn(new TurnstileResponse(true, null, null, null));

        User testUser = TestDataUtil.buildTestUser();
        Mockito.when(userService.existsByEmail(testUser.getEmail()))
                .thenReturn(false);

        Mockito.when(userService.getById(Mockito.any()))
                .thenReturn(testUser);

        Mockito.doNothing().when(emailSender)
                .sendRegistrationEmail(Mockito.any());

        given()
                .formParam("fullName", testUser.getFullName())
                .formParam("email", testUser.getEmail())
                .formParam("password", "Password123")
                .formParam("confirmPassword", "Password123")
                .formParam("cf-turnstile-response", "test-token")
                .contentType(ContentType.URLENC)
                .header("X-Forwarded-For", TestDataUtil.CLIENT_IP)
                .when().post("/auth/registration")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Confirm email"));
    }

    @Test
    void testRegistrationWithPasswordMismatch() {
        Mockito.when(turnstileService.verifyToken(Mockito.any()))
                .thenReturn(new TurnstileResponse(true, null, null, null));

        User testUser = TestDataUtil.buildTestUser();

        given()
                .formParam("fullName", testUser.getFullName())
                .formParam("email", testUser.getEmail())
                .formParam("password", "Password123")
                .formParam("confirmPassword", "MismatchPassword")
                .formParam("cf-turnstile-response", "test-token")
                .contentType(ContentType.URLENC)
                .header("X-Forwarded-For", TestDataUtil.CLIENT_IP)
                .when().post("/auth/registration")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Registration"));
    }

    @Test
    void testRegistrationWithExistingEmail() {
        Mockito.when(turnstileService.verifyToken(Mockito.any()))
                .thenReturn(new TurnstileResponse(true, null, null, null));

        User testUser = TestDataUtil.buildTestUser();
        Mockito.when(userService.existsByEmail(testUser.getEmail()))
                .thenReturn(true);

        given()
                .formParam("fullName", testUser.getFullName())
                .formParam("email", testUser.getEmail())
                .formParam("password", "Password123")
                .formParam("confirmPassword", "Password123")
                .formParam("cf-turnstile-response", "test-token")
                .contentType(ContentType.URLENC)
                .header("X-Forwarded-For", TestDataUtil.CLIENT_IP)
                .when().post("/auth/registration")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Registration"));
    }

    @Test
    void testTurnstileValidationFailure() {
        Mockito.when(turnstileService.verifyToken(Mockito.any()))
                .thenReturn(new TurnstileResponse(false, null, null, null));

        User testUser = TestDataUtil.buildTestUser();

        given()
                .formParam("fullName", testUser.getFullName())
                .formParam("email", testUser.getEmail())
                .formParam("password", "Password123")
                .formParam("confirmPassword", "Password123")
                .formParam("cf-turnstile-response", "test-token")
                .contentType(ContentType.URLENC)
                .header("X-Forwarded-For", TestDataUtil.CLIENT_IP)
                .when().post("/auth/registration")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Registration"));
    }
}
