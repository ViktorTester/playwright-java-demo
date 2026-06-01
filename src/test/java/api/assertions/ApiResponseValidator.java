package api.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static api.constants.ApiAssertionMessages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static utils.ApiLogger.logResponse;

/**
 * Utility class for validating Playwright API responses.

 * Supports:
 * - Status code validation
 * - JSON response body validation by path
 * - Reusable matchers like Exists, NotNull, NotEmptyList, NotEmptyMap
 */
public class ApiResponseValidator {

    public static final ExpectedValue Exists = ExpectedValue.exists();
    public static final ExpectedValue NotNull = ExpectedValue.notNull();
    public static final ExpectedValue NotEmpty = ExpectedValue.notEmpty();
    public static final ExpectedValue NotEmptyList = ExpectedValue.notEmptyList();
    public static final ExpectedValue NotEmptyMap = ExpectedValue.notEmptyMap();
    public static final ExpectedValue NotEmptyLinkedMap = ExpectedValue.notEmptyMap();

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private ApiResponseValidator() {
    }

    /**
     * Verifies that response status is 200 OK.
     *
     * @param response Playwright API response
     */
    public static void verifySuccessResponse(APIResponse response) {
        verifyStatus(response);
    }

    /**
     * Verifies response status and expected values in the JSON response body.

     * Expected body keys can be direct fields or nested paths, for example:
     * - "id"
     * - "transactions.first.id"
     * - "user.roles[0].name"
     *
     * @param response Playwright API response
     * @param expectedStatusCode expected HTTP status code
     * @param expectedBody map of expected JSON paths and values
     */
    public static void verifyApiResponse(
            APIResponse response,
            int expectedStatusCode,
            Map<String, Object> expectedBody
    ) {
        assertNotNull(response, "API response must not be null");

        String responseBody = response.text();

        logResponse(response);

        assertEquals(
                expectedStatusCode,
                response.status(),
                buildStatusErrorMessage(expectedStatusCode, response.status(), responseBody)
        );

        if (expectedBody == null || expectedBody.isEmpty()) {
            return;
        }

        Object actualBody = parseJson(responseBody);

        for (Map.Entry<String, Object> expectation : expectedBody.entrySet()) {
            String path = expectation.getKey();
            Object expectedValue = expectation.getValue();

            Object actualValue = JsonPathReader.read(actualBody, path);

            validateValue(path, expectedValue, actualValue, responseBody);
        }
    }

    /**
     * Verifies default successful response status.
     *
     * @param response Playwright API response
     */
    private static void verifyStatus(APIResponse response) {
        assertNotNull(response, "API response must not be null");

        String responseBody = response.text();

        logResponse(response);

        assertEquals(
                api.constants.ApiStatusCodes.OK,
                response.status(),
                buildStatusErrorMessage(api.constants.ApiStatusCodes.OK, response.status(), responseBody)
        );
    }

    /**
     * Parses raw JSON response body into Java objects.
     *
     * @param responseBody raw response body
     * @return parsed JSON object
     */
    private static Object parseJson(String responseBody) {
        try {
            return OBJECT_MAPPER.readValue(responseBody, Object.class);
        } catch (JsonProcessingException e) {
            throw new AssertionError(
                    FAILED_TO_PARSE_RESPONSE_BODY
                            + System.lineSeparator()
                            + "Body: " + responseBody,
                    e
            );
        }
    }

    /**
     * Validates actual value against either direct expected value or custom matcher.
     *
     * @param path JSON path under validation
     * @param expectedValue expected direct value or ExpectedValue matcher
     * @param actualValue actual value from response body
     * @param responseBody raw response body for assertion evidence
     */
    private static void validateValue(
            String path,
            Object expectedValue,
            Object actualValue,
            String responseBody
    ) {
        if (expectedValue instanceof ExpectedValue expectedMatcher) {
            expectedMatcher.validate(path, actualValue, responseBody);
            return;
        }

        Object normalizedExpected = normalizeNumber(expectedValue);
        Object normalizedActual = normalizeNumber(actualValue);

        if (!Objects.equals(normalizedExpected, normalizedActual)) {
            fail(
                    UNEXPECTED_VALUE_IN_API_RESPONSE
                            + System.lineSeparator()
                            + "Path: " + path
                            + System.lineSeparator()
                            + "Expected: " + expectedValue
                            + System.lineSeparator()
                            + "Actual: " + actualValue
                            + System.lineSeparator()
                            + "Body: " + responseBody
            );
        }
    }


    /**
     * Converts numeric values to string representation before comparison.
     *
     * @param value value to normalize
     * @return normalized value
     */
    private static Object normalizeNumber(Object value) {
        if (value instanceof Number number) {
            return number.toString();
        }

        return value;
    }

    /**
     * Builds detailed status mismatch message.
     *
     * @param expectedStatus expected HTTP status code
     * @param actualStatus actual HTTP status code
     * @param responseBody raw response body
     * @return formatted assertion message
     */
    private static String buildStatusErrorMessage(
            int expectedStatus,
            int actualStatus,
            String responseBody
    ) {
        return "Unexpected response status"
                + System.lineSeparator()
                + "Expected: " + expectedStatus
                + System.lineSeparator()
                + "Actual: " + actualStatus
                + System.lineSeparator()
                + "Body: " + responseBody;
    }

    /**
     * Custom expected value matcher used for flexible API response assertions.
     */
    public static final class ExpectedValue {

        private final ExpectedValueType type;

        private ExpectedValue(ExpectedValueType type) {
            this.type = type;
        }

        private static ExpectedValue exists() {
            return new ExpectedValue(ExpectedValueType.EXISTS);
        }

        private static ExpectedValue notNull() {
            return new ExpectedValue(ExpectedValueType.NOT_NULL);
        }

        private static ExpectedValue notEmpty() {
            return new ExpectedValue(ExpectedValueType.NOT_EMPTY);
        }

        private static ExpectedValue notEmptyList() {
            return new ExpectedValue(ExpectedValueType.NOT_EMPTY_LIST);
        }

        private static ExpectedValue notEmptyMap() {
            return new ExpectedValue(ExpectedValueType.NOT_EMPTY_MAP);
        }


        /**
         * Applies matcher-specific validation.
         *
         * @param path JSON path under validation
         * @param actualValue actual value from response body
         * @param responseBody raw response body for assertion evidence
         */
        private void validate(String path, Object actualValue, String responseBody) {
            switch (type) {
                case EXISTS -> validateExists(path, actualValue, responseBody);
                case NOT_NULL -> validateNotNull(path, actualValue, responseBody);
                case NOT_EMPTY -> validateNotEmpty(path, actualValue, responseBody);
                case NOT_EMPTY_LIST -> validateNotEmptyList(path, actualValue, responseBody);
                case NOT_EMPTY_MAP -> validateNotEmptyMap(path, actualValue, responseBody);
                default -> throw new IllegalStateException("Unsupported expected value type: " + type);
            }
        }

        private void validateExists(String path, Object actualValue, String responseBody) {
            if (actualValue == JsonPathReader.MISSING_VALUE) {
                fail(buildMatcherError(EXPECTED_PATH_TO_EXIST, path, actualValue, responseBody));
            }
        }

        private void validateNotNull(String path, Object actualValue, String responseBody) {
            validateExists(path, actualValue, responseBody);

            if (actualValue == null) {
                fail(buildMatcherError(RESPONSE_MUST_NOT_BE_NULL, path, actualValue, responseBody));
            }
        }

        private void validateNotEmpty(String path, Object actualValue, String responseBody) {
            validateExists(path, actualValue, responseBody);

            if (actualValue == null) {
                fail(buildMatcherError(EXPECTED_VALUE_NOT_TO_BE_NULL_OR_EMPTY, path, actualValue, responseBody));
            }

            if (actualValue instanceof String stringValue) {

                if (stringValue.isBlank()) {
                    fail(buildMatcherError(EXPECTED_STRING_NOT_TO_BE_EMPTY, path, actualValue, responseBody));
                }

                return;
            }

            if (actualValue instanceof List<?> listValue) {

                if (listValue.isEmpty()) {
                    fail(buildMatcherError(EXPECTED_LIST_NOT_TO_BE_EMPTY, path, actualValue, responseBody));
                }

                return;
            }

            if (actualValue instanceof Map<?, ?> mapValue) {

                if (mapValue.isEmpty()) {
                    fail(buildMatcherError(EXPECTED_MAP_NOT_TO_BE_EMPTY, path, actualValue, responseBody));
                }
            }
        }

        private void validateNotEmptyList(String path, Object actualValue, String responseBody) {
            validateExists(path, actualValue, responseBody);

            if (!(actualValue instanceof List<?>)) {
                fail(buildMatcherError(EXPECTED_VALUE_TO_BE_A_LIST, path, actualValue, responseBody));
            }

            List<?> listValue = (List<?>) actualValue;

            if (listValue.isEmpty()) {
                fail(buildMatcherError(EXPECTED_LIST_NOT_TO_BE_EMPTY, path, actualValue, responseBody));
            }
        }

        private void validateNotEmptyMap(String path, Object actualValue, String responseBody) {
            validateExists(path, actualValue, responseBody);

            if (!(actualValue instanceof Map<?, ?>)) {
                fail(buildMatcherError(EXPECTED_VALUE_TO_BE_A_MAP, path, actualValue, responseBody));
            }

            Map<?, ?> mapValue = (Map<?, ?>) actualValue;

            if (mapValue.isEmpty()) {
                fail(buildMatcherError(EXPECTED_MAP_NOT_TO_BE_EMPTY, path, actualValue, responseBody));
            }
        }

        /**
         * Builds a detailed matcher failure message.
         *
         * @param message assertion message
         * @param path JSON path under validation
         * @param actualValue actual value from response body
         * @param responseBody raw response body
         * @return formatted assertion message
         */
        private String buildMatcherError(
                String message,
                String path,
                Object actualValue,
                String responseBody
        ) {
            return message
                    + System.lineSeparator()
                    + "Path: " + path
                    + System.lineSeparator()
                    + "Actual: " + actualValue
                    + System.lineSeparator()
                    + "Body: " + responseBody;
        }
    }


    /**
     * Supported matcher types for expected API response values.
     */
    private enum ExpectedValueType {
        EXISTS,
        NOT_NULL,
        NOT_EMPTY,
        NOT_EMPTY_LIST,
        NOT_EMPTY_MAP
    }
}