package fun.freechat.util;

import java.util.UUID;

public class IdUtils {
    public static String newId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
