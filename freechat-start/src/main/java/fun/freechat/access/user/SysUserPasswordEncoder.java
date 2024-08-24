package fun.freechat.access.user;

import fun.freechat.service.common.EncryptionService;
import org.springframework.security.crypto.password.PasswordEncoder;

public record SysUserPasswordEncoder(EncryptionService encryptionService) implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return encryptionService.encrypt(rawPassword.toString());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
