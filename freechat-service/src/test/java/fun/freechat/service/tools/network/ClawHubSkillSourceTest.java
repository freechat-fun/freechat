package fun.freechat.service.tools.network;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_AMBIGUOUS;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_NOT_ALLOWED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_UNAVAILABLE;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSUPPORTED_CONTENT;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fun.freechat.service.skills.SkillBundleValidator;
import fun.freechat.service.skills.SkillException;
import fun.freechat.service.skills.SkillException.Code;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class ClawHubSkillSourceTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String ORIGIN = "https://clawhub.ai";
    private static final String API = ORIGIN + "/api/v1/skills/story";
    private static final URI REGISTRY = URI.create(API + "?owner=writer");
    private static final URI ROOT = URI.create(ORIGIN + "/writer/skills/story");
    private static final String VERSION = "2.0.1";
    private static final String MAIN =
            "---\r\nname: story\r\ndescription: Narration\r\n---\r\nKeep {{literal}} text.\r\n";
    private static final Map<String, String> FILES = Map.of(
            "SKILL.md", MAIN,
            "references/voice/tone.md", "# Tone\nPreserve café, 汉字, and tabs\there.\n",
            "notes/outline.txt", "Beginning\r\nMiddle\r\nEnd\r\n");

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/writer/skills/story",
                "/writer/story",
                "/writer/skills/story/",
                "/Writer/story",
                "/Writer/skills/story",
                "/writer/skills/story?utm_source=shared&utm_campaign=docs"
            })
    void rootsAndAliasesImportTheWholeBundleWithoutChangingPathsOrText(String path) {
        Reader reader = hosted(FILES, VERSION);
        SkillSourceBundle bundle = new ClawHubSkillSource().fetch(URI.create(ORIGIN + path), reader);
        assertEquals(FILES, bundle.files());
        assertEquals(ROOT, bundle.source());
        assertEquals(VERSION, bundle.revision());
        assertThrows(UnsupportedOperationException.class, () -> bundle.files().put("extra.md", "extra"));
        assertRequests(reader, VERSION, FILES);
    }

    @Test
    void hiddenFilesAndDirectoryDescendantsAreIgnoredBeforeValidationCountsAndDownloads() {
        List<Map<String, Object>> files = new ArrayList<>();
        files.add(entry(".gitignore", "Ignored"));
        files.add(Map.of("path", ".env"));
        Map<String, Object> invalidSize = entry(".DS_Store", "Ignored");
        invalidSize.put("size", "invalid");
        files.add(invalidSize);
        Map<String, Object> invalidHash = entry(".hidden/instructions.md", "Ignored");
        invalidHash.put("sha256", "invalid");
        files.add(invalidHash);
        Map<String, Object> script = entry("references/.cache/scripts/run.py", "not fetched");
        script.put("contentType", "application/octet-stream");
        files.add(script);
        Map<String, Object> symlink = entry(".hidden/nested/link.md", "Ignored");
        symlink.put("type", "symlink");
        symlink.put("target", "/etc/passwd");
        symlink.put("mode", "120000");
        files.add(symlink);
        Map<String, Object> large = entry("references/.cache/nested/large.bin", "Ignored");
        large.put("size", Long.MAX_VALUE);
        files.add(large);
        files.add(entry(".hidden/" + "nested/".repeat(9) + "x".repeat(129) + ".md", "Ignored"));
        files.add(entry(".hidden/SKILL.md", "Hidden skill"));
        files.addAll(entries(FILES));
        for (int i = 0; i < 17; i++) {
            Map<String, Object> hidden = entry("references/.cache/nested/note" + i + ".txt", "Ignored");
            hidden.put("size", SkillBundleValidator.MAX_REFERENCE_BYTES);
            files.add(hidden);
        }
        Reader reader = hosted(FILES, VERSION);
        reader.responses.put(versionUri(VERSION), manifest("story", VERSION, files));

        assertEquals(FILES, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, FILES);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/api/v1/skills/story/file?path=SKILL.md&version=2.0.1",
                "/api/v1/skills/story/file?version=2.0.1&path=SKILL.md",
                "/api/v1/download?slug=story&version=2.0.1",
                "/download?version=2.0.1&slug=story",
                "/writer/skills/story?version=2.0.1"
            })
    void explicitVersionOverridesLatestAndRawLinksStillImportEveryManifestFile(String path) {
        URI source = URI.create(ORIGIN + path);
        URI initialMetadata = path.startsWith("/writer/") ? REGISTRY : URI.create(API);
        Reader reader = hosted(FILES, VERSION, initialMetadata);
        reader.responses.put(initialMetadata, registry("writer", "story", "9.9.9"));
        SkillSourceBundle bundle = new ClawHubSkillSource().fetch(source, reader);
        assertEquals(VERSION, bundle.revision());
        assertEquals(FILES, bundle.files());
        assertEquals(ROOT, bundle.source());
        assertRequests(reader, VERSION, FILES, initialMetadata);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"/api/v1/skills/story/file?path=SKILL.md", "/api/v1/download?slug=story", "/download?slug=story"
            })
    void unversionedLinksResolveLatestOnlyOnce(String path) {
        URI source = URI.create(ORIGIN + path);
        URI initialMetadata = URI.create(API);
        Reader reader = hosted(FILES, VERSION, initialMetadata);
        SkillSourceBundle bundle = new ClawHubSkillSource().fetch(source, reader);
        assertEquals(VERSION, bundle.revision());
        assertEquals(FILES, bundle.files());
        assertEquals(ROOT, bundle.source());
        assertRequests(reader, VERSION, FILES, initialMetadata);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/api/v1/skills/story/file?path=SKILL.md&owner=writer",
                "/api/v1/skills/story/file?owner=Writer&path=SKILL.md",
                "/api/v1/download?slug=story&owner=writer",
                "/api/v1/download?owner=Writer&slug=story",
                "/download?slug=story&owner=writer",
                "/download?owner=Writer&slug=story"
            })
    void rawAndDownloadOwnerQueriesPinTheOwnerOnEveryFileWithLatestOrExplicitVersion(String path) {
        for (String versionQuery : List.of("", "&version=" + VERSION)) {
            Reader reader = hosted(FILES, VERSION);
            if (!versionQuery.isEmpty()) {
                reader.responses.put(REGISTRY, registry("writer", "story", "9.9.9"));
            }
            URI source = URI.create(ORIGIN + path + versionQuery);
            SkillSourceBundle bundle = new ClawHubSkillSource().fetch(source, reader);
            assertEquals(VERSION, bundle.revision());
            assertEquals(FILES, bundle.files());
            assertEquals(ROOT, bundle.source());
            assertRequests(reader, VERSION, FILES);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"/writer/skills/story", "/Writer/story"})
    void ownerQualifiedRootsResolveDuplicateSlugsWhenBareMetadataIsUnavailable(String path) {
        Reader reader = hosted(FILES, VERSION);
        URI bareMetadata = URI.create(API);
        reader.responses.put(bareMetadata, null);
        URI ownerlessSource = URI.create(ORIGIN + "/api/v1/skills/story/file?path=SKILL.md");
        failure(SKILL_SOURCE_UNAVAILABLE, ownerlessSource, reader);
        assertEquals(
                List.of(bareMetadata),
                reader.requests.stream().map(Request::uri).toList());
        assertNoFiles(reader);

        reader.requests.clear();
        SkillSourceBundle bundle = new ClawHubSkillSource().fetch(URI.create(ORIGIN + path), reader);
        assertEquals(FILES, bundle.files());
        assertEquals(ROOT, bundle.source());
        assertEquals(VERSION, bundle.revision());
        assertRequests(reader, VERSION, FILES);
    }

    @Test
    void returnedOwnerCaseIsPreservedInCanonicalSourceButNormalizedForEveryRequest() {
        for (URI source : List.of(
                ROOT,
                URI.create(ORIGIN + "/api/v1/skills/story/file?path=SKILL.md"),
                URI.create(ORIGIN + "/api/v1/download?slug=story"),
                URI.create(ORIGIN + "/download?slug=story"))) {
            URI initialMetadata = source.equals(ROOT) ? REGISTRY : URI.create(API);
            Reader reader = hosted(FILES, VERSION, initialMetadata);
            reader.responses.put(initialMetadata, registry("Writer", "story", VERSION));
            SkillSourceBundle bundle = new ClawHubSkillSource().fetch(source, reader);
            assertEquals(URI.create(ORIGIN + "/Writer/skills/story"), bundle.source());
            assertEquals(FILES, bundle.files());
            assertEquals(VERSION, bundle.revision());
            assertRequests(reader, VERSION, FILES, initialMetadata);
        }
    }

    @Test
    void semanticVersionPrereleaseAndBuildMetadataAreEncodedAndPinnedExactly() {
        String version = "2.0.1-rc.1+build.7";
        URI initialMetadata = URI.create(API);
        Reader reader = hosted(FILES, version, initialMetadata);
        URI source = URI.create(ORIGIN + "/api/v1/download?slug=story&version=" + encode(version));
        assertEquals(version, new ClawHubSkillSource().fetch(source, reader).revision());
        assertRequests(reader, version, FILES, initialMetadata);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/writer/skills/story",
                "/Writer/story",
                "/api/v1/skills/story/file?path=SKILL.md&owner=writer",
                "/api/v1/download?slug=story&owner=Writer",
                "/download?slug=story&owner=writer"
            })
    void wrongOwnerOrRegistrySlugStopsBeforeRequestingAVersion(String path) {
        URI source = URI.create(ORIGIN + path);
        for (String registry : List.of(registry("impostor", "story", VERSION), registry("writer", "other", VERSION))) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(REGISTRY, registry);
            failure(SKILL_SOURCE_AMBIGUOUS, source, reader);
            assertEquals(1, reader.requests.size());
            assertEquals(REGISTRY, reader.requests.get(0).uri());
            assertNoFiles(reader);
        }
    }

    @Test
    void wrongManifestVersionOrSlugStopsBeforeFiles() {
        for (String manifest :
                List.of(manifest("story", "2.0.2", entries(FILES)), manifest("other", VERSION, entries(FILES)))) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(versionUri(VERSION), manifest);
            Code expected = manifest.contains("other") ? SKILL_SOURCE_AMBIGUOUS : SKILL_INVALID_FORMAT;
            failure(expected, reader);
            assertEquals(2, reader.requests.size());
            assertNoFiles(reader);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "../escape.md",
                "/absolute.md",
                "references/../../escape.md",
                "references\\escape.md",
                "references//empty.md",
                "references/./dot.md",
                "references/%2e%2e/escape.md",
                ".hidden/../escape.md",
                ".hidden/./escape.md",
                "references/.hidden/nested/../escape.md",
                "references/.hidden/nested/./escape.md",
                "../.hidden/escape.md",
                "./.hidden/escape.md",
                ".hidden/.",
                ".hidden/..",
                ".hidden//escape.md",
                ".hidden/notes\\escape.md",
                ".hidden/escape.md/",
                "https://evil.example/file.md",
                "notes:stream.txt",
                "notes?.txt",
                "CON.txt",
                "notes.txt ",
                "notes.txt.",
                "a/b/c/d/e/f/g/h/file.md",
                "skill.md"
            })
    void anyForbiddenManifestPathRejectsTheEntireBundleEvenWithHiddenFiles(String path) {
        List<Map<String, Object>> entries = new ArrayList<>(entries(Map.of("SKILL.md", MAIN)));
        entries.add(Map.of("path", ".env"));
        entries.add(entry(".hidden/install.sh", "not fetched"));
        entries.add(entry(path, "not fetched"));
        Reader reader = hosted(FILES, VERSION);
        reader.responses.put(versionUri(VERSION), manifest("story", VERSION, entries));
        assertThrows(SkillException.class, () -> new ClawHubSkillSource().fetch(ROOT, reader));
        assertNoFiles(reader);
    }

    @Test
    void visiblePathLengthRemainsBoundedByTheSharedValidator() {
        String path = "references/" + "a".repeat(60) + "/" + "b".repeat(60) + "/notes.md";
        Reader reader = hosted(FILES, VERSION);
        reader.responses.put(
                versionUri(VERSION),
                manifest("story", VERSION, List.of(entry("SKILL.md", MAIN), entry(path, "not fetched"))));

        failure(SKILL_INVALID_FORMAT, reader);
        assertNoFiles(reader);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "scripts/x_hots.py",
                "hooks/install.sh",
                "binary.png",
                "archive.zip",
                "_meta.json",
                "LICENSE",
                "README"
            })
    void inertUtf8FilesAreImportedRegardlessOfSuffixWhileHiddenFilesStayOmitted(String path) {
        String content = "Documentation only. Café, 汉字.\r\n";
        Map<String, String> files = Map.of("SKILL.md", MAIN, path, content);
        Reader reader = hosted(files, VERSION);
        Map<String, Object> file = entry(path, content);
        file.put("contentType", "application/octet-stream");
        reader.responses.put(
                versionUri(VERSION),
                manifest(
                        "story",
                        VERSION,
                        List.of(
                                Map.of("path", ".env"),
                                entry(".hidden/scripts/ignored.py", "not fetched"),
                                entry("SKILL.md", MAIN),
                                file)));

        assertEquals(files, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, files);
    }

    @Test
    void preservesLicenseLocalizedReadmeConfigSpacesAndUnicodePaths() {
        Map<String, String> files = Map.of(
                "SKILL.md", MAIN,
                "LICENSE", "MIT license\n",
                "README.zh-CN.md", "使用说明\n",
                "config/rclone.conf.example", "[archive]\ntype = local\n",
                "reference notes/你好.txt", "Reference text\n",
                "cafe\u0301.txt", "Decomposed filename\n",
                "a..txt", "Two dots are not traversal\n",
                "docs.v1/guide", "Extensionless guide\n");
        Reader reader = hosted(files, VERSION);

        assertEquals(files, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, files);
    }

    @ParameterizedTest
    @ValueSource(strings = {"notes/a.md", "notes/A.md"})
    void duplicateAndCaseCollidingPathsFailPreflight(String second) {
        Reader reader = hosted(FILES, VERSION);
        reader.responses.put(
                versionUri(VERSION),
                manifest(
                        "story",
                        VERSION,
                        List.of(entry("SKILL.md", MAIN), entry("notes/a.md", "a"), entry(second, "b"))));
        failure(SKILL_INVALID_FORMAT, reader);
        assertNoFiles(reader);
    }

    @ParameterizedTest
    @ValueSource(strings = {"notes/cafe\u0301.md", "notes/CAFE\u0301.md"})
    void unicodeCanonicalAliasesFailPreflight(String second) {
        Reader reader = hosted(FILES, VERSION);
        reader.responses.put(
                versionUri(VERSION),
                manifest(
                        "story",
                        VERSION,
                        List.of(entry("SKILL.md", MAIN), entry("notes/café.md", "a"), entry(second, "b"))));
        failure(SKILL_INVALID_FORMAT, reader);
        assertNoFiles(reader);
    }

    @Test
    void linksDirectoriesAndUnknownFileMetadataFailClosed() {
        for (Map<String, Object> attributes : List.of(
                Map.<String, Object>of("type", "symlink"),
                Map.<String, Object>of("type", "directory"),
                Map.<String, Object>of("type", "executable"),
                Map.<String, Object>of("symlink", true),
                Map.<String, Object>of("target", "/etc/passwd"),
                Map.<String, Object>of("mode", "120000"),
                Map.<String, Object>of("mode", "100755"),
                Map.<String, Object>of("mode", "100644"),
                Map.<String, Object>of("href", "https://evil.example/private-sentinel"))) {
            Map<String, Object> file = entry("notes.txt", "never fetched");
            file.putAll(attributes);
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(
                    versionUri(VERSION), manifest("story", VERSION, List.of(entry("SKILL.md", MAIN), file)));
            failure(SKILL_UNSUPPORTED_CONTENT, reader);
            assertNoFiles(reader);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "text/markdown; charset=utf-8",
                "application/json",
                "application/octet-stream",
                "text/x-config",
                "text/html",
                "custom-label"
            })
    void optionalRegularFileTypeAndArbitraryMimeLabelsAreAccepted(String mediaType) {
        Map<String, String> files = Map.of("SKILL.md", MAIN);
        Reader reader = hosted(files, VERSION);
        Map<String, Object> file = entry("SKILL.md", MAIN);
        file.put("type", "file");
        file.put("contentType", mediaType);
        reader.responses.put(versionUri(VERSION), manifest("story", VERSION, List.of(file)));
        assertEquals(files, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, files);
    }

    @Test
    void contentTypeMetadataStillRequiresABoundedNonemptyStringWhenPresent() {
        for (Object contentType :
                List.of(7, true, List.of("text/plain"), Map.of("mime", "text/plain"), "", "x".repeat(129))) {
            Reader reader = hosted(FILES, VERSION);
            Map<String, Object> file = entry("SKILL.md", MAIN);
            file.put("contentType", contentType);
            reader.responses.put(versionUri(VERSION), manifest("story", VERSION, List.of(file)));
            failure(
                    contentType instanceof String value && value.length() > 128
                            ? SKILL_LIMIT_EXCEEDED
                            : SKILL_INVALID_FORMAT,
                    reader);
            assertNoFiles(reader);
        }
    }

    @Test
    void hashAndUtf8ByteSizeMustBothMatchExactly() {
        String original = "café\r\n";
        for (String changed : List.of("cafe\r\n", "café\n", "cafè\r\n", original + "extra")) {
            Reader reader = hosted(Map.of("SKILL.md", original), VERSION);
            reader.responses.put(fileUri("SKILL.md", VERSION), changed);
            failure(SKILL_INVALID_FORMAT, reader);
        }
        Reader reader = hosted(Map.of("SKILL.md", original), VERSION);
        Map<String, Object> file = entry("SKILL.md", original);
        file.put("size", original.length());
        reader.responses.put(versionUri(VERSION), manifest("story", VERSION, List.of(file)));
        failure(SKILL_INVALID_FORMAT, reader);
    }

    @Test
    void malformedUtf16IsNotSilentlyReplacedWhenHashing() {
        Reader reader = hosted(Map.of("SKILL.md", "?"), VERSION);
        reader.responses.put(fileUri("SKILL.md", VERSION), "\uD800");
        failure(SKILL_UNSUPPORTED_CONTENT, reader);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "\r\n", "Plain instructions without frontmatter."})
    void emptyOrUnformattedMainIsAcceptedWhenEntrypointIsPresent(String content) {
        Map<String, String> files = Map.of("SKILL.md", content);
        Reader reader = hosted(files, VERSION);

        assertEquals(files, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, files);
    }

    @Test
    void missingMainAndInvalidFileShapesFailPreflight() {
        List<Object> invalid = List.of(
                List.of(),
                Map.of("SKILL.md", entry("SKILL.md", MAIN)),
                List.of(entry("notes.txt", "notes")),
                List.of(entry(".hidden/SKILL.md", MAIN)),
                List.of(entry("references/.cache/SKILL.md", MAIN), entry(".SKILL.md", MAIN)),
                List.of("SKILL.md"),
                List.of(Map.of("path", "SKILL.md", "sha256", digest(MAIN))),
                List.of(Map.of("path", "SKILL.md", "size", 1, "sha256", "not-a-hash")),
                List.of(Map.of("path", 7, "size", 1, "sha256", digest(MAIN))));
        for (Object files : invalid) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(versionUri(VERSION), manifest("story", VERSION, files));
            failure(SKILL_INVALID_FORMAT, reader);
            assertNoFiles(reader);
        }
        for (Object size : List.of(-1, 1.0, "1", true, Map.of("bytes", 1))) {
            Map<String, Object> file = entry("SKILL.md", MAIN);
            file.put("size", size);
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(versionUri(VERSION), manifest("story", VERSION, List.of(file)));
            failure(SKILL_INVALID_FORMAT, reader);
            assertNoFiles(reader);
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "",
                "null",
                "[]",
                "{}",
                "{\"skill\":{}} true",
                "{\"skill\":{},\"skill\":{}}",
                "{\"owner\":{\"handle\":\"one\",\"handle\":\"two\"}}",
                "{\"version\":{\"version\":\"2.0.1\",\"files\":[],\"files\":[]}}",
                "{\"skill\":true}",
                "{\"skill\":null}",
                "{\"skill\":{\"slug\":7}}",
                "{\"skill\":{\"slug\":\"story\"},\"owner\":[],\"latestVersion\":{\"version\":\"2.0.1\"}}",
                "{\"private-sentinel\":NaN}",
                "{ /* comment */ \"skill\": {} }"
            })
    void malformedMetadataDuplicateKeysAndTrailingTokensAreRejected(String body) {
        for (URI endpoint : List.of(REGISTRY, versionUri(VERSION))) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(endpoint, body);
            failure(SKILL_INVALID_FORMAT, reader);
            assertNoFiles(reader);
        }
    }

    @Test
    void otherwiseValidJsonStillRejectsTrailingOrDuplicateContent() {
        for (URI endpoint : List.of(REGISTRY, versionUri(VERSION))) {
            for (boolean duplicate : List.of(false, true)) {
                Reader reader = hosted(FILES, VERSION);
                String valid = reader.responses.get(endpoint);
                String invalid = duplicate ? valid.substring(0, valid.length() - 1) + ",\"skill\":{}}" : valid + " {}";
                reader.responses.put(endpoint, invalid);
                failure(SKILL_INVALID_FORMAT, reader);
                assertNoFiles(reader);
            }
        }
    }

    @Test
    void manifestLimitsAreCheckedBeforeFiles() {
        List<List<Map<String, Object>>> invalid = new ArrayList<>();
        List<Map<String, Object>> tooMany = new ArrayList<>();
        tooMany.add(entry("SKILL.md", MAIN));
        for (int i = 0; i < SkillBundleValidator.MAX_FILES; i++) {
            tooMany.add(entry("note" + i + ".txt", ""));
        }
        invalid.add(tooMany);
        for (String path : List.of("SKILL.md", "note.txt")) {
            Map<String, Object> tooBig = entry(path, "");
            tooBig.put(
                    "size",
                    (path.equals("SKILL.md")
                                    ? SkillBundleValidator.MAX_SKILL_BYTES
                                    : SkillBundleValidator.MAX_REFERENCE_BYTES)
                            + 1);
            invalid.add(path.equals("SKILL.md") ? List.of(tooBig) : List.of(entry("SKILL.md", MAIN), tooBig));
        }
        List<Map<String, Object>> aggregate = new ArrayList<>();
        aggregate.add(entry("SKILL.md", MAIN));
        for (int i = 0; i < 4; i++) {
            Map<String, Object> file = entry("ref" + i + ".txt", "");
            file.put("size", SkillBundleValidator.MAX_REFERENCE_BYTES);
            aggregate.add(file);
        }
        invalid.add(aggregate);
        for (List<Map<String, Object>> files : invalid) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(versionUri(VERSION), manifest("story", VERSION, files));
            failure(SKILL_LIMIT_EXCEEDED, reader);
            assertNoFiles(reader);
        }
    }

    @Test
    void sixteenVisibleFilesAreAllowedAlongsideMoreThanSixteenHiddenFiles() {
        Map<String, String> visible = new LinkedHashMap<>();
        visible.put("SKILL.md", MAIN);
        for (int i = 0; i < SkillBundleValidator.MAX_FILES - 1; i++) {
            visible.put("note" + i + ".txt", "Note " + i);
        }
        List<Map<String, Object>> files = new ArrayList<>(entries(visible));
        for (int i = 0; i < 17; i++) {
            files.add(Map.of("path", ".hidden/note" + i + ".txt"));
        }
        Reader reader = hosted(visible, VERSION);
        reader.responses.put(versionUri(VERSION), manifest("story", VERSION, files));

        assertEquals(visible, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, visible);
    }

    @Test
    void hiddenManifestEntriesStillObeyRawMetadataShapeStringAndByteBounds() {
        List<Map<String, Object>> tooMany = new ArrayList<>(entries(Map.of("SKILL.md", MAIN)));
        for (int i = 0; i < 128; i++) {
            tooMany.add(Map.of("path", ".hidden/note" + i + ".txt"));
        }
        List<Map<String, Object>> tooLarge = new ArrayList<>(entries(Map.of("SKILL.md", MAIN)));
        for (int i = 0; i < 3; i++) {
            tooLarge.add(Map.of("path", "." + "x".repeat(24 * 1024) + i));
        }
        for (Object files : List.of(
                tooMany,
                tooLarge,
                List.of(entry("SKILL.md", MAIN), Map.of("path", "." + "x".repeat(32 * 1024))),
                List.of(
                        entry("SKILL.md", MAIN),
                        Map.of("path", ".env", "extra", java.util.Collections.nCopies(129, "item"))))) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(versionUri(VERSION), manifest("story", VERSION, files));
            assertThrows(SkillException.class, () -> new ClawHubSkillSource().fetch(ROOT, reader));
            assertNoFiles(reader);
        }
    }

    @Test
    void oversizedMetadataShapesStringsAndReturnedFileBodiesAreBounded() {
        for (String body : List.of(
                " ".repeat(64 * 1024 + 1),
                json(Map.of("description", "x".repeat(32 * 1024 + 1))),
                json(Map.of("items", java.util.Collections.nCopies(129, "item"))))) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(REGISTRY, body);
            assertThrows(SkillException.class, () -> new ClawHubSkillSource().fetch(ROOT, reader));
            assertNoFiles(reader);
        }
        Reader deep = hosted(FILES, VERSION);
        deep.responses.put(REGISTRY, "{\"nested\":" + "[".repeat(20) + "0" + "]".repeat(20) + "}");
        failure(SKILL_INVALID_FORMAT, deep);
        Reader oversized = hosted(Map.of("SKILL.md", MAIN), VERSION);
        oversized.responses.put(fileUri("SKILL.md", VERSION), "x".repeat(SkillBundleValidator.MAX_SKILL_BYTES + 1));
        failure(SKILL_LIMIT_EXCEEDED, oversized);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "https://github.com/writer/story",
                "https://clawhub.ai.evil.example/writer/story",
                "https://sub.clawhub.ai/writer/story",
                "https://clawhub.ai./writer/story",
                "https://127.0.0.1/writer/story",
                "http://clawhub.ai/writer/story",
                "https://user@clawhub.ai/writer/story",
                "https://clawhub.ai:444/writer/story",
                "file:///etc/passwd"
            })
    void unapprovedOriginsNeverReachTheReader(String source) {
        Reader reader = new Reader();
        assertEquals(
                SKILL_SOURCE_NOT_ALLOWED,
                assertThrows(SkillException.class, () -> new ClawHubSkillSource().fetch(URI.create(source), reader))
                        .code());
        assertTrue(reader.requests.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"/api/v1/skills/story/file?path=SKILL.md", "/api/v1/download?slug=story", "/download?slug=story"
            })
    void invalidRawAndDownloadOwnerQueriesNeverReachTheReader(String path) {
        for (String owner : List.of(
                "",
                " ",
                " writer",
                "writer ",
                "../writer",
                "writer/other",
                "writer.name",
                "writer&slug=other",
                "writer?version=1.0.0",
                "writer@example.com",
                "_writer",
                "-writer",
                "wrïter",
                "w".repeat(129))) {
            Reader reader = new Reader();
            URI source = URI.create(ORIGIN + path + "&owner=" + encode(owner));
            failure(SKILL_SOURCE_AMBIGUOUS, source, reader);
            assertTrue(reader.requests.isEmpty());
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "/",
                "/writer/skills/story/extra",
                "/writer/../story",
                "/writer/%73tory",
                "/writer/story#private-sentinel",
                "/writer/story?url=https://evil.example",
                "/writer/skills/story?owner=writer",
                "/writer/skills/story?owner=other",
                "/writer/story?owner=writer",
                "/writer/story?owner=other",
                "/api/v1/skills/story/file?path=references/note.md&version=2.0.1",
                "/api/v1/skills/story/file?path=SKILL.md&preview=1",
                "/api/v1/skills/story/file?path=SKILL.md&owner=writer&owner=writer",
                "/api/v1/skills/story/file?path=SKILL.md&owner=writer&%6fwner=other",
                "/api/v1/download?slug=story&owner=writer&owner=other",
                "/download?slug=story&owner=writer&owner=other",
                "/api/v1/download?slug=story&tag=latest",
                "/api/v1/download?slug=story&slug=other",
                "/api/v1/download?slug=story&%73lug=other",
                "/api/v1/download?version=2.0.1",
                "/api/v1/download?slug=story&version=latest",
                "/api/v1/download?slug=story&version=..%2Fother",
                "/api/v1/download?slug=story&version=",
                "/api/v1/download?slug=story&&version=2.0.1"
            })
    void unsupportedOrAmbiguousRoutesAndQueriesNeverReachTheReader(String path) {
        Reader reader = new Reader();
        assertEquals(
                SKILL_SOURCE_AMBIGUOUS,
                assertThrows(
                                SkillException.class,
                                () -> new ClawHubSkillSource().fetch(URI.create(ORIGIN + path), reader))
                        .code());
        assertTrue(reader.requests.isEmpty());
    }

    @Test
    void githubHandoffsAndArchivePointersAreRejectedWithoutFollowingThem() {
        for (URI endpoint : List.of(REGISTRY, versionUri(VERSION))) {
            Reader reader = hosted(FILES, VERSION);
            reader.responses.put(
                    endpoint,
                    json(Map.of(
                            "sourceRef", "public-github",
                            "repo", "writer/repo",
                            "commit", "private-sentinel",
                            "archiveUrl", "https://github.com/writer/repo/archive/main.zip")));
            failure(SKILL_UNSUPPORTED_CONTENT, reader);
            assertNoFiles(reader);
        }
        Reader reader = hosted(FILES, VERSION);
        reader.responses.put(
                REGISTRY, "{\"skill\":{\"slug\":\"story\"},\"owner\":{\"handle\":\"writer\"},\"latestVersion\":null}");
        failure(SKILL_UNSUPPORTED_CONTENT, reader);
        assertEquals(1, reader.requests.size());
    }

    @Test
    void unrelatedProviderLinksAreNeverUsedAsRequestTargets() {
        Reader reader = hosted(FILES, VERSION);
        String body = reader.responses.get(REGISTRY);
        reader.responses.put(
                REGISTRY,
                body.substring(0, body.length() - 1)
                        + ",\"href\":\"https://evil.example/private-sentinel\",\"image\":\"https://github.com/image\"}");
        assertEquals(FILES, new ClawHubSkillSource().fetch(ROOT, reader).files());
        assertRequests(reader, VERSION, FILES);
    }

    @Test
    void readerFailuresAreSanitizedWithoutProviderBodyOrCause() {
        SkillException unavailable = assertThrows(
                SkillException.class,
                () -> new ClawHubSkillSource().fetch(ROOT, (uri, media, max) -> {
                    throw new IllegalStateException("private-sentinel");
                }));
        assertEquals(SKILL_SOURCE_UNAVAILABLE, unavailable.code());
        assertNull(unavailable.getCause());
        assertEquals(SKILL_SOURCE_UNAVAILABLE.name(), unavailable.getMessage());
        SkillException quota = assertThrows(
                SkillException.class,
                () -> new ClawHubSkillSource().fetch(ROOT, (uri, media, max) -> {
                    SkillException failure = new SkillException(SKILL_LIMIT_EXCEEDED);
                    failure.initCause(new IllegalStateException("private-sentinel"));
                    throw failure;
                }));
        assertEquals(SKILL_LIMIT_EXCEEDED, quota.code());
        assertNull(quota.getCause());
    }

    private static Reader hosted(Map<String, String> files, String version) {
        return hosted(files, version, REGISTRY);
    }

    private static Reader hosted(Map<String, String> files, String version, URI initialMetadata) {
        Reader reader = new Reader();
        reader.responses.put(initialMetadata, registry("writer", "story", version));
        reader.responses.put(versionUri(version), manifest("story", version, entries(files)));
        files.forEach((path, content) -> reader.responses.put(fileUri(path, version), content));
        return reader;
    }

    private static String registry(String owner, String slug, String latest) {
        return json(Map.of(
                "skill",
                Map.of("slug", slug),
                "owner",
                Map.of("handle", owner),
                "latestVersion",
                Map.of("version", latest)));
    }

    private static String manifest(String slug, String version, Object files) {
        return json(Map.of("skill", Map.of("slug", slug), "version", Map.of("version", version, "files", files)));
    }

    private static List<Map<String, Object>> entries(Map<String, String> files) {
        return files.entrySet().stream()
                .map(file -> entry(file.getKey(), file.getValue()))
                .toList();
    }

    private static Map<String, Object> entry(String path, String content) {
        return new LinkedHashMap<>(Map.of(
                "path", path, "size", content.getBytes(StandardCharsets.UTF_8).length, "sha256", digest(content)));
    }

    private static String digest(String content) {
        try {
            return HexFormat.of()
                    .formatHex(MessageDigest.getInstance("SHA-256").digest(content.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException impossible) {
            throw new AssertionError(impossible);
        }
    }

    private static String json(Object value) {
        try {
            return JSON.writeValueAsString(value);
        } catch (JsonProcessingException failure) {
            throw new AssertionError(failure);
        }
    }

    private static URI versionUri(String version) {
        return URI.create(API + "/versions/" + encode(version) + "?owner=writer");
    }

    private static URI fileUri(String path, String version) {
        return URI.create(API + "/file?path=" + encode(path) + "&version=" + encode(version) + "&owner=writer");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static void failure(Code expected, Reader reader) {
        failure(expected, ROOT, reader);
    }

    private static void failure(Code expected, URI source, Reader reader) {
        SkillException failure =
                assertThrows(SkillException.class, () -> new ClawHubSkillSource().fetch(source, reader));
        assertEquals(expected, failure.code());
        assertEquals(expected.name(), failure.getMessage());
        assertNull(failure.getCause());
    }

    private static void assertNoFiles(Reader reader) {
        assertTrue(reader.requests.stream()
                .noneMatch(request -> request.uri().getPath().endsWith("/file")));
    }

    private static void assertRequests(Reader reader, String version, Map<String, String> files) {
        assertRequests(reader, version, files, REGISTRY);
    }

    private static void assertRequests(Reader reader, String version, Map<String, String> files, URI initialMetadata) {
        assertEquals(2 + files.size(), reader.requests.size());
        assertEquals(initialMetadata, reader.requests.get(0).uri());
        assertEquals(versionUri(version), reader.requests.get(1).uri());
        assertEquals(
                Set.copyOf(files.keySet().stream()
                        .map(path -> fileUri(path, version))
                        .toList()),
                Set.copyOf(reader.requests.subList(2, reader.requests.size()).stream()
                        .map(Request::uri)
                        .toList()));
        for (Request request : reader.requests) {
            assertEquals("https", request.uri().getScheme());
            assertEquals("clawhub.ai", request.uri().getHost());
            if (request.uri().getPath().endsWith("/file")) {
                assertEquals(Set.of(), request.media());
                String path = files.keySet().stream()
                        .filter(name -> fileUri(name, version).equals(request.uri()))
                        .findFirst()
                        .orElseThrow();
                assertEquals(
                        path.equals("SKILL.md")
                                ? SkillBundleValidator.MAX_SKILL_BYTES
                                : SkillBundleValidator.MAX_REFERENCE_BYTES,
                        request.limit());
            } else {
                assertEquals(Set.of("application/json"), request.media());
                assertTrue(request.limit() > 0 && request.limit() <= 64 * 1024);
            }
        }
    }

    private record Request(URI uri, Set<String> media, int limit) {}

    private static final class Reader implements SkillSourceReader {
        private final Map<URI, String> responses = new LinkedHashMap<>();
        private final List<Request> requests = new ArrayList<>();

        @Override
        public String get(URI uri, Set<String> mediaTypes, int maxBytes) {
            requests.add(new Request(uri, Set.copyOf(mediaTypes), maxBytes));
            assertTrue(responses.containsKey(uri), "Unexpected request: " + uri);
            return responses.get(uri);
        }
    }
}
