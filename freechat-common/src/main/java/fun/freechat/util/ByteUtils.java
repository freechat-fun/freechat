package fun.freechat.util;

public class ByteUtils {
    public static boolean isTrue(Byte field) {
        return (Byte.valueOf((byte) 1).equals(field));
    }

    public static boolean isFalse(Byte field) {
        return (Byte.valueOf((byte) 0).equals(field));
    }

    public static boolean isNotTrue(Byte field) {
        return !isTrue(field);
    }

    public static boolean isNotFalse(Byte field) {
        return !isFalse(field);
    }
}
