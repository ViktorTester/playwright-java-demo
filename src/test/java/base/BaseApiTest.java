package base;

import api.client.ApiClient;
import api.domains.ApiContainer;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import helpers.AuthHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import static config.TestConfig.baseApiUrl;

public class BaseApiTest {
    protected static Playwright playwright;
    protected static APIRequestContext request;
    protected static ApiClient apiClient;
    protected static ApiContainer api;
    static String authToken;

    @BeforeAll
    static void setupApi() {

        playwright = Playwright.create();

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseApiUrl()));

        apiClient = new ApiClient(request);
        api = new ApiContainer(apiClient);

        // Get authToken
        AuthHelper authHelper = new AuthHelper(apiClient);
        authToken = authHelper.getAuthToken();

    }

    @AfterAll
    static void tearDownApi() {
        if (request != null) {
            request.dispose();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}