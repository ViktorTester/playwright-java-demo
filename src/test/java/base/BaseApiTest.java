package base;

import api.client.ApiClient;
import api.domains.ApiContainer;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.Playwright;
import config.Config;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

public abstract class BaseApiTest {

    protected Playwright playwright;
    protected APIRequestContext request;
    protected ApiContainer api;

    @BeforeEach
    void setUpApi() {
        Config.validate();

        playwright = Playwright.create();

        request = playwright.request().newContext(new APIRequest.NewContextOptions()
                .setBaseURL(Config.baseApiUrl())
                .setTimeout(Config.apiTimeoutMs())
        );

        api = new ApiContainer(new ApiClient(request, Map.of()));
    }

    @AfterEach
    void tearDownApi() {
        if (request != null) {
            request.dispose();
        }

        if (playwright != null) {
            playwright.close();
        }
    }
}