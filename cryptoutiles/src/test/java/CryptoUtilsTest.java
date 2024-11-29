package cat.uvic.teknos.coursemanagement.cryptoutils;

import cat.uvic.teknos.shoeshop.cryptoutils.CryptoUtils;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class CryptoUtilsTest {

    @Test
    void getHash() {
        var text = "Some text...";
        var base64Text = "quonJ6BjRSC1DBOGuBWNdqixj8z20nuP+QH7cVvp7PI="; // Base64 esperado

        assertEquals(base64Text, CryptoUtils.getHash(text));
    }

    @Test
    void createSecretKey() {
        var secretKey = CryptoUtils.createSecretKey();

        assertNotNull(secretKey);

        var bytes = secretKey.getEncoded();
        System.out.println(CryptoUtils.toBase64(bytes));  // Imprime la clave secreta codificada en Base64
    }

    @Test
    void decodeSecretKey() {
        var secretKeyBase64 = "jaruKzlE7xerbNSjxiVjZtuAeYWrcyMGsA8TaTqZ8AM=";

        var secretKey = CryptoUtils.decodeSecretKey(secretKeyBase64);

        assertNotNull(secretKey);
        assertEquals("AES", secretKey.getAlgorithm());
    }

    @Test
    void encrypt() {
        var secretKey = CryptoUtils.createSecretKey();
        var plainText = "This is a secret message";

        var encryptedText = CryptoUtils.encrypt(plainText, secretKey);

        assertNotNull(encryptedText);
        assertNotEquals(plainText, encryptedText); // El texto cifrado no debe ser igual al texto plano
        System.out.println("Encrypted Text: " + encryptedText);
    }

    @Test
    void decrypt() {
        var secretKey = CryptoUtils.createSecretKey();
        var plainText = "This is a secret message";
        var encryptedText = CryptoUtils.encrypt(plainText, secretKey);

        var decryptedText = CryptoUtils.decrypt(encryptedText, secretKey);

        assertNotNull(decryptedText);
        assertEquals(plainText, decryptedText); // El texto descifrado debe ser igual al texto original
    }

    @Test
    void asymmetricEncrypt() {
        try {
            KeyPair keyPair = CryptoUtils.KeyGenerationUtils.generateRSAKeyPair(); // Genera el par de claves
            String plainText = "This is a public key encrypted message";
            String encryptedText = CryptoUtils.asymmetricEncrypt(CryptoUtils.toBase64(plainText.getBytes()), keyPair.getPublic());

            assertNotNull(encryptedText);
            assertNotEquals(plainText, encryptedText);  // El texto cifrado debe ser diferente al original
            System.out.println("Asymmetric Encrypted Text: " + encryptedText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    void asymmetricDecrypt() {
        try {
            KeyPair keyPair = CryptoUtils.KeyGenerationUtils.generateRSAKeyPair(); // Genera el par de claves
            String plainText = "This is a public key encrypted message";
            String encryptedText = CryptoUtils.asymmetricEncrypt(CryptoUtils.toBase64(plainText.getBytes()), keyPair.getPublic());

            String decryptedText = CryptoUtils.asymmetricDecrypt(encryptedText, keyPair.getPrivate());

            assertNotNull(decryptedText);
            assertEquals(plainText, decryptedText); // El texto descifrado debe ser igual al texto original
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
