package fun.freechat.service.tools.workspace;

import fun.freechat.service.tools.ToolArguments;
import fun.freechat.service.tools.ToolContext;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.SecureDirectoryStream;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/** Descriptor-relative operations, with no insecure provider fallback. Caller holds the worker lock. */
final class SecureWorkspace implements AutoCloseable {
    static final int MAX_FILE_BYTES = 131_072;
    static final int MAX_ENTRIES = 1000;
    private static final LinkOption NOFOLLOW = LinkOption.NOFOLLOW_LINKS;
    private static final String TEMP_PREFIX = ".freechat-write-";
    final Path rootPath;
    final Object rootKey;
    private final SecureDirectoryStream<Path> root;

    SecureWorkspace(Path trustedRoot) throws IOException {
        if (trustedRoot == null || !trustedRoot.isAbsolute() || Files.isSymbolicLink(trustedRoot)) {
            throw ToolArguments.invalid();
        }
        // Trusted ancestors may be platform aliases (e.g. /var on macOS). Model paths never use realpath.
        rootPath = trustedRoot.toRealPath();
        SecureDirectoryStream<Path> current = secure(Files.newDirectoryStream(rootPath.getRoot()));
        try {
            for (Path component : rootPath) {
                SecureDirectoryStream<Path> next = current.newDirectoryStream(component, NOFOLLOW);
                try {
                    current.close();
                } catch (IOException e) {
                    next.close();
                    throw e;
                }
                current = next;
            }
            rootKey = directoryAttributes(current).fileKey();
            if (rootKey == null) {
                throw failure("Workspace file identities unavailable");
            }
            root = current;
        } catch (IOException | RuntimeException e) {
            current.close();
            throw e;
        }
    }

    private static SecureDirectoryStream<Path> secure(DirectoryStream<Path> stream) throws IOException {
        if (stream instanceof SecureDirectoryStream<Path> secure) {
            return secure;
        }
        stream.close();
        throw failure("Secure workspace directory handles unavailable");
    }

    static Path path(String value, boolean directory) {
        if (directory && value.isEmpty()) {
            return Path.of("");
        }
        if (value.isEmpty()
                || value.length() > 512
                || value.startsWith("/")
                || value.contains("\\")
                || value.contains(":")) {
            throw ToolArguments.invalid();
        }
        String[] parts = value.split("/", -1);
        if (parts.length > 8) {
            throw ToolArguments.invalid();
        }
        for (String part : parts) {
            if (part.isEmpty()
                    || part.equals(".")
                    || part.equals("..")
                    || part.startsWith(TEMP_PREFIX)
                    || part.codePoints().anyMatch(Character::isISOControl)) {
                throw ToolArguments.invalid();
            }
        }
        Path path = Path.of(value);
        if (path.isAbsolute()) {
            throw ToolArguments.invalid();
        }
        return path;
    }

    SecureDirectoryStream<Path> directory(Path relative) throws IOException {
        SecureDirectoryStream<Path> current = root.newDirectoryStream(Path.of("."), NOFOLLOW);
        try {
            if (!relative.toString().isEmpty()) {
                for (Path component : relative) {
                    SecureDirectoryStream<Path> next = current.newDirectoryStream(component, NOFOLLOW);
                    try {
                        current.close();
                    } catch (IOException e) {
                        next.close();
                        throw e;
                    }
                    current = next;
                }
            }
            return current;
        } catch (IOException | RuntimeException e) {
            current.close();
            throw e;
        }
    }

    static BasicFileAttributes directoryAttributes(SecureDirectoryStream<Path> directory) throws IOException {
        return directory.getFileAttributeView(BasicFileAttributeView.class).readAttributes();
    }

    static BasicFileAttributes attributes(SecureDirectoryStream<Path> directory, Path name) throws IOException {
        BasicFileAttributes attributes = directory
                .getFileAttributeView(name, BasicFileAttributeView.class, NOFOLLOW)
                .readAttributes();
        if (attributes.isSymbolicLink()
                || (!attributes.isDirectory() && !attributes.isRegularFile())
                || attributes.fileKey() == null) {
            throw failure("Unsafe workspace entry");
        }
        return attributes;
    }

    static List<Path> names(SecureDirectoryStream<Path> directory, Counter counter, ToolContext context) {
        List<Path> names = new ArrayList<>();
        for (Path entry : directory) {
            context.check();
            if (++counter.entries > MAX_ENTRIES) {
                // Never return an arbitrary filesystem-order subset as a sorted page.
                throw failure("Workspace traversal entry limit exceeded");
            }
            Path name = entry.getFileName();
            path(name.toString(), false);
            names.add(name);
        }
        names.sort(Comparator.comparing(Path::toString));
        return names;
    }

    Snapshot read(Path relative, ToolContext context) throws IOException {
        try (var parent = directory(parentOf(relative))) {
            return read(parent, relative.getFileName(), context);
        }
    }

    static Snapshot read(SecureDirectoryStream<Path> parent, Path name, ToolContext context) throws IOException {
        context.check();
        BasicFileAttributes before = attributes(parent, name);
        if (!before.isRegularFile()) {
            throw failure("Workspace entry is not a regular file");
        }
        if (before.size() > MAX_FILE_BYTES) {
            throw failure("Workspace file size limit exceeded");
        }
        byte[] bytes;
        try (SeekableByteChannel channel = parent.newByteChannel(name, Set.of(StandardOpenOption.READ, NOFOLLOW))) {
            if (channel.size() > MAX_FILE_BYTES) {
                throw failure("Workspace file size limit exceeded");
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteBuffer buffer = ByteBuffer.allocate(4096);
            while (true) {
                context.check();
                int read = channel.read(buffer);
                if (read < 0) {
                    break;
                }
                if (output.size() + read > MAX_FILE_BYTES) {
                    throw failure("Workspace file size limit exceeded");
                }
                output.write(buffer.array(), 0, read);
                buffer.clear();
            }
            bytes = output.toByteArray();
        }
        if (!same(before, attributes(parent, name)) || bytes.length != before.size()) {
            throw failure("Workspace file changed during read");
        }
        return new Snapshot(bytes, decode(bytes), hash(bytes), before);
    }

    String write(Path relative, byte[] content, String expected, ToolContext context) throws IOException {
        if (content.length > MAX_FILE_BYTES) {
            throw failure("Workspace file size limit exceeded");
        }
        Path parentPath = parentOf(relative);
        Path name = relative.getFileName();
        try (var parent = directory(parentPath)) {
            Object parentKey = directoryAttributes(parent).fileKey();
            Snapshot original = existing(parent, name, context);
            if ((original == null && expected != null)
                    || (original != null && !original.version().equals(expected))) {
                throw failure("Workspace version conflict");
            }
            Set<PosixFilePermission> permissions = original == null
                    ? Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE)
                    : parent.getFileAttributeView(name, PosixFileAttributeView.class, NOFOLLOW)
                            .readAttributes()
                            .permissions();
            Path temporary = Path.of(TEMP_PREFIX + UUID.randomUUID());
            boolean created = false;
            try {
                context.check();
                try (SeekableByteChannel channel = parent.newByteChannel(
                        temporary,
                        Set.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE, NOFOLLOW),
                        PosixFilePermissions.asFileAttribute(
                                Set.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE)))) {
                    created = true;
                    ByteBuffer buffer = ByteBuffer.wrap(content);
                    while (buffer.hasRemaining()) {
                        context.check();
                        channel.write(buffer);
                    }
                    if (channel instanceof FileChannel file) {
                        file.force(true);
                    }
                }
                // Recheck the parent identity and target immediately before descriptor-relative rename.
                try (var currentParent = directory(parentPath)) {
                    if (!parentKey.equals(directoryAttributes(currentParent).fileKey())) {
                        throw failure("Workspace version conflict");
                    }
                }
                Snapshot current = existing(parent, name, context);
                if ((original == null) != (current == null)
                        || (original != null
                                && (!same(original.attributes(), current.attributes())
                                        || !original.version().equals(current.version())))) {
                    throw failure("Workspace version conflict");
                }
                parent.getFileAttributeView(temporary, PosixFileAttributeView.class, NOFOLLOW)
                        .setPermissions(permissions);
                context.check();
                // Atomic rename on this required secure provider; NOT an OS-level hash CAS.
                parent.move(temporary, parent, name);
                created = false;
                return hash(content);
            } finally {
                if (created) {
                    parent.deleteFile(temporary);
                }
            }
        }
    }

    private static Snapshot existing(SecureDirectoryStream<Path> parent, Path name, ToolContext context)
            throws IOException {
        try {
            return read(parent, name, context);
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    private static boolean same(BasicFileAttributes first, BasicFileAttributes second) {
        return first.fileKey().equals(second.fileKey())
                && first.size() == second.size()
                && first.lastModifiedTime().equals(second.lastModifiedTime());
    }

    static Path parentOf(Path path) {
        return path.getParent() == null ? Path.of("") : path.getParent();
    }

    static String decode(byte[] bytes) {
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException e) {
            throw failure("Workspace file is not valid UTF-8");
        }
    }

    static String hash(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw failure("Workspace hashing unavailable");
        }
    }

    static IllegalStateException failure(String message) {
        return new IllegalStateException(message);
    }

    @Override
    public void close() throws IOException {
        root.close();
    }

    static final class Counter {
        int entries;
    }

    record Snapshot(byte[] bytes, String text, String version, BasicFileAttributes attributes) {}
}
