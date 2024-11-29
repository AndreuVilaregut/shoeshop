package cat.uvic.teknos.shoeshop.cryptoutils;

import cat.uvic.teknos.shoeshop.cryptoutils.exceptions.CryptoException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Base64;

public class CryptoUtils {
    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    public class KeyGenerationUtils {

        public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048); // Tamaño de la clave RSA (2048 bits)
            return keyPairGenerator.generateKeyPair();
        }
    }

    // Método para obtener un hash de texto (por ejemplo, contraseñas)
    public static String getHash(String text) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(text.getBytes());
            return toBase64(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    // Método para crear una clave secreta AES
    public static SecretKey createSecretKey() {
        try {
            return KeyGenerator.getInstance("AES").generateKey();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    // Método para decodificar la clave secreta desde una cadena base64
    public static SecretKey decodeSecretKey(String base64SecretKey) {
        var bytes = decoder.decode(base64SecretKey);
        return new SecretKeySpec(bytes, 0, bytes.length, "AES");
    }

    // Método para cifrar texto utilizando AES
    public static String encrypt(String plainText, SecretKey key) {
        try {
            var cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return toBase64(cipher.doFinal(plainText.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new CryptoException(e);
        }
    }

    // Método para descifrar texto cifrado utilizando AES
    public static String decrypt(String encryptedTextBase64, SecretKey key) {
        try {
            var cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(fromBase64(encryptedTextBase64)));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new CryptoException(e);
        }
    }

    // Métodos para convertir entre base64 y bytes
    public static String toBase64(byte[] bytes) {
        return encoder.encodeToString(bytes);
    }

    public static byte[] fromBase64(String base64) {
        return decoder.decode(base64);
    }

    public static String asymmetricEncrypt(String plainTextBase64, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedBytes = cipher.doFinal(fromBase64(plainTextBase64));
            return toBase64(encryptedBytes);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    // Descifrado asimétrico con clave privada (RSA)
    public static String asymmetricDecrypt(String encryptedTextBase64, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(fromBase64(encryptedTextBase64));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
}
