package api.endpoints;

public enum Endpoint {

    GET_ROOM("api/room"),
    POST_AUTH_LOGIN("api/auth/login");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}