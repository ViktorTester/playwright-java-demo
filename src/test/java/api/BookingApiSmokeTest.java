package api;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import helpers.AuthHelper;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BookingApiSmokeTest {

    private static Playwright playwright;
    private static APIRequestContext request;
    static String authToken;

    @BeforeAll
    public static void createApiContext() {
        playwright = Playwright.create();

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL("https://automationintesting.online"));

        // Get authToken
        AuthHelper authHelper = new AuthHelper(request);
        authToken = authHelper.getAdminToken();

    }

    @AfterAll
    static void closeApiContext() {
        request.dispose();
        playwright.close();
    }

    @Test
    void shouldReturnBookingsList() {

        APIResponse response = request.get("api/room/",
                RequestOptions.create()
                        .setHeader("Cookie", "token=" + authToken));

        System.out.println("THIS IS RESPONSE2: " + response.text());

        assertEquals(200, response.status(), "Expected to return 200");

        String body = response.text();

        assertFalse(body.isEmpty(), "Expected response body not to be empty");
    }
}