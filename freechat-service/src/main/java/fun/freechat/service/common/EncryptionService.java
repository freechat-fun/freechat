package fun.freechat.service.common;

public interface EncryptionService {
    String encrypt(String plainText);
    String decrypt(String cipherText);
}
