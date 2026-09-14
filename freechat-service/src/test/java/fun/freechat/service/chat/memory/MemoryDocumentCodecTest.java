package fun.freechat.service.chat.memory;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import fun.freechat.service.enums.EmbeddingStoreType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class MemoryDocumentCodecTest {
    private static final String ID = UUID.randomUUID().toString();
    private static final String COMMIT = UUID.randomUUID().toString();
    private static final Instant OBSERVED = Instant.parse("2026-09-12T10:00:00Z");
    private static final String FINGERPRINT = MemoryDocumentCodec.hash("baseline");
    private static final MemoryScope SCOPE =
            new MemoryScope("chat", "user", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY);
    private final LongTermMemoryProperties properties = new LongTermMemoryProperties();
    private final MemoryDocumentCodec codec =
            new MemoryDocumentCodec(new MemoryBounds(MemoryBoundsTest.estimator(ignored -> 1), properties), properties);

    @ParameterizedTest
    @MethodSource("documents")
    void roundTripsEveryRecordKindWithImmutableScopeAndProvenance(MemoryDocument document) {
        TextSegment encoded = codec.encode(document);
        MemoryDocument decoded = codec.decode(encoded, SCOPE, ID, MemoryDocumentCodec.hash(encoded.text()));
        assertEquals(document, decoded);
        assertTrue(SCOPE.filter().test(encoded.metadata()));
        assertEquals("chat", encoded.metadata().getString("memory_id"));
        assertEquals(1, encoded.metadata().getInteger("schema_version"));
        assertTrue(MemoryBounds.bytes(encoded.text()) < MemoryBounds.VECTOR_RECORD_BYTES);
    }

    @Test
    void exactReadRequiresMatchingHashIdAndEveryScopeDimension() {
        TextSegment segment = codec.encode(summary());
        String hash = MemoryDocumentCodec.hash(segment.text());
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(segment, SCOPE, UUID.randomUUID().toString(), hash));
        assertThrows(IllegalArgumentException.class, () -> codec.decode(segment, SCOPE, ID, "0".repeat(64)));
        for (MemoryScope other : List.of(
                new MemoryScope("other", "user", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY),
                new MemoryScope("chat", "other", "character", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY),
                new MemoryScope("chat", "user", "other", 7, EmbeddingStoreType.EN_LONG_TERM_MEMORY),
                new MemoryScope("chat", "user", "character", 8, EmbeddingStoreType.EN_LONG_TERM_MEMORY),
                new MemoryScope("chat", "user", "character", 7, EmbeddingStoreType.ZH_LONG_TERM_MEMORY))) {
            assertFalse(other.filter().test(segment.metadata()));
            assertThrows(IllegalArgumentException.class, () -> codec.decode(segment, other, ID, hash));
        }
    }

    @Test
    void metadataMustAgreeWithHashedDocumentAndRejectLegacyPairs() {
        TextSegment segment = codec.encode(summary());
        String hash = MemoryDocumentCodec.hash(segment.text());
        for (String key : segment.metadata().toMap().keySet()) {
            Metadata missing = segment.metadata().copy();
            missing.remove(key);
            assertThrows(
                    IllegalArgumentException.class,
                    () -> codec.decode(TextSegment.from(segment.text(), missing), SCOPE, ID, hash));
        }
        Metadata forged = segment.metadata().copy().put("generation", 8L);
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(TextSegment.from(segment.text(), forged), SCOPE, ID, hash));
        Metadata unknown = segment.metadata().copy().put("new_privilege", "admin");
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(TextSegment.from(segment.text(), unknown), SCOPE, ID, hash));
        Metadata legacy = new Metadata().put("memory_id", "chat");
        assertFalse(SCOPE.filter().test(legacy));
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(TextSegment.from("[]", legacy), SCOPE, ID, MemoryDocumentCodec.hash("[]")));
    }

    @Test
    void sdkLongMetadataAndLargeSourceIdsDoNotLosePrecision() {
        MemoryDocument document = new MemoryDocument(
                ID,
                SCOPE,
                COMMIT,
                MemoryDocument.Kind.WINDOW_SUMMARY,
                true,
                Long.MAX_VALUE - 1,
                Long.MAX_VALUE,
                OBSERVED,
                FINGERPRINT,
                "Large source IDs",
                List.of(),
                List.of());
        TextSegment encoded = codec.encode(document);
        Metadata metadata = encoded.metadata().copy().put("schema_version", 1L).put("archive", 1L);
        assertEquals(
                document,
                codec.decode(
                        TextSegment.from(encoded.text(), metadata),
                        SCOPE,
                        ID,
                        MemoryDocumentCodec.hash(encoded.text())));
        metadata.put("source_end_id", Long.MAX_VALUE - 1);
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(
                        TextSegment.from(encoded.text(), metadata),
                        SCOPE,
                        ID,
                        MemoryDocumentCodec.hash(encoded.text())));
    }

    @Test
    void profilesPublishAsOnePairAndFactsMustBeTraceable() {
        MemoryDocument profile = profile();
        assertDoesNotThrow(() -> codec.validateSources(profile, Set.of(1L, 2L)));
        assertThrows(IllegalArgumentException.class, () -> codec.validateSources(profile, Set.of(1L)));
        assertEquals(1, profile.userFacts().size());
        assertEquals(1, profile.characterDeltas().size());
        assertFalse(profile.userFacts().getFirst().fictional());
        assertTrue(profile.characterDeltas().getFirst().fictional());
    }

    @Test
    void checkpointsAndRollingHeadsAreNeverDiscoverable() {
        assertTrue(summary().discoverable());
        assertFalse(profile().discoverable());
        assertFalse(document(MemoryDocument.Kind.EXTRACTION_CHECKPOINT, false, "checkpoint", List.of(), List.of())
                .discoverable());
        assertFalse(document(MemoryDocument.Kind.WINDOW_SUMMARY, false, "rolling", List.of(), List.of())
                .discoverable());
        assertTrue(document(MemoryDocument.Kind.WINDOW_SUMMARY, true, "archive chunk", List.of(), List.of())
                .discoverable());
    }

    @Test
    void copiesFactAndProfileListsBeforeTheyCanBeMutated() {
        List<Long> sources = new ArrayList<>(List.of(1L));
        MemoryDocument.Fact fact =
                new MemoryDocument.Fact("preference", "explicit preference", sources, OBSERVED, false);
        sources.clear();
        List<MemoryDocument.Fact> facts = new ArrayList<>(List.of(fact));
        MemoryDocument document = document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", facts, List.of());
        facts.clear();
        assertEquals(List.of(1L), document.userFacts().getFirst().sourceIds());
        assertThrows(
                UnsupportedOperationException.class, () -> document.userFacts().clear());
        assertThrows(UnsupportedOperationException.class, () -> fact.sourceIds().clear());
    }

    @Test
    void rejectsUnknownPrivilegedMissingDuplicateAndTrailingFields() {
        assertInvalidJson(root -> root.put("permissions", "admin"));
        assertInvalidJson(root -> root.remove("summary"));
        assertInvalidJson(root -> root.putNull("summary"));
        assertInvalidJson(root -> root.put("kind", 0));
        assertInvalidJson(root -> root.put("sourceStartId", "1"));
        assertInvalidJson(root -> root.put("sourceStartId", 1.5));
        assertInvalidJson(root -> root.putNull("archive"));
        assertInvalidJson(root -> root.put("archive", "false"));
        TextSegment original = codec.encode(summary());
        assertRejected(original, original.text() + " {}");
        assertRejected(original, "{\"summary\":\"duplicate\"," + original.text().substring(1));
        assertRejected(original, "{\"private transcript\"");
    }

    @Test
    void rejectsInvalidFactsAndModelInventedSourceReferences() {
        for (MemoryDocument.Fact fact : List.of(
                new MemoryDocument.Fact("privilege key", "value", List.of(1L), OBSERVED, false),
                new MemoryDocument.Fact("key", " ", List.of(1L), OBSERVED, false),
                new MemoryDocument.Fact("key", "value", List.of(), OBSERVED, false),
                new MemoryDocument.Fact("key", "value", List.of(0L), OBSERVED, false),
                new MemoryDocument.Fact("key", "value", List.of(1L, 1L), OBSERVED, false),
                new MemoryDocument.Fact("key", "value", List.of(1L), OBSERVED.plusSeconds(1), false),
                new MemoryDocument.Fact("key", "\uD800", List.of(1L), OBSERVED, false))) {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> codec.encode(
                            document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", List.of(fact), List.of())));
        }
        MemoryDocument.Fact fact = profile().userFacts().getFirst();
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.encode(
                        document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", List.of(fact, fact), List.of())));
    }

    @Test
    void enforcesFactTokenByteAndRecordCaps() {
        properties.setProfileMaxFacts(1);
        MemoryDocument.Fact first = profile().userFacts().getFirst();
        MemoryDocument.Fact second = new MemoryDocument.Fact("other", "value", List.of(1L), OBSERVED, false);
        assertThrows(
                IllegalArgumentException.class,
                () -> codec.encode(
                        document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", List.of(first, second), List.of())));
        assertThrows(
                MemoryBounds.CapacityException.class,
                () -> codec.encode(
                        document(MemoryDocument.Kind.EPISODE_SUMMARY, false, "字".repeat(3000), List.of(), List.of())));
        MemoryDocument.Fact large = new MemoryDocument.Fact("large", "字".repeat(3000), List.of(1L), OBSERVED, false);
        assertThrows(
                MemoryBounds.CapacityException.class,
                () -> codec.encode(
                        document(MemoryDocument.Kind.PROFILE_SNAPSHOT, false, "", List.of(large), List.of())));
        MemoryDocumentCodec tokenCodec = new MemoryDocumentCodec(
                new MemoryBounds(MemoryBoundsTest.estimator(ignored -> 1000), properties), properties);
        assertThrows(MemoryBounds.CapacityException.class, () -> tokenCodec.encode(summary()));
        TextSegment segment = codec.encode(summary());
        assertRejected(segment, "x".repeat(MemoryBounds.VECTOR_RECORD_BYTES + 1));
    }

    @Test
    void rejectsUnsupportedScopesAndTemporaryMirrors() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new MemoryScope("chat-assist", "user", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY));
        assertThrows(
                IllegalArgumentException.class,
                () -> new MemoryScope("chat", "user", "character", 0, EmbeddingStoreType.EN_LONG_TERM_MEMORY));
        for (EmbeddingStoreType type : List.of(
                EmbeddingStoreType.EN_CHARACTER_DOCUMENT,
                EmbeddingStoreType.ZH_CHARACTER_DOCUMENT,
                EmbeddingStoreType.DEFAULT_CHARACTER_DOCUMENT)) {
            assertThrows(IllegalArgumentException.class, () -> new MemoryScope("chat", "user", "character", 1, type));
        }
        assertThrows(IllegalArgumentException.class, () -> new MemoryScope("chat", "user", "character", 1, null));
        assertThrows(
                IllegalArgumentException.class,
                () -> new MemoryScope("x".repeat(33), "user", "character", 1, EmbeddingStoreType.EN_LONG_TERM_MEMORY));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1-1-1-1-1", "12345678-1234-1234-ABCD-123456789ABC", "", "not-an-id"})
    void rejectsNoncanonicalIds(String value) {
        assertFalse(MemoryDocumentCodec.canonicalUuid(value));
    }

    private void assertInvalidJson(Consumer<ObjectNode> mutation) {
        TextSegment segment = codec.encode(summary());
        try {
            ObjectNode root = (ObjectNode) new ObjectMapper().readTree(segment.text());
            mutation.accept(root);
            assertRejected(segment, root.toString());
        } catch (java.io.IOException error) {
            throw new AssertionError(error);
        }
    }

    private void assertRejected(TextSegment original, String json) {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> codec.decode(
                        TextSegment.from(json, original.metadata()), SCOPE, ID, MemoryDocumentCodec.hash(json)));
        assertEquals("Invalid memory document", error.getMessage());
        assertNull(error.getCause());
        assertFalse(error.toString().contains("private transcript"));
    }

    private static Stream<MemoryDocument> documents() {
        return Stream.of(
                summary(),
                profile(),
                document(MemoryDocument.Kind.WINDOW_SUMMARY, false, "Rolling summary", List.of(), List.of()),
                document(MemoryDocument.Kind.WINDOW_SUMMARY, true, "Overflow chunk", List.of(), List.of()),
                document(
                        MemoryDocument.Kind.EXTRACTION_CHECKPOINT,
                        false,
                        "Fragment facts",
                        profile().userFacts(),
                        profile().characterDeltas()));
    }

    private static MemoryDocument summary() {
        return document(
                MemoryDocument.Kind.EPISODE_SUMMARY, false, "A dated conversation summary 中文", List.of(), List.of());
    }

    private static MemoryDocument profile() {
        return document(
                MemoryDocument.Kind.PROFILE_SNAPSHOT,
                false,
                "",
                List.of(new MemoryDocument.Fact("tea_preference", "Prefers green tea", List.of(1L), OBSERVED, false)),
                List.of(new MemoryDocument.Fact(
                        "story_state", "Arrived at the observatory", List.of(2L), OBSERVED, true)));
    }

    private static MemoryDocument document(
            MemoryDocument.Kind kind,
            boolean archive,
            String summary,
            List<MemoryDocument.Fact> user,
            List<MemoryDocument.Fact> character) {
        return new MemoryDocument(
                ID, SCOPE, COMMIT, kind, archive, 1, 3, OBSERVED, FINGERPRINT, summary, user, character);
    }
}
