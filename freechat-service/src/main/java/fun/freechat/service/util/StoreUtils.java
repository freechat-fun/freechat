package fun.freechat.service.util;

import fun.freechat.service.common.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;

@Component
@Slf4j
public class StoreUtils implements ApplicationContextAware {
    private static final String DEFAULT_FILE_STORE_NAME = "localFileStore";

    private static FileStore localFileStore;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        localFileStore = applicationContext.getBean(DEFAULT_FILE_STORE_NAME, FileStore.class);
    }

    public static FileStore defaultFileStore() {
        return localFileStore;
    }

    public static String guessMimeTypeOfBase64Data(String base64Data) {
        try (InputStream in = new ByteArrayInputStream(
                Base64.getDecoder().decode(base64Data))) {
            return URLConnection.guessContentTypeFromStream(in);
        } catch (IOException e) {
            return null;
        }
    }
}
