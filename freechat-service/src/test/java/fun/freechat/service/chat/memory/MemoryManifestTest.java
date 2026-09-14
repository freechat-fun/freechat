package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemoryManifestTest {
    private static final String HASH = "abcdef0123456789".repeat(4);
    private static final String PRIVATE_TEXT = "private-memory-payload";

    @Test
    void acceptsEverySizeFromOneThroughOneHundredButRejectsEmptyAndOversized() {
        for (int size = 1; size <= 100; size++) {
            MemoryManifest manifest = new MemoryManifest(entries(size));
            assertEquals(size, manifest.entries().size());
            assertEquals(manifest, MemoryDocumentCodec.decodeManifest(MemoryDocumentCodec.encodeManifest(manifest)));
        }
        assertSanitized(assertThrows(IllegalArgumentException.class, () -> new MemoryManifest(List.of())));
        assertSanitized(assertThrows(IllegalArgumentException.class, () -> new MemoryManifest(entries(101))));
    }

    @Test
    void defensivelyCopiesEntriesAndReturnsImmutableOrderedIds() {
        List<MemoryManifest.Entry> source = new ArrayList<>(entries(2));
        MemoryManifest manifest = new MemoryManifest(source);
        source.clear();
        assertEquals(entries(2), manifest.entries());
        assertEquals(List.of(id(1), id(2)), manifest.ids());
        assertThrows(
                UnsupportedOperationException.class, () -> manifest.entries().clear());
        assertThrows(UnsupportedOperationException.class, () -> manifest.ids().clear());
        assertEquals(manifest.entries().getFirst(), manifest.find(id(1)).orElseThrow());
        assertTrue(manifest.find(id(3)).isEmpty());
    }

    @Test
    void rejectsDuplicateIdsEvenWhenOtherEntryFieldsDifferAndRejectsNullEntries() {
        MemoryManifest.Entry first = entries(1).getFirst();
        MemoryManifest.Entry duplicate =
                new MemoryManifest.Entry(first.id(), MemoryDocument.Kind.WINDOW_SUMMARY, true, "0".repeat(64));
        assertSanitized(
                assertThrows(IllegalArgumentException.class, () -> new MemoryManifest(List.of(first, duplicate))));
        assertSanitized(assertThrows(RuntimeException.class, () -> new MemoryManifest(null)));
        assertSanitized(assertThrows(RuntimeException.class, () -> new MemoryManifest(Arrays.asList(first, null))));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {
                "",
                "1-1-1-1-1",
                "12345678-1234-4ABC-8ABC-123456789ABC",
                "1234567812344abc8abc123456789abc",
                " 12345678-1234-4abc-8abc-123456789abc",
                PRIVATE_TEXT
            })
    void rejectsNoncanonicalIds(String value) {
        assertSanitized(assertThrows(
                IllegalArgumentException.class,
                () -> new MemoryManifest.Entry(value, MemoryDocument.Kind.EPISODE_SUMMARY, false, HASH)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(
            strings = {
                "",
                "abc",
                "ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789",
                "g000000000000000000000000000000000000000000000000000000000000000",
                PRIVATE_TEXT
            })
    void rejectsInvalidHashes(String hash) {
        assertSanitized(assertThrows(
                IllegalArgumentException.class,
                () -> new MemoryManifest.Entry(id(1), MemoryDocument.Kind.EPISODE_SUMMARY, false, hash)));
    }

    @Test
    void requiresKnownKindAndOnlyWindowSummariesCanBeArchived() {
        assertSanitized(
                assertThrows(IllegalArgumentException.class, () -> new MemoryManifest.Entry(id(1), null, false, HASH)));
        for (MemoryDocument.Kind kind : MemoryDocument.Kind.values()) {
            MemoryManifest.Entry entry = new MemoryManifest.Entry(id(1), kind, false, HASH);
            assertEquals(kind == MemoryDocument.Kind.EPISODE_SUMMARY, entry.discoverable());
            if (kind == MemoryDocument.Kind.WINDOW_SUMMARY) {
                assertTrue(new MemoryManifest.Entry(id(1), kind, true, HASH).discoverable());
            } else {
                assertSanitized(assertThrows(
                        IllegalArgumentException.class, () -> new MemoryManifest.Entry(id(1), kind, true, HASH)));
            }
        }
    }

    @Test
    void derivesHashFromExactUtf8EncodedDocumentAndPreservesOrder() throws Exception {
        LongTermMemoryProperties properties = new LongTermMemoryProperties();
        MemoryDocumentCodec codec = new MemoryDocumentCodec(
                new MemoryBounds(MemoryBoundsTest.estimator(ignored -> 1), properties), properties);
        MemoryScope scope = new MemoryScope("chat", "user", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
        MemoryDocument first = new MemoryDocument(
                id(2),
                scope,
                id(1000),
                MemoryDocument.Kind.WINDOW_SUMMARY,
                true,
                1,
                2,
                Instant.parse("2026-09-12T10:00:00Z"),
                HASH,
                "A remembered conversation 中文",
                List.of(),
                List.of());
        MemoryDocument second = new MemoryDocument(
                id(1),
                scope,
                id(1000),
                MemoryDocument.Kind.PROFILE_SNAPSHOT,
                false,
                1,
                2,
                first.observedAt(),
                HASH,
                "",
                List.of(),
                List.of());
        MemoryManifest manifest = MemoryManifest.of(List.of(first, second), codec);
        assertEquals(List.of(first.id(), second.id()), manifest.ids());
        assertEquals(first.kind(), manifest.entries().getFirst().kind());
        assertTrue(manifest.entries().getFirst().archive());
        for (MemoryDocument document : List.of(first, second)) {
            String expected = HexFormat.of()
                    .formatHex(MessageDigest.getInstance("SHA-256")
                            .digest(codec.encode(document).text().getBytes(StandardCharsets.UTF_8)));
            assertEquals(expected, manifest.find(document.id()).orElseThrow().hash());
        }
    }

    @Test
    void encodingIsOnlyTheStrictManifestObjectWithoutDocumentPayload() throws Exception {
        MemoryManifest manifest = new MemoryManifest(entries(1));
        String encoded = MemoryDocumentCodec.encodeManifest(manifest);
        assertEquals(jsonEntry(id(1), "EPISODE_SUMMARY", "false", HASH), encoded);
        ObjectNode tree = (ObjectNode) new ObjectMapper().readTree(encoded);
        assertEquals(1, tree.size());
        assertEquals(4, tree.withArray("entries").get(0).size());
        assertEquals(manifest, MemoryDocumentCodec.decodeManifest(" \n" + encoded + "\t "));
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidManifests")
    void decodingRejectsMalformedOrNoncanonicalInputWithoutPayloadBearingCauses(String name, String json) {
        assertSanitized(assertThrows(IllegalArgumentException.class, () -> MemoryDocumentCodec.decodeManifest(json)));
    }

    private static Stream<Arguments> invalidManifests() throws Exception {
        String valid = jsonEntry(id(1), "EPISODE_SUMMARY", "false", HASH);
        List<Arguments> cases = new ArrayList<>();
        cases.add(Arguments.of("null input", (String) null));
        cases.add(Arguments.of("empty input", ""));
        cases.add(Arguments.of("null root", "null"));
        cases.add(Arguments.of("wrong root type", "[]"));
        cases.add(Arguments.of("missing entries", "{}"));
        cases.add(Arguments.of("null entries", "{\"entries\":null}"));
        cases.add(Arguments.of("null entry", "{\"entries\":[null]}"));
        cases.add(Arguments.of("empty entries", "{\"entries\":[]}"));
        cases.add(Arguments.of("unknown root property", "{\"payload\":\"" + PRIVATE_TEXT + "\"," + valid.substring(1)));
        cases.add(Arguments.of("duplicate entries", valid.substring(0, valid.length() - 1) + "," + valid.substring(1)));
        cases.add(
                Arguments.of("duplicate entry property", valid.replace("\"id\":", "\"id\":\"" + id(1) + "\",\"id\":")));
        cases.add(Arguments.of("duplicate identifier", "{\"entries\":[" + validEntry() + "," + validEntry() + "]}"));
        cases.add(Arguments.of(
                "oversized manifest",
                new ObjectMapper().writeValueAsString(java.util.Map.of("entries", entries(101)))));
        cases.add(Arguments.of("prose prefix", PRIVATE_TEXT + valid));
        cases.add(Arguments.of("markdown fence", "```json\n" + valid + "\n```"));
        cases.add(Arguments.of("trailing object", valid + " {}"));
        cases.add(Arguments.of("trailing null", valid + " null"));
        cases.add(Arguments.of("trailing prose", valid + PRIVATE_TEXT));
        cases.add(Arguments.of("oversized input", " ".repeat(MemoryBounds.VECTOR_RECORD_BYTES) + valid));
        ObjectMapper mapper = new ObjectMapper();
        for (String field : List.of("id", "kind", "archive", "hash")) {
            ObjectNode missing = (ObjectNode) mapper.readTree(valid);
            ((ObjectNode) missing.withArray("entries").get(0)).remove(field);
            cases.add(Arguments.of("missing " + field, missing.toString()));
            ObjectNode explicitNull = (ObjectNode) mapper.readTree(valid);
            ((ObjectNode) explicitNull.withArray("entries").get(0)).putNull(field);
            cases.add(Arguments.of("null " + field, explicitNull.toString()));
        }
        cases.add(Arguments.of(
                "unknown entry property", valid.replace("\"id\":", "\"payload\":\"" + PRIVATE_TEXT + "\",\"id\":")));
        cases.add(Arguments.of("unknown kind", jsonEntry(id(1), PRIVATE_TEXT, "false", HASH)));
        cases.add(Arguments.of("numeric kind", valid.replace("\"EPISODE_SUMMARY\"", "0")));
        cases.add(Arguments.of("string archive", valid.replace("false", "\"false\"")));
        cases.add(Arguments.of("numeric archive", valid.replace("false", "0")));
        cases.add(Arguments.of("invalid archive kind", jsonEntry(id(1), "PROFILE_SNAPSHOT", "true", HASH)));
        cases.add(Arguments.of("noncanonical id", jsonEntry("1-1-1-1-1", "EPISODE_SUMMARY", "false", HASH)));
        cases.add(Arguments.of("invalid hash", jsonEntry(id(1), "EPISODE_SUMMARY", "false", PRIVATE_TEXT)));
        return cases.stream();
    }

    private static String validEntry() {
        String json = jsonEntry(id(1), "EPISODE_SUMMARY", "false", HASH);
        return json.substring("{\"entries\":[".length(), json.length() - 2);
    }

    private static String jsonEntry(String id, String kind, String archive, String hash) {
        return "{\"entries\":[{\"id\":\"" + id + "\",\"kind\":\"" + kind + "\",\"archive\":" + archive + ",\"hash\":\""
                + hash + "\"}]}";
    }

    private static List<MemoryManifest.Entry> entries(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(
                        index -> new MemoryManifest.Entry(id(index), MemoryDocument.Kind.EPISODE_SUMMARY, false, HASH))
                .toList();
    }

    private static String id(int number) {
        return new UUID(0x1234567812344000L, 0x8000000000000000L + number).toString();
    }

    private static void assertSanitized(RuntimeException failure) {
        assertNull(failure.getCause());
        assertEquals(0, failure.getSuppressed().length);
        assertFalse(failure.toString().contains(PRIVATE_TEXT));
    }
}
