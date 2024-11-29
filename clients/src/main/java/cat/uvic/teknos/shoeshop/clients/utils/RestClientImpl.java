package cat.uvic.teknos.shoeshop.clients.utils;

import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import rawhttp.core.RawHttp;

import java.io.IOException;
import java.net.Socket;

public class RestClientImpl implements RestClient {
    private final String host;
    private final int port;

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public <T> T get(String path, Class<T> returnType) throws RequestException {
        return execRequest("GET", path, null, returnType);
    }

    @Override
    public <T> T[] getAll(String path, Class<T[]> returnType) throws RequestException {
        return execRequest("GET", path, null, returnType);
    }

    @Override
    public void post(String path, String body) throws RequestException {
        execRequest("POST", path, body, Void.class);
    }

    @Override
    public void put(String path, String body) throws RequestException {
        execRequest("PUT", path, body, Void.class);
    }

    @Override
    public void asd(String path) throws RequestException {
        execRequest("DELETE", path, "", Void.class);  // Delete without body
    }

    @Override
    public void delete(String path, String body) throws RequestException {
        execRequest("DELETE", path, body, Void.class);
    }

    protected <T> T execRequest(String method, String path, String body, Class<T> returnType) throws RequestException {
            var rawHttp = new RawHttp();
            try (var socket = new Socket(host, port)) {
                if (body == null) {
                    body = "";  // Empty body if not provided
                }

                // Build the HTTP request string
                String requestString = String.format(
                        "%s http://%s:%d/%s HTTP/1.1\r\n" +
                                "Host: %s\r\n" +
                                "User-Agent: RawHTTP\r\n" +
                                "Content-Type: application/json\r\n" +
                                "Content-Length: %d\r\n" +
                                "\r\n" +
                                "%s", method, host, port, path.startsWith("/") ? path.substring(1) : path, host, body.length(), body);

                var request = rawHttp.parseRequest(requestString);
                request.writeTo(socket.getOutputStream());

                // Read the response from the server
                var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

                // Check the status code
                int statusCode = response.getStatusCode();
                if (statusCode != 200) {
                    throw new RequestException("Unexpected response status: " + statusCode);
                }

                // Process the response body
                String responseBody = response.getBody()
                        .map(bodyPart -> {
                            try {
                                return new String(bodyPart.decodeBody(), "UTF-8");  // Decode the body to string
                            } catch (IOException e) {
                                throw new RuntimeException("Failed to decode response body", e);
                            }
                        })
                        .orElse("");  // If there's no body, we return an empty string

                // If the body is empty, it may still be a successful delete request
                if (responseBody.isEmpty() && returnType.equals(Void.class)) {
                    return null;  // If the method expects no return (Void), we return null
                }

                // If the response is non-empty, we deserialize the body into the returnType
                if (!responseBody.isEmpty() && returnType != Void.class) {
                    return Mappers.get().readValue(responseBody, returnType);
                }

                return null;  // For methods like POST, PUT, DELETE that don't return a body

            } catch (IOException e) {
                throw new RequestException("Request failed: " + e.getMessage(), e);
            }

    }
}
