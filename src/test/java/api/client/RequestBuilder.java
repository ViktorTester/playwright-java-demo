package api.client;

import api.endpoints.Endpoint;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
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
    private final Map<String, String> params = new HashMap<>();

    private Object body;

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

    public RequestBuilder param(String name, String value) {
        params.put(name, value);
        return this;
    }

    public RequestBuilder body(Object body) {
        this.body = body;
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
        validateBodyAndFormParams();

        RequestOptions options = RequestOptions.create();

        Map<String, String> allHeaders = new HashMap<>();

        if (defaultHeaders != null) {
            allHeaders.putAll(defaultHeaders);
        }

        allHeaders.putAll(headers);

        for (Map.Entry<String, String> header : allHeaders.entrySet()) {
            options.setHeader(header.getKey(), header.getValue());
        }

        for (Map.Entry<String, String> queryParam : queryParams.entrySet()) {
            options.setQueryParam(
                    queryParam.getKey(),
                    String.valueOf(queryParam.getValue())
            );
        }

        if (body != null) {
            options.setData(body);
        }

        if (!params.isEmpty()) {
            options.setData(params);
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
        if (body != null) {
            return body;
        }

        if (!params.isEmpty()) {
            return params;
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

    private void validateBodyAndFormParams() {
        if (body != null && !params.isEmpty()) {
            throw new IllegalStateException(
                    "Request cannot contain both body and form parameters"
            );
        }
    }
}