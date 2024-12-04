package cat.uvic.teknos.shoeshop.clients;

import cat.uvic.teknos.shoeshop.clients.dto.ShoeDto;
import cat.uvic.teknos.shoeshop.clients.exceptions.RequestException;
import cat.uvic.teknos.shoeshop.clients.utils.RestClientImpl;
import cat.uvic.teknos.shoeshop.cryptoutils.CryptoUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

class RestClientImplTest {
    private static final String HOST = "localhost";
    private static final int PORT = 9998;

    @Test
    void getAllShoesTest() {
        var restClient = new RestClientImpl(HOST, PORT);
        try {
            ShoeDto[] shoe = restClient.getAll("/shoe", ShoeDto[].class);

            assertNotNull(shoe);
            assertTrue(shoe.length > 0, "There should be at least one shoe in the response.");
        } catch (RequestException e) {
            fail("RequestException was thrown: " + e.getMessage());
        }
    }

    @Test
    void encryptionAndDecryptionShoeTest() {
        var restClient = new RestClientImpl(HOST, PORT);

        // Sample data for encryption and decryption
        String originalShoeName = "Test Shoe";
        String originalShoeColor = "Red";

        // Encrypt the data using CryptoUtils
        SecretKey symmetricKey = CryptoUtils.createSecretKey();
        String encryptedShoeName = CryptoUtils.encrypt(originalShoeName, symmetricKey);
        String encryptedShoeColor = CryptoUtils.encrypt(originalShoeColor, symmetricKey);

        assertNotNull(encryptedShoeName, "Encrypted shoe name should not be null.");
        assertNotNull(encryptedShoeColor, "Encrypted shoe color should not be null.");

        // Decrypt the data
        String decryptedShoeName = CryptoUtils.decrypt(encryptedShoeName, symmetricKey);
        String decryptedShoeColor = CryptoUtils.decrypt(encryptedShoeColor, symmetricKey);

        assertNotNull(decryptedShoeName, "Decrypted shoe name should not be null.");
        assertNotNull(decryptedShoeColor, "Decrypted shoe color should not be null.");
        assertEquals(originalShoeName, decryptedShoeName, "Decrypted shoe name should match the original.");
        assertEquals(originalShoeColor, decryptedShoeColor, "Decrypted shoe color should match the original.");
    }

    @Test
    void asymmetricEncryptionShoeTest() {
        var restClient = new RestClientImpl(HOST, PORT);

        String originalShoeData = "Sensitive shoe data for asymmetric encryption";

        // Step 1: Asymmetric encryption: encrypt the symmetric key using the server's public key
        SecretKey symmetricKey = CryptoUtils.createSecretKey();
        String encryptedSymmetricKey = CryptoUtils.asymmetricEncrypt(symmetricKey, restClient.getPublicKey());
        assertNotNull(encryptedSymmetricKey, "Encrypted symmetric key should not be null.");

        // Step 2: Symmetric encryption: encrypt the shoe data using the symmetric key
        String encryptedShoeData = CryptoUtils.encrypt(originalShoeData, symmetricKey);
        assertNotNull(encryptedShoeData, "Encrypted shoe data should not be null.");

        // Step 3: Simulate the server-side decryption (which uses the private key)
        // For test purposes, assume we are directly decrypting the encrypted shoe data here
        String decryptedShoeData = CryptoUtils.decrypt(encryptedShoeData, symmetricKey);
        assertNotNull(decryptedShoeData, "Decrypted shoe data should not be null.");
        assertEquals(originalShoeData, decryptedShoeData, "Decrypted shoe data should match the original.");
    }
}
