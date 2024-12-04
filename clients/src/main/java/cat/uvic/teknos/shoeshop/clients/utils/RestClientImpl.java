package cat.uvic.teknos.shoeshop.clients.utils;

import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import cat.uvic.teknos.shoeshop.cryptoutils.CryptoUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

import rawhttp.core.*;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.KeyStore;

public class RestClientImpl {

    private final int port;
    private final String host;
    private final PublicKey publicKey;

    public RestClientImpl(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            InputStream keystoreStream = RestClientImpl.class.getResourceAsStream("/client1.p12");
            if (keystoreStream == null) {
                throw new RuntimeException("Keystore file not found in classpath");
            }
            keyStore.load(keystoreStream, "Teknos01.".toCharArray());
            Certificate serverCertificate = keyStore.getCertificate("server");
            if (serverCertificate == null) {
                throw new RuntimeException("Certificate with alias 'server' not found in keystore");
            }
            publicKey = serverCertificate.getPublicKey();
        } catch (Exception e) {
            throw new RuntimeException("Error loading server certificate", e);
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public <T> T get(String path, Class<T> returnType) throws RequestException {
        return execRequest("GET", path, null, returnType);
    }

    public <T> T[] getAll(String path, Class<T[]> returnType) throws RequestException {
        return execRequest("GET", path, null, returnType);
    }

    public void post(String path, String body) throws RequestException {
        execRequest("POST", path, body, Void.class);
    }

    public void put(String path, String body) throws RequestException {
        execRequest("PUT", path, body, Void.class);
    }

    public void delete(String path, String body) throws RequestException {
        execRequest("DELETE", path, body, Void.class);
    }

    protected <T> T execRequest(String method, String path, String body, Class<T> returnType) throws RequestException {
        var rawHttp = new RawHttp();
        try (var socket = new Socket(host, port)) {
            if (body == null) body = "";

            // Step 1: Generate symmetric key (for encrypting body)
            SecretKey symmetricKey = CryptoUtils.createSecretKey();

            String encryptedBody = "";
            String hash = "";
            if (!body.isEmpty()) {
                encryptedBody = CryptoUtils.encrypt(body, symmetricKey);  // Encrypt the data with the symmetric key
                hash = CryptoUtils.getHash(encryptedBody);
            }

            System.out.println("Encrypted Body: " + encryptedBody);

            String encryptedKey = CryptoUtils.asymmetricEncrypt(symmetricKey, publicKey);

            System.out.println("Encrypted Symmetric Key: " + encryptedKey);

            String requestString = String.format(
                    "%s http://%s:%d/%s HTTP/1.1\r\n" +
                            "Host: %s\r\n" +
                            "User-Agent: RawHTTP\r\n" +
                            "Content-Length: %d\r\n" +
                            "Content-Type: application/json\r\n" +
                            "X-Symmetric-Key: %s\r\n" +
                            "X-Body-Hash: %s\r\n" +
                            "\r\n" +
                            "%s",
                    method, host, port, path, host, encryptedBody.length(), encryptedKey, hash, encryptedBody);

            var request = rawHttp.parseRequest(requestString);
            request.writeTo(socket.getOutputStream());
            socket.getOutputStream().flush();

            T returnValue = null;
            var response = rawHttp.parseResponse(socket.getInputStream()).eagerly();

            int responseCode = response.getStatusCode();
            if (responseCode != 200 && responseCode != 201) {
                throw new RequestException("Request failed with HTTP code " + responseCode);
            }

            if (!returnType.isAssignableFrom(Void.class)) {
                String encryptedResponse = response.getBody().map(b -> {
                    try {
                        return b.asRawString(StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).orElse("{}");

                // Step 4: Decrypt response body (use symmetric key)
                String decryptedResponse = CryptoUtils.decrypt(encryptedResponse, symmetricKey);

                // Debugging: Print the decrypted response to ensure correct decryption
                System.out.println("Decrypted Response: " + decryptedResponse);

                returnValue = Mappers.get().readValue(decryptedResponse, returnType);
            }

            return returnValue;
        } catch (IOException | RuntimeException e) {
            throw new RequestException("Error executing request: " + e.getMessage(), e);
        }
    }

}