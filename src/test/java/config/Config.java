package config;

import io.github.cdimascio.dotenv.Dotenv;

/**
 * Centralized test configuration.

 * Priority:
 * 1. JVM system property: -DBASE_URL=...
 * 2. Environment variable: BASE_URL=...
 * 3. Local .env file
 * 4. Default value, if explicitly provided
 */
public final class Config {

    private static final Dotenv DOTENV = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

    private Config() {
    }

    public static String baseUrl() {
        return required("BASE_URL");
    }

    public static String baseApiUrl() {
        return required("BASE_API_URL");
    }

    public static String adminUsername() {
        return required("ADMIN_USERNAME");
    }

    public static String adminPassword() {
        return required("ADMIN_PASSWORD");
    }

    public static BrowserType browser() {
        return BrowserType.from(optional("BROWSER", BrowserType.CHROMIUM.value()));
    }

    public static boolean headless() {
        return optionalBoolean("HEADLESS", true);
    }

    public static double defaultTimeoutMs() {
        return optionalDouble("DEFAULT_TIMEOUT_MS", 10_000);
    }

    public static double apiTimeoutMs() {
        return optionalDouble("API_TIMEOUT_MS", 10_000);
    }

    /**
     * Validates critical configuration before tests start.
     */
    public static void validate() {
        baseUrl();
        baseApiUrl();
        adminUsername();
        adminPassword();

        browser();
        headless();
        defaultTimeoutMs();
        apiTimeoutMs();
    }

    private static String required(String key) {
        String value = getValue(key);

        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing required configuration value: " + key
                            + ". Provide it via JVM property, environment variable, or .env file."
            );
        }

        return value.trim();
    }

    private static String optional(String key, String defaultValue) {
        String value = getValue(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value.trim();
    }

    private static boolean optionalBoolean(String key, boolean defaultValue) {
        String value = getValue(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        String normalized = value.trim().toLowerCase();

        return switch (normalized) {
            case "true", "yes", "1" -> true;
            case "false", "no", "0" -> false;
            default -> throw new IllegalArgumentException(
                    "Invalid boolean value for " + key + ": " + value
                            + ". Supported values: true, false, yes, no, 1, 0"
            );
        };
    }

    private static double optionalDouble(String key, double defaultValue) {
        String value = getValue(key);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        try {
            double parsedValue = Double.parseDouble(value.trim());

            if (parsedValue <= 0) {
                throw new IllegalArgumentException(
                        "Configuration value " + key + " must be greater than 0. Actual value: " + value
                );
            }

            return parsedValue;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    "Invalid numeric value for " + key + ": " + value,
                    exception
            );
        }
    }

    private static String getValue(String key) {
        String systemPropertyValue = System.getProperty(key);
        if (systemPropertyValue != null && !systemPropertyValue.isBlank()) {
            return systemPropertyValue;
        }

        String environmentValue = System.getenv(key);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }

        return DOTENV.get(key);
    }
}