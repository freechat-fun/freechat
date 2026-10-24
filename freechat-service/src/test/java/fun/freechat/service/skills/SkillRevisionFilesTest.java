package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.*;
import static org.junit.jupiter.api.Assertions.*;

import fun.freechat.service.common.FileStore;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SkillRevisionFilesTest {
    private static final String OWNER = "owner/../../秘密";
    private static final String SKILL = "1".repeat(32);
    private static final String REVISION = "2".repeat(32);
    private static final Map<String, String> INPUT = Map.of(
            "SKILL.md", "---\nname: writing\ndescription: Friendly writing\n---\nKeep paragraphs short.\r\n中文 café",
            "references/guide.txt", "Use everyday words.\n");

    @Test
    void exactPlainFilesSurviveNewInstanceWithNonlocalBackend() {
        RemoteFiles remote = new RemoteFiles();
        SkillRevisionFiles files = new SkillRevisionFiles(remote);
        var snapshot = SkillRevisionFiles.snapshot(INPUT);
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        assertEquals(INPUT, new SkillRevisionFiles(remote).read(OWNER, SKILL, REVISION, snapshot.manifest()));
        String root = SkillRevisionFiles.root(OWNER, SKILL, REVISION);
        assertTrue(root.matches("private/skills/[0-9a-f]{64}/[0-9a-f]{32}/[0-9a-f]{32}/"));
        assertFalse(root.contains(OWNER));
        INPUT.forEach((name, content) ->
                assertArrayEquals(content.getBytes(StandardCharsets.UTF_8), remote.objects.get(root + name)));
        assertFalse(snapshot.manifest().contains("everyday"));
        assertFalse(snapshot.manifest().contains("paragraphs"));
    }

    @Test
    void hiddenFilesNeverEnterStagingManifestsOrReviewedBundles() {
        var input = new HashMap<>(INPUT);
        input.put(".gitignore", null);
        input.put("references/.hidden.md", "broken\ud800");
        input.put(".config/install.sh", "bash secret-command");
        input.put("references/.cache/content.bin", "x".repeat(SkillRevisionFiles.MAX_TOTAL_BYTES + 1));
        for (int i = 0; i < 20; i++) {
            input.put(".hidden" + i + "/SKILL.md", "hidden instructions");
        }
        var snapshot = SkillRevisionFiles.snapshot(input);
        assertEquals(INPUT.keySet(), snapshot.bytes().keySet());
        assertEquals(SkillRevisionFiles.snapshot(INPUT).manifest(), snapshot.manifest());
        var remote = new RemoteFiles();
        var files = new SkillRevisionFiles(remote);
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        var read = files.read(OWNER, SKILL, REVISION, snapshot.manifest());
        assertEquals(INPUT, read);
        assertEquals(INPUT.size(), remote.objects.size());
        var validator = new SkillBundleValidator();
        assertEquals(validator.validate(INPUT), validator.validate(read));
    }

    @Test
    void visibleFilesStillObeyLimitsAndScriptRejectionAfterFiltering() {
        var input = new HashMap<>(INPUT);
        input.put(".env", "hidden");
        for (int i = INPUT.size(); i < SkillRevisionFiles.MAX_FILES; i++) {
            input.put("ref" + i + ".txt", "text");
        }
        assertEquals(
                SkillRevisionFiles.MAX_FILES,
                SkillRevisionFiles.snapshot(input).bytes().size());
        input.put("extra.txt", "text");
        assertCode(SKILL_INVALID_FORMAT, () -> SkillRevisionFiles.snapshot(input));
        var scripts = SkillRevisionFiles.snapshot(
                Map.of("SKILL.md", INPUT.get("SKILL.md"), ".gitignore", "ignored", "run.sh", "bash run"));
        var remote = new RemoteFiles();
        var files = new SkillRevisionFiles(remote);
        files.stage(OWNER, SKILL, REVISION, scripts, () -> {});
        assertCode(
                SKILL_UNSAFE_INSTRUCTIONS,
                () -> new SkillBundleValidator().validate(files.read(OWNER, SKILL, REVISION, scripts.manifest())));
        assertCode(
                SKILL_INVALID_FORMAT,
                () -> SkillRevisionFiles.snapshot(
                        Map.of("SKILL.md", INPUT.get("SKILL.md"), ".env", "ignored", "large.txt", "x".repeat(65537))));
    }

    @Test
    void hiddenOnlyInputsCannotProduceAnEmptyInstallableSnapshot() {
        assertCode(SKILL_INVALID_FORMAT, () -> SkillRevisionFiles.snapshot(Map.of(".hidden/SKILL.md", "text")));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                ".hidden/../run.sh",
                ".hidden/./file.txt",
                ".hidden//file.txt",
                "/.hidden/file.txt",
                ".hidden\\file.txt"
            })
    void malformedPathsCannotHideBehindAHiddenComponent(String path) {
        assertFalse(SkillBundleValidator.isHiddenPath(path));
        assertCode(
                SKILL_INVALID_FORMAT,
                () -> SkillRevisionFiles.snapshot(Map.of("SKILL.md", INPUT.get("SKILL.md"), path, "text")));
    }

    @Test
    void arbitraryTextFileNamesSurviveStorageAndCleanupUnchanged() {
        var input = Map.of(
                "SKILL.md",
                "# Plain instructions\nUse concise prose.",
                "LICENSE",
                "Copyright Example",
                "README.zh-CN.md",
                "说明",
                "config/rclone.conf.example",
                "[notes]\nkind = local",
                "references/写作 指南 (v2).data",
                "保持简洁。",
                "cafe\u0301.txt",
                "Text");
        var snapshot = SkillRevisionFiles.snapshot(input);
        var remote = new RemoteFiles();
        var files = new SkillRevisionFiles(remote);
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        var read = files.read(OWNER, SKILL, REVISION, snapshot.manifest());
        assertEquals(input, read);
        assertEquals(input, new SkillBundleValidator().validate(read).files());
        files.delete(OWNER, SKILL, REVISION, snapshot.manifest());
        assertTrue(remote.objects.isEmpty());
    }

    @Test
    void aliasAndFileDirectoryCollisionsFailBeforeStorage() {
        for (var input : List.of(
                Map.of("Café", "one", "Cafe\u0301", "two"),
                Map.of("Guide", "one", "guide/notes", "two"),
                Map.of("café", "one", "cafe\u0301/notes", "two"))) {
            assertCode(SKILL_INVALID_FORMAT, () -> SkillRevisionFiles.snapshot(input));
        }
    }

    @Test
    void rejectsSameLengthTamperExtraBytesAndMissingFile() {
        for (int mode = 0; mode < 3; mode++) {
            RemoteFiles remote = new RemoteFiles();
            SkillRevisionFiles files = new SkillRevisionFiles(remote);
            var snapshot = SkillRevisionFiles.snapshot(INPUT);
            files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
            String path = SkillRevisionFiles.root(OWNER, SKILL, REVISION) + "SKILL.md";
            if (mode == 0) {
                remote.objects.get(path)[0] = '?';
            } else if (mode == 1) {
                remote.objects.put(path, Arrays.copyOf(remote.objects.get(path), remote.objects.get(path).length + 1));
            } else {
                remote.objects.remove(path);
            }
            assertCode(SKILL_STORAGE_UNAVAILABLE, () -> files.read(OWNER, SKILL, REVISION, snapshot.manifest()));
        }
    }

    @Test
    void partialOrCorruptWriteCannotChangePreviousRevision() {
        RemoteFiles remote = new RemoteFiles();
        SkillRevisionFiles files = new SkillRevisionFiles(remote);
        var snapshot = SkillRevisionFiles.snapshot(INPUT);
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        Map<String, byte[]> before = new HashMap<>(remote.objects);
        remote.corruptWrites = true;
        assertCode(SKILL_STORAGE_UNAVAILABLE, () -> files.stage(OWNER, SKILL, "3".repeat(32), snapshot, () -> {}));
        before.forEach((path, bytes) -> assertArrayEquals(bytes, remote.objects.get(path)));
        assertEquals(INPUT, files.read(OWNER, SKILL, REVISION, snapshot.manifest()));
        assertCode(SKILL_STORAGE_UNAVAILABLE, () -> files.stage(OWNER, SKILL, REVISION, snapshot, () -> {}));
    }

    @Test
    void expiryFencePreventsAnyFurtherStagingWrites() {
        RemoteFiles remote = new RemoteFiles();
        var files = new SkillRevisionFiles(remote);
        assertCode(
                SKILL_DRAFT_EXPIRED,
                () -> files.stage(OWNER, SKILL, REVISION, SkillRevisionFiles.snapshot(INPUT), () -> {
                    throw new SkillException(SKILL_DRAFT_EXPIRED);
                }));
        assertTrue(remote.objects.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "../SKILL.md",
                "/SKILL.md",
                "references/../other.txt",
                "references//x.md",
                "references\\x.md",
                "references/%2e%2e.txt",
                "references/CON.txt"
            })
    void refusesTraversalAndUnsupportedPathsBeforeIo(String filename) {
        assertCode(SKILL_INVALID_FORMAT, () -> SkillRevisionFiles.snapshot(Map.of(filename, "text")));
    }

    @Test
    void malformedUtf16CannotBeSilentlyReplaced() {
        assertCode(SKILL_INVALID_FORMAT, () -> SkillRevisionFiles.snapshot(Map.of("SKILL.md", "broken\ud800")));
    }

    @Test
    void rejectsPoisonedManifestsBeforeDeletingAnyFiles() {
        RemoteFiles remote = new RemoteFiles();
        var files = new SkillRevisionFiles(remote);
        var snapshot = SkillRevisionFiles.snapshot(INPUT);
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        for (String bad : List.of(
                snapshot.manifest().replace("references/guide.txt", "../guide.txt"),
                snapshot.manifest().replace("references/guide.txt", ".hidden/guide.txt"),
                snapshot.manifest().replace("\"length\":", "\"length\":999999999,\"ignored\":"),
                "{\"SKILL.md\":{\"length\":-1,\"sha256\":\"" + "a".repeat(64) + "\"}}")) {
            assertCode(SKILL_STORAGE_UNAVAILABLE, () -> files.delete(OWNER, SKILL, REVISION, bad));
        }
        assertTrue(remote.deleted.isEmpty());
        assertCode(SKILL_STORAGE_UNAVAILABLE, () -> files.delete(OWNER, "../escape", REVISION, snapshot.manifest()));
    }

    @Test
    void cleanupOrdersUnicodeDirectoryAliasesByDepthRatherThanStringLength() {
        String root = SkillRevisionFiles.root(OWNER, SKILL, REVISION);
        var remote = new RemoteFiles() {
            private final java.util.Set<String> directories = new java.util.HashSet<>();

            private String canonical(String path) {
                return java.text.Normalizer.normalize(path, java.text.Normalizer.Form.NFC);
            }

            @Override
            public void createDirectories(String path) {
                String normalized = canonical(path);
                for (int end = normalized.length();
                        end >= root.length() - 1;
                        end = normalized.lastIndexOf('/', end - 1)) {
                    directories.add(normalized.substring(0, end));
                }
            }

            @Override
            public boolean exists(String path) {
                return super.exists(path) || directories.contains(canonical(path));
            }

            @Override
            public void delete(String path) {
                String normalized = canonical(path);
                if (directories.contains(normalized)) {
                    if (directories.stream().anyMatch(child -> child.startsWith(normalized + "/"))) {
                        throw new IllegalStateException("Directory is not empty");
                    }
                    directories.remove(normalized);
                }
                super.delete(path);
            }
        };
        var files = new SkillRevisionFiles(remote);
        var snapshot = SkillRevisionFiles.snapshot(
                Map.of("SKILL.md", "Plain instructions", "e\u0301e\u0301e\u0301/a", "One", "ééé/b/c", "Two"));
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        assertDoesNotThrow(() -> files.delete(OWNER, SKILL, REVISION, snapshot.manifest()));
        assertTrue(remote.objects.isEmpty());
        assertFalse(remote.exists(root.substring(0, root.length() - 1)));
        assertDoesNotThrow(() -> files.delete(OWNER, SKILL, REVISION, snapshot.manifest()));
    }

    @Test
    void cleanupDeletesOnlyKnownRevisionFilesAndCanRepeat() {
        RemoteFiles remote = new RemoteFiles();
        var files = new SkillRevisionFiles(remote);
        var snapshot = SkillRevisionFiles.snapshot(INPUT);
        files.stage(OWNER, SKILL, REVISION, snapshot, () -> {});
        files.stage(OWNER, SKILL, "4".repeat(32), snapshot, () -> {});
        remote.objects.put("private/unrelated.txt", new byte[] {1});
        files.delete(OWNER, SKILL, REVISION, snapshot.manifest());
        files.delete(OWNER, SKILL, REVISION, snapshot.manifest());
        assertEquals(INPUT, files.read(OWNER, SKILL, "4".repeat(32), snapshot.manifest()));
        assertTrue(remote.objects.containsKey("private/unrelated.txt"));
        assertTrue(remote.deleted.stream()
                .allMatch(path -> path.startsWith(SkillRevisionFiles.root(OWNER, SKILL, REVISION))));
    }

    private static void assertCode(SkillException.Code code, org.junit.jupiter.api.function.Executable operation) {
        SkillException error = assertThrows(SkillException.class, operation);
        assertEquals(code, error.code());
        assertEquals(code.name(), error.getMessage());
        assertNull(error.getCause());
    }

    /** Object-store semantics: no local paths, rename, listing, compression, or directory objects. */
    private static class RemoteFiles implements FileStore {
        final Map<String, byte[]> objects = new HashMap<>();
        final List<String> deleted = new ArrayList<>();
        boolean corruptWrites;

        @Override
        public Path toPath(String path) {
            throw new AssertionError("local paths forbidden");
        }

        @Override
        public void close() {}

        @Override
        public long size(String path) {
            throw new AssertionError("size hints are not authoritative");
        }

        @Override
        public List<String> list(String path, String regex, boolean recursive) {
            throw new AssertionError("listing forbidden");
        }

        @Override
        public void createDirectories(String path) {}

        @Override
        public long write(String path, InputStream stream, Long size, Instant modified) {
            throw new AssertionError("unexpected stream write");
        }

        @Override
        public long write(String path, byte[] bytes, Instant modified) {
            byte[] stored = bytes.clone();
            if (corruptWrites && stored.length > 0) {
                stored[0] ^= 1;
            }
            objects.put(path, stored);
            return bytes.length;
        }

        @Override
        public OutputStream newOutputStream(String path) {
            throw new AssertionError("unexpected output stream");
        }

        @Override
        public InputStream newInputStream(String path) throws IOException {
            if (!objects.containsKey(path)) {
                throw new IOException("private backend details");
            }
            return new ByteArrayInputStream(objects.get(path));
        }

        @Override
        public boolean exists(String path) {
            return objects.containsKey(path);
        }

        @Override
        public void delete(String path) {
            deleted.add(path);
            objects.remove(path);
        }

        @Override
        public void move(String source, String target) {
            throw new AssertionError("non-atomic move forbidden");
        }

        @Override
        public long getLastModifiedTime(String path) {
            throw new AssertionError("not needed");
        }

        @Override
        public void setLastModifiedTime(String path, long time) {
            throw new AssertionError("not needed");
        }
    }
}
