package pragmasoft.k1teauth.auth;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import pragmasoft.k1teauth.TestDataUtil;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
class LogoutResourceTest {

    @Test
    void testSuccessfulLogout() {
        ValidatableResponse response = TestDataUtil.getResponseFromSuccessfulGetRequest("/profile");

        String csrfTokenCookie = response.extract().cookie("csrf-token");
        String csrfTokenForm = TestDataUtil.extractCsrfTokenForm(response.extract().body().asString());

        given()
                .cookie("csrf-token", csrfTokenCookie)
                .formParam("csrf-token", csrfTokenForm)
                .when().post("/auth/logout")
                .then()
                .statusCode(302)
                .header("Location", containsString("/auth/login"));
    }
}
