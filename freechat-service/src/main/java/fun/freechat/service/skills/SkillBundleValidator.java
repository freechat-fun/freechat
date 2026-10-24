package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSAFE_INSTRUCTIONS;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSUPPORTED_CONTENT;

import com.google.re2j.Pattern;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;

@Component
public final class SkillBundleValidator {
    public static final int MAX_FILES = 16;
    public static final int MAX_SKILL_BYTES = 32 * 1024;
    public static final int MAX_REFERENCE_BYTES = 64 * 1024;
    public static final int MAX_TOTAL_BYTES = 256 * 1024;
    private static final int MAX_FRONTMATTER_BYTES = 4096;
    private static final Pattern DEVICE_NAME = Pattern.compile("(?i)(con|prn|aux|nul|com[0-9]|lpt[0-9])");
    private static final Pattern HEADING = Pattern.compile("(?m)^#{1,6}[ \\t]+(.+)$");
    private static final Pattern SCRIPT_FENCE = Pattern.compile(
            "(?im)^[ \\t>]*(?:(?:[-+*]|[0-9]{1,9}[.)])[ \\t]+)?(?:`{3,}|~{3,})[ \\t]*(?:\\{\\.)?"
                    + "(?:bash|sh|zsh|fish|shell|console|powershell|pwsh|ps1|cmd|bat|batch|python[23]?|py|javascript|js|typescript|ts|node|deno|perl|php|ruby|rb|lua)(?:$|[ \\t}])");
    private static final Pattern ENCODED_BINARY =
            Pattern.compile("(?m)^\\s*(?:UEsDB|H4sI|f0VMRg|TVqQAAM|JVBERi0|iVBORw0KGgo)[A-Za-z0-9+/=]{32,}\\s*$");
    private static final Pattern ACTIVE_HTML = Pattern.compile(
            "(?is)<\\s*/?\\s*(?:script|iframe|object|embed|svg|math|link|meta|base|form|input|button|style|img|video|audio|source)\\b"
                    + "|<[^>]*\\s(?:on[a-z]+|style|srcdoc)\\s*=|(?:javascript|vbscript)\\s*:"
                    + "|data\\s*:\\s*(?:text/|application/|image/|audio/|video/|;base64)");
    private static final Pattern DIRECTIVES =
            Pattern.compile("(?im)(?:^\\s*|[,{]\\s*)[\"']?(?:allowed[-_]tools|tools|tool[-_]choice|tool[-_]grants|"
                    + "permissions|hooks|preinstall|postinstall|on[-_]install|model|model[-_]id|model[-_]override|"
                    + "system[-_]prompt|developer[-_]prompt|api[-_]key|base[-_]url|trusted|builtin|classification|tags)"
                    + "[\"']?\\s*[:=]");
    private static final Pattern REMOTE_INCLUDE =
            Pattern.compile("(?im)^\\s*(?:!include|@import|#include|(?:remote[-_])?include\\s*:)"
                    + "|!\\[[^\\]\\r\\n]*\\]\\s*(?:\\(|\\[)"
                    + "|\\{[%{][-~]?\\s*(?:include|import|extends)\\b");
    private static final Pattern EXECUTION = Pattern.compile(
            "(?im)^\\s*(?:#!\\s*/|(?:\\$\\s*)?(?:sudo\\s+)?(?:bash|sh|zsh|fish|powershell|pwsh|cmd(?:\\.exe)?|"
                    + "curl|wget|python[23]?|node|deno|npm|npx|pip[3]?|chmod|eval|exec)\\s+|"
                    + "(?:import\\s+(?:os|subprocess|sys)\\b|from\\s+(?:os|subprocess)\\s+import\\b)|"
                    + "(?:def|function)\\s+[A-Za-z_]\\w*\\s*\\()"
                    + "|\\b(?:os\\.system|subprocess\\.[A-Za-z_]+|Runtime\\.getRuntime|ProcessBuilder|child_process|"
                    + "document\\.write|fetch)\\s*\\(|\\$\\([^\\r\\n]*\\)|`(?:curl|wget|bash|sh)\\s");
    private static final byte[] DIGEST_DOMAIN = "freechat-prompt-only-v1\0".getBytes(StandardCharsets.US_ASCII);

    public SkillBundle validate(Map<String, String> files) {
        if (files == null || files.isEmpty()) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        if (files.size() > MAX_FILES) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        validateFilePaths(files.keySet());
        Map<String, String> snapshot = new TreeMap<>();
        long total = DIGEST_DOMAIN.length + Integer.BYTES;
        for (Map.Entry<String, String> file : files.entrySet()) {
            String path = file.getKey();
            String content = file.getValue();
            byte[] bytes = textBytes(content, path.equals("SKILL.md") ? MAX_SKILL_BYTES : MAX_REFERENCE_BYTES);
            total += 2L * Integer.BYTES + path.getBytes(StandardCharsets.UTF_8).length + bytes.length;
            requireTotal(total);
            snapshot.put(path, content);
        }
        if (!snapshot.containsKey("SKILL.md")) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        Map<String, String> metadata = metadata(snapshot.get("SKILL.md"));
        String name = metadata.get("name");
        String description = metadata.get("description");
        // Count derived metadata, fixed classification and digest as well as filenames, contents and framing.
        total += textBytes(name, 512).length
                + textBytes(description, 4 * 1024).length
                + "user".length()
                + "prompt-only".length()
                + 64
                + 5L * Integer.BYTES;
        requireTotal(total);
        for (String content : snapshot.values()) {
            scan(content);
        }
        // Scan the decoded metadata too: YAML quoting/escapes must not hide directive signatures.
        scan(name);
        scan(description);
        return new SkillBundle(name, description, snapshot, digest(snapshot));
    }

    public static boolean isHiddenPath(String path) {
        if (path == null || path.indexOf('\\') >= 0) {
            return false;
        }
        boolean hidden = false;
        for (String part : path.split("/", -1)) {
            if (part.isEmpty() || part.equals(".") || part.equals("..")) {
                return false;
            }
            hidden |= part.startsWith(".");
        }
        return hidden;
    }

    public static void validatePath(String path) {
        // Generated JavaScript map serializers cannot preserve this own-property name.
        if ("__proto__".equals(path)) throw new SkillException(SKILL_INVALID_FORMAT);
        if (path == null
                || path.isEmpty()
                || path.length() > 128
                || path.codePoints()
                        .anyMatch(c -> Character.isISOControl(c)
                                || Character.getType(c) == Character.FORMAT
                                || "\\:%<>\"|?*".indexOf(c) >= 0)) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        textBytes(path, 512);
        String[] parts = path.split("/", -1);
        if (parts.length > 8) throw new SkillException(SKILL_INVALID_FORMAT);
        for (String part : parts) {
            String base = part.split("\\.", 2)[0];
            if (part.isEmpty()
                    || part.startsWith(".")
                    || part.endsWith(".")
                    || part.endsWith(" ")
                    || part.getBytes(StandardCharsets.UTF_8).length > 255
                    || DEVICE_NAME.matcher(base).matches()) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
        }
    }

    public static String pathKey(String path) {
        return Normalizer.normalize(path, Normalizer.Form.NFC).toLowerCase(Locale.ROOT);
    }

    public static void validateFilePaths(Set<String> paths) {
        Set<String> keys = new HashSet<>();
        for (String path : paths) {
            validatePath(path);
            if (!keys.add(pathKey(path))) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
        }
        for (String key : keys) {
            for (int slash = key.indexOf('/'); slash >= 0; slash = key.indexOf('/', slash + 1)) {
                if (keys.contains(key.substring(0, slash))) {
                    throw new SkillException(SKILL_INVALID_FORMAT);
                }
            }
        }
    }

    /** Reject invalid UTF-16 before encoding; UTF-8 replacement would otherwise change the reviewed bytes. */
    static byte[] textBytes(String text, int limit) {
        if (text == null) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        if (text.length() > limit) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            int point = c;
            if (Character.isHighSurrogate(c)) {
                if (++i >= text.length() || !Character.isLowSurrogate(text.charAt(i))) {
                    throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
                }
                point = Character.toCodePoint(c, text.charAt(i));
            } else if (Character.isLowSurrogate(c)) {
                throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
            }
            if ((Character.isISOControl(point) && point != '\n' && point != '\r' && point != '\t')
                    || (Character.getType(point) == Character.FORMAT
                            && point != 0x200c
                            && point != 0x200d
                            && point != 0xfeff)) {
                throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
            }
        }
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > limit) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
        return bytes;
    }

    private static Map<String, String> metadata(String skill) {
        Map<?, ?> values = Map.of();
        String[] lines = skill.split("\\r?\\n", -1);
        if (lines[0].equals("---")) {
            StringBuilder header = new StringBuilder();
            int end = 1;
            while (end < lines.length && !lines[end].equals("---") && header.length() <= MAX_FRONTMATTER_BYTES) {
                header.append(lines[end++]).append('\n');
            }
            if (end < lines.length
                    && lines[end].equals("---")
                    && header.toString().getBytes(StandardCharsets.UTF_8).length <= MAX_FRONTMATTER_BYTES) {
                LoaderOptions options = new LoaderOptions();
                options.setAllowDuplicateKeys(false);
                options.setWarnOnDuplicateKeys(false);
                options.setAllowRecursiveKeys(false);
                options.setMaxAliasesForCollections(0);
                options.setNestingDepthLimit(8);
                options.setCodePointLimit(MAX_FRONTMATTER_BYTES);
                options.setTagInspector(tag -> false);
                try {
                    Object parsed = new Yaml(new SafeConstructor(options)).load(header.toString());
                    if (parsed instanceof Map<?, ?> map) values = map;
                } catch (RuntimeException ignored) {
                    // Frontmatter is optional display metadata, never executable configuration.
                }
            }
        }
        var heading = HEADING.matcher(skill);
        String fallbackName = heading.find() ? heading.group(1).strip() : "Imported skill";
        return Map.of(
                "name", displayText(values.get("name"), fallbackName, 128),
                "description", displayText(values.get("description"), "", 1024));
    }

    private static String displayText(Object value, String fallback, int limit) {
        String text = value instanceof String string && !string.isBlank() ? string : fallback;
        textBytes(text, MAX_SKILL_BYTES);
        scan(text);
        int end = Math.min(text.length(), limit);
        if (end < text.length() && Character.isHighSurrogate(text.charAt(end - 1))) end--;
        return text.substring(0, end);
    }

    private static void scan(String text) {
        // Normalize line boundaries only for signature detection; stored/reviewed/hashed bytes stay exact.
        text = text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u2028', '\n')
                .replace('\u2029', '\n')
                .replace("\uFEFF", "");
        if (ENCODED_BINARY.matcher(text).find()) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
        if (ACTIVE_HTML.matcher(text).find()
                || DIRECTIVES.matcher(text).find()
                || REMOTE_INCLUDE.matcher(text).find()
                || EXECUTION.matcher(text).find()) {
            throw new SkillException(SKILL_UNSAFE_INSTRUCTIONS);
        }
        if (SCRIPT_FENCE.matcher(text).find()) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
        // These are only signatures for obvious active payloads, not proof that prose is benign.
        // Every accepted snapshot still needs the independent, tool-free semantic review.
    }

    private static void requireTotal(long bytes) {
        if (bytes > MAX_TOTAL_BYTES) {
            throw new SkillException(SKILL_LIMIT_EXCEEDED);
        }
    }

    private static String digest(Map<String, String> files) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(DIGEST_DOMAIN);
            sha256.update(
                    ByteBuffer.allocate(Integer.BYTES).putInt(files.size()).array());
            files.forEach((path, content) -> {
                hashField(sha256, path.getBytes(StandardCharsets.UTF_8));
                hashField(sha256, content.getBytes(StandardCharsets.UTF_8));
            });
            return HexFormat.of().formatHex(sha256.digest());
        } catch (NoSuchAlgorithmException impossible) {
            throw new IllegalStateException("SHA-256 unavailable");
        }
    }

    private static void hashField(MessageDigest digest, byte[] bytes) {
        digest.update(ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array());
        digest.update(bytes);
    }
}
