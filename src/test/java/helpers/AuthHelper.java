package helpers;

import api.client.ApiClient;
import api.domains.ApiContainer;
import com.microsoft.playwright.APIResponse;

import static api.assertions.ApiResponseValidator.verifyStatus;

public class AuthHelper {

    private final ApiContainer api;

    public AuthHelper(ApiClient apiClient) {
        this.api = new ApiContainer(apiClient);
    }

    public String getAuthToken() {
        APIResponse response = api.auth
                .postAuthLogin()
                .param("username", "admin")
                .param("password", "password")
                .post();

        String responseBody = response.text();

        verifyStatus(response, 200);

        return extractToken(responseBody);
    }

    private String extractToken(String responseBody) {
        return responseBody
                .replace("{\"token\":\"", "")
                .replace("\"}", "");
    }
}