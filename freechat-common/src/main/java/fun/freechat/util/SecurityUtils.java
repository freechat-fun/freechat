package fun.freechat.util;

import org.apache.commons.lang3.StringUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class SecurityUtils {
    public static String filterPath(String path) {
        if (StringUtils.isEmpty(path)) {
            return path;
        }

        String temp = path;
        while (temp.indexOf('%') != -1) {
            try {
                temp = URLDecoder.decode(temp, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return null;
            }
        }

        if (temp.contains("..") || temp.charAt(0) == '/') {
            return null;
        }

        return path;
    }
}
