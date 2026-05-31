package config;

public final class TestConfig {

    public static final String DEFAULT_BASE_URL = "https://automationintesting.online/";
    public static final String DEFAULT_BASE_API_URL = "https://automationintesting.online/api/";

    private TestConfig() {
    }

    public static String baseUrl() {
        return getRequiredProperty("baseUrl", DEFAULT_BASE_URL);
    }

    public static String baseApiUrl() {
        return getRequiredProperty("baseApiUrl", DEFAULT_BASE_API_URL);
    }

    private static String getRequiredProperty(String propertyName, String defaultValue) {
        String value = System.getProperty(propertyName);

        if (value == null || value.isBlank()) {
            value = System.getenv(toEnvName(propertyName));
        }

        if (value == null || value.isBlank()) {
            value = defaultValue;
        }

        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required config value: " + propertyName);
        }

        return value;
    }

    private static String toEnvName(String propertyName) {
        return propertyName
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }
}
