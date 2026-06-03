package api.assertions;

import api.assertions.matchers.ApiExpectedValue;

import static api.assertions.matchers.ApiExpectedValueType.CONTAINS;
import static api.assertions.matchers.ApiExpectedValueType.EQUALS;
import static api.assertions.matchers.ApiExpectedValueType.EXISTS;
import static api.assertions.matchers.ApiExpectedValueType.GREATER_THAN;
import static api.assertions.matchers.ApiExpectedValueType.LESS_THAN;
import static api.assertions.matchers.ApiExpectedValueType.MATCHES_REGEX;
import static api.assertions.matchers.ApiExpectedValueType.NOT_BLANK_STRING;
import static api.assertions.matchers.ApiExpectedValueType.NOT_EMPTY_LIST;
import static api.assertions.matchers.ApiExpectedValueType.NOT_EMPTY_MAP;
import static api.assertions.matchers.ApiExpectedValueType.NOT_NULL;

public final class ApiExpect {

    private ApiExpect() {
    }

    public static ApiExpectedValue equalsTo(Object expectedValue) {
        return new ApiExpectedValue(EQUALS, expectedValue);
    }

    public static ApiExpectedValue exists() {
        return new ApiExpectedValue(EXISTS, null);
    }

    public static ApiExpectedValue notNull() {
        return new ApiExpectedValue(NOT_NULL, null);
    }

    public static ApiExpectedValue notBlank() {
        return new ApiExpectedValue(NOT_BLANK_STRING, null);
    }

    public static ApiExpectedValue notEmptyList() {
        return new ApiExpectedValue(NOT_EMPTY_LIST, null);
    }

    public static ApiExpectedValue notEmptyMap() {
        return new ApiExpectedValue(NOT_EMPTY_MAP, null);
    }

    public static ApiExpectedValue contains(String expectedValue) {
        return new ApiExpectedValue(CONTAINS, expectedValue);
    }

    public static ApiExpectedValue matchesRegex(String regex) {
        return new ApiExpectedValue(MATCHES_REGEX, regex);
    }

    public static ApiExpectedValue greaterThan(Number expectedValue) {
        return new ApiExpectedValue(GREATER_THAN, expectedValue);
    }

    public static ApiExpectedValue lessThan(Number expectedValue) {
        return new ApiExpectedValue(LESS_THAN, expectedValue);
    }
}