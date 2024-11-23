package org.acme.admin;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;
import org.acme.TestDataUtil;
import org.acme.user.User;
import org.acme.user.UserRole;
import org.acme.user.UserService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@QuarkusTest
class AdminResourceTest {

    @ConfigProperty(name = "admin.email")
    String adminEmail;

    @ConfigProperty(name = "admin.password")
    String adminPassword;

    @ConfigProperty(name = "quarkus.http.auth.form.cookie-name")
    String cookieName;

    @Inject
    UserService userService;

    private User testUser;

    @BeforeEach
    void setup() {
        testUser = TestDataUtil.buildTestUser();
        userService.create(testUser);
    }

    @AfterEach
    void cleanup() {
        userService.delete(testUser.getId());
    }

    @Test
    void testSuccessfulEditUser() {
        ValidatableResponse response = TestDataUtil.getResponseFromSuccessfulGetRequest("/admin/edit-user");

        String csrfTokenCookie = response.extract().cookie("csrf-token");
        String csrfTokenForm = TestDataUtil.extractCsrfTokenForm(response.extract().body().asString());

        String authCookie = TestDataUtil.extractAuthCookieFromLogin(cookieName, adminEmail, adminPassword);

        given()
                .formParam("id", testUser.getId())
                .formParam("fullName", "Tom Brown")
                .formParam("email", "tom.brown@example.com")
                .formParam("role", UserRole.ADMIN.toString())
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .cookie("csrf-token", csrfTokenCookie)
                .formParam("csrf-token", csrfTokenForm)
                .when().post("/admin/edit-user")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Users list"));

        User user = userService.getById(testUser.getId());

        assertEquals("Tom Brown", user.getFullName());
        assertEquals("tom.brown@example.com", user.getEmail());
        assertEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testEditUserWithAlreadyTakenEmail() {
        ValidatableResponse response = TestDataUtil.getResponseFromSuccessfulGetRequest("/admin/edit-user");

        String csrfTokenCookie = response.extract().cookie("csrf-token");
        String csrfTokenForm = TestDataUtil.extractCsrfTokenForm(response.extract().body().asString());

        String authCookie = TestDataUtil.extractAuthCookieFromLogin(cookieName, adminEmail, adminPassword);

        given()
                .formParam("id", testUser.getId())
                .formParam("fullName", "Tom Brown")
                .formParam("email", adminEmail)
                .formParam("role", UserRole.ADMIN.toString())
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .cookie("csrf-token", csrfTokenCookie)
                .formParam("csrf-token", csrfTokenForm)
                .when().post("/admin/edit-user")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Edit user"));

        User user = userService.getById(testUser.getId());

        assertNotEquals("Tom Brown", user.getFullName());
        assertNotEquals(adminEmail, user.getEmail());
        assertNotEquals(UserRole.ADMIN, user.getRole());
    }

    @Test
    void testDeleteUser() {
        ValidatableResponse response = TestDataUtil.getResponseFromSuccessfulGetRequest("/admin/users-list");

        String csrfTokenCookie = response.extract().cookie("csrf-token");
        String csrfTokenForm = TestDataUtil.extractCsrfTokenForm(response.extract().body().asString());

        String authCookie = TestDataUtil.extractAuthCookieFromLogin(cookieName, adminEmail, adminPassword);

        given()
                .formParam("id", testUser.getId())
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .cookie("csrf-token", csrfTokenCookie)
                .formParam("csrf-token", csrfTokenForm)
                .when().post("/admin/delete-user")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Users list"));

        assertFalse(userService.existsById(testUser.getId()));
    }
}
