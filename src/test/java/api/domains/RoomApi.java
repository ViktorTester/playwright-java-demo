package api.domains;

import api.client.ApiClient;
import api.client.RequestBuilder;
import api.endpoints.Endpoint;

public class RoomApi {

    private final ApiClient client;

    public RoomApi(ApiClient client) {
        this.client = client;
    }

    public RequestBuilder getRoom() {
        return client.request(Endpoint.GET_ROOM);
    }

}