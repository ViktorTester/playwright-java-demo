package api.domains;

import api.client.ApiClient;

public class ApiContainer {

    public final AuthApi auth;
    public final RoomApi room;

    public ApiContainer(ApiClient client) {
        this.auth = new AuthApi(client);
        this.room = new RoomApi(client);
    }
}