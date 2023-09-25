package fun.freechat.util;

public class TestCommonUtils {
    public static void waitAWhile() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
