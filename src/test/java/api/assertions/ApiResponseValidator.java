package api.assertions;

import com.microsoft.playwright.APIResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.ApiLogger.logResponse;

public class ApiResponseValidator {

    public static void verifyStatus(APIResponse response, int expectedStatus) {

        logResponse(response);

        assertEquals(
                expectedStatus,
                response.status(),
                "Unexpected response status. Body: " + response.text()
        );
    }

    public static void verifyBodyIsNotEmpty(APIResponse response) {

        logResponse(response);

        assertTrue(
                response.text() != null && !response.text().isEmpty(),
                "Expected response body not to be empty"
        );
    }
}