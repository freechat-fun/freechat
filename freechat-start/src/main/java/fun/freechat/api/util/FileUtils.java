package fun.freechat.api.util;

import fun.freechat.service.common.FileStore;
import fun.freechat.util.IdUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Slf4j
public class FileUtils {
    public static final String PUBLIC_DIR = "public/";
    public static final String PRIVATE_DIR = "private/";
    
    public static String transfer(MultipartFile file, FileStore fileStore, String dstDir, int maxCount)
            throws IOException {
        if (fileStore.exists(dstDir)) {
            List<String> pictures = fileStore.list(dstDir, null, false);
            int purgeCount = pictures.size() - maxCount;
            if (purgeCount > 0) {
                pictures.subList(0, purgeCount).forEach(fileStore::tryDelete);
            }
        } else {
            fileStore.createDirectories(dstDir);
        }
        String dstPath = dstDir + "/" + IdUtils.newId() + "-" + file.getOriginalFilename();
        fileStore.write(dstPath, file.getInputStream());
        log.info("Saved file: {}", dstPath);
        return dstPath;
    }

    public static String getDefaultPublicUrlForImage(HttpServletRequest request, String path) {
        String sharePath = path.substring(PUBLIC_DIR.length());
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/public/image/" +
                        Base64.getUrlEncoder().encodeToString(sharePath.getBytes(StandardCharsets.UTF_8)))
                .build()
                .toString();
    }

    public static String getDefaultPrivateUrlForImage(HttpServletRequest request, String path, String userId) {
        String sharePath = path.substring(PRIVATE_DIR.length() + userId.length() + 1);
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/my/image/" +
                        Base64.getUrlEncoder().encodeToString(sharePath.getBytes(StandardCharsets.UTF_8)))
                .build()
                .toString();
    }
}
