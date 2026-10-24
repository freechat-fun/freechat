package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_STORAGE_UNAVAILABLE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.service.common.FileStore;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/** Bounded exact-byte I/O. No local paths, moves, compression, directory listing, or recursive deletion. */
final class SkillRevisionFiles {
    // Storage envelopes, independently enforced even for corrupted database metadata.
    static final int MAX_FILES = SkillBundleValidator.MAX_FILES;
    static final int MAX_FILE_BYTES = SkillBundleValidator.MAX_REFERENCE_BYTES;
    static final int MAX_TOTAL_BYTES = SkillBundleValidator.MAX_TOTAL_BYTES;
    private static final ObjectMapper JSON =
            new ObjectMapper().enable(com.fasterxml.jackson.core.JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    private static final TypeReference<TreeMap<String, Entry>> MANIFEST_TYPE = new TypeReference<>() {};
    private final FileStore store;

    record Entry(int length, String sha256) {}

    record Snapshot(Map<String, byte[]> bytes, String manifest) {}

    SkillRevisionFiles(FileStore store) {
        this.store = store;
    }

    static Snapshot snapshot(Map<String, String> input) {
        if (input == null || input.isEmpty()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        var bytes = new TreeMap<String, byte[]>();
        var entries = new TreeMap<String, Entry>();
        int total = 0;
        for (var file : input.entrySet()) {
            if (SkillBundleValidator.isHiddenPath(file.getKey())) {
                continue;
            }
            if (bytes.size() >= MAX_FILES
                    || !safeFilename(file.getKey())
                    || file.getValue() == null
                    || file.getValue().length() > MAX_FILE_BYTES) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            byte[] content = file.getValue().getBytes(StandardCharsets.UTF_8);
            if (!decode(content).equals(file.getValue()) || content.length > MAX_FILE_BYTES) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            total += content.length;
            if (total > MAX_TOTAL_BYTES) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            bytes.put(file.getKey(), content);
            entries.put(file.getKey(), new Entry(content.length, hash(content)));
        }
        if (bytes.isEmpty()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        SkillBundleValidator.validateFilePaths(bytes.keySet());
        try {
            return new Snapshot(Collections.unmodifiableMap(bytes), JSON.writeValueAsString(entries));
        } catch (IOException ignored) {
            throw unavailable();
        }
    }

    void stage(String owner, String skill, String revision, Snapshot snapshot, Runnable fence) {
        String root = root(owner, skill, revision);
        try {
            for (var entry : snapshot.bytes().entrySet()) {
                fence.run();
                String path = root + entry.getKey();
                store.createDirectories(path.substring(0, path.lastIndexOf('/')));
                // IDs are server generated, never reused. A collision is a failure, never an overwrite.
                if (store.exists(path) || store.write(path, entry.getValue()) != entry.getValue().length) {
                    throw unavailable();
                }
            }
            var actual = read(owner, skill, revision, snapshot.manifest());
            for (var entry : snapshot.bytes().entrySet()) {
                if (!Arrays.equals(entry.getValue(), actual.get(entry.getKey()).getBytes(StandardCharsets.UTF_8))) {
                    throw unavailable();
                }
            }
        } catch (IOException ignored) {
            throw unavailable();
        } catch (SkillException failure) {
            throw failure;
        } catch (RuntimeException ignored) {
            throw unavailable();
        }
    }

    Map<String, String> read(String owner, String skill, String revision, String manifest) {
        String root = root(owner, skill, revision);
        var entries = manifest(manifest);
        var result = new TreeMap<String, String>();
        try {
            for (var entry : entries.entrySet()) {
                byte[] bytes;
                try (var stream = store.newInputStream(root + entry.getKey())) {
                    if (stream == null) {
                        throw unavailable();
                    }
                    // Read one extra byte, not an unbounded read or an untrusted backend size hint.
                    bytes = stream.readNBytes(entry.getValue().length() + 1);
                }
                if (bytes.length != entry.getValue().length()
                        || !hash(bytes).equals(entry.getValue().sha256())) {
                    throw unavailable();
                }
                result.put(entry.getKey(), decode(bytes));
            }
            return Collections.unmodifiableMap(result);
        } catch (IOException | RuntimeException ignored) {
            throw unavailable();
        }
    }

    void delete(String owner, String skill, String revision, String manifest) {
        String root = root(owner, skill, revision);
        if ("{}".equals(manifest)) return;
        var entries = manifest(manifest); // Validate the entire manifest before touching any files.
        try {
            var directories = new TreeSet<String>(Comparator.comparingLong(
                            (String path) -> path.chars().filter(c -> c == '/').count())
                    .reversed()
                    .thenComparing(Comparator.naturalOrder()));
            for (String name : entries.keySet()) {
                String path = root + name;
                // FileStore implementations may differ on deleting a missing object.
                if (store.exists(path)) {
                    store.delete(path);
                }
                for (int slash = name.lastIndexOf('/'); slash >= 0; slash = name.lastIndexOf('/', slash - 1)) {
                    directories.add(root + name.substring(0, slash));
                }
            }
            directories.add(root.substring(0, root.length() - 1));
            // Only exact, server-owned revision directories. Never delete an owner or skill prefix.
            for (String directory : directories) {
                if (store.exists(directory)) {
                    store.delete(directory);
                }
            }
        } catch (IOException | RuntimeException ignored) {
            throw unavailable();
        }
    }

    static String root(String owner, String skill, String revision) {
        if (owner == null || owner.isEmpty() || owner.length() > 32 || !opaqueId(skill) || !opaqueId(revision)) {
            throw unavailable();
        }
        // A server-side encoding is independent of OS/path semantics and does not expose raw owner IDs.
        return "private/skills/" + hash(owner.getBytes(StandardCharsets.UTF_8)) + "/" + skill + "/" + revision + "/";
    }

    static boolean opaqueId(String value) {
        return value != null && value.matches("[0-9a-f]{32}");
    }

    static boolean safeFilename(String name) {
        try {
            SkillBundleValidator.validatePath(name);
            return true;
        } catch (SkillException ignored) {
            return false;
        }
    }

    private static Map<String, Entry> manifest(String text) {
        try {
            if (text == null || text.length() > 32 * 512) {
                throw unavailable();
            }
            var entries = JSON.readValue(text, MANIFEST_TYPE);
            if (entries == null || entries.isEmpty() || entries.size() > MAX_FILES) {
                throw unavailable();
            }
            SkillBundleValidator.validateFilePaths(entries.keySet());
            long total = 0;
            for (var entry : entries.entrySet()) {
                Entry value = entry.getValue();
                if (!safeFilename(entry.getKey())
                        || value == null
                        || value.length() < 0
                        || value.length() > MAX_FILE_BYTES
                        || value.sha256() == null
                        || !value.sha256().matches("[0-9a-f]{64}")) {
                    throw unavailable();
                }
                total += value.length();
            }
            if (total > MAX_TOTAL_BYTES) {
                throw unavailable();
            }
            return entries;
        } catch (IOException | RuntimeException ignored) {
            throw unavailable();
        }
    }

    private static String decode(byte[] bytes) {
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException ignored) {
            throw unavailable();
        }
    }

    private static String hash(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException ignored) {
            throw unavailable();
        }
    }

    static SkillException unavailable() {
        return new SkillException(SKILL_STORAGE_UNAVAILABLE);
    }
}
