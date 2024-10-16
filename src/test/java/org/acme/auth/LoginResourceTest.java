package org.acme.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class LoginResourceTest {

    @ConfigProperty(name = "admin.email")
    String adminEmail;

    @ConfigProperty(name = "admin.password")
    String adminPassword;

    @Test
    void testSuccessfulLogin() {
        given()
                .formParam("j_email", adminEmail)
                .formParam("j_password", adminPassword)
                .contentType(ContentType.URLENC)
                .when().post("/j_security_check")
                .then()
                .statusCode(302)
                .header("Location", containsString("/profile"));
    }

    @Test
    void testInvalidLogin() {
        given()
                .formParam("j_email", "invalid.email@example.com")
                .formParam("j_password", "WrongPassword123")
                .contentType(ContentType.URLENC)
                .when().post("/j_security_check")
                .then()
                .statusCode(302)
                .header("Location", containsString("/auth/login?error=true"));
    }
}
