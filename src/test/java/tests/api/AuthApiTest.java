package tests.api;

import api.assertions.ApiExpect;
import base.BaseApiTest;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static api.assertions.ApiResponseValidator.verifyApiResponse;
import static api.constants.ApiStatusCodes.OK;
import static api.constants.ApiStatusCodes.UNAUTHORIZED;
import static testdata.TestUsers.adminCredentials;
import static testdata.TestUsers.invalidAdminCredentials;
import static tests.tags.TestTags.API;
import static tests.tags.TestTags.REGRESSION;
import static tests.tags.TestTags.SMOKE;

/**
  Positive
  {@link #shouldLoginAsAdmin} (POST/auth/login) Should log in as admin

  Negative
  {@link #shouldNotLoginWithInvalidPassword} (POST/auth/login) Should not log in with invalid password
 */

@Tag(API)
@Feature("Login")
class AuthApiTest extends BaseApiTest {

    @Test
    @Tag(SMOKE)
    @DisplayName("(POST/auth/login) Should log in as admin")
    void shouldLoginAsAdmin() {
        APIResponse response = api.auth
                .postAuthLogin()
                .jsonBody(adminCredentials())
                .post();

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("token", ApiExpect.notBlank());

        verifyApiResponse(response, OK, expectedBody);
    }

    @Test
    @Tag(REGRESSION)
    @DisplayName("(POST/auth/login) Should not log in with invalid password")
    void shouldNotLoginWithInvalidPassword() {
        APIResponse response = api.auth
                .postAuthLogin()
                .jsonBody(invalidAdminCredentials())
                .post();

        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("error", "Invalid credentials");


        verifyApiResponse(response, UNAUTHORIZED, expectedBody);
    }
}