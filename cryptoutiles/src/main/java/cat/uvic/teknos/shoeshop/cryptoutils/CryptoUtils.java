package cat.uvic.teknos.shoeshop.cryptoutils;

import cat.uvic.teknos.shoeshop.cryptoutils.exceptions.CryptoException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;


public class CryptoUtils {
    public static final Base64.Encoder encoder = Base64.getEncoder();
    public static final Base64.Decoder decoder = Base64.getDecoder();

    static{
        Security.addProvider(new BouncyCastleProvider());
    }

    public static String getHash(String text){
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(text.getBytes());
            return toBase64(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }

    public static SecretKey createSecretKey(){

        try {
            return KeyGenerator.getInstance("AES").generateKey();        //
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException(e);
        }
    }

    public static SecretKey decodeSecretKey(String base64SecretKey){
        var bytes = decoder.decode(base64SecretKey);
        return new SecretKeySpec(bytes,0,bytes.length,"AES");        //
    }

    public static String encrypt(String plainText, SecretKey key) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return toBase64(cipher.doFinal(plainText.getBytes()));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new CryptoException(e);
        }
    }

    public static String decrypt(String encryptedTextBase64, SecretKey key) {
        try {
            var cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedBytes = cipher.doFinal(fromBase64(encryptedTextBase64));
            return new String(decryptedBytes);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException |
                 BadPaddingException e) {
            throw new CryptoException(e);
        }
    }

    public static String asymmetricEncrypt(SecretKey plainTextBase64, Key key) {
        try {
            var cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return toBase64(cipher.doFinal(plainTextBase64.getEncoded()));
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }

    public static SecretKey asymmetricDecrypt(String encryptedTextBase64, Key key) {
        try {
            var cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryptedKeyBytes = cipher.doFinal(fromBase64(encryptedTextBase64));
            return new javax.crypto.spec.SecretKeySpec(decryptedKeyBytes, "AES");
        } catch (Exception e) {
            throw new CryptoException(e);
        }
    }
    public static String toBase64(byte[] bytes){
        return encoder.encodeToString(bytes);
    }

    public static byte[] fromBase64(String base64) {
        return decoder.decode(base64);
    }


}