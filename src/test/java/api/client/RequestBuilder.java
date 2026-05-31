package api.client;

import api.endpoints.Endpoint;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.FormData;
import com.microsoft.playwright.options.RequestOptions;
import utils.ApiLogger;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class RequestBuilder {

    private final APIRequestContext request;
    private final Endpoint endpoint;
    private final Map<String, String> defaultHeaders;

    private final Map<String, String> pathParams = new HashMap<>();
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();

    private Object jsonBody;
    private FormData formBody;

    public RequestBuilder(
            APIRequestContext request,
            Endpoint endpoint,
            Map<String, String> defaultHeaders
    ) {
        this.request = request;
        this.endpoint = endpoint;
        this.defaultHeaders = defaultHeaders;
    }

    public RequestBuilder pathParam(String name, String value) {
        pathParams.put(name, value);
        return this;
    }

    public RequestBuilder queryParam(String name, String value) {
        queryParams.put(name, value);
        return this;
    }

    public RequestBuilder header(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public RequestBuilder jsonBody(Object body) {
        this.jsonBody = body;
        return this;
    }

    public RequestBuilder formBody(FormData body) {
        this.formBody = body;
        return this;
    }

    public APIResponse get() {
        logRequest("GET");
        return request.get(buildUrl(), buildOptions());
    }

    public APIResponse post() {
        logRequest("POST");
        return request.post(buildUrl(), buildOptions());
    }

    public APIResponse put() {
        logRequest("PUT");
        return request.put(buildUrl(), buildOptions());
    }

    public APIResponse delete() {
        logRequest("DELETE");
        return request.delete(buildUrl(), buildOptions());
    }

    private String buildUrl() {
        String url = endpoint.path();

        for (Map.Entry<String, String> entry : pathParams.entrySet()) {
            url = url.replace(
                    "{" + entry.getKey() + "}",
                    String.valueOf(entry.getValue())
            );
        }

        return url;
    }

    private RequestOptions buildOptions() {
        validateRequestBody();

        RequestOptions options = RequestOptions.create();

        Map<String, String> allHeaders = buildAllHeaders();

        for (Map.Entry<String, String> header : allHeaders.entrySet()) {
            options.setHeader(header.getKey(), header.getValue());
        }

        for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
            options.setQueryParam(
                    queryParam.getKey(),
                    String.valueOf(queryParam.getValue())
            );
        }

        if (jsonBody != null) {
            options.setData(jsonBody);
        }

        if (formBody != null) {
            options.setData(formBody);
        }

        return options;
    }

    private void logRequest(String method) {
        ApiLogger.logRequest(
                method,
                buildUrlWithQueryParams(),
                buildAllHeaders(),
                buildLoggedBody()
        );
    }

    private Map<String, String> buildAllHeaders() {
        Map<String, String> allHeaders = new HashMap<>();

        if (defaultHeaders != null) {
            allHeaders.putAll(defaultHeaders);
        }

        allHeaders.putAll(headers);

        return allHeaders;
    }

    private Object buildLoggedBody() {
        if (jsonBody != null) {
            return jsonBody;
        }

        if (formBody != null) {
            return formBody;
        }

        return null;
    }

    private String buildUrlWithQueryParams() {
        String url = buildUrl();

        if (queryParams.isEmpty()) {
            return url;
        }

        StringBuilder queryString = new StringBuilder();

        for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
            if (!queryString.isEmpty()) {
                queryString.append("&");
            }

            queryString
                    .append(encode(queryParam.getKey()))
                    .append("=")
                    .append(encode(queryParam.getValue()));
        }

        return url + "?" + queryString;
    }

    private String encode(String value) {
        return URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
    }

    private void validateRequestBody() {
        if (jsonBody != null && formBody != null) {
            throw new IllegalStateException(
                    "Request cannot contain both JSON body and form body"
            );
        }
    }
}