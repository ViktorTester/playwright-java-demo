package helpers;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuthHelper {

    private final APIRequestContext request;

    public AuthHelper(APIRequestContext request) {
        this.request = request;
    }

    public String getAdminToken() {
        APIResponse response = request.post("/api/auth/login",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(Map.of(
                                "username", "admin",
                                "password", "password"
                        ))
        );

        assertEquals(200, response.status(), "Login request failed. Body: " + response.text());

        return extractToken(response.text());
    }

    private String extractToken(String responseBody) {
        return responseBody
                .replace("{\"token\":\"", "")
                .replace("\"}", "");
    }
}