package org.acme;

import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.acme.user.User;
import org.jsoup.Jsoup;

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

    public static ValidatableResponse getResponseFromSuccessfulGetRequest(String path, Object... pathParams) {
        return given()
                .when().get(path, pathParams)
                .then()
                .statusCode(200);
    }

    public static String extractCsrfTokenForm(String htmlPage) {
        return Jsoup.parse(htmlPage)
                .body()
                .getElementsByAttributeValue("name", "csrf-token")
                .val();
    }
}
