package base;

import api.client.ApiClient;
import api.domains.ApiContainer;
import helpers.AuthTokenProvider;
import org.junit.jupiter.api.BeforeEach;

import java.util.Map;

public abstract class BaseAuthenticatedApiTest extends BaseApiTest {

    protected ApiContainer authenticatedApi;

    @BeforeEach
    void setUpAuthenticatedApi() {
        String authToken = new AuthTokenProvider(api).getAdminToken();

        authenticatedApi = new ApiContainer(new ApiClient(request, Map.of(
                "Cookie", "token=" + authToken
        )));
    }
}