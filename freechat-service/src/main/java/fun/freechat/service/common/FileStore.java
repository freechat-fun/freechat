package fun.freechat.service.common;

import org.apache.commons.io.IOUtils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public interface FileStore extends Closeable {
    Path toPath(String path);
    void close();
    long size(String path) throws IOException;
    List<String> list(String path, String regex, boolean recursive) throws IOException;
    void createDirectories(String path) throws IOException;
    long write(String path, InputStream stream, Long contentLength, Instant lastModified) throws IOException;
    default long write(String path, InputStream stream) throws IOException {
        return write(path, stream, null, null);
    }
    InputStream read(String path) throws IOException;
    default byte[] readBytes(String path) throws IOException {
        InputStream stream = read(path);
        return Objects.nonNull(stream) ? IOUtils.toByteArray(stream) : new byte[0];
    }
    boolean exists(String path);
    void delete(String path) throws IOException;
    default void tryDelete(String path) {
        try {
            delete(path);
        } catch (IOException ignored) {
        }
    }
    default long copy(String sourcePath, String destinationPath) throws IOException {
        return write(destinationPath, read(sourcePath));
    }
    long getLastModifiedTime(String path) throws IOException;
    default String getShareUrl(String path, int expireTimeInSecond) {
        return null;
    }
}
