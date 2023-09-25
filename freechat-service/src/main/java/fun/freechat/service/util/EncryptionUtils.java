package fun.freechat.service.util;

import fun.freechat.util.IdUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Slf4j
public class EncryptionUtils {

    private final Cipher encryptCipher;
    private final Cipher decryptCipher;

    public EncryptionUtils(String aesKey)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] bytes = aesKey.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(bytes, "AES");
        encryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        encryptCipher.init(Cipher.ENCRYPT_MODE, keySpec);
        decryptCipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        decryptCipher.init(Cipher.DECRYPT_MODE, keySpec);
    }

    public String encrypt(String plainText) {
        if (StringUtils.isBlank(plainText)) {
            return plainText;
        }
        try {
            byte[] encrypted = encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.error("Encrypt {} failed!", plainText, e);
            return plainText;
        }
    }

    public String decrypt(String cipherText) {
        if (StringUtils.isBlank(cipherText)) {
            return cipherText;
        }
        try {
            byte[] encrypted = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = decryptCipher.doFinal(encrypted);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.error("Decrypt {} failed!", cipherText, e);
            return cipherText;
        }
    }

    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        String aesKey = IdUtils.newId();
        System.out.println(aesKey);
        String text = "Hello World!";
        EncryptionUtils service = new EncryptionUtils(aesKey);
        String cipherText = service.encrypt(text);
        System.out.println(text + ": " + cipherText + " -> " +  service.decrypt(cipherText));
        text = "I am robot!";
        cipherText = service.encrypt(text);
        System.out.println(text + ": " + cipherText + " -> " +  service.decrypt(cipherText));
    }
}
