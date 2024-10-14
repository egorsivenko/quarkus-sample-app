package org.acme.admin;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
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
        String authCookie = extractAuthCookieFromLogin();

        given()
                .formParam("id", testUser.getId())
                .formParam("fullName", "Tom Brown")
                .formParam("email", "tom.brown@example.com")
                .formParam("role", UserRole.ADMIN.toString())
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .when().post("/admin/edit-user")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Users list"));

        assertEquals("Tom Brown", testUser.getFullName());
        assertEquals("tom.brown@example.com", testUser.getEmail());
        assertEquals(UserRole.ADMIN, testUser.getRole());
    }

    @Test
    void testEditUserWithAlreadyTakenEmail() {
        String authCookie = extractAuthCookieFromLogin();

        given()
                .formParam("id", testUser.getId())
                .formParam("fullName", "Tom Brown")
                .formParam("email", adminEmail)
                .formParam("role", UserRole.ADMIN.toString())
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .when().post("/admin/edit-user")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Edit user"));

        assertNotEquals("Tom Brown", testUser.getFullName());
        assertNotEquals("tom.brown@example.com", testUser.getEmail());
        assertNotEquals(UserRole.ADMIN, testUser.getRole());
    }

    @Test
    void testDeleteUser() {
        String authCookie = extractAuthCookieFromLogin();

        given()
                .formParam("id", testUser.getId())
                .contentType(ContentType.URLENC)
                .cookie(cookieName, authCookie)
                .when().post("/admin/delete-user")
                .then()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString("Users list"));

        assertFalse(userService.existsByEmail(testUser.getEmail()));
    }

    private String extractAuthCookieFromLogin() {
        return given()
                .formParam("j_email", adminEmail)
                .formParam("j_password", adminPassword)
                .contentType(ContentType.URLENC)
                .when().post("/j_security_check")
                .then()
                .extract()
                .cookie(cookieName);
    }
}
