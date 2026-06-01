package api.assertions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class JsonPathReader {

    static final Object MISSING_VALUE = new Object();

    private JsonPathReader() {
    }

    static Object read(Object jsonBody, String path) {
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("JSON path must not be null or blank");
        }

        Object current = jsonBody;

        for (PathToken token : tokenize(path)) {
            if (current == MISSING_VALUE) {
                return MISSING_VALUE;
            }

            current = resolveToken(current, token);
        }

        return current;
    }

    private static Object resolveToken(Object current, PathToken token) {
        Object value = current;

        if (token.fieldName() != null && !token.fieldName().isBlank()) {
            value = resolveField(value, token.fieldName());
        }

        for (Integer index : token.indexes()) {
            value = resolveIndex(value, index);
        }

        return value;
    }

    private static Object resolveField(Object current, String fieldName) {
        if (!(current instanceof Map<?, ?> map)) {
            return MISSING_VALUE;
        }

        if (!map.containsKey(fieldName)) {
            return MISSING_VALUE;
        }

        return map.get(fieldName);
    }

    private static Object resolveIndex(Object current, int index) {
        if (!(current instanceof List<?> list)) {
            return MISSING_VALUE;
        }

        if (index < 0 || index >= list.size()) {
            return MISSING_VALUE;
        }

        return list.get(index);
    }

    private static List<PathToken> tokenize(String path) {
        List<PathToken> tokens = new ArrayList<>();

        String[] rawTokens = path.split("\\.");

        for (String rawToken : rawTokens) {
            tokens.add(parseToken(rawToken));
        }

        return tokens;
    }

    private static PathToken parseToken(String rawToken) {
        StringBuilder fieldName = new StringBuilder();
        List<Integer> indexes = new ArrayList<>();

        int position = 0;

        while (position < rawToken.length()) {
            char currentChar = rawToken.charAt(position);

            if (currentChar == '[') {
                int closingBracketIndex = rawToken.indexOf(']', position);

                if (closingBracketIndex == -1) {
                    throw new IllegalArgumentException("Invalid JSON path token: " + rawToken);
                }

                String indexValue = rawToken.substring(position + 1, closingBracketIndex);

                indexes.add(Integer.parseInt(indexValue));

                position = closingBracketIndex + 1;
            } else {
                fieldName.append(currentChar);
                position++;
            }
        }

        return new PathToken(fieldName.toString(), indexes);
    }

    private record PathToken(String fieldName, List<Integer> indexes) {
    }
}