package fun.freechat.service.tools.network;

import static fun.freechat.service.skills.SkillBundleValidator.MAX_FILES;
import static fun.freechat.service.skills.SkillBundleValidator.MAX_REFERENCE_BYTES;
import static fun.freechat.service.skills.SkillBundleValidator.MAX_SKILL_BYTES;
import static fun.freechat.service.skills.SkillBundleValidator.MAX_TOTAL_BYTES;
import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_AMBIGUOUS;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_NOT_ALLOWED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_UNAVAILABLE;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSUPPORTED_CONTENT;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.service.skills.SkillBundleValidator;
import fun.freechat.service.skills.SkillException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/** Read-only GitHub snapshots. Web refs support branch names (including slashes) and full commit SHAs, not tags. */
final class GithubSkillSource {
    private static final int MAX_METADATA_BYTES = 256 * 1024;
    private static final int MAX_TREE_BYTES = 1024 * 1024;
    private static final int MAX_TREE_ENTRIES = 4096;
    private static final int MAX_REF_LENGTH = 255;
    private static final Set<String> JSON_TYPES = Set.of("application/json", "application/vnd.github+json");
    private static final ObjectMapper JSON = new ObjectMapper(JsonFactory.builder()
            .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
            .streamReadConstraints(StreamReadConstraints.builder()
                    .maxNestingDepth(16)
                    .maxStringLength(MAX_TREE_BYTES)
                    .maxNumberLength(20)
                    .build())
            .build());

    SkillSourceBundle fetch(URI source, SkillSourceReader reader) {
        try {
            Location location = location(source);
            JsonNode repository = publicRepository(location, reader);
            Snapshot snapshot = resolve(location, reader, repository);
            JsonNode commit = get(reader, location, "/git/commits/" + snapshot.commit(), MAX_METADATA_BYTES);
            requireSha(commit, snapshot.commit());
            String treeSha = sha(commit.path("tree"));
            JsonNode tree = get(reader, location, "/git/trees/" + treeSha + "?recursive=1", MAX_TREE_BYTES);
            requireSha(tree, treeSha);
            Map<String, Entry> entries = entries(tree);
            String directory = selectDirectory(entries, snapshot.scope(), location.blob());
            List<Entry> files = validateSelection(entries, directory);
            Map<String, String> contents = new TreeMap<>();
            // All selected entries, paths, declared sizes and modes are checked before the first content read.
            for (Entry file : files) {
                contents.put(relative(directory, file.path()), blob(reader, location, file));
            }
            publicRepository(location, reader);
            URI pinnedSource = new URI(
                    "https",
                    null,
                    "github.com",
                    -1,
                    "/" + location.owner() + "/" + location.repository() + "/tree/" + snapshot.commit()
                            + (directory.isEmpty() ? "" : "/" + directory),
                    null,
                    null);
            return new SkillSourceBundle(contents, pinnedSource, snapshot.commit());
        } catch (SkillException failure) {
            throw new SkillException(failure.code());
        } catch (RuntimeException | URISyntaxException ignored) {
            // Never expose response bodies, source paths, upstream exceptions or credentials.
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
    }

    private static Location location(URI source) {
        if (source == null
                || !"https".equals(source.getScheme())
                || !"github.com".equals(source.getHost())
                || source.getRawUserInfo() != null
                || source.getPort() != -1
                || source.getRawQuery() != null
                || source.getRawFragment() != null) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        String path = source.getRawPath();
        if (path == null || path.length() < 2 || path.length() > 2048 || !path.startsWith("/")) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        // Split before decoding so escaped separators cannot change the path structure.
        String[] parts = path.substring(1).split("/", -1);
        if (parts.length < 2 || parts.length > 32) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        for (int i = 0; i < parts.length; i++) {
            parts[i] = decodeSegment(parts[i]);
        }
        String owner = parts[0];
        String repository = parts[1];
        if (parts.length == 2 && repository.endsWith(".git")) {
            repository = repository.substring(0, repository.length() - 4);
        }
        if (!owner.matches("[A-Za-z0-9][A-Za-z0-9-]{0,38}")
                || !repository.matches("[A-Za-z0-9_.-]{1,100}")
                || repository.equals(".")
                || repository.equals("..")) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        if (parts.length == 2) {
            return new Location(owner, repository, "", false);
        }
        boolean blob = parts[2].equals("blob");
        if ((!blob && !parts[2].equals("tree"))
                || parts.length < 4
                || (blob && (parts.length < 5 || !parts[parts.length - 1].equals("SKILL.md")))) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        String remainder = String.join("/", java.util.Arrays.copyOfRange(parts, 3, parts.length - (blob ? 1 : 0)));
        return new Location(owner, repository, remainder, blob);
    }

    private static String decodeSegment(String raw) {
        String part;
        try {
            // The raw path budget bounds these buffers. Encode strictly so raw Unicode is not replaced either.
            ByteBuffer encoded = StandardCharsets.UTF_8
                    .newEncoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .encode(CharBuffer.wrap(raw));
            ByteBuffer bytes = ByteBuffer.allocate(encoded.remaining());
            while (encoded.hasRemaining()) {
                byte next = encoded.get();
                if (next == '%') {
                    if (encoded.remaining() < 2) {
                        throw new IllegalArgumentException();
                    }
                    next = (byte)
                            ((HexFormat.fromHexDigit(encoded.get()) << 4) | HexFormat.fromHexDigit(encoded.get()));
                }
                // Unlike form decoding, '+' is a literal path character.
                bytes.put(next);
            }
            bytes.flip();
            part = StandardCharsets.UTF_8
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(bytes)
                    .toString();
        } catch (CharacterCodingException | IllegalArgumentException ignored) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        if (part.isEmpty()
                || part.equals(".")
                || part.equals("..")
                || part.indexOf('/') >= 0
                || part.indexOf('\\') >= 0
                || part.indexOf('%') >= 0
                || part.codePoints()
                        .anyMatch(c -> Character.isISOControl(c) || Character.getType(c) == Character.FORMAT)) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        return part;
    }

    private static JsonNode publicRepository(Location location, SkillSourceReader reader) {
        JsonNode repository = get(reader, location, "", MAX_METADATA_BYTES);
        JsonNode privateRepository = repository.path("private");
        // Do not disclose whether shared credentials can see a private repository.
        if (!privateRepository.isBoolean() || privateRepository.booleanValue()) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        return repository;
    }

    private static Snapshot resolve(Location location, SkillSourceReader reader, JsonNode repository) {
        if (location.remainder().isEmpty()) {
            String branch = text(repository, "default_branch");
            requireBranch(branch);
            return new Snapshot(ref(reader, location, branch), "");
        }
        String remainder = location.remainder();
        int slash = remainder.indexOf('/');
        String first = slash < 0 ? remainder : remainder.substring(0, slash);
        if (first.matches("[0-9a-fA-F]{40}")) {
            return new Snapshot(first.toLowerCase(Locale.ROOT), slash < 0 ? "" : remainder.substring(slash + 1));
        }
        requireBranch(first);
        // A bounded prefix lookup resolves slash branches without shorter-scope retries.
        JsonNode refs = get(reader, location, "/git/matching-refs/heads/" + first, MAX_METADATA_BYTES);
        if (!refs.isArray()) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        if (refs.size() > MAX_TREE_ENTRIES) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        String selected = null;
        Set<String> names = new HashSet<>();
        for (JsonNode candidate : refs) {
            String name = text(candidate, "ref");
            if (!name.startsWith("refs/heads/")) {
                throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
            }
            String branch = name.substring("refs/heads/".length());
            requireBranch(branch);
            if (!branch.startsWith(first) || !names.add(branch)) {
                throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
            }
            commitObject(candidate);
            if ((remainder.equals(branch) || remainder.startsWith(branch + "/"))
                    && (selected == null || branch.length() > selected.length())) {
                selected = branch;
            }
        }
        if (selected == null) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        String scope = remainder.length() == selected.length() ? "" : remainder.substring(selected.length() + 1);
        return new Snapshot(ref(reader, location, selected), scope);
    }

    private static String ref(SkillSourceReader reader, Location location, String branch) {
        JsonNode ref = get(reader, location, "/git/ref/heads/" + branch, MAX_METADATA_BYTES);
        if (!text(ref, "ref").equals("refs/heads/" + branch)) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        return commitObject(ref);
    }

    private static String commitObject(JsonNode ref) {
        JsonNode object = ref.path("object");
        if (!text(object, "type").equals("commit")) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        return sha(object);
    }

    private static void requireBranch(String branch) {
        if (branch.length() > MAX_REF_LENGTH
                || !branch.matches("[A-Za-z0-9_-][A-Za-z0-9._/-]*")
                || branch.contains("..")) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        for (String part : branch.split("/", -1)) {
            if (part.isEmpty() || part.startsWith(".") || part.endsWith(".") || part.endsWith(".lock")) {
                throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
            }
        }
    }

    private static Map<String, Entry> entries(JsonNode tree) {
        JsonNode truncated = tree.path("truncated");
        JsonNode manifest = tree.path("tree");
        if (!truncated.isBoolean() || !manifest.isArray()) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        if (truncated.booleanValue() || manifest.size() > MAX_TREE_ENTRIES) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        Map<String, Entry> entries = new TreeMap<>();
        for (JsonNode item : manifest) {
            String path = text(item, "path");
            requireTreePath(path);
            if (SkillBundleValidator.isHiddenPath(path)) {
                continue;
            }
            Entry entry = new Entry(
                    path, text(item, "type"), text(item, "mode"), sha(item), item.has("size") ? size(item) : -1);
            if (entries.putIfAbsent(path, entry) != null) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
        }
        return entries;
    }

    private static void requireTreePath(String path) {
        if (path.isEmpty()
                || path.length() > 4096
                || path.indexOf('\\') >= 0
                || path.indexOf('%') >= 0
                || path.codePoints().anyMatch(Character::isISOControl)) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        for (String part : path.split("/", -1)) {
            if (part.isEmpty() || part.equals(".") || part.equals("..")) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
        }
    }

    private static String selectDirectory(Map<String, Entry> entries, String scope, boolean exact) {
        String prefix = scope.isEmpty() ? "" : scope + "/";
        if (entries.containsKey(prefix + "SKILL.md")) {
            return scope;
        }
        if (exact) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        String selected = null;
        for (String path : entries.keySet()) {
            if (path.startsWith(prefix) && path.endsWith("/SKILL.md")) {
                String directory = path.substring(0, path.length() - "/SKILL.md".length());
                if (selected != null) {
                    throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
                }
                selected = directory;
            }
        }
        if (selected == null) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        return selected;
    }

    private static List<Entry> validateSelection(Map<String, Entry> entries, String directory) {
        requireParents(entries, directory.isEmpty() ? "SKILL.md" : directory + "/SKILL.md");
        List<Entry> files = new ArrayList<>();
        Set<String> names = new HashSet<>();
        long total = 0;
        for (Entry entry : entries.values()) {
            if (!directory.isEmpty() && !entry.path().startsWith(directory + "/")) {
                continue;
            }
            String path = relative(directory, entry.path());
            if (!names.add(SkillBundleValidator.pathKey(path))) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            requireParents(entries, entry.path());
            if (entry.type().equals("tree") && entry.mode().equals("040000")) {
                // Git can contain empty trees too; do not silently admit unsafe directory paths.
                SkillBundleValidator.validatePath(path + "/a.md");
                continue;
            }
            if (!entry.type().equals("blob") || !entry.mode().equals("100644")) {
                throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
            }
            SkillBundleValidator.validatePath(path);
            if (entry.size() < 0) {
                throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
            }
            if (entry.size() > (path.equals("SKILL.md") ? MAX_SKILL_BYTES : MAX_REFERENCE_BYTES)
                    || files.size() >= MAX_FILES) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            total += entry.size();
            if (total > MAX_TOTAL_BYTES) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            files.add(entry);
        }
        return files;
    }

    private static void requireParents(Map<String, Entry> entries, String path) {
        for (int slash = path.indexOf('/'); slash >= 0; slash = path.indexOf('/', slash + 1)) {
            Entry parent = entries.get(path.substring(0, slash));
            if (parent == null) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            if (!parent.type().equals("tree") || !parent.mode().equals("040000")) {
                throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
            }
        }
    }

    private static String relative(String directory, String path) {
        return directory.isEmpty() ? path : path.substring(directory.length() + 1);
    }

    private static String blob(SkillSourceReader reader, Location location, Entry entry) {
        JsonNode blob = get(reader, location, "/git/blobs/" + entry.sha(), MAX_METADATA_BYTES);
        requireSha(blob, entry.sha());
        if (size(blob) != entry.size() || !text(blob, "encoding").equals("base64")) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        // GitHub wraps base64 with LF. Accept CRLF as well, but never MIME's arbitrary ignored characters.
        String encoded = text(blob, "content").replace("\r\n", "\n").replace("\n", "");
        if (encoded.length() != 4 * ((entry.size() + 2) / 3)) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        byte[] bytes;
        try {
            bytes = Base64.getDecoder().decode(encoded);
        } catch (IllegalArgumentException ignored) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        if (bytes.length != entry.size()
                || !Base64.getEncoder().encodeToString(bytes).equals(encoded)
                || !gitBlobSha(bytes).equals(entry.sha())) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        try {
            return StandardCharsets.UTF_8
                    .newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString();
        } catch (CharacterCodingException ignored) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
    }

    private static String gitBlobSha(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(("blob " + bytes.length + "\0").getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(digest.digest(bytes));
        } catch (NoSuchAlgorithmException impossible) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
    }

    private static JsonNode get(SkillSourceReader reader, Location location, String suffix, int maxBytes) {
        // Every API URI is constructed here; response url/href/download_url fields are deliberately unused.
        URI uri = URI.create("https://api.github.com/repos/" + location.owner() + "/" + location.repository() + suffix);
        String body = reader.get(uri, JSON_TYPES, maxBytes);
        if (body == null) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        if (body.length() > maxBytes || body.getBytes(StandardCharsets.UTF_8).length > maxBytes) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        try (JsonParser parser = JSON.createParser(body)) {
            JsonNode parsed = JSON.readTree(parser);
            if (parsed == null || parser.nextToken() != null) {
                throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
            }
            return parsed;
        } catch (IOException ignored) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
    }

    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        if (!value.isTextual()) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        return value.textValue();
    }

    private static String sha(JsonNode node) {
        String value = text(node, "sha");
        if (!value.matches("[0-9a-f]{40}")) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        return value;
    }

    private static void requireSha(JsonNode node, String expected) {
        if (!sha(node).equals(expected)) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
    }

    private static long size(JsonNode node) {
        JsonNode value = node.path("size");
        if (!value.isIntegralNumber() || !value.canConvertToLong() || value.longValue() < 0) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
        return value.longValue();
    }

    private record Location(String owner, String repository, String remainder, boolean blob) {}

    private record Snapshot(String commit, String scope) {}

    private record Entry(String path, String type, String mode, String sha, long size) {}
}
