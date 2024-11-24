package com.cmpt370T7.PRJFlow.util.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Manages HTTP connections for web API requests.
 * Supports GET and POST methods with configurable headers and request bodies.
 */
public class WebConnection {
    private static final Logger logger = LoggerFactory.getLogger(WebConnection.class);

    // Connection config
    private final String url;
    private final WebConnectionMethod method;
    private final String body;
    private final String contentType;
    private final String accept;
    private final String authorization;
    private final String userAgent;

    // Active connection
    private HttpURLConnection connection;

    /**
     * Creates a new WebConnection with specified parameters.
     *
     * @param url           The URL to connect to
     * @param method        The HTTP method (GET or POST)
     * @param body          The request body (for POST requests)
     * @param contentType   The Content-Type header value
     * @param accept        The Accept header value
     * @param authorization The Authorization header value
     * @param userAgent     The User-Agent header value
     */
    public WebConnection(String url, WebConnectionMethod method, String body, String contentType, String accept, String authorization, String userAgent) {
        this.url = url;
        this.method = method;
        this.body = body;
        this.contentType = contentType;
        this.accept = accept;
        this.authorization = authorization;
        this.userAgent = userAgent;
        this.connection = null;
    }

    // Getters
    public String getUrl() { return url; }
    public WebConnectionMethod getMethod() { return method; }
    public String getBody() { return body; }
    public String getContentType() { return contentType; }
    public String getAccept() { return accept; }
    public String getAuthorization() { return authorization; }
    public String getUserAgent() { return userAgent; }

    /**
     * Establishes a connection to the specified URL with configured parameters.
     *
     * @return true if connection is successfully established, false otherwise
     * @throws IOException if connection fails or there's an I/O error
     */
    public boolean connect() throws IOException {
        if (connection != null) {
            logger.error("Connection already established");
            return false;
        }

        try {
            connection = createConnection();
            configureConnection();
            sendRequestBody();
            connection.connect();

            return validateResponse();
        } catch (IOException e) {
            logger.error("Failed to connect to URL", e);
            throw e;
        }
    }

    public String getResponse() throws IOException {
        if (connection == null) {
            logger.error("Connection not established");
            return null;
        }
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }
        return response.toString();
    }

    /**
     * Creates and returns a new HTTP connection.
     */
    private HttpURLConnection createConnection() throws IOException {
        URL urlObj = URI.create(this.url).toURL();
        return (HttpURLConnection) urlObj.openConnection();
    }

    /**
     * Configures the HTTP connection with headers and method.
     */
    private void configureConnection() throws IOException {
        connection.setRequestMethod(method.toString());

        if (contentType != null) {
            connection.setRequestProperty("Content-Type", contentType);
        }
        connection.setRequestProperty("Accept", accept);
        connection.setRequestProperty("Authorization", authorization);
        connection.setRequestProperty("User-Agent", userAgent);
    }

    /**
     * Sends the request body for POST requests.
     */
    private void sendRequestBody() throws IOException {
        if (method == WebConnectionMethod.POST && body != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
    }

    /**
     * Validates the response code from the server.
     */
    private boolean validateResponse() throws IOException {
        if (connection.getResponseCode() != 200) {
            logger.error("Received status code {} from url: {}",
                    connection.getResponseCode(), this.url);
            logger.error("Response message: {}", connection.getResponseMessage());
            logger.error("Sent request: {}", body);
            return false;
        }
        return true;
    }
}
