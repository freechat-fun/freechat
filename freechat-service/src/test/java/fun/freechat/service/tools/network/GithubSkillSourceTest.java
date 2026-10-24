package fun.freechat.service.tools.network;

import static fun.freechat.service.skills.SkillException.Code.SKILL_INVALID_FORMAT;
import static fun.freechat.service.skills.SkillException.Code.SKILL_LIMIT_EXCEEDED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_AMBIGUOUS;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_NOT_ALLOWED;
import static fun.freechat.service.skills.SkillException.Code.SKILL_SOURCE_UNAVAILABLE;
import static fun.freechat.service.skills.SkillException.Code.SKILL_UNSUPPORTED_CONTENT;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.freechat.service.skills.SkillException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

@Timeout(5)
class GithubSkillSourceTest {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final String API = "/repos/acme/skills";
    private static final String COMMIT = "1".repeat(40);
    private static final String TREE = "2".repeat(40);
    private static final String OTHER = "3".repeat(40);
    private static final String SKILL =
            "---\r\nname: story\r\ndescription: A story helper\r\nlicense: MIT\r\n---\r\n# 你好\r\nKeep {{literal}}.\r\n";

    @ParameterizedTest
    @ValueSource(strings = {"", "/", ".git", ".git/"})
    void repositoryRootPreservesCompleteMultiFileBundleAndMetadata(String suffix) {
        Fixture fixture = new Fixture();
        fixture.file("SKILL.md", SKILL);
        fixture.file("Guide.txt", "Some text\n");
        fixture.file("references/nested/guide.md", "A reference\r\n");
        fixture.file("resources/examples/sample.txt", "An example\n");

        SkillSourceBundle bundle = fixture.fetch("https://github.com/acme/skills" + suffix);

        assertEquals(
                Map.of(
                        "SKILL.md", SKILL,
                        "Guide.txt", "Some text\n",
                        "references/nested/guide.md", "A reference\r\n",
                        "resources/examples/sample.txt", "An example\n"),
                bundle.files());
        assertEquals(URI.create("https://github.com/acme/skills/tree/" + COMMIT), bundle.source());
        assertEquals(COMMIT, bundle.revision());
        assertThrows(UnsupportedOperationException.class, () -> bundle.files().put("extra.md", "no"));
        assertEquals(
                List.of(
                        API,
                        API + "/git/ref/heads/main",
                        API + "/git/commits/" + COMMIT,
                        API + "/git/trees/" + TREE + "?recursive=1"),
                fixture.paths().subList(0, 4));
        assertEquals(API, fixture.paths().getLast());
        assertEquals(9, fixture.requests.size());
    }

    @Test
    void hiddenFilesAndDirectoriesAreIgnoredBeforeValidationCountsAndBlobReads() {
        Fixture fixture = new Fixture();
        String skillSha = fixture.file("SKILL.md", SKILL).path("sha").asText();
        String notesSha =
                fixture.file("references/notes.md", "Notes").path("sha").asText();
        fixture.entry(".gitignore", "100644", "blob", OTHER, 1);
        fixture.manifest.addObject().put("path", ".env");
        fixture.entry(".DS_Store", "100644", "blob", "not-a-sha", 1);
        fixture.entry(".hidden", "040000", "tree", TREE, -1);
        fixture.entry(".hidden/instructions.md", "100644", "blob", OTHER, 1);
        fixture.entry(".hidden/invalid-size.md", "100644", "blob", OTHER, 1).put("size", "invalid");
        fixture.entry(".hidden/invalid-mode.md", "100644", "blob", OTHER, 1).put("mode", false);
        fixture.entry(".hidden/invalid-type.md", "100644", "blob", OTHER, 1).put("type", false);
        fixture.entry("references/.cache", "120000", "blob", OTHER, 1);
        fixture.entry("references/.cache/scripts/run.sh", "100755", "blob", OTHER, 1);
        fixture.entry("references/.cache/nested/link.md", "120000", "blob", OTHER, 1);
        fixture.entry("references/.cache/nested/large.bin", "100644", "blob", OTHER, Long.MAX_VALUE);
        fixture.entry(".hidden/" + "nested/".repeat(9) + "x".repeat(129) + ".md", "100644", "blob", OTHER, 1);
        for (int i = 0; i < 17; i++) {
            fixture.entry("references/.cache/nested/note" + i + ".md", "100644", "blob", OTHER, 64 * 1024);
        }

        assertEquals(
                Map.of("SKILL.md", SKILL, "references/notes.md", "Notes"),
                fixture.fetch().files());
        assertEquals(
                List.of(API + "/git/blobs/" + skillSha, API + "/git/blobs/" + notesSha),
                fixture.paths().stream()
                        .filter(path -> path.contains("/git/blobs/"))
                        .toList());
        assertEquals(7, fixture.requests.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/tree/main/skills"})
    void repositoryRootAndDirectoryFindSingleNestedSkillAndExcludeUnrelatedSiblings(String suffix) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        fixture.file("skills/story/SKILL.md", SKILL);
        fixture.file("skills/story/references/research/notes.md", "Notes");
        fixture.file("README.md", "Unrelated");
        fixture.file("LICENSE", "Unrelated license");
        fixture.file("src/run.sh", "a script").put("mode", "100755");
        fixture.entry("external", "160000", "commit", OTHER, -1);

        SkillSourceBundle bundle = fixture.fetch("https://github.com/acme/skills" + suffix);

        assertEquals(Map.of("SKILL.md", SKILL, "references/research/notes.md", "Notes"), bundle.files());
        assertEquals(URI.create("https://github.com/acme/skills/tree/" + COMMIT + "/skills/story"), bundle.source());
        assertEquals(API, fixture.paths().getFirst());
        assertEquals(API, fixture.paths().getLast());
        assertEquals(suffix.isEmpty() ? 7 : 8, fixture.requests.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"tree/main/skills/story", "blob/main/skills/story/SKILL.md"})
    void explicitDirectoryAndBlobUrlsSelectOnlyTheirScope(String path) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        fixture.file("SKILL.md", "Other skill");
        fixture.file("skills/story/SKILL.md", SKILL);
        fixture.file("skills/story/references/notes.txt", "Notes");
        fixture.file("skills/other/SKILL.md", "Other skill");

        SkillSourceBundle bundle = fixture.fetch("https://github.com/acme/skills/" + path);

        assertEquals(Map.of("SKILL.md", SKILL, "references/notes.txt", "Notes"), bundle.files());
        assertEquals(URI.create("https://github.com/acme/skills/tree/" + COMMIT + "/skills/story"), bundle.source());
        assertEquals(API, fixture.paths().getFirst());
        assertEquals(API, fixture.paths().getLast());
        assertEquals(8, fixture.requests.size());
    }

    @Test
    void discoveredUnicodeDirectoryProducesReimportableCanonicalSource() {
        Fixture fixture = new Fixture();
        fixture.file("skills/技能 文档/SKILL.md", SKILL);
        fixture.file("skills/技能 文档/references/说明.txt", "Notes\r\n");
        fixture.file("README.md", "Unrelated");

        SkillSourceBundle bundle = fixture.fetch();

        assertEquals(Map.of("SKILL.md", SKILL, "references/说明.txt", "Notes\r\n"), bundle.files());
        assertEquals(COMMIT, bundle.revision());
        assertEquals(URI.create("https://github.com/acme/skills/tree/" + COMMIT + "/skills/技能%20文档"), bundle.source());
        assertEquals(
                "https://github.com/acme/skills/tree/" + COMMIT + "/skills/%E6%8A%80%E8%83%BD%20%E6%96%87%E6%A1%A3",
                bundle.source().toASCIIString());
        assertEquals(bundle, fixture.fetch(bundle.source().toString()));
        assertEquals(bundle, fixture.fetch(bundle.source().toASCIIString()));
        assertEquals(
                1,
                fixture.paths().stream()
                        .filter(path -> path.contains("/git/ref/heads/"))
                        .count());
        assertFalse(fixture.paths().stream().anyMatch(path -> path.contains("matching-refs")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"tree", "blob"})
    void explicitUnicodeAndSpaceDirectoriesAcceptRawEncodedAndMixedPaths(String kind) throws URISyntaxException {
        URI source = new URI(
                "https",
                null,
                "github.com",
                -1,
                "/acme/skills/" + kind + "/main/skills/技能 文档" + (kind.equals("blob") ? "/SKILL.md" : ""),
                null,
                null);
        for (String input : List.of(
                source.toString(), source.toASCIIString(), source.toString().replace("技能", "%E6%8A%80%E8%83%BD"))) {
            Fixture fixture = new Fixture();
            fixture.branches("main");
            fixture.file("SKILL.md", "Other root skill");
            fixture.file("skills/技能 文档/SKILL.md", SKILL);
            fixture.file("skills/技能 文档/references/notes.txt", "Notes");
            fixture.file("skills/other/SKILL.md", "Other sibling skill");

            SkillSourceBundle bundle = fixture.fetch(input);

            assertEquals(Map.of("SKILL.md", SKILL, "references/notes.txt", "Notes"), bundle.files());
            assertEquals(
                    URI.create("https://github.com/acme/skills/tree/" + COMMIT + "/skills/技能%20文档"), bundle.source());
            assertEquals(
                    List.of(API, API + "/git/matching-refs/heads/main", API + "/git/ref/heads/main"),
                    fixture.paths().subList(0, 3));
            assertEquals(API, fixture.paths().getLast());
            assertEquals(8, fixture.requests.size());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"docs+notes", "docs%2Bnotes", "docs%2bnotes"})
    void plusInDirectoryAndFilenamesRemainsLiteralRatherThanBecomingASpace(String directory) {
        for (String kind : List.of("tree", "blob")) {
            Fixture fixture = new Fixture();
            fixture.branches("main");
            fixture.file("docs+notes/SKILL.md", SKILL);
            fixture.file("docs+notes/reference+notes.txt", "Literal plus");
            fixture.file("docs notes/SKILL.md", "Wrong scope");

            SkillSourceBundle bundle = fixture.fetch("https://github.com/acme/skills/" + kind + "/main/" + directory
                    + (kind.equals("blob") ? "/SKILL.md" : ""));

            assertEquals(Map.of("SKILL.md", SKILL, "reference+notes.txt", "Literal plus"), bundle.files());
            assertEquals(URI.create("https://github.com/acme/skills/tree/" + COMMIT + "/docs+notes"), bundle.source());
            assertEquals(bundle, fixture.fetch(bundle.source().toString()));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"tree/feature/docs/skills/story", "blob/feature/docs/skills/story/SKILL.md"})
    void longestMatchingBranchHandlesSlashesWithoutGuessing(String path) {
        Fixture fixture = new Fixture();
        fixture.branches("feature", "feature/docs", "feature/documentation");
        fixture.file("skills/story/SKILL.md", SKILL);
        fixture.file("docs/skills/story/SKILL.md", "Wrong scope on shorter branch");

        SkillSourceBundle bundle = fixture.fetch("https://github.com/acme/skills/" + path);

        assertEquals(Map.of("SKILL.md", SKILL), bundle.files());
        assertEquals(
                List.of(API, API + "/git/matching-refs/heads/feature", API + "/git/ref/heads/feature/docs"),
                fixture.paths().subList(0, 3));
        assertEquals(API, fixture.paths().getLast());
        assertEquals(7, fixture.requests.size());
    }

    @Test
    void slashContainingDefaultBranchIsUsedAsAnExactRef() {
        Fixture fixture = new Fixture();
        fixture.response("", JSON.createObjectNode().put("private", false).put("default_branch", "release/docs"));
        fixture.response("/git/ref/heads/release/docs", ref("release/docs"));
        fixture.file("SKILL.md", SKILL);

        fixture.fetch();

        assertEquals(API + "/git/ref/heads/release/docs", fixture.paths().get(1));
        assertFalse(fixture.paths().stream().anyMatch(path -> path.contains("matching-refs")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"tree/", "blob/"})
    void fullCommitInputSkipsMutableRefsAndProducesReimportableCanonicalSource(String kind) {
        Fixture fixture = new Fixture();
        String blobSha =
                fixture.file("skills/story/SKILL.md", SKILL).path("sha").asText();
        String suffix = kind.equals("blob/") ? "/SKILL.md" : "";

        SkillSourceBundle bundle =
                fixture.fetch("https://github.com/acme/skills/" + kind + COMMIT + "/skills/story" + suffix);
        assertEquals(COMMIT, bundle.revision());
        List<String> expectedRequests = List.of(
                API,
                API + "/git/commits/" + COMMIT,
                API + "/git/trees/" + TREE + "?recursive=1",
                API + "/git/blobs/" + blobSha,
                API);
        assertEquals(expectedRequests, fixture.paths());
        assertEquals(bundle, fixture.fetch(bundle.source().toString()));
        assertEquals(10, fixture.requests.size());
        assertEquals(expectedRequests, fixture.paths().subList(5, 10));
    }

    @Test
    void neverReresolvesBranchAfterPinningEvenIfTheBranchMoves() {
        Fixture fixture = new Fixture();
        fixture.file("SKILL.md", SKILL);
        SkillSourceReader moving = (uri, mediaTypes, limit) -> {
            String body = fixture.get(uri, mediaTypes, limit);
            if (uri.getPath().endsWith("/git/ref/heads/main")) {
                ObjectNode moved = ref("main");
                ((ObjectNode) moved.get("object")).put("sha", OTHER);
                fixture.response("/git/ref/heads/main", moved);
            }
            return body;
        };

        SkillSourceBundle bundle = new GithubSkillSource().fetch(URI.create("https://github.com/acme/skills"), moving);

        assertEquals(COMMIT, bundle.revision());
        assertEquals(
                1,
                fixture.paths().stream()
                        .filter(path -> path.endsWith("/git/ref/heads/main"))
                        .count());
        assertFalse(fixture.paths().stream().anyMatch(path -> path.contains(OTHER)));
    }

    @ParameterizedTest
    @MethodSource("repositorySources")
    void privateRepositoriesAreRejectedBeforeAnyRefsTreesOrBlobs(String source) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        fixture.file("skills/story/SKILL.md", SKILL);
        fixture.response("", JSON.createObjectNode().put("private", true).put("default_branch", "main"));

        failure(SKILL_SOURCE_UNAVAILABLE, fixture, source);

        assertEquals(List.of(API), fixture.paths());
    }

    @ParameterizedTest
    @MethodSource("invalidPrivacyMetadata")
    void missingOrNonBooleanPrivacyFlagsFailClosedBeforeAnyRefsTreesOrBlobs(String metadata) {
        for (String source : repositorySources()) {
            Fixture fixture = new Fixture();
            fixture.branches("main");
            fixture.file("skills/story/SKILL.md", SKILL);
            fixture.response("", metadata);

            failure(SKILL_SOURCE_UNAVAILABLE, fixture, source);

            assertEquals(List.of(API), fixture.paths(), source);
        }
    }

    @ParameterizedTest
    @MethodSource("repositorySources")
    void repositoryBecomingPrivateDuringBlobDownloadIsRejectedRatherThanReturned(String source) {
        assertRejectedAfterBlobDownload(
                source,
                JSON.createObjectNode()
                        .put("private", true)
                        .put("default_branch", "main")
                        .toString(),
                SKILL_SOURCE_UNAVAILABLE);
    }

    @ParameterizedTest
    @MethodSource("invalidPrivacyMetadata")
    void missingOrNonBooleanPrivacyFlagsOnFinalCheckRejectTheDownloadedBundle(String metadata) {
        for (String source : repositorySources()) {
            assertRejectedAfterBlobDownload(source, metadata, SKILL_SOURCE_UNAVAILABLE);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/tree/main/skills"})
    void multipleDescendantSkillsAreAmbiguousBeforeReadingAnyContents(String suffix) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        fixture.file("skills/a/SKILL.md", SKILL);
        fixture.file("skills/b/SKILL.md", SKILL);

        failure(SKILL_SOURCE_AMBIGUOUS, fixture, "https://github.com/acme/skills" + suffix);
        fixture.assertNoBlobs();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/tree/main/skills"})
    void hiddenSkillsDoNotMakeVisibleSkillDiscoveryAmbiguous(String suffix) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        String skillSha =
                fixture.file("skills/story/SKILL.md", SKILL).path("sha").asText();
        fixture.file(".cache/SKILL.md", "Hidden root skill");
        fixture.file("skills/.hidden/SKILL.md", "Hidden sibling skill");
        fixture.file("skills/story/.history/SKILL.md", "Hidden nested skill");

        SkillSourceBundle bundle = fixture.fetch("https://github.com/acme/skills" + suffix);

        assertEquals(Map.of("SKILL.md", SKILL), bundle.files());
        assertEquals(URI.create("https://github.com/acme/skills/tree/" + COMMIT + "/skills/story"), bundle.source());
        assertEquals(
                List.of(API + "/git/blobs/" + skillSha),
                fixture.paths().stream()
                        .filter(path -> path.contains("/git/blobs/"))
                        .toList());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "/tree/main/skills"})
    void hiddenSkillsAloneAreNeverAutoDiscovered(String suffix) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        fixture.file("skills/.hidden/SKILL.md", SKILL);
        fixture.file("skills/.cache/nested/SKILL.md", SKILL);

        failure(SKILL_INVALID_FORMAT, fixture, "https://github.com/acme/skills" + suffix);
        fixture.assertNoBlobs();
    }

    @Test
    void explicitHiddenScopesNeverSelectHiddenSkillsOrFallBackToVisibleSiblings() {
        for (String ref : List.of("main", COMMIT)) {
            for (String scope : List.of(".hidden", "skills/.hidden")) {
                for (String path : List.of("tree/" + ref + "/" + scope, "blob/" + ref + "/" + scope + "/SKILL.md")) {
                    Fixture fixture = new Fixture();
                    fixture.branches("main");
                    fixture.file("SKILL.md", SKILL);
                    fixture.file("skills/story/SKILL.md", SKILL);
                    fixture.file(scope + "/SKILL.md", "Hidden skill");
                    fixture.file(scope + "/nested/SKILL.md", "Hidden descendant");

                    failure(SKILL_INVALID_FORMAT, fixture, "https://github.com/acme/skills/" + path);
                    fixture.assertNoBlobs();
                }
            }
        }
    }

    @Test
    void rootSkillIsNotSkippedInFavorOfAnOtherwiseValidNestedSkill() {
        Fixture fixture = new Fixture();
        fixture.file("SKILL.md", SKILL).put("mode", "120000");
        fixture.file("skills/story/SKILL.md", SKILL);

        failure(SKILL_UNSUPPORTED_CONTENT, fixture);
        fixture.assertNoBlobs();
    }

    @Test
    void blobUrlRequiresThatExactSkillRatherThanADeeperDescendant() {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        fixture.file("skills/story/deeper/SKILL.md", SKILL);

        failure(SKILL_INVALID_FORMAT, fixture, "https://github.com/acme/skills/blob/main/skills/story/SKILL.md");
        fixture.assertNoBlobs();
    }

    @Test
    void missingSkillIsRejected() {
        Fixture fixture = new Fixture();
        fixture.file("README.md", "Not a skill");
        failure(SKILL_INVALID_FORMAT, fixture);
        fixture.assertNoBlobs();
    }

    @Test
    void truncatedTreeIsNeverTreatedAsACompleteSnapshot() {
        Fixture fixture = new Fixture();
        fixture.file("SKILL.md", SKILL);
        fixture.entry(".gitignore", "100644", "blob", OTHER, 1);
        fixture.tree.put("truncated", true);
        failure(SKILL_LIMIT_EXCEEDED, fixture);
        fixture.assertNoBlobs();
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ".hidden/"})
    void rejectsOversizedTreeAndMoreThan4096EntriesEvenWhenHidden(String prefix) {
        Fixture oversized = new Fixture();
        oversized.file("SKILL.md", SKILL);
        oversized.entry(prefix + "notes.md", "100644", "blob", OTHER, 1).put("padding", "x".repeat(1024 * 1024));
        failure(SKILL_LIMIT_EXCEEDED, oversized);
        oversized.assertNoBlobs();

        Fixture crowded = new Fixture();
        crowded.file("SKILL.md", SKILL);
        for (int i = 0; i < 4096; i++) {
            crowded.entry(prefix + "file" + i + ".md", "100644", "blob", OTHER, 1);
        }
        failure(SKILL_LIMIT_EXCEEDED, crowded);
        crowded.assertNoBlobs();
    }

    @Test
    void countsUtf8MetadataBytesRatherThanOnlyCharacters() {
        Fixture fixture = new Fixture();
        fixture.response(
                "",
                JSON.createObjectNode()
                        .put("private", false)
                        .put("default_branch", "main")
                        .put("padding", "界".repeat(90 * 1024)));
        failure(SKILL_LIMIT_EXCEEDED, fixture);
        assertEquals(1, fixture.requests.size());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "{\"private\":false,\"default_branch\":\"main\",\"default_branch\":\"other\"}",
                "{\"private\":false,\"default_branch\":\"main\",\"extra\":{\"x\":1,\"x\":2}}",
                "{\"private\":false,\"default_branch\":\"main\"} {}",
                "{\"private\":false,\"default_branch\":\"main\"} trailing",
                "{\"private\":false,\"default_branch\":\"main\",}",
                "{\"private\":false,\"default_branch\":\"main\",\"extra\":NaN}",
                "[]",
                "null"
            })
    void jsonParserRejectsDuplicatesTrailingTokensAndMalformedMetadata(String body) {
        Fixture fixture = new Fixture();
        fixture.response("", body);
        failure(SKILL_SOURCE_UNAVAILABLE, fixture);
        assertEquals(1, fixture.requests.size());
    }

    @Test
    void jsonNestingIsStrictlyBounded() {
        Fixture fixture = new Fixture();
        fixture.response(
                "",
                "{\"private\":false,\"default_branch\":\"main\",\"extra\":" + "[".repeat(17) + "0" + "]".repeat(17)
                        + "}");
        failure(SKILL_SOURCE_UNAVAILABLE, fixture);
    }

    @ParameterizedTest
    @ValueSource(strings = {"120000", "100755", "160000", "040000", "100600"})
    void rejectsSymlinksExecutablesSubmodulesAndSpecialModesBeforeAnyBlobRead(String mode) {
        Fixture fixture = new Fixture();
        fixture.file("SKILL.md", SKILL);
        fixture.file("references/z.md", "Not read")
                .put("mode", mode)
                .put("type", mode.equals("160000") ? "commit" : "blob");
        failure(SKILL_UNSUPPORTED_CONTENT, fixture);
        fixture.assertNoBlobs();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"run.sh", "package.json", "image.png", "archive.zip", "references/run.py", "LICENSE", "README"})
    void acceptsInertUtf8RegardlessOfFilenameWhileStillOmittingHiddenFiles(String path) {
        Fixture fixture = new Fixture();
        String skillSha = fixture.file("SKILL.md", SKILL).path("sha").asText();
        fixture.entry(".env", "100644", "blob", OTHER, 1);
        fixture.entry(".hidden/install.sh", "100755", "blob", OTHER, 1);
        String content = "Documentation only. Café, 汉字.\r\n";
        String sha = fixture.file(path, content).path("sha").asText();

        assertEquals(Map.of("SKILL.md", SKILL, path, content), fixture.fetch().files());
        assertEquals(
                Set.of(API + "/git/blobs/" + skillSha, API + "/git/blobs/" + sha),
                new HashSet<>(fixture.paths().stream()
                        .filter(request -> request.contains("/git/blobs/"))
                        .toList()));
    }

    @Test
    void preservesLicenseLocalizedReadmeConfigSpacesAndUnicodePaths() {
        Map<String, String> files = Map.of(
                "SKILL.md", SKILL,
                "LICENSE", "MIT license\n",
                "README.zh-CN.md", "使用说明\n",
                "config/rclone.conf.example", "[archive]\ntype = local\n",
                "reference notes/你好.txt", "Reference text\n",
                "cafe\u0301.txt", "Decomposed filename\n",
                "a..txt", "Two dots are not traversal\n",
                "docs.v1/guide", "Extensionless guide\n");
        Fixture fixture = new Fixture();
        files.forEach((path, content) -> fixture.file(path, content));

        assertEquals(files, fixture.fetch().files());
        assertEquals(5 + files.size(), fixture.requests.size());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "../SKILL.md",
                "references/../payload.md",
                "/SKILL.md",
                "references\\payload.md",
                "references//payload.md",
                "references/%2e%2e/payload.md",
                "references/./payload.md",
                "notes:stream.txt",
                "notes?.txt",
                "CON.txt",
                "notes.txt ",
                "notes.txt.",
                ".hidden/../payload.md",
                ".hidden/./payload.md",
                "references/.hidden/nested/../payload.md",
                "references/.hidden/nested/./payload.md",
                "../.hidden/payload.md",
                "./.hidden/payload.md",
                ".hidden/.",
                ".hidden/..",
                ".hidden//payload.md",
                ".hidden/notes\\payload.md",
                ".hidden/payload.md/"
            })
    void rejectsTraversalAndConfusableTreePaths(String path) {
        Fixture fixture = new Fixture();
        fixture.file("SKILL.md", SKILL);
        fixture.entry(path, "100644", "blob", OTHER, 1);
        failure(SKILL_INVALID_FORMAT, fixture);
        fixture.assertNoBlobs();
    }

    @Test
    void validatesSelectedRootAncestorsAndRejectsMissingParentDirectories() {
        Fixture symlink = new Fixture();
        symlink.entry("skills", "120000", "blob", OTHER, 1);
        symlink.file("skills/story/SKILL.md", SKILL);
        failure(SKILL_UNSUPPORTED_CONTENT, symlink);
        symlink.assertNoBlobs();

        Fixture missing = new Fixture();
        missing.entry("skills/story/SKILL.md", "100644", "blob", OTHER, 1);
        failure(SKILL_INVALID_FORMAT, missing);
        missing.assertNoBlobs();
    }

    @Test
    void rejectsCaseCollidingFilesDirectoriesAndDuplicateEntries() {
        Fixture files = new Fixture();
        files.file("SKILL.md", SKILL);
        files.file("references/Guide.md", "A");
        files.file("references/guide.md", "B");
        failure(SKILL_INVALID_FORMAT, files);
        files.assertNoBlobs();

        Fixture directories = new Fixture();
        directories.file("SKILL.md", SKILL);
        directories.file("references/One/a.md", "A");
        directories.file("references/one/b.md", "B");
        failure(SKILL_INVALID_FORMAT, directories);
        directories.assertNoBlobs();

        Fixture duplicate = new Fixture();
        ObjectNode entry = duplicate.file("SKILL.md", SKILL);
        duplicate.manifest.add(entry.deepCopy());
        failure(SKILL_INVALID_FORMAT, duplicate);
        duplicate.assertNoBlobs();
    }

    @ParameterizedTest
    @ValueSource(strings = {"cafe\u0301", "CAFE\u0301"})
    void rejectsUnicodeCanonicalAliasesForFilesAndDirectoriesBeforeBlobReads(String alias) {
        Fixture files = new Fixture();
        files.file("SKILL.md", SKILL);
        files.file("notes/café.md", "A");
        files.file("notes/" + alias + ".md", "B");
        failure(SKILL_INVALID_FORMAT, files);
        files.assertNoBlobs();

        Fixture directories = new Fixture();
        directories.file("SKILL.md", SKILL);
        directories.file("café/a.md", "A");
        directories.file(alias + "/b.md", "B");
        failure(SKILL_INVALID_FORMAT, directories);
        directories.assertNoBlobs();
    }

    @Test
    void enforcesLogicalPathDepthLengthAndSafeDirectoryNamesThroughSharedValidator() {
        for (String path : List.of(
                "references/a/b/c/d/e/f/g/h.md",
                "references/" + "a".repeat(60) + "/" + "b".repeat(60) + "/notes.md",
                "CON.txt/instructions.md")) {
            Fixture fixture = new Fixture();
            fixture.file("SKILL.md", SKILL);
            fixture.file(path, "Notes");
            assertThrows(SkillException.class, fixture::fetch);
            fixture.assertNoBlobs();
        }
    }

    @Test
    void checksFileCountIndividualSizesAndTotalBeforeAnyContents() {
        Fixture count = new Fixture();
        count.file("SKILL.md", SKILL);
        for (int i = 0; i < 16; i++) count.file("note" + i + ".md", "Note");
        failure(SKILL_LIMIT_EXCEEDED, count);
        count.assertNoBlobs();

        for (String path : List.of("SKILL.md", "references/guide.md")) {
            Fixture size = new Fixture();
            if (!path.equals("SKILL.md")) size.file("SKILL.md", SKILL);
            size.file(path, "Note").put("size", (path.equals("SKILL.md") ? 32 : 64) * 1024 + 1);
            failure(SKILL_LIMIT_EXCEEDED, size);
            size.assertNoBlobs();
        }

        Fixture total = new Fixture();
        total.file("SKILL.md", SKILL);
        for (int i = 0; i < 4; i++) total.file("note" + i + ".md", "Note").put("size", 64 * 1024);
        failure(SKILL_LIMIT_EXCEEDED, total);
        total.assertNoBlobs();
    }

    @Test
    void sixteenFilesStayWellWithinTheShared32RequestBudget() {
        Fixture fixture = new Fixture();
        fixture.branches("feature", "feature/docs");
        fixture.file("SKILL.md", SKILL);
        for (int i = 0; i < 15; i++) fixture.file("note" + i + ".md", "Note " + i);
        for (int i = 0; i < 17; i++) {
            fixture.entry(".hidden/note" + i + ".md", "100644", "blob", OTHER, 64 * 1024);
        }

        assertEquals(
                16,
                fixture.fetch("https://github.com/acme/skills/tree/feature/docs")
                        .files()
                        .size());
        assertEquals(API, fixture.paths().getFirst());
        assertEquals(API, fixture.paths().getLast());
        assertEquals(22, fixture.requests.size());
    }

    @Test
    void responseUrlsAndRepositoryIdentityNeverControlRequestDestinations() {
        Fixture fixture = new Fixture();
        fixture.response(
                "",
                JSON.createObjectNode()
                        .put("private", false)
                        .put("default_branch", "main")
                        .put("full_name", "attacker/repository")
                        .put("url", "http://127.0.0.1/private")
                        .put("trees_url", "https://attacker.invalid/tree"));
        ObjectNode skill = fixture.file("SKILL.md", SKILL);
        skill.put("url", "http://127.0.0.1/private").put("download_url", "https://attacker.invalid/skill");
        fixture.tree.put("url", "https://attacker.invalid/tree");
        ObjectNode response = blob(SKILL.getBytes(StandardCharsets.UTF_8));
        response.put("url", "https://attacker.invalid/blob").put("download_url", "https://attacker.invalid/raw");
        fixture.response("/git/blobs/" + skill.path("sha").asText(), response);

        assertEquals(SKILL, fixture.fetch().files().get("SKILL.md"));
        assertTrue(fixture.requests.stream().allMatch(uri -> uri.getHost().equals("api.github.com")));
        assertTrue(fixture.paths().stream().allMatch(path -> path.startsWith(API)));
    }

    @ParameterizedTest
    @ValueSource(
            strings = {"../other", "main?token=secret", "main#fragment", "https://attacker.invalid", "main/%2fother"})
    void metadataCannotInjectApiPathsOrQueries(String branch) {
        Fixture fixture = new Fixture();
        fixture.response("", JSON.createObjectNode().put("private", false).put("default_branch", branch));
        failure(SKILL_SOURCE_UNAVAILABLE, fixture);
        assertEquals(1, fixture.requests.size());
    }

    @Test
    void requiresExactRefCommitAndTreeIdentityAndWellTypedManifest() {
        Fixture ref = new Fixture();
        ref.response("/git/ref/heads/main", ref("different"));
        failure(SKILL_SOURCE_UNAVAILABLE, ref);

        Fixture commit = new Fixture();
        commit.response("/git/commits/" + COMMIT, JSON.createObjectNode().put("sha", OTHER));
        failure(SKILL_SOURCE_UNAVAILABLE, commit);

        Fixture tree = new Fixture();
        tree.tree.put("sha", OTHER);
        failure(SKILL_SOURCE_UNAVAILABLE, tree);

        Fixture treeUrl = new Fixture();
        ObjectNode maliciousCommit = JSON.createObjectNode().put("sha", COMMIT);
        maliciousCommit.putObject("tree").put("sha", "../../secret");
        treeUrl.response("/git/commits/" + COMMIT, maliciousCommit);
        failure(SKILL_SOURCE_UNAVAILABLE, treeUrl);
        assertEquals(3, treeUrl.requests.size());

        Fixture malformed = new Fixture();
        malformed.tree.put("truncated", "false");
        failure(SKILL_SOURCE_UNAVAILABLE, malformed);
    }

    @Test
    void missingOrInvalidRefLookupNeverFallsBackToAnAlternativeScope() {
        for (JsonNode response : List.of(
                JSON.createArrayNode(),
                JSON.createObjectNode(),
                JSON.createArrayNode().add(ref("different")))) {
            Fixture fixture = new Fixture();
            fixture.response("/git/matching-refs/heads/main", response);
            failure(SKILL_SOURCE_UNAVAILABLE, fixture, "https://github.com/acme/skills/tree/main/skills/story");
            assertEquals(List.of(API, API + "/git/matching-refs/heads/main"), fixture.paths());
        }
    }

    @Test
    void missingBlobsAndTransportFailuresAreSanitized() {
        Fixture fixture = new Fixture();
        ObjectNode entry = fixture.file("SKILL.md", SKILL);
        fixture.responses.remove(API + "/git/blobs/" + entry.path("sha").asText());
        failure(SKILL_SOURCE_UNAVAILABLE, fixture);

        SkillException failure = assertThrows(
                SkillException.class,
                () -> new GithubSkillSource()
                        .fetch(URI.create("https://github.com/acme/skills"), (uri, types, limit) -> {
                            throw new IllegalStateException("private-source-token");
                        }));
        assertEquals(SKILL_SOURCE_UNAVAILABLE.name(), failure.getMessage());
        assertNull(failure.getCause());
    }

    @Test
    void blobShaDeclaredSizeAndGitObjectHashMustAllMatch() {
        for (String mutation : List.of("sha", "size", "content", "encoding")) {
            Fixture fixture = new Fixture();
            ObjectNode entry = fixture.file("SKILL.md", "a");
            ObjectNode response = blob(new byte[] {'a'});
            switch (mutation) {
                case "sha" -> response.put("sha", OTHER);
                case "size" -> response.put("size", 2);
                case "content" -> response.put("content", "Yg==");
                case "encoding" -> response.put("encoding", "utf-8");
                default -> fail("Unexpected mutation");
            }
            fixture.response("/git/blobs/" + entry.path("sha").asText(), response);
            failure(SKILL_SOURCE_UNAVAILABLE, fixture);
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"!g==", "Zg", "Zg=", "Zh==", " Zg==", "Zg==\r", "Zg==Zg=="})
    void rejectsMalformedOrNoncanonicalBase64(String encoded) {
        Fixture fixture = new Fixture();
        ObjectNode entry = fixture.file("SKILL.md", "f");
        fixture.response(
                "/git/blobs/" + entry.path("sha").asText(),
                blob(new byte[] {'f'}).put("content", encoded));
        failure(SKILL_SOURCE_UNAVAILABLE, fixture);
    }

    @Test
    void verifiesAgainstAnIndependentKnownGitBlobHash() {
        Fixture fixture = new Fixture();
        String knownSha = "ce013625030ba8dba906f756967f9e9ca394464a";
        fixture.entry("SKILL.md", "100644", "blob", knownSha, 6);
        fixture.response(
                "/git/blobs/" + knownSha,
                JSON.createObjectNode()
                        .put("sha", knownSha)
                        .put("size", 6)
                        .put("encoding", "base64")
                        .put("content", "aGVsbG8K"));
        assertEquals("hello\n", fixture.fetch().files().get("SKILL.md"));
    }

    @Test
    void acceptsWrappedBase64ButDoesNotNormalizeDecodedDocuments() {
        Fixture fixture = new Fixture();
        ObjectNode entry = fixture.file("SKILL.md", SKILL);
        ObjectNode response = blob(SKILL.getBytes(StandardCharsets.UTF_8));
        String encoded = response.path("content").asText();
        response.put("content", encoded.substring(0, 60) + "\r\n" + encoded.substring(60) + "\n");
        fixture.response("/git/blobs/" + entry.path("sha").asText(), response);
        assertEquals(SKILL, fixture.fetch().files().get("SKILL.md"));
    }

    @Test
    void rejectsInvalidUtf8EvenWithValidSizeAndGitHash() {
        for (byte[] bytes :
                List.of(new byte[] {(byte) 0xc3, 0x28}, new byte[] {(byte) 0xed, (byte) 0xa0, (byte) 0x80}, new byte[] {
                    (byte) 0xff
                })) {
            Fixture fixture = new Fixture();
            fixture.file("SKILL.md", bytes);
            failure(SKILL_UNSUPPORTED_CONTENT, fixture);
        }
    }

    @Test
    void strictJsonAndSizeLimitsAlsoApplyToBlobResponses() {
        Fixture duplicate = new Fixture();
        String sha = duplicate.file("SKILL.md", "f").path("sha").asText();
        duplicate.response("/git/blobs/" + sha, "{\"size\":1,\"size\":1}");
        failure(SKILL_SOURCE_UNAVAILABLE, duplicate);

        Fixture oversized = new Fixture();
        String otherSha = oversized.file("SKILL.md", "f").path("sha").asText();
        oversized.response("/git/blobs/" + otherSha, " ".repeat(256 * 1024 + 1));
        failure(SKILL_LIMIT_EXCEEDED, oversized);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "%2f",
                "%2F",
                "a%2fb",
                "%5c",
                "a%5Cb",
                "%25",
                "%252e",
                "%252e%252e",
                "%252f",
                "%255c",
                "%25GG",
                ".",
                "..",
                "%2e",
                "%2E%2e",
                ".%2e",
                "%2e.",
                "a//b",
                "%80",
                "%FF",
                "%C3%28",
                "%E6%8A",
                "%E6%8A文",
                "%C0%AF",
                "%E0%80%AF",
                "%ED%A0%80",
                "%ED%B0%80",
                "%F0%80%80%AF",
                "%F4%90%80%80",
                "%00",
                "%09",
                "%0A",
                "%0D",
                "%1F",
                "%7F",
                "%C2%85",
                "%E2%80%8B",
                "%E2%80%AE",
                "%EF%BB%BF",
                "%F3%A0%80%81",
                "before\u200Bafter"
            })
    void unsafeDecodedSegmentsAreRejectedBeforeAnyNetworkForBranchesAndPins(String segment) {
        for (String ref : List.of("main", COMMIT)) {
            for (String kind : List.of("tree", "blob")) {
                Fixture fixture = new Fixture();
                failure(
                        SKILL_SOURCE_NOT_ALLOWED,
                        fixture,
                        "https://github.com/acme/skills/" + kind + "/" + ref + "/skills/" + segment
                                + (kind.equals("blob") ? "/SKILL.md" : ""));
                assertTrue(fixture.requests.isEmpty());
            }
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"%", "%2", "%GG", "%0G", "%G0", "%ＦＦ"})
    void malformedPercentEscapesAreRejectedByUriParsingBeforeAnyNetwork(String segment) {
        Fixture fixture = new Fixture();

        assertThrows(
                IllegalArgumentException.class,
                () -> fixture.fetch("https://github.com/acme/skills/tree/main/" + segment));
        assertTrue(fixture.requests.isEmpty());
    }

    @Test
    void rawPathSizeAndSegmentBudgetsStillApplyBeforeDecodingOrNetwork() {
        for (String path : List.of(
                "/acme/skills/tree/main/" + "a".repeat(2048),
                "/acme/skills/tree/main/" + "%61".repeat(700),
                "/acme/skills/tree/main/" + "技能".repeat(1024),
                "/acme/skills/tree/main/" + "a/".repeat(28) + "a")) {
            Fixture fixture = new Fixture();
            failure(SKILL_SOURCE_NOT_ALLOWED, fixture, "https://github.com" + path);
            assertTrue(fixture.requests.isEmpty());
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "技能",
                "%E6%8A%80%E8%83%BD",
                "main%20docs",
                "main+docs",
                "main%2Bdocs",
                ".main",
                "main.",
                "main.lock",
                "main..docs"
            })
    void directoryDecodingDoesNotRelaxFirstBranchComponentPolicy(String branch) {
        Fixture fixture = new Fixture();

        failure(SKILL_SOURCE_UNAVAILABLE, fixture, "https://github.com/acme/skills/tree/" + branch + "/技能%20文档");

        assertEquals(List.of(API), fixture.paths());
    }

    @ParameterizedTest
    @ValueSource(strings = {"main/技能", "main/docs notes", "main/docs+notes", "main/.hidden", "main/docs.lock"})
    void directoryDecodingDoesNotRelaxMatchingBranchPolicy(String branch) {
        Fixture fixture = new Fixture();
        fixture.branches("main", branch);

        failure(SKILL_SOURCE_UNAVAILABLE, fixture, "https://github.com/acme/skills/tree/main/技能%20文档");

        assertEquals(List.of(API, API + "/git/matching-refs/heads/main"), fixture.paths());
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "http://github.com/acme/skills",
                "https://api.github.com/acme/skills",
                "https://github.com.attacker.invalid/acme/skills",
                "https://user:token@github.com/acme/skills",
                "https://github.com:443/acme/skills",
                "https://github.com/acme/skills?token=x",
                "https://github.com/acme/skills#main",
                "https://github.com/acme/skills/../other",
                "https://github.com/acme/skills/tree/main/%2e%2e",
                "https://github.com/acme/skills/tree/main%2fother",
                "https://github.com/acme/skills//",
                "https://github.com/acme/skills/blob/main/README.md",
                "https://github.com/acme/skills/tree",
                "https://github.com/acme/skills/raw/main/SKILL.md",
                "https://github.com/acme/.git",
                "https://github.com",
                "https://github.com/",
                "https://github.com/acme",
                "HTTPS://github.com/acme/skills",
                "https://GitHub.com/acme/skills",
                "https://www.github.com/acme/skills",
                "https://@github.com/acme/skills",
                "https://github.com/acme/skills?",
                "https://github.com/acme/skills#",
                "https://github.com/技能/skills",
                "https://github.com/%E6%8A%80%E8%83%BD/skills",
                "https://github.com/acme/技能",
                "https://github.com/acme/%E6%8A%80%E8%83%BD",
                "https://github.com/acme%20owner/skills",
                "https://github.com/acme/skills%20repo",
                "https://github.com/acme+owner/skills",
                "https://github.com/acme/skills+repo"
            })
    void noncanonicalUnsafeOrUnsupportedInputNeverReachesReader(String source) {
        Fixture fixture = new Fixture();
        failure(SKILL_SOURCE_NOT_ALLOWED, fixture, source);
        assertTrue(fixture.requests.isEmpty());
    }

    private static List<String> repositorySources() {
        return List.of(
                "https://github.com/acme/skills",
                "https://github.com/acme/skills/tree/main/skills/story",
                "https://github.com/acme/skills/blob/main/skills/story/SKILL.md",
                "https://github.com/acme/skills/tree/" + COMMIT + "/skills/story",
                "https://github.com/acme/skills/blob/" + COMMIT + "/skills/story/SKILL.md");
    }

    private static List<String> invalidPrivacyMetadata() {
        return List.of(
                "{\"default_branch\":\"main\"}",
                "{\"default_branch\":\"main\",\"private\":null}",
                "{\"default_branch\":\"main\",\"private\":\"false\"}",
                "{\"default_branch\":\"main\",\"private\":\"true\"}",
                "{\"default_branch\":\"main\",\"private\":\"\"}",
                "{\"default_branch\":\"main\",\"private\":0}",
                "{\"default_branch\":\"main\",\"private\":1}",
                "{\"default_branch\":\"main\",\"private\":[]}",
                "{\"default_branch\":\"main\",\"private\":{}}");
    }

    private static void assertRejectedAfterBlobDownload(String source, String metadata, SkillException.Code code) {
        Fixture fixture = new Fixture();
        fixture.branches("main");
        String skillBlob = API + "/git/blobs/"
                + fixture.file("skills/story/SKILL.md", SKILL).path("sha").asText();
        String notesBlob = API + "/git/blobs/"
                + fixture.file("skills/story/references/notes.md", "Notes")
                        .path("sha")
                        .asText();
        SkillSourceReader changingVisibility = (uri, mediaTypes, limit) -> {
            String body = fixture.get(uri, mediaTypes, limit);
            // Change visibility during the last blob read so an earlier recheck cannot pass this test.
            if (uri.getPath().equals(notesBlob)) {
                fixture.response("", metadata);
            }
            return body;
        };

        SkillException failure = assertThrows(
                SkillException.class,
                () -> new GithubSkillSource().fetch(URI.create(source), changingVisibility),
                source);

        assertEquals(code, failure.code(), source);
        assertEquals(code.name(), failure.getMessage());
        assertNull(failure.getCause());
        List<String> paths = fixture.paths();
        assertEquals(API, paths.getFirst(), source);
        assertEquals(2, paths.stream().filter(API::equals).count(), source);
        assertEquals(List.of(skillBlob, notesBlob, API), paths.subList(paths.size() - 3, paths.size()), source);
    }

    private static void failure(SkillException.Code code, Fixture fixture) {
        failure(code, fixture, "https://github.com/acme/skills");
    }

    private static void failure(SkillException.Code code, Fixture fixture, String source) {
        SkillException failure = assertThrows(SkillException.class, () -> fixture.fetch(source));
        assertEquals(code, failure.code());
        assertEquals(code.name(), failure.getMessage());
        assertNull(failure.getCause());
    }

    private static ObjectNode ref(String branch) {
        ObjectNode ref =
                JSON.createObjectNode().put("ref", "refs/heads/" + branch).put("url", "https://attacker.invalid/ref");
        ref.putObject("object").put("type", "commit").put("sha", COMMIT).put("url", "https://attacker.invalid/commit");
        return ref;
    }

    private static ObjectNode blob(byte[] bytes) {
        return JSON.createObjectNode()
                .put("sha", hash(bytes))
                .put("size", bytes.length)
                .put("encoding", "base64")
                .put("content", Base64.getEncoder().encodeToString(bytes));
    }

    private static String hash(byte[] bytes) {
        try {
            MessageDigest hash = MessageDigest.getInstance("SHA-1");
            hash.update(("blob " + bytes.length + "\0").getBytes(StandardCharsets.US_ASCII));
            return HexFormat.of().formatHex(hash.digest(bytes));
        } catch (Exception impossible) {
            throw new AssertionError(impossible);
        }
    }

    private static final class Fixture implements SkillSourceReader {
        private final Map<String, String> responses = new HashMap<>();
        private final List<URI> requests = new ArrayList<>();
        private final Set<String> paths = new HashSet<>();
        private final ObjectNode tree = JSON.createObjectNode().put("sha", TREE).put("truncated", false);
        private final ArrayNode manifest = tree.putArray("tree");

        private Fixture() {
            response("", JSON.createObjectNode().put("private", false).put("default_branch", "main"));
            response("/git/ref/heads/main", ref("main"));
            ObjectNode commit = JSON.createObjectNode().put("sha", COMMIT);
            commit.putObject("tree").put("sha", TREE).put("url", "https://attacker.invalid/tree");
            response("/git/commits/" + COMMIT, commit);
        }

        private void branches(String... branches) {
            ArrayNode refs = JSON.createArrayNode();
            for (String branch : branches) {
                refs.add(ref(branch));
                response("/git/ref/heads/" + branch, ref(branch));
            }
            response("/git/matching-refs/heads/" + branches[0].split("/")[0], refs);
        }

        private ObjectNode file(String path, String content) {
            return file(path, content.getBytes(StandardCharsets.UTF_8));
        }

        private ObjectNode file(String path, byte[] bytes) {
            for (int slash = path.indexOf('/'); slash >= 0; slash = path.indexOf('/', slash + 1)) {
                String parent = path.substring(0, slash);
                if (!paths.contains(parent)) entry(parent, "040000", "tree", TREE, -1);
            }
            String sha = hash(bytes);
            response("/git/blobs/" + sha, blob(bytes));
            return entry(path, "100644", "blob", sha, bytes.length);
        }

        private ObjectNode entry(String path, String mode, String type, String sha, long size) {
            ObjectNode entry = manifest.addObject()
                    .put("path", path)
                    .put("mode", mode)
                    .put("type", type)
                    .put("sha", sha);
            if (size >= 0) entry.put("size", size);
            paths.add(path);
            return entry;
        }

        private void response(String suffix, JsonNode node) {
            response(suffix, node.toString());
        }

        private void response(String suffix, String body) {
            responses.put(API + suffix, body);
        }

        private SkillSourceBundle fetch() {
            return fetch("https://github.com/acme/skills");
        }

        private SkillSourceBundle fetch(String source) {
            return new GithubSkillSource().fetch(URI.create(source), this);
        }

        private List<String> paths() {
            return requests.stream().map(Fixture::path).toList();
        }

        private static String path(URI uri) {
            return uri.getRawPath() + (uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery());
        }

        private void assertNoBlobs() {
            assertFalse(paths().stream().anyMatch(path -> path.contains("/git/blobs/")));
        }

        @Override
        public String get(URI uri, Set<String> mediaTypes, int maxBytes) {
            requests.add(uri);
            assertTrue(requests.size() <= 32);
            assertEquals("https", uri.getScheme());
            assertEquals("api.github.com", uri.getHost());
            assertNull(uri.getRawUserInfo());
            assertEquals(-1, uri.getPort());
            assertNull(uri.getFragment());
            assertEquals(Set.of("application/json", "application/vnd.github+json"), mediaTypes);
            String path = path(uri);
            assertTrue(path.startsWith(API));
            assertEquals(path.contains("/git/trees/") ? 1024 * 1024 : 256 * 1024, maxBytes);
            String response = responses.get(path);
            if (response != null) return response;
            if (path.equals(API + "/git/trees/" + TREE + "?recursive=1")) return tree.toString();
            throw new IllegalStateException("private-source-token: unscripted response");
        }
    }
}
