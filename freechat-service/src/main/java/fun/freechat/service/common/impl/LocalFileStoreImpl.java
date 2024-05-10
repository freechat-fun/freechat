package fun.freechat.service.common.impl;

import fun.freechat.service.common.FileStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service("localFileStore")
@Slf4j
@SuppressWarnings("unused")
public class LocalFileStoreImpl implements FileStore {
    @Value("${file.basePath}")
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

            if (Objects.isNull(regex)) {
                return listing.map(this::relativePath)
                        .map(Path::toString)
                        .toList();
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
        if (Objects.nonNull(lastModified)) {
            Files.setLastModifiedTime(filePath, FileTime.from(lastModified));
        }
        return size;
    }

    @Override
    public InputStream read(String path) throws IOException {
        return Files.newInputStream(toPath(path));
    }

    @Override
    public boolean exists(String path) {
        return Files.exists(toPath(path));
    }

    @Override
    public void delete(String path) throws IOException {
        Files.deleteIfExists(toPath(path));
    }

    @Override
    public long getLastModifiedTime(String path) throws IOException {
        Path filePath = toPath(path);
        FileTime lastModifiedTime = Files.getLastModifiedTime(filePath, LinkOption.NOFOLLOW_LINKS);
        return lastModifiedTime.toMillis();
    }
}
