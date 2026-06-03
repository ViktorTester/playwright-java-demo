package api.assertions.matchers;

public enum ApiExpectedValueType {
    EQUALS,
    EXISTS,
    NOT_NULL,
    NOT_BLANK_STRING,
    NOT_EMPTY_LIST,
    NOT_EMPTY_MAP,
    CONTAINS,
    MATCHES_REGEX,
    GREATER_THAN,
    LESS_THAN
}