package fun.freechat.service.tools.network;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_AMBIGUOUS;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_NOT_ALLOWED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_UNAVAILABLE;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSUPPORTED_CONTENT;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import fun.freechat.service.skills.SkillBundleValidator;
import fun.freechat.service.skills.SkillException;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/** Reads only hosted, version-pinned ClawHub manifests; never follows provider links or downloads archives. */
final class ClawHubSkillSource {
    private static final String ORIGIN = "https://clawhub.ai";
    private static final int MAX_METADATA_BYTES = 64 * 1024;
    private static final int MAX_JSON_STRING = 32 * 1024;
    private static final Set<String> JSON_MEDIA = Set.of("application/json");
    private static final Set<String> FILE_MEDIA = Set.of(); // Any MIME label, still bounded and decoded as UTF-8.
    private static final Set<String> FILE_FIELDS = Set.of("path", "size", "sha256", "contentType", "type");
    private static final Pattern IDENTIFIER = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_-]{0,127}");
    private static final Pattern VERSION = Pattern.compile("(?:0|[1-9][0-9]*)\\.(?:0|[1-9][0-9]*)\\.(?:0|[1-9][0-9]*)"
            + "(?:-[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?(?:\\+[0-9A-Za-z-]+(?:\\.[0-9A-Za-z-]+)*)?");
    private static final Pattern SHA256 = Pattern.compile("[0-9a-fA-F]{64}");
    private static final ObjectMapper JSON = JsonMapper.builder(JsonFactory.builder()
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(12)
                            .maxStringLength(MAX_JSON_STRING)
                            .maxNameLength(128)
                            .maxNumberLength(20)
                            .build())
                    .build())
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .build();

    SkillSourceBundle fetch(URI source, SkillSourceReader reader) {
        Selection selection = select(source);
        String api = ORIGIN + "/api/v1/skills/" + selection.slug();
        String requestedOwner = selection.owner() == null
                ? ""
                : "?owner=" + encode(selection.owner().toLowerCase(Locale.ROOT));
        JsonNode registry = metadata(reader, URI.create(api + requestedOwner));
        JsonNode skill = object(registry.get("skill"));
        rejectHandoff(skill);
        requireSlug(skill, selection.slug());
        String owner = text(object(registry.get("owner")), "handle", 128);
        if (!IDENTIFIER.matcher(owner).matches()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        if (selection.owner() != null && !selection.owner().equalsIgnoreCase(owner)) {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
        String version = selection.version();
        if (version == null) {
            JsonNode latest = registry.get("latestVersion");
            if (latest == null || latest.isNull()) {
                throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
            }
            version = text(object(latest), "version", 128);
        }
        if (!validVersion(version)) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        String ownerQuery = "owner=" + encode(owner.toLowerCase(Locale.ROOT));
        JsonNode response = metadata(reader, URI.create(api + "/versions/" + encode(version) + "?" + ownerQuery));
        requireSlug(object(response.get("skill")), selection.slug());
        JsonNode manifest = object(response.get("version"));
        rejectHandoff(manifest);
        if (!version.equals(text(manifest, "version", 128))) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }

        // Complete preflight happens before the first file request. Keep every visible file for content review.
        List<ManifestFile> files = manifestFiles(manifest.get("files"));
        Map<String, String> snapshot = new LinkedHashMap<>();
        for (ManifestFile file : files) {
            URI uri = URI.create(
                    api + "/file?path=" + encode(file.path()) + "&version=" + encode(version) + "&" + ownerQuery);
            String content = read(reader, uri, FILE_MEDIA, fileLimit(file.path()));
            byte[] bytes = utf8(content, fileLimit(file.path()));
            if (bytes.length != file.size()
                    || !MessageDigest.isEqual(sha256(bytes), HexFormat.of().parseHex(file.sha256()))) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            snapshot.put(file.path(), content);
        }
        return new SkillSourceBundle(
                snapshot, URI.create(ORIGIN + "/" + owner + "/skills/" + selection.slug()), version);
    }

    private static Selection select(URI source) {
        if (source == null
                || !"https".equalsIgnoreCase(source.getScheme())
                || !"clawhub.ai".equalsIgnoreCase(source.getHost())
                || source.getRawUserInfo() != null
                || source.getPort() != -1 && source.getPort() != 443) {
            throw new SkillException(SKILL_SOURCE_NOT_ALLOWED);
        }
        if (source.toString().length() > 2048 || source.getRawFragment() != null) {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
        String path = source.getRawPath();
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String[] parts = path.split("/", -1);
        Map<String, String> query = query(source.getRawQuery());
        String owner = null;
        String slug;
        if (path.equals("/api/v1/download") || path.equals("/download")) {
            requireQuery(query, Set.of("slug", "version", "owner"));
            slug = query.get("slug");
            owner = query.get("owner");
        } else if (parts.length == 6
                && parts[1].equals("api")
                && parts[2].equals("v1")
                && parts[3].equals("skills")
                && parts[5].equals("file")) {
            requireQuery(query, Set.of("path", "version", "owner"));
            if (!"SKILL.md".equals(query.get("path"))) {
                throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
            }
            slug = parts[4];
            owner = query.get("owner");
        } else if (parts.length == 4 && parts[2].equals("skills")) {
            requireQuery(query, Set.of("version"));
            owner = parts[1];
            slug = parts[3];
        } else if (parts.length == 3 && !parts[1].equals("api")) {
            requireQuery(query, Set.of("version"));
            owner = parts[1];
            slug = parts[2];
        } else {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
        String version = query.get("version");
        if (slug == null
                || !IDENTIFIER.matcher(slug).matches()
                || owner != null && !IDENTIFIER.matcher(owner).matches()
                || version != null && !validVersion(version)) {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
        return new Selection(owner, slug, version);
    }

    private static Map<String, String> query(String raw) {
        Map<String, String> result = new HashMap<>();
        if (raw == null) {
            return result;
        }
        String[] fields = raw.split("&", -1);
        if (fields.length > 16) {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
        for (String field : fields) {
            int separator = field.indexOf('=');
            if (separator <= 0 || separator == field.length() - 1) {
                throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
            }
            try {
                String key = URLDecoder.decode(field.substring(0, separator), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(field.substring(separator + 1), StandardCharsets.UTF_8);
                if (key.matches("utm_[a-z_]+")) continue;
                if (result.putIfAbsent(key, value) != null) {
                    throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
                }
            } catch (IllegalArgumentException ignored) {
                throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
            }
        }
        return result;
    }

    private static void requireQuery(Map<String, String> query, Set<String> allowed) {
        if (!allowed.containsAll(query.keySet())) {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
    }

    private static List<ManifestFile> manifestFiles(JsonNode entries) {
        if (entries == null || !entries.isArray() || entries.isEmpty()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        List<ManifestFile> result = new ArrayList<>();
        Set<String> paths = new HashSet<>();
        long total = 0;
        boolean main = false;
        for (JsonNode entry : entries) {
            object(entry);
            String path = text(entry, "path", MAX_JSON_STRING);
            if (SkillBundleValidator.isHiddenPath(path)) {
                continue;
            }
            SkillBundleValidator.validatePath(path);
            if (!paths.add(SkillBundleValidator.pathKey(path))) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            for (var names = entry.fieldNames(); names.hasNext(); ) {
                if (!FILE_FIELDS.contains(names.next())) {
                    // The hosted API only describes regular files. Unknown link/mode metadata fails closed.
                    throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
                }
            }
            if (entry.has("type") && !"file".equals(text(entry, "type", 32))) {
                throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
            }
            if (entry.hasNonNull("contentType")) {
                // Preserve the metadata schema, but MIME labels do not determine whether file bytes are safe.
                text(entry, "contentType", 128);
            }
            JsonNode size = entry.get("size");
            if (size == null || !size.isIntegralNumber() || !size.canConvertToLong() || size.longValue() < 0) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            long bytes = size.longValue();
            total += bytes;
            if (bytes > fileLimit(path)
                    || total > SkillBundleValidator.MAX_TOTAL_BYTES
                    || result.size() >= SkillBundleValidator.MAX_FILES) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            String hash = text(entry, "sha256", 64);
            if (!SHA256.matcher(hash).matches()) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            main |= path.equals("SKILL.md");
            result.add(new ManifestFile(path, (int) bytes, hash));
        }
        if (!main) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        return List.copyOf(result);
    }

    private static JsonNode metadata(SkillSourceReader reader, URI uri) {
        String body = read(reader, uri, JSON_MEDIA, MAX_METADATA_BYTES);
        utf8(body, MAX_METADATA_BYTES);
        try {
            JsonNode result = object(JSON.readTree(body));
            boundedShape(result, new int[] {2048});
            rejectHandoff(result);
            return result;
        } catch (IOException ignored) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
    }

    private static void boundedShape(JsonNode node, int[] remaining) {
        if (--remaining[0] < 0
                || node.size() > 128
                || node.isTextual() && node.textValue().length() > MAX_JSON_STRING) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        if (node.isTextual()) {
            utf8(node.textValue(), MAX_JSON_STRING);
        }
        for (JsonNode child : node) {
            boundedShape(child, remaining);
        }
    }

    private static void rejectHandoff(JsonNode node) {
        if (node.hasNonNull("sourceRef") || node.hasNonNull("archiveUrl")) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
    }

    private static void requireSlug(JsonNode skill, String slug) {
        if (!slug.equals(text(skill, "slug", 128))) {
            throw new SkillException(SKILL_SOURCE_AMBIGUOUS);
        }
    }

    private static JsonNode object(JsonNode node) {
        if (node == null || !node.isObject()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        return node;
    }

    private static String text(JsonNode node, String field, int limit) {
        JsonNode value = node.get(field);
        if (value == null || !value.isTextual() || value.textValue().isEmpty()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        if (value.textValue().length() > limit) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        return value.textValue();
    }

    private static String read(SkillSourceReader reader, URI uri, Set<String> media, int limit) {
        try {
            String result = reader.get(uri, media, limit);
            if (result == null) {
                throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
            }
            return result;
        } catch (SkillException failure) {
            throw new SkillException(failure.code());
        } catch (RuntimeException ignored) {
            throw new SkillException(SKILL_SOURCE_UNAVAILABLE);
        }
    }

    private static byte[] utf8(String text, int limit) {
        if (text.length() > limit) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        try {
            var encoded = StandardCharsets.UTF_8.newEncoder().encode(CharBuffer.wrap(text));
            if (encoded.remaining() > limit) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            byte[] bytes = new byte[encoded.remaining()];
            encoded.get(bytes);
            return bytes;
        } catch (CharacterCodingException ignored) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
    }

    private static byte[] sha256(byte[] bytes) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(bytes);
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 unavailable");
        }
    }

    private static int fileLimit(String path) {
        return path.equals("SKILL.md")
                ? SkillBundleValidator.MAX_SKILL_BYTES
                : SkillBundleValidator.MAX_REFERENCE_BYTES;
    }

    private static boolean validVersion(String version) {
        return version.length() <= 128 && VERSION.matcher(version).matches();
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private record Selection(String owner, String slug, String version) {}

    private record ManifestFile(String path, int size, String sha256) {}
}
