package api.assertions.matchers;

public record ApiExpectedValue(
        ApiExpectedValueType type,
        Object expectedValue
) {
}