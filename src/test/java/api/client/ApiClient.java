package api.client;

import api.endpoints.Endpoint;
import com.microsoft.playwright.APIRequestContext;

import java.util.Map;

public class ApiClient {

    private final APIRequestContext request;
    private final Map<String, String> defaultHeaders;

    public ApiClient(APIRequestContext request, Map<String, String> defaultHeaders) {
        this.request = request;
        this.defaultHeaders = defaultHeaders;
    }

    public RequestBuilder request(Endpoint endpoint) {
        return new RequestBuilder(request, endpoint, defaultHeaders);
    }

}