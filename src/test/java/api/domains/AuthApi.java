package api.domains;

import api.client.ApiClient;
import api.client.RequestBuilder;
import api.endpoints.Endpoint;

public class AuthApi {

    private final ApiClient client;

    public AuthApi(ApiClient client) {
        this.client = client;
    }

    public RequestBuilder postAuthLogin() {
        return client.request(Endpoint.POST_AUTH_LOGIN);
    }

}