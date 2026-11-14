package fun.freechat.service.common.impl;

import fun.freechat.service.common.FileStore;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("localFileStore")
@Slf4j
@SuppressWarnings("unused")
public class LocalFileStoreImpl implements FileStore {
    @Value("${disk.workdir}")
    private String basePathStr;

    private Path basePath;

    private Path relativePath(Path path) {
        return basePath.relativize(path);
    }

    @PostConstruct
    public void postConstruct() {
        basePath = Path.of(basePathStr);
        log.info("File store base path: {}", basePathStr);
    }

    @Override
    public Path toPath(String path) {
        return StringUtils.isBlank(basePathStr) ? Paths.get(path) : Paths.get(basePathStr, path);
    }

    @Override
    public void close() {}

    @Override
    public long size(String path) throws IOException {
        return Files.size(toPath(path));
    }

    @Override
    public List<String> list(String path, String regex, boolean recursive) throws IOException {
        Stream<Path> listing = null;
        try {
            if (recursive) {
                listing = Files.walk(toPath(path)).filter(Files::isRegularFile);
            } else {
                listing = Files.list(toPath(path));
            }

            if (regex == null) {
                return listing.map(this::relativePath).map(Path::toString).toList();
            }
            Pattern pattern = Pattern.compile(regex);
            return listing.map(this::relativePath)
                    .map(Path::toString)
                    .filter(fileName -> pattern.matcher(fileName).find())
                    .toList();
        } finally {
            Optional.ofNullable(listing).ifPresent(Stream::close);
        }
    }

    @Override
    public void createDirectories(String path) throws IOException {
        Files.createDirectories(toPath(path));
    }

    @Override
    public long write(String path, InputStream stream, Long contentLength, Instant lastModified) throws IOException {
        Path filePath = toPath(path);
        long size = Files.copy(stream, filePath, StandardCopyOption.REPLACE_EXISTING);
        if (lastModified != null) {
            Files.setLastModifiedTime(filePath, FileTime.from(lastModified));
        }
        return size;
    }

    @Override
    public long write(String path, byte[] bytes, Instant lastModified) throws IOException {
        Path filePath = toPath(path);
        Files.write(
                filePath,
                bytes,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.CREATE);
        if (lastModified != null) {
            Files.setLastModifiedTime(filePath, FileTime.from(lastModified));
        }
        return bytes.length;
    }

    @Override
    public OutputStream newOutputStream(String path) throws IOException {
        Path filePath = toPath(path);
        return Files.newOutputStream(filePath);
    }

    @Override
    public InputStream newInputStream(String path) throws IOException {
        return Files.newInputStream(toPath(path));
    }

    @Override
    public byte[] readBytes(String path) throws IOException {
        return Files.readAllBytes(toPath(path));
    }

    @Override
    public String readString(String path) throws IOException {
        return Files.readString(toPath(path), StandardCharsets.UTF_8);
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(toPath(path));
    }

    @Override
    public boolean isRegularFile(String path) {
        try {
            return Files.isRegularFile(regularFilePath(path, false), LinkOption.NOFOLLOW_LINKS);
        } catch (IOException | InvalidPathException | SecurityException ignored) {
            return false;
        }
    }

    @Override
    public void writeRegularFile(String path, byte[] bytes) throws IOException {
        Files.write(
                regularFilePath(path, true),
                bytes,
                StandardOpenOption.WRITE,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                LinkOption.NOFOLLOW_LINKS);
    }

    private Path regularFilePath(String path, boolean createParents) throws IOException {
        if (path == null) throw new IOException("Invalid file path");
        Path relative = Path.of(path);
        if (relative.isAbsolute()) throw new IOException("Expected a relative file path");
        Path root = toPath("").toRealPath();
        Path file = root;
        int index = 0;
        for (Path component : relative) {
            if (!Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) throw new IOException("Invalid parent directory");
            // Check each component before a later ".." can remove it from the path.
            file = file.resolve(component).normalize();
            if (!file.startsWith(root) || Files.isSymbolicLink(file)) throw new IOException("Unsafe file path");
            if (++index < relative.getNameCount() && createParents && !Files.exists(file, LinkOption.NOFOLLOW_LINKS)) {
                try {
                    Files.createDirectory(file);
                } catch (FileAlreadyExistsException ignored) {
                    if (!Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS))
                        throw new IOException("Invalid parent directory");
                }
            }
        }
        return file;
    }

    @Override
    public void delete(String path) throws IOException {
        Files.deleteIfExists(toPath(path));
    }

    @Override
    public void copy(String sourcePath, String destinationPath) throws IOException {
        Files.copy(toPath(sourcePath), toPath(destinationPath), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public void move(String sourcePath, String destinationPath) throws IOException {
        Files.move(toPath(sourcePath), toPath(destinationPath), StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public long getLastModifiedTime(String path) throws IOException {
        Path filePath = toPath(path);
        FileTime lastModifiedTime = Files.getLastModifiedTime(filePath, LinkOption.NOFOLLOW_LINKS);
        return lastModifiedTime.toMillis();
    }

    @Override
    public void setLastModifiedTime(String path, long lastModifiedTime) throws IOException {
        Path filePath = toPath(path);
        Files.setLastModifiedTime(filePath, FileTime.fromMillis(lastModifiedTime));
    }
}
