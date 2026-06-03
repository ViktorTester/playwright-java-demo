package helpers;

import api.domains.ApiContainer;
import api.dto.auth.AuthResponse;
import com.microsoft.playwright.APIResponse;

import static api.assertions.ApiResponseValidator.verifySuccessResponse;
import static testdata.TestUsers.adminCredentials;
import static utils.JsonUtils.fromJson;

public class AuthTokenProvider {

    private final ApiContainer api;

    public AuthTokenProvider(ApiContainer api) {
        this.api = api;
    }

    public String getAdminToken() {
        APIResponse response = api.auth
                .postAuthLogin()
                .header("Content-Type", "application/json")
                .jsonBody(adminCredentials())
                .post();

        verifySuccessResponse(response);

        AuthResponse authResponse = fromJson(response.text(), AuthResponse.class);
        return authResponse.token();
    }
}