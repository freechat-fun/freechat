package fun.freechat.service.common;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;

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
    default long write(String path, String content) throws IOException {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return write(path, new ByteArrayInputStream(bytes), (long) bytes.length, Instant.now());
    }
    InputStream read(String path) throws IOException;
    default byte[] readBytes(String path) throws IOException {
        InputStream stream = read(path);
        return stream != null ? IOUtils.toByteArray(stream) : null;
    }
    default String readString(String path) throws IOException {
        InputStream stream = read(path);
        return stream != null ? IOUtils.toString(stream, StandardCharsets.UTF_8) : null;
    }
    boolean exists(String path);
    void delete(String path) throws IOException;
    default void tryDelete(String path) {
        try {
            delete(path);
        } catch (IOException ignored) {
        }
    }
    default void copy(String sourcePath, String destinationPath) throws IOException {
        write(destinationPath, read(sourcePath));
    }
    default void move(String sourcePath, String destinationPath) throws IOException {
        copy(sourcePath, destinationPath);
        delete(sourcePath);
    }
    long getLastModifiedTime(String path) throws IOException;
    default String getShareUrl(String path, int expireTimeInSecond) {
        return null;
    }
}
