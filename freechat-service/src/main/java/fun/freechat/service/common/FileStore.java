package fun.freechat.service.common;

import org.apache.commons.io.IOUtils;

import java.io.*;
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
        try (InputStream stream = new ByteArrayInputStream(bytes)) {
            return write(path, stream, (long) bytes.length, Instant.now());
        }
    }
    OutputStream newOutputStream(String path) throws IOException;
    InputStream newInputStream(String path) throws IOException;
    default byte[] readBytes(String path) throws IOException {
        try (InputStream stream = newInputStream(path)) {
            return IOUtils.toByteArray(stream);
        } catch (NullPointerException ignored) {
            // ignored
            return null;
        }
    }
    default String readString(String path) throws IOException {
        try (InputStream stream = newInputStream(path)) {
            return IOUtils.toString(stream, StandardCharsets.UTF_8);
        } catch (NullPointerException ignored) {
            // ignored
            return null;
        }
    }
    boolean exists(String path);
    void delete(String path) throws IOException;
    default void tryDelete(String path) {
        try {
            delete(path);
        } catch (IOException ignored) {
            // ignored
        }
    }
    default void copy(String sourcePath, String destinationPath) throws IOException {
        try (InputStream stream = newInputStream(sourcePath)) {
            write(destinationPath, stream);
        } catch (NullPointerException ignored) {
            // ignored
        }
    }
    default void move(String sourcePath, String destinationPath) throws IOException {
        copy(sourcePath, destinationPath);
        delete(sourcePath);
    }
    long getLastModifiedTime(String path) throws IOException;
    void setLastModifiedTime(String path, long lastModifiedTime) throws IOException;
    default String getShareUrl(String path, int expireTimeInSecond) {
        return null;
    }
}
