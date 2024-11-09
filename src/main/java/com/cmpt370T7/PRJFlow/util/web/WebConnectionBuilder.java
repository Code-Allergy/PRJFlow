package com.cmpt370T7.PRJFlow.util.web;

public class WebConnectionBuilder {
    // Default header values
    public static final String JSON_CONTENT_TYPE = "application/json";

    // Connection parameters
    private String url;
    private WebConnectionMethod method;
    private String body;
    private String contentType;
    private String accept;
    private String authorization;
    private String userAgent;

    /**
     * Private constructor to enforce builder pattern usage.
     * Initializes default values for connection parameters.
     */
    private WebConnectionBuilder() {
        this.url = "";
        this.method = WebConnectionMethod.GET;
        this.body = "";
        this.contentType = null;
        this.accept = null;
        this.authorization = null;
        this.userAgent = null;
    }

    /**
     * Creates a new instance of WebConnectionBuilder.
     *
     * @return A new WebConnectionBuilder instance
     */
    public static WebConnectionBuilder create() {
        return new WebConnectionBuilder();
    }

    /**
     * Sets the URL for the connection.
     *
     * @param url The target URL
     * @return This builder instance
     */
    public WebConnectionBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * Sets the HTTP method for the connection.
     *
     * @param method The HTTP method (GET or POST)
     * @return This builder instance
     */
    public WebConnectionBuilder setMethod(WebConnectionMethod method) {
        this.method = method;
        return this;
    }

    /**
     * Sets the request body.
     *
     * @param body The request body content
     * @return This builder instance
     */
    public WebConnectionBuilder setBody(String body) {
        this.body = body;
        return this;
    }

    /**
     * Sets the Content-Type header.
     *
     * @param contentType The content type value
     * @return This builder instance
     */
    public WebConnectionBuilder setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Sets the Accept header.
     *
     * @param accept The accept header value
     * @return This builder instance
     */
    public WebConnectionBuilder setAccept(String accept) {
        this.accept = accept;
        return this;
    }

    /**
     * Sets the Authorization header.
     *
     * @param authorization The authorization header value
     * @return This builder instance
     */
    public WebConnectionBuilder setAuthorization(String authorization) {
        this.authorization = authorization;
        return this;
    }

    /**
     * Sets the User-Agent header.
     *
     * @param userAgent The user agent value
     * @return This builder instance
     */
    public WebConnectionBuilder setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }


    /**
     * Configures the connection for JSON communication.
     * Sets both Content-Type and Accept headers to "application/json".
     *
     * @param body The JSON request body
     * @return This builder instance
     */
    public WebConnectionBuilder setJson(String body) {
        this.contentType = JSON_CONTENT_TYPE;
        this.accept = JSON_CONTENT_TYPE;
        this.body = body;
        return this;
    }

    /**
     * Builds and returns a new WebConnection instance with the configured parameters.
     *
     * @return A new WebConnection instance
     * @throws IllegalStateException if required parameters are missing
     */
    public WebConnection build() {
        validateConfiguration();
        return new WebConnection(url, method, body, contentType, accept, authorization, userAgent);
    }

    /**
     * Validates the builder configuration before creating a WebConnection.
     *
     * @throws IllegalStateException if the configuration is invalid
     */
    private void validateConfiguration() {
        if (url == null || url.isEmpty()) {
            throw new IllegalStateException("URL must be set");
        }

        if (method == WebConnectionMethod.POST && (body == null || body.isEmpty())) {
            throw new IllegalStateException("POST requests require a body");
        }
    }
}
