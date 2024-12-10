package fun.freechat.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

public class SecurityUtils {
    private static final Logger log = LoggerFactory.getLogger(HttpUtils.class);

    public static String filterPath(String path) throws AccessDeniedException {
        if (StringUtils.isEmpty(path)) {
            return path;
        }

        String temp = path;
        while (temp.indexOf('%') != -1) {
            try {
                temp = URLDecoder.decode(temp, StandardCharsets.UTF_8);
            } catch (Exception e) {
                log.warn("Invalid path: {}", path, e);
                throw new IllegalArgumentException("Invalid path: " + path);
            }
        }

        if (temp.contains("..") || temp.charAt(0) == '/') {
            log.warn("Invalid path: {}", path);
            throw new AccessDeniedException("Invalid path: " + path);
        }

        return path;
    }
}
