package fun.freechat.service.common.impl;

import fun.freechat.service.common.EncryptionService;
import fun.freechat.service.util.EncryptionUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
@SuppressWarnings("unused")
public class EncryptionServiceImpl implements EncryptionService {
    @Value("${auth.aes.key}")
    private String aesKey;

    private EncryptionUtils encryptionUtils;

    @PostConstruct
    public void init()
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        encryptionUtils = new EncryptionUtils(aesKey);
    }

    @Override
    public String encrypt(String plainText) {
        return encryptionUtils.encrypt(plainText);
    }

    @Override
    public String decrypt(String cipherText) {
        return encryptionUtils.decrypt(cipherText);
    }
}
