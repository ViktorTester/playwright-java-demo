package api.endpoints;

public enum Endpoint {

    GET_ROOM("room"),
    POST_AUTH_LOGIN("auth/login");

    private final String path;

    Endpoint(String path) {
        this.path = path;
    }

    public String path() {
        return path;
    }
}