package config;

import io.github.cdimascio.dotenv.Dotenv;

public final class Config {

    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();

    private Config() {
    }

    public static String baseUrl() {
        return get("BASE_URL");
    }

    public static String baseApiUrl() {
        return get("BASE_API_URL");
    }

    public static String adminUsername() {
        return get("ADMIN_USERNAME");
    }

    public static String adminPassword() {
        return get("ADMIN_PASSWORD");
    }

    private static String get(String key) {
        String value = System.getenv(key);

        if (value == null || value.isBlank()) {
            value = dotenv.get(key);
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config value: " + key);
        }

        return value;
    }
}