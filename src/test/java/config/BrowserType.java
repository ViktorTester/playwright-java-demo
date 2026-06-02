package config;

/**
 * Supported browser engines for Playwright UI tests.
 */
public enum BrowserType {

    CHROMIUM("chromium"),
    FIREFOX("firefox"),
    WEBKIT("webkit");

    private final String value;

    BrowserType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static BrowserType from(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            return CHROMIUM;
        }

        for (BrowserType browserType : values()) {
            if (browserType.value.equalsIgnoreCase(rawValue.trim())) {
                return browserType;
            }
        }

        throw new IllegalArgumentException(
                "Unsupported browser: " + rawValue
                        + ". Supported values: chromium, firefox, webkit"
        );
    }
}