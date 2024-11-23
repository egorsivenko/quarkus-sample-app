package org.acme.auth;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
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
class ForgotPasswordResourceTest {

    @InjectMock
    @RestClient
    TurnstileService turnstileService;

    @InjectMock
    UserService userService;

    @InjectMock
    EmailSender emailSender;

    @Test
    void testSuccessfulForgotPassword() {
        ValidatableResponse response = TestDataUtil.getResponseFromSuccessfulGetRequest("/auth/forgot-password");

        String csrfTokenCookie = response.extract().cookie("csrf-token");
        String csrfTokenForm = TestDataUtil.extractCsrfTokenForm(response.extract().body().asString());

        Mockito.when(turnstileService.verifyToken(Mockito.any()))
                .thenReturn(new TurnstileResponse(true, null, null, null));

        User testUser = TestDataUtil.buildTestUser();
        String email = testUser.getEmail();
        Mockito.when(userService.existsByEmail(email))
                .thenReturn(true);

        Mockito.when(userService.getByEmail(email))
                .thenReturn(testUser);

        Mockito.doNothing().when(emailSender)
                .sendResetPasswordEmail(Mockito.any());

        given()
                .formParam("email", email)
                .formParam("cf-turnstile-response", "test-token")
                .contentType(ContentType.URLENC)
                .header("X-Forwarded-For", TestDataUtil.CLIENT_IP)
                .cookie("csrf-token", csrfTokenCookie)
                .formParam("csrf-token", csrfTokenForm)
                .when().post("/auth/forgot-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Confirm email"));
    }

    @Test
    void testForgotPasswordWithNonExistentEmail() {
        ValidatableResponse response = TestDataUtil.getResponseFromSuccessfulGetRequest("/auth/forgot-password");

        String csrfTokenCookie = response.extract().cookie("csrf-token");
        String csrfTokenForm = TestDataUtil.extractCsrfTokenForm(response.extract().body().asString());

        Mockito.when(turnstileService.verifyToken(Mockito.any()))
                .thenReturn(new TurnstileResponse(true, null, null, null));

        String email = TestDataUtil.buildTestUser().getEmail();
        Mockito.when(userService.existsByEmail(email))
                .thenReturn(false);

        given()
                .formParam("email", email)
                .formParam("cf-turnstile-response", "test-token")
                .contentType(ContentType.URLENC)
                .header("X-Forwarded-For", TestDataUtil.CLIENT_IP)
                .cookie("csrf-token", csrfTokenCookie)
                .formParam("csrf-token", csrfTokenForm)
                .when().post("/auth/forgot-password")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Forgot password"));
    }
}
