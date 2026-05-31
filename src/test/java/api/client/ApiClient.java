package api.client;

import api.endpoints.Endpoint;
import com.microsoft.playwright.APIRequestContext;

public class ApiClient {

    private final APIRequestContext request;

    public ApiClient(APIRequestContext request) {
        this.request = request;
    }

    public RequestBuilder request(Endpoint endpoint) {
        return new RequestBuilder(request, endpoint, null);
    }
}