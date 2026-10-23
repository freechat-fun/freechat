package fun.freechat.service.skills;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSAFE_INSTRUCTIONS;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSUPPORTED_CONTENT;

import com.google.re2j.Pattern;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;

/** A deliberately small, inert document format; never an execution or permission boundary on its own. */
@Component
public final class SkillBundleValidator {
    public static final int MAX_FILES = 16;
    public static final int MAX_SKILL_BYTES = 32 * 1024;
    public static final int MAX_REFERENCE_BYTES = 64 * 1024;
    public static final int MAX_TOTAL_BYTES = 256 * 1024;
    private static final int MAX_FRONTMATTER_BYTES = 4096;
    private static final Pattern FILE_NAME = Pattern.compile("[A-Za-z0-9][A-Za-z0-9_-]{0,63}\\.(md|txt)");
    private static final Pattern SKILL_NAME = Pattern.compile("[a-z0-9]+(?:-[a-z0-9]+)*");
    private static final Pattern DEVICE_NAME = Pattern.compile("(?i)(con|prn|aux|nul|com[0-9]|lpt[0-9])");
    private static final Pattern FENCE =
            Pattern.compile("^[ \\t>]*(?:(?:[-+*]|[0-9]{1,9}[.)])[ \\t]+)?(`{3,}|~{3,})(.*)$");
    private static final Set<String> DATA_FENCES =
            Set.of("", "text", "txt", "plaintext", "markdown", "md", "json", "yaml", "yml", "csv");
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
        Map<String, String> snapshot = new TreeMap<>();
        Set<String> names = new HashSet<>();
        long total = DIGEST_DOMAIN.length + Integer.BYTES;
        for (Map.Entry<String, String> file : files.entrySet()) {
            String path = file.getKey();
            validatePath(path);
            if (!names.add(path.toLowerCase(Locale.ROOT))) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            if (snapshot.size() >= MAX_FILES) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            String content = file.getValue();
            byte[] bytes = textBytes(content, path.equals("SKILL.md") ? MAX_SKILL_BYTES : MAX_REFERENCE_BYTES);
            total += 2L * Integer.BYTES + path.length() + bytes.length;
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
        total += textBytes(name, 64).length
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

    private static void validatePath(String path) {
        if (path == null
                || path.isEmpty()
                || path.length() > 128
                || path.indexOf('\\') >= 0
                || path.startsWith("/")
                || path.contains("..")) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        if (path.equals("SKILL.md")) {
            return;
        }
        String leaf = path.startsWith("references/") ? path.substring("references/".length()) : path;
        if (leaf.indexOf('/') >= 0 || leaf.startsWith(".") || !leaf.matches("[A-Za-z0-9_.-]+")) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        if (!FILE_NAME.matcher(leaf).matches()) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
        if (DEVICE_NAME.matcher(leaf.substring(0, leaf.lastIndexOf('.'))).matches()
                || leaf.equalsIgnoreCase("SKILL.md")) {
            throw new SkillException(SKILL_INVALID_FORMAT);
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
                    || (Character.getType(point) == Character.FORMAT && point != 0x200c && point != 0x200d)) {
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
        String[] lines = skill.split("\\r?\\n", -1);
        if (!lines[0].equals("---")) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        StringBuilder header = new StringBuilder();
        int end = 1;
        while (end < lines.length && !lines[end].equals("---")) {
            header.append(lines[end++]).append('\n');
            if (header.length() > MAX_FRONTMATTER_BYTES) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
        }
        if (end == lines.length || java.util.Arrays.stream(lines).skip(end + 1L).allMatch(String::isBlank)) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
        textBytes(header.toString(), MAX_FRONTMATTER_BYTES);
        LoaderOptions options = new LoaderOptions();
        options.setAllowDuplicateKeys(false);
        options.setWarnOnDuplicateKeys(false);
        options.setAllowRecursiveKeys(false);
        options.setMaxAliasesForCollections(0);
        options.setNestingDepthLimit(2);
        options.setCodePointLimit(MAX_FRONTMATTER_BYTES);
        options.setTagInspector(tag -> false);
        Yaml yaml = new Yaml(new SafeConstructor(options));
        try {
            int collections = 0;
            int scalars = 0;
            for (Event event : yaml.parse(new StringReader(header.toString()))) {
                if (event instanceof AliasEvent
                        || event instanceof NodeEvent node && node.getAnchor() != null
                        || event instanceof DocumentStartEvent start && start.getExplicit()
                        || event instanceof DocumentEndEvent finish && finish.getExplicit()) {
                    throw new SkillException(SKILL_INVALID_FORMAT);
                }
                if (event instanceof CollectionStartEvent collection) {
                    if (!(event instanceof MappingStartEvent) || ++collections != 1 || collection.getTag() != null) {
                        throw new SkillException(SKILL_INVALID_FORMAT);
                    }
                }
                if (event instanceof ScalarEvent scalar) {
                    if (++scalars > 4
                            || scalar.getTag() != null
                            || scalar.isLiteral()
                            || scalar.isFolded()
                            || scalar.getStartMark().getLine()
                                    != scalar.getEndMark().getLine()) {
                        throw new SkillException(SKILL_INVALID_FORMAT);
                    }
                }
            }
            Object parsed = yaml.load(header.toString());
            if (!(parsed instanceof Map<?, ?> map)
                    || map.size() != 2
                    || !(map.get("name") instanceof String name)
                    || !(map.get("description") instanceof String description)) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            textBytes(name, MAX_FRONTMATTER_BYTES);
            textBytes(description, MAX_FRONTMATTER_BYTES);
            if (name.length() > 64 || description.length() > 1024) {
                throw new SkillException(SKILL_LIMIT_EXCEEDED);
            }
            if (!SKILL_NAME.matcher(name).matches()
                    || description.isBlank()
                    || description
                            .codePoints()
                            .anyMatch(c -> c == '\n' || c == '\r' || c == 0x85 || c == 0x2028 || c == 0x2029)) {
                throw new SkillException(SKILL_INVALID_FORMAT);
            }
            return Map.of("name", name, "description", description);
        } catch (SkillException failure) {
            throw failure;
        } catch (RuntimeException ignored) {
            throw new SkillException(SKILL_INVALID_FORMAT);
        }
    }

    private static void scan(String text) {
        // Normalize line boundaries only for signature detection; stored/reviewed/hashed bytes stay exact.
        text = text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .replace('\u2028', '\n')
                .replace('\u2029', '\n');
        if (ENCODED_BINARY.matcher(text).find()) {
            throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
        }
        if (ACTIVE_HTML.matcher(text).find()
                || DIRECTIVES.matcher(text).find()
                || REMOTE_INCLUDE.matcher(text).find()
                || EXECUTION.matcher(text).find()) {
            throw new SkillException(SKILL_UNSAFE_INSTRUCTIONS);
        }
        char fenceChar = 0;
        int fenceLength = 0;
        for (String line : text.split("\\r?\\n", -1)) {
            var fence = FENCE.matcher(line);
            if (!fence.matches()) {
                continue;
            }
            String marker = fence.group(1);
            String language = fence.group(2).strip().toLowerCase(Locale.ROOT);
            if (fenceChar == 0) {
                if (!DATA_FENCES.contains(language)) {
                    throw new SkillException(SKILL_UNSUPPORTED_CONTENT);
                }
                fenceChar = marker.charAt(0);
                fenceLength = marker.length();
            } else if (marker.charAt(0) == fenceChar && marker.length() >= fenceLength && language.isEmpty()) {
                fenceChar = 0;
            }
        }
        if (fenceChar != 0) {
            throw new SkillException(SKILL_INVALID_FORMAT);
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
