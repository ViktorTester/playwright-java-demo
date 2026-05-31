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

    private static Playwright playwright;
    private static APIRequestContext request;

    protected static ApiContainer api;
    protected static ApiContainer authenticatedApi;

    @BeforeAll
    static void setupApi() {
        playwright = Playwright.create();

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(baseApiUrl()));

        ApiClient apiClient = new ApiClient(request);
        api = new ApiContainer(apiClient);

        AuthHelper authHelper = new AuthHelper(apiClient);
        String authToken = authHelper.getAuthToken();

        ApiClient authenticatedApiClient = apiClient.withCookieToken(authToken);
        authenticatedApi = new ApiContainer(authenticatedApiClient);
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