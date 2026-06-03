package api.assertions;

import api.assertions.matchers.ApiExpectedValue;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static api.constants.ApiStatusCodes.OK;
import static org.junit.jupiter.api.Assertions.*;

public final class ApiResponseValidator {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiResponseValidator() {
    }

    public static void verifySuccessResponse(APIResponse response) {
        assertEquals(
                OK,
                response.status(),
                "Expected successful API response. Response body: " + response.text()
        );
    }

    public static void verifyApiResponse(
            APIResponse response,
            int expectedStatusCode,
            Map<String, Object> expectedBody
    ) {
        assertEquals(
                expectedStatusCode,
                response.status(),
                "Unexpected API status code. Response body: " + response.text()
        );

        if (expectedBody == null || expectedBody.isEmpty()) {
            return;
        }

        Map<String, Object> actualBody = parseResponseBody(response);

        expectedBody.forEach((path, expectedValue) -> {
            Object actualValue = JsonPathReader.read(actualBody, path);
            verifyValue(path, actualValue, expectedValue);
        });
    }

    private static Map<String, Object> parseResponseBody(APIResponse response) {
        try {
            return OBJECT_MAPPER.readValue(
                    response.text(),
                    new TypeReference<>() {
                    }
            );
        } catch (Exception exception) {
            throw new AssertionError(
                    "Failed to parse API response body as JSON. Body: " + response.text(),
                    exception
            );
        }
    }

    private static void verifyValue(String path, Object actualValue, Object expectedValue) {
        if (expectedValue instanceof ApiExpectedValue apiExpectedValue) {
            verifyMatcher(path, actualValue, apiExpectedValue);
            return;
        }

        assertPathExists(path, actualValue);

        assertEquals(
                expectedValue,
                actualValue,
                "Unexpected value for JSON path: " + path
                        + ". Expected: " + expectedValue
                        + ". Actual: " + actualValue
        );
    }

    private static void verifyMatcher(
            String path,
            Object actualValue,
            ApiExpectedValue expectedValue
    ) {
        switch (expectedValue.type()) {
            case EQUALS -> {
                assertPathExists(path, actualValue);

                assertEquals(
                        expectedValue.expectedValue(),
                        actualValue,
                        "Unexpected value for JSON path: " + path
                                + ". Expected: " + expectedValue.expectedValue()
                                + ". Actual: " + actualValue
                );
            }

            case EXISTS -> assertTrue(
                    pathExists(actualValue),
                    "Expected JSON path to exist: " + path
            );

            case NOT_NULL -> {
                assertPathExists(path, actualValue);

                assertNotNull(actualValue, "Expected value not to be null for JSON path: " + path);
            }

            case NOT_BLANK_STRING -> {
                assertPathExists(path, actualValue);

                assertTrue(
                        actualValue instanceof String stringValue && !stringValue.isBlank(),
                        "Expected non-blank string for JSON path: " + path
                                + ". Actual value: " + actualValue
                );
            }

            case NOT_EMPTY_LIST -> {
                assertPathExists(path, actualValue);

                assertTrue(
                        actualValue instanceof List<?> listValue && !listValue.isEmpty(),
                        "Expected non-empty list for JSON path: " + path
                                + ". Actual value: " + actualValue
                );
            }

            case NOT_EMPTY_MAP -> {
                assertPathExists(path, actualValue);

                assertTrue(
                        actualValue instanceof Map<?, ?> mapValue && !mapValue.isEmpty(),
                        "Expected non-empty map for JSON path: " + path
                                + ". Actual value: " + actualValue
                );
            }

            case CONTAINS -> {
                assertPathExists(path, actualValue);
                assertContains(path, actualValue, expectedValue.expectedValue());
            }

            case MATCHES_REGEX -> {
                assertPathExists(path, actualValue);
                assertMatchesRegex(path, actualValue, expectedValue.expectedValue());
            }

            case GREATER_THAN -> {
                assertPathExists(path, actualValue);
                assertGreaterThan(path, actualValue, expectedValue.expectedValue());
            }

            case LESS_THAN -> {
                assertPathExists(path, actualValue);
                assertLessThan(path, actualValue, expectedValue.expectedValue());
            }
        }
    }

    private static void assertContains(String path, Object actualValue, Object expectedValue) {
        assertTrue(
                actualValue instanceof String actualString
                        && expectedValue instanceof String expectedString
                        && actualString.contains(expectedString),
                "Expected string for JSON path '" + path + "' to contain: " + expectedValue
                        + ". Actual value: " + actualValue
        );
    }

    private static void assertMatchesRegex(String path, Object actualValue, Object expectedValue) {
        assertTrue(
                actualValue instanceof String actualString
                        && expectedValue instanceof String regex
                        && Pattern.compile(regex).matcher(actualString).matches(),
                "Expected string for JSON path '" + path + "' to match regex: " + expectedValue
                        + ". Actual value: " + actualValue
        );
    }

    private static void assertGreaterThan(String path, Object actualValue, Object expectedValue) {
        assertTrue(
                actualValue instanceof Number actualNumber
                        && expectedValue instanceof Number expectedNumber
                        && actualNumber.doubleValue() > expectedNumber.doubleValue(),
                "Expected numeric value for JSON path '" + path + "' to be greater than: " + expectedValue
                        + ". Actual value: " + actualValue
        );
    }

    private static void assertLessThan(String path, Object actualValue, Object expectedValue) {
        assertTrue(
                actualValue instanceof Number actualNumber
                        && expectedValue instanceof Number expectedNumber
                        && actualNumber.doubleValue() < expectedNumber.doubleValue(),
                "Expected numeric value for JSON path '" + path + "' to be less than: " + expectedValue
                        + ". Actual value: " + actualValue
        );
    }

    private static void assertPathExists(String path, Object actualValue) {
        assertTrue(
                pathExists(actualValue),
                "Expected JSON path to exist: " + path
        );
    }

    private static boolean pathExists(Object actualValue) {
        return actualValue != JsonPathReader.MISSING_VALUE;
    }
}