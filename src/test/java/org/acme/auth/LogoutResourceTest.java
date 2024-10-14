package org.acme.auth;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class LogoutResourceTest {

    @Test
    void testSuccessfulLogout() {
        given()
                .when().post("/auth/logout")
                .then()
                .statusCode(302)
                .header("Location", containsString("/auth/login"));
    }
}
