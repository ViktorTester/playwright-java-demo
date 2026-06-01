package helpers;

import api.client.ApiClient;
import api.domains.ApiContainer;
import api.dto.auth.AuthResponse;
import com.microsoft.playwright.APIResponse;

import java.util.Map;

import static api.assertions.ApiResponseValidator.verifyStatus;
import static config.Config.adminPassword;
import static config.Config.adminUsername;
import static utils.JsonUtils.fromJson;

public class AuthHelper {

    private final ApiContainer api;

    public AuthHelper(ApiClient apiClient) {
        this.api = new ApiContainer(apiClient);
    }

    public String getAuthToken() {
        APIResponse response = api.auth
                .postAuthLogin()
                .jsonBody(Map.of(
                        "username", adminUsername(),
                        "password", adminPassword()
                ))
                .post();

        String responseBody = response.text();

        verifyStatus(response, 200);

        AuthResponse authResponse = fromJson(responseBody, AuthResponse.class);

        if (authResponse.token() == null || authResponse.token().isBlank()) {
            throw new IllegalStateException(
                    "Auth token is missing or empty. Response body: " + responseBody
            );
        }

        return authResponse.token();
    }
}