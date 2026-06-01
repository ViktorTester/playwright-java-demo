package api.constants;

public final class ApiAssertionMessages {

    public static final String RESPONSE_MUST_NOT_BE_NULL = "API response must not be null";
    public static final String FAILED_TO_PARSE_RESPONSE_BODY = "Failed to parse response body as JSON";

    public static final String EXPECTED_PATH_TO_EXIST = "Expected path to exist";
    public static final String EXPECTED_VALUE_NOT_TO_BE_NULL_OR_EMPTY = "Expected value not to be null or empty";
    public static final String EXPECTED_STRING_NOT_TO_BE_EMPTY = "Expected string not to be empty";
    public static final String EXPECTED_LIST_NOT_TO_BE_EMPTY = "Expected list not to be empty";
    public static final String EXPECTED_MAP_NOT_TO_BE_EMPTY = "Expected map not to be empty";
    public static final String EXPECTED_VALUE_TO_BE_A_LIST = "Expected value to be a list";
    public static final String EXPECTED_VALUE_TO_BE_A_MAP = "Expected value to be a map";

    public static final String UNEXPECTED_VALUE_IN_API_RESPONSE = "Unexpected value in API response";

    private ApiAssertionMessages() {
    }
}