package utils;

import com.microsoft.playwright.APIResponse;

import java.util.Map;

public class ApiLogger {

    public static void logRequest(String method, String url, Map<String, String> headers, Object body) {
        System.out.println("\n===== API REQUEST =====");
        System.out.println("METHOD: " + method);
        System.out.println("URL: " + url);

        System.out.println("HEADERS:");
        if (headers == null || headers.isEmpty()) {
            System.out.println("<empty>");
        } else {
            headers.forEach((key, value) ->
                    System.out.println(key + ": " + maskSensitiveValue(key, value))
            );
        }

        System.out.println("BODY:");
        System.out.println(body == null ? "<empty>" : body);

        System.out.println("=======================\n");
    }

    public static void logResponse(APIResponse response) {
        System.out.println("\n===== API RESPONSE =====");
        System.out.println("STATUS: " + response.status());
        System.out.println("STATUS TEXT: " + response.statusText());
        System.out.println("URL: " + response.url());

        System.out.println("HEADERS:");
        response.headers().forEach((key, value) ->
                System.out.println(key + ": " + maskSensitiveValue(key, value))
        );

        System.out.println("BODY:");
        try {
            System.out.println(response.text());
        } catch (Exception e) {
            System.out.println("<failed to read response body: " + e.getMessage() + ">");
        }

        System.out.println("========================\n");
    }

    private static String maskSensitiveValue(String key, String value) {
        if (value == null) {
            return null;
        }

        String lowerKey = key.toLowerCase();

        if (lowerKey.contains("authorization")
                || lowerKey.contains("cookie")
                || lowerKey.contains("token")) {
            return "***masked***";
        }

        return value;
    }
}