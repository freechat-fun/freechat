package fun.freechat.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestResourceUtils {
    public static byte[] resourceData(String path) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (InputStream in = TestResourceUtils.class.getResourceAsStream(path)) {
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

    public static MultiValueMap<String, HttpEntity<?>> bodyFrom(String resourcePath) {
        Path path = Path.of(resourcePath);
        String filename = path.getFileName().toString();

        final byte[] fileContent = resourceData(resourcePath);

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", fileContent).filename(filename);

        return builder.build();
    }
}
