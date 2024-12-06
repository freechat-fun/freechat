package fun.freechat.util;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.UUID;

public class IdUtils {
    public static String newId() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String newShortId() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidBytes = new byte[16];
        ByteBuffer.wrap(uuidBytes)
                .putLong(uuid.getMostSignificantBits())
                .putLong(uuid.getLeastSignificantBits());
        return Base64.getUrlEncoder().withoutPadding().encodeToString(uuidBytes);
    }
}
