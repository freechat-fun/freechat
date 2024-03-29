package fun.freechat.service.common;

import fun.freechat.annotation.Encrypted;
import fun.freechat.service.util.EncryptionUtils;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.crypto.NoSuchPaddingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@SuppressWarnings("unused")
public class EncryptedPropertyProcessor implements BeanPostProcessor {
    private static final List<Pair<String, String>> INNER_SECRET_CONFIG_LIST = List.of(
            // Pair.of("org.springframework.boot.autoconfigure.jdbc.DataSourceProperties", "password")
    );

    @Value("${auth.aes.key}")
    private String aesKey;
    private EncryptionUtils encryptionUtils;

    @PostConstruct
    public void init()
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        encryptionUtils = new EncryptionUtils(aesKey);
    }

    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean == this) {
            return bean;
        }
        Class<? extends Object> clazz = bean.getClass();
        for (Pair<String, String> pair : INNER_SECRET_CONFIG_LIST) {
            String className = pair.getLeft();
            String fieldName = pair.getRight();
            if (className.equals(clazz.getCanonicalName())) {
                String methodName = StringUtils.capitalize(fieldName);
                String setterName = "set" + methodName;
                String getterName = "get" + methodName;
                try {
                    Method setterMethod = clazz.getMethod(setterName, String.class);
                    Method getterMethod = clazz.getMethod(getterName);
                    String secretText = (String) getterMethod.invoke(bean);
                    String plainText = encryptionUtils.decrypt(secretText);
                    if (StringUtils.isNotBlank(plainText)) {
                        setterMethod.invoke(bean, plainText);
                    }
                    return bean;
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Encrypted encrypted = field.getAnnotation(Encrypted.class);
            if (Objects.isNull(encrypted)) {
                continue;
            }
            try {
                Object v = field.get(bean);
                if (!(v instanceof String)) {
                    continue;
                }
                String plainText = encryptionUtils.decrypt((String)v);
                if (StringUtils.isNotBlank(plainText)) {
                    field.set(bean, plainText);
                    log.info("Decrypted {}.{} ", clazz.getSimpleName(), field.getName());
                }
            } catch (Exception e) {
                log.error("Get {}.{} failed!", clazz.getSimpleName(), field.getName(), e);
            }
        }

        return bean;
    }
}
