package fun.freechat.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestImageUtils {
    public static byte[] imageData(String path) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = TestImageUtils.class.getResourceAsStream(path)) {
            assertThat(in).isNotNull();
            byte[] data = new byte[512];
            int n;
            while ((n = in.read(data)) != -1) {
                buffer.write(data, 0, n);
            }
        } catch (IOException e) {
            fail("", e.getMessage());
        }

        return buffer.toByteArray();
    }
}
