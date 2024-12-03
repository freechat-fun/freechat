package fun.freechat.api.util;

import fun.freechat.service.common.FileStore;
import fun.freechat.util.IdUtils;
import fun.freechat.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;

import static fun.freechat.service.util.StoreUtils.PRIVATE_DIR;
import static fun.freechat.service.util.StoreUtils.PUBLIC_DIR;

@Slf4j
public class FileUtils {
    private static String filenameFor(MultipartFile file) {
        String filteredOriginFilename = SecurityUtils.filterPath(file.getOriginalFilename());
        return StringUtils.isBlank(filteredOriginFilename) ?
                IdUtils.newId() :
                IdUtils.newId() + "-" + filteredOriginFilename.replaceAll("/", "-");
    }
    
    public static String transfer(MultipartFile file, FileStore fileStore, String dstDir, int maxCount)
            throws IOException {
        if (fileStore.exists(dstDir)) {
            List<String> files = fileStore.list(dstDir, null, false);
            int purgeCount = files.size() - maxCount;
            if (purgeCount > 0) {
                files.subList(0, purgeCount).forEach(fileStore::tryDelete);
            }
        } else {
            fileStore.createDirectories(dstDir);
        }
        String dstPath = dstDir + "/" + filenameFor(file);
        fileStore.write(dstPath, file.getInputStream());
        log.info("Saved file: {}", dstPath);
        return dstPath;
    }

    public static String move(Path file, FileStore fileStore, String dstDir) throws IOException {
        if (!fileStore.exists(dstDir)) {
            fileStore.createDirectories(dstDir);
        }
        String dstPath = dstDir + "/" + file.getFileName().toString();
        try (FileInputStream in = new FileInputStream(file.toFile())) {
            fileStore.write(dstPath, in);
        }
        return dstPath;
    }

    private static String getDefaultPublicUrl(HttpServletRequest request, String path, String dataType) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/public/" + dataType + "/" + getDefaultPublicKey(path))
                .build()
                .toString();
    }

    public static String getDefaultPublicKey(String path) {
        String subPath = path.substring(PUBLIC_DIR.length());
        return Base64.getUrlEncoder().encodeToString(subPath.getBytes(StandardCharsets.UTF_8));
    }

    public static String getDefaultPublicUrlForImage(HttpServletRequest request, String path) {
        return getDefaultPublicUrl(request, path, "image");
    }

    public static String getDefaultPublicUrlForDocument(HttpServletRequest request, String path) {
        return getDefaultPublicUrl(request, path, "document");
    }

    public static String getDefaultPublicPath(String key) {
        String subPath = new String(Base64.getUrlDecoder().decode(key), StandardCharsets.UTF_8);
        return PUBLIC_DIR + subPath;
    }

    private static String getDefaultPrivateUrl(HttpServletRequest request, String path, String userId, String dataType) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath("/my/" + dataType + "/" + getDefaultPrivateKey(path, userId))
                .build()
                .toString();
    }

    public static String getDefaultPrivateKey(String path, String userId) {
        String subPath = path.substring(PRIVATE_DIR.length() + userId.length() + 1);
        return Base64.getUrlEncoder().encodeToString(subPath.getBytes(StandardCharsets.UTF_8));
    }

    public static String getDefaultPrivateUrlForImage(HttpServletRequest request, String path, String userId) {
        return getDefaultPrivateUrl(request, path, userId, "image");
    }

    public static String getDefaultPrivateUrlForDocument(HttpServletRequest request, String path, String userId) {
        return getDefaultPrivateUrl(request, path, userId, "document");
    }

    public static String getDefaultPrivatePath(String key, String userId) {
        String subPath = new String(Base64.getUrlDecoder().decode(key), StandardCharsets.UTF_8);
        return PRIVATE_DIR + userId + "/" + subPath;
    }

    public static String getKeyFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }
}
