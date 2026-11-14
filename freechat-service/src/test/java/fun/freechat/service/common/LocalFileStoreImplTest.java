package fun.freechat.service.common;

import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.common.impl.LocalFileStoreImpl;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalFileStoreImplTest {
    @TempDir
    Path directory;

    private Path root;
    private LocalFileStoreImpl files;

    @BeforeEach
    void configureLocalStorage() throws Exception {
        root = Files.createDirectory(directory.resolve("store"));
        files = new LocalFileStoreImpl();
        var basePath = LocalFileStoreImpl.class.getDeclaredField("basePathStr");
        basePath.setAccessible(true);
        basePath.set(files, root.toString());
        files.postConstruct();
    }

    @Test
    void acceptsRegularFilesButNotDirectories() throws Exception {
        Files.writeString(root.resolve("plain.txt"), "plain");
        Files.createDirectory(root.resolve("nested"));
        Files.createFile(root.resolve("nested/empty.txt"));

        assertTrue(files.isRegularFile("plain.txt"));
        assertTrue(files.isRegularFile("nested/empty.txt"));
        assertTrue(files.isRegularFile("nested/../plain.txt"));
        assertFalse(files.isRegularFile("nested"));
        assertFalse(files.isRegularFile(""));
        assertFalse(files.isRegularFile("."));
    }

    @Test
    void rejectsMissingInvalidAndNonDirectoryComponents() throws Exception {
        Files.writeString(root.resolve("plain.txt"), "plain");

        assertFalse(files.isRegularFile("missing.txt"));
        assertFalse(files.isRegularFile("missing/../plain.txt"));
        assertFalse(files.isRegularFile("plain.txt/../plain.txt"));
        assertFalse(files.isRegularFile(null));
        assertFalse(files.isRegularFile("invalid\u0000.txt"));
    }

    @Test
    void rejectsFileSymlinksEvenWhenTheirTargetsAreInsideRoot() throws Exception {
        Files.writeString(root.resolve("plain.txt"), "plain");
        Path outside = Files.writeString(directory.resolve("outside.txt"), "outside");
        Files.createSymbolicLink(root.resolve("inside-link"), root.resolve("plain.txt"));
        Files.createSymbolicLink(root.resolve("outside-link"), outside);
        Files.createSymbolicLink(root.resolve("dangling-link"), root.resolve("missing.txt"));

        assertFalse(files.isRegularFile("inside-link"));
        assertFalse(files.isRegularFile("outside-link"));
        assertFalse(files.isRegularFile("dangling-link"));
    }

    @Test
    void rejectsAncestorSymlinksIncludingThoseHiddenByParentTraversal() throws Exception {
        Files.createDirectory(root.resolve("nested"));
        Files.createDirectory(root.resolve("target"));
        Files.writeString(root.resolve("target/plain.txt"), "inside");
        Files.writeString(root.resolve("nested/plain.txt"), "plain");
        Path outside = Files.createDirectory(directory.resolve("outside"));
        Files.writeString(outside.resolve("plain.txt"), "outside");
        Files.createSymbolicLink(root.resolve("nested/inside-link"), root.resolve("target"));
        Files.createSymbolicLink(root.resolve("nested/outside-link"), outside);

        assertFalse(files.isRegularFile("nested/inside-link/plain.txt"));
        assertFalse(files.isRegularFile("nested/outside-link/plain.txt"));
        assertFalse(files.isRegularFile("nested/inside-link/../plain.txt"));
        assertFalse(files.isRegularFile("nested/outside-link/../plain.txt"));
    }

    @Test
    void rejectsAbsoluteOutsidePathsAndTraversalBeyondRoot() throws Exception {
        Path sibling = Files.createDirectory(directory.resolve("store-other"));
        Path outside = Files.writeString(sibling.resolve("plain.txt"), "outside");
        Files.writeString(root.resolve("plain.txt"), "inside");

        assertFalse(files.isRegularFile(outside.toString()));
        assertFalse(files.isRegularFile("../store-other/plain.txt"));
        assertFalse(files.isRegularFile("../store/plain.txt"));
    }
}
