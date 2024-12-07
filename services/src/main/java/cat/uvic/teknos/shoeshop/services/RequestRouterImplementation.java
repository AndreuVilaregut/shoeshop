package cat.uvic.teknos.shoeshop.services;

import cat.uvic.teknos.shoeshop.cryptoutils.CryptoUtils;
import cat.uvic.teknos.shoeshop.services.controllers.Controller;
import cat.uvic.teknos.shoeshop.services.exceptions.ResourceNotFoundException;
import rawhttp.core.RawHttp;
import rawhttp.core.RawHttpRequest;
import rawhttp.core.RawHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Base64;
import java.util.Map;

public class RequestRouterImplementation implements RequestRouter {
    private static final RawHttp rawHttp = new RawHttp();
    private final Map<String, Controller> controllers;
    private final PrivateKey privateKey;
    private static final Logger logger = LoggerFactory.getLogger(RequestRouterImplementation.class);

    // Constructor to initialize with controllers and privateKey
    public RequestRouterImplementation(Map<String, Controller> controllers) {
        this.controllers = controllers;
        this.privateKey = loadPrivateKey(); // Load private key from keystore
    }

    // Load the private key from a PKCS12 keystore
    private PrivateKey loadPrivateKey() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(RequestRouterImplementation.class.getResourceAsStream("/server.p12"), "Teknos01.".toCharArray());
            return (PrivateKey) keyStore.getKey("server", "Teknos01.".toCharArray());
        } catch (KeyStoreException | UnrecoverableKeyException | IOException | NoSuchAlgorithmException | CertificateException e) {
            throw new RuntimeException("Error loading the private key", e);
        }
    }

    // Check if a string is valid Base64
    private boolean isBase64Encoded(String input) {
        try {
            Base64.getDecoder().decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public RawHttpResponse<?> route(RawHttpRequest request) {
        try {
            // Debugging: Print headers and request method/path
            logger.debug("Request Headers: {}", request.getHeaders());
            logger.debug("Request Method: {}", request.getMethod());
            logger.debug("Request Path: {}", request.getUri().getPath());

            // Extract the encrypted symmetric key from request headers
            String encryptedKeyBase64 = request.getHeaders().get("X-Symmetric-Key").stream()
                    .findFirst()
                    .orElseThrow(() -> {
                        logger.warn("No Encrypted Key Found in headers");
                        return new RuntimeException("No Encrypted Key Found");
                    });

            // Check if the encrypted symmetric key is Base64 valid
            if (!isBase64Encoded(encryptedKeyBase64)) {
                throw new RuntimeException("Invalid encrypted symmetric key (incorrect Base64 encoding)");
            }

            // Decrypt the symmetric key using the private key
            SecretKey symmetricKey = CryptoUtils.asymmetricDecrypt(encryptedKeyBase64, privateKey);
            logger.debug("Symmetric key decrypted successfully");

            // Decrypt the body of the request, if it exists
            String decryptedBody = "";
            if (request.getBody().isPresent() && !request.getBody().get().decodeBodyToString(Charset.defaultCharset()).isEmpty()) {
                String encryptedBody = request.getBody().get().decodeBodyToString(Charset.defaultCharset());
                decryptedBody = CryptoUtils.decrypt(encryptedBody, symmetricKey);
                logger.debug("Decrypted request body: {}", decryptedBody);
            }

            // Process the request based on HTTP method and path
            var path = request.getUri().getPath();
            var method = request.getMethod();
            var pathParts = path.split("/");

            // Call the appropriate controller handler for the request
            String responseJsonBody = handleRequest(method, pathParts, decryptedBody);

            // Encrypt the response before sending
            String encryptedResponse = CryptoUtils.encrypt(responseJsonBody, symmetricKey);
            String responseHash = CryptoUtils.getHash(encryptedResponse);

            // Return the encrypted response with the hash in the header
            return rawHttp.parseResponse(
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + encryptedResponse.getBytes(Charset.defaultCharset()).length + "\r\n" +
                            "X-Body-Hash: " + responseHash + "\r\n" +
                            "\r\n" +
                            encryptedResponse
            );
        } catch (ResourceNotFoundException e) {
            return generateErrorResponse(404, "Not Found: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Internal server error occurred: ", e);
            return generateErrorResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }

    // Handle different HTTP methods and route to the appropriate controller
    private String handleRequest(String method, String[] pathParts, String decryptedBody) throws Exception {
        Controller controller = controllers.get(pathParts[1]);

        if (controller == null) {
            throw new ResourceNotFoundException("Controller not found: " + pathParts[1]);
        }

        switch (method) {
            case "POST":
                controller.post(decryptedBody);
                return "{\"message\": \"Resource created successfully.\"}";

            case "GET":
                if (pathParts.length == 2) {
                    return controller.get();
                } else if (pathParts.length == 3) {
                    return controller.get(Integer.parseInt(pathParts[2]));
                }
                break;

            case "PUT":
                if (pathParts.length < 3) throw new IllegalArgumentException("ID missing for PUT request");
                controller.put(Integer.parseInt(pathParts[2]), decryptedBody);
                return "{\"message\": \"Resource updated successfully.\"}";

            case "DELETE":
                if (pathParts.length < 3) throw new IllegalArgumentException("ID missing for DELETE request");
                controller.delete(Integer.parseInt(pathParts[2]));
                return "{\"message\": \"Resource deleted successfully.\"}";

            default:
                throw new IllegalArgumentException("Method not allowed: " + method);
        }

        return null;
    }

    // Generate an error response with the specified HTTP status code and message
    private RawHttpResponse<?> generateErrorResponse(int statusCode, String message) {
        String responseBody = "{\"error\": \"" + message + "\"}";
        return rawHttp.parseResponse(
                "HTTP/1.1 " + statusCode + " " + getReasonPhrase(statusCode) + "\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + responseBody.length() + "\r\n" +
                        "\r\n" +
                        responseBody
        );
    }

    // Get the reason phrase for the given HTTP status code
    private String getReasonPhrase(int statusCode) {
        switch (statusCode) {
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            default:
                return "Unknown";
        }
    }
}
