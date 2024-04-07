package fun.freechat.service.util;

import fun.freechat.service.common.FileStore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Base64;

@Component
@Slf4j
public class StoreUtils implements ApplicationContextAware {
    private static final String FILE_STORE_BEAN_NAME_SUFFIX = "FileStore";

    private static FileStore fileStore;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Environment env = applicationContext.getEnvironment();

        String fileStoreType = env.getProperty("file.storeType");
        if (StringUtils.isBlank(fileStoreType)) {
            fileStoreType = "local";
        }
        String fileStoreBeanName = fileStoreType + FILE_STORE_BEAN_NAME_SUFFIX;
        try {
            fileStore = applicationContext.getBean(fileStoreBeanName, FileStore.class);
        } catch (BeansException e) {
            log.warn("Failed to find file store: {}", fileStoreBeanName, e);
        }
    }

    public static FileStore defaultFileStore() {
        return fileStore;
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
