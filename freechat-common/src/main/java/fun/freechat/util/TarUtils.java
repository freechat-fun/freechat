package fun.freechat.util;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import static java.nio.file.StandardOpenOption.WRITE;
import static org.apache.commons.compress.archivers.tar.TarArchiveOutputStream.LONGFILE_GNU;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_DIR;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_GNUTYPE_LONGNAME;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_NORMAL;
import static org.apache.commons.compress.archivers.tar.TarConstants.LF_OLDNORM;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.function.IOSupplier;

public class TarUtils {
    private static final int BUFFER_SIZE = 4096;
    private static final int RECORD_SIZE = 512;
    private static final int MAX_METADATA_BYTES = 64 * 1024;
    private static final int MAX_TRAILING_PADDING = 20 * RECORD_SIZE;

    public static final Limits DEFAULT_LIMITS = new Limits(256L * 1024 * 1024, 1024L * 1024 * 1024, 4096);

    public record Entry(String name, IOSupplier<InputStream> inputSupplier, long size) {
        public Entry {
            Objects.requireNonNull(name, "name");
            Objects.requireNonNull(inputSupplier, "inputSupplier");
            if (size < 0) {
                throw new IllegalArgumentException("Entry size must be known and non-negative");
            }
        }
    }

    public record Limits(long maxFileBytes, long maxTotalBytes, int maxEntries) {
        public Limits {
            if (maxFileBytes < 0 || maxTotalBytes < 0 || maxEntries < 0) {
                throw new IllegalArgumentException("Limits must be non-negative");
            }
        }
    }

    public static void compressGzip(List<Entry> entries, OutputStream out) throws IOException {
        try (out;
                GzipCompressorOutputStream gzipOut = new GzipCompressorOutputStream(out);
                TarArchiveOutputStream tarOut = new TarArchiveOutputStream(gzipOut, "UTF-8")) {
            tarOut.setLongFileMode(LONGFILE_GNU);
            Set<Path> names = new HashSet<>();
            byte[] buffer = new byte[BUFFER_SIZE];
            for (Entry entry : entries) {
                Path name = entryPath(entry.name());
                if (entry.name().endsWith("/") || !names.add(name)) {
                    throw new IOException("Directory or duplicate entry: " + entry.name());
                }
                TarArchiveEntry tarEntry = new TarArchiveEntry(name.toString().replace('\\', '/'));
                tarEntry.setSize(entry.size());
                tarOut.putArchiveEntry(tarEntry);
                try (InputStream in = entry.inputSupplier().get()) {
                    if (in == null) {
                        throw new IOException("Null input for entry: " + entry.name());
                    }
                    long remaining = entry.size();
                    while (remaining > 0) {
                        int len = in.read(buffer, 0, (int) Math.min(buffer.length, remaining));
                        if (len == -1) {
                            throw new EOFException("Entry shorter than declared: " + entry.name());
                        }
                        tarOut.write(buffer, 0, len);
                        remaining -= len;
                    }
                    if (in.read() != -1) {
                        throw new IOException("Entry longer than declared: " + entry.name());
                    }
                }
                tarOut.closeArchiveEntry();
            }
            tarOut.finish();
        }
    }

    public static void extractGzip(InputStream inputStream, Path dstDir) throws IOException {
        extractGzip(inputStream, dstDir, DEFAULT_LIMITS);
    }

    public static void extractGzip(InputStream inputStream, Path dstDir, Limits limits) throws IOException {
        Objects.requireNonNull(limits, "limits");
        try (inputStream;
                GzipCompressorInputStream gzipIn = GzipCompressorInputStream.builder()
                        .setInputStream(inputStream)
                        .setDecompressConcatenated(true)
                        .get();
                TarArchiveInputStream tarIn = new CheckedTarInputStream(gzipIn, limits)) {
            Path root = dstDir.toAbsolutePath().normalize();
            ensureDirectory(root);
            root = root.toRealPath();
            Set<Path> names = new HashSet<>();
            byte[] buffer = new byte[BUFFER_SIZE];
            long total = 0;
            TarArchiveEntry entry;
            while ((entry = tarIn.getNextEntry()) != null) {
                validateType(entry);
                checkSize(entry.getSize(), limits.maxFileBytes(), limits.maxTotalBytes() - total);
                Path relative = entryPath(entry.getName());
                Path target = root.resolve(relative).normalize();
                if (!target.startsWith(root) || !names.add(target)) {
                    throw new IOException("Escaping or duplicate entry: " + entry.getName());
                }
                Path directory = root;
                Path parent = entry.isDirectory() ? relative : relative.getParent();
                if (parent != null) {
                    for (Path component : parent) {
                        directory = directory.resolve(component);
                        ensureDirectory(directory);
                    }
                }
                if (entry.isDirectory()) {
                    continue;
                }
                OutputStream out = Files.newOutputStream(target, WRITE, CREATE_NEW, NOFOLLOW_LINKS);
                try (out) {
                    long size = 0;
                    int len;
                    while ((len = tarIn.read(buffer)) != -1) {
                        checkSize(len, limits.maxFileBytes() - size, limits.maxTotalBytes() - total);
                        out.write(buffer, 0, len);
                        size += len;
                        total += len;
                    }
                    if (size != entry.getSize()) {
                        throw new EOFException("Entry length mismatch: " + entry.getName());
                    }
                } catch (IOException | RuntimeException e) {
                    try {
                        Files.deleteIfExists(target);
                    } catch (IOException cleanup) {
                        e.addSuppressed(cleanup);
                    }
                    throw e;
                }
            }
            int padding = 0;
            int value;
            while ((value = gzipIn.read()) != -1) {
                if (value != 0 || ++padding > MAX_TRAILING_PADDING) {
                    throw new IOException("Unexpected trailing tar data");
                }
            }
        }
    }

    private static Path entryPath(String name) throws IOException {
        if (name.isEmpty() || name.startsWith("/") || name.indexOf('\\') >= 0 || name.indexOf(':') >= 0) {
            throw new IOException("Unsafe entry name: " + name);
        }
        try {
            Path path = Path.of(name);
            for (Path component : path) {
                if (component.toString().equals("..")) {
                    throw new IOException("Unsafe entry name: " + name);
                }
            }
            path = path.normalize();
            if (path.isAbsolute() || path.toString().isEmpty()) {
                throw new IOException("Unsafe entry name: " + name);
            }
            return path;
        } catch (InvalidPathException e) {
            throw new IOException("Invalid entry name: " + name, e);
        }
    }

    private static void ensureDirectory(Path directory) throws IOException {
        if (!Files.exists(directory, NOFOLLOW_LINKS)) {
            Path parent = directory.getParent();
            if (parent != null) {
                ensureDirectory(parent);
            }
            try {
                Files.createDirectory(directory);
            } catch (FileAlreadyExistsException ignored) {
                // Validate even when another creator won the race.
            }
        }
        if (!Files.isDirectory(directory, NOFOLLOW_LINKS)) {
            throw new IOException("Not a directory or is a symbolic link: " + directory);
        }
    }

    private static void checkSize(long size, long fileRemaining, long totalRemaining) throws IOException {
        if (size < 0 || size > fileRemaining || size > totalRemaining) {
            throw new IOException("Archive size limit exceeded");
        }
    }

    private static void validateType(TarArchiveEntry entry) throws IOException {
        byte type = entry.getLinkFlag();
        if (entry.isSparse()
                || (type != LF_NORMAL && type != LF_OLDNORM && type != LF_DIR)
                || (entry.isDirectory() && entry.getSize() != 0)) {
            throw new IOException("Unsupported tar entry: " + entry.getName());
        }
    }

    private static final class CheckedTarInputStream extends TarArchiveInputStream {
        private final Limits limits;
        private int entries;
        private int metadataRun;
        private boolean end;

        private CheckedTarInputStream(InputStream in, Limits limits) {
            super(in, RECORD_SIZE, RECORD_SIZE, "UTF-8");
            this.limits = limits;
        }

        @Override
        protected byte[] readRecord() throws IOException {
            byte[] record = super.readRecord();
            if (record == null) {
                throw new EOFException("Missing tar end records");
            }
            if (isEOFRecord(record)) {
                if (metadataRun != 0) {
                    throw new EOFException("Missing entry after long name");
                }
                end = true;
                return record;
            }
            if (end || entries >= limits.maxEntries()) {
                throw new IOException("Invalid tar ending or entry count limit exceeded");
            }
            ++entries;
            TarArchiveEntry header;
            try {
                header = new TarArchiveEntry(record);
            } catch (IllegalArgumentException e) {
                throw new IOException("Invalid tar header", e);
            }
            if (!header.isCheckSumOK()) {
                throw new IOException("Invalid tar header checksum");
            }
            byte type = header.getLinkFlag();
            if (type == LF_GNUTYPE_LONGNAME) {
                if (++metadataRun > 1 || header.getSize() > MAX_METADATA_BYTES || header.getSize() < 0) {
                    throw new IOException("Tar metadata limit exceeded");
                }
            } else {
                metadataRun = 0;
                validateType(header);
                checkSize(header.getSize(), limits.maxFileBytes(), limits.maxTotalBytes());
            }
            return record;
        }
    }
}
