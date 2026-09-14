package fun.freechat.service.chat.memory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

public final class MemoryDocumentCodec {
    private static final Pattern FACT_KEY = Pattern.compile("[a-z][a-z0-9_.-]{0,95}");
    private static final Pattern HASH = Pattern.compile("[0-9a-f]{64}");
    private static final ObjectMapper MAPPER = JsonMapper.builder(JsonFactory.builder()
                    .enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION)
                    .streamReadConstraints(StreamReadConstraints.builder()
                            .maxNestingDepth(16)
                            .maxStringLength(MemoryBounds.VECTOR_RECORD_BYTES)
                            .build())
                    .build())
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
            .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
            .enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
            .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
            .build();

    private final MemoryBounds bounds;
    private final LongTermMemoryProperties properties;

    public MemoryDocumentCodec(MemoryBounds bounds, LongTermMemoryProperties properties) {
        this.bounds = Objects.requireNonNull(bounds);
        this.properties = Objects.requireNonNull(properties);
    }

    public TextSegment encode(MemoryDocument document) {
        validate(document);
        String json = json(document);
        require(MemoryBounds.bytes(json) <= MemoryBounds.VECTOR_RECORD_BYTES);
        Metadata metadata = document.scope()
                .metadata()
                .put("commit_id", document.commitId())
                .put("record_kind", document.kind().name())
                .put("archive", document.archive() ? 1 : 0)
                .put("source_start_id", document.sourceStartId())
                .put("source_end_id", document.sourceEndId())
                .put("observed_at", document.observedAt().toString())
                .put("fingerprint", document.fingerprint());
        return TextSegment.from(json, metadata);
    }

    public MemoryDocument decode(TextSegment segment, MemoryScope scope, String expectedId, String expectedHash) {
        require(scope != null
                && canonicalUuid(expectedId)
                && expectedHash != null
                && HASH.matcher(expectedHash).matches());
        require(segment != null && MemoryBounds.bytes(segment.text()) <= MemoryBounds.VECTOR_RECORD_BYTES);
        require(hash(segment.text()).equals(expectedHash));
        MemoryDocument document;
        try {
            document = MAPPER.readValue(segment.text(), MemoryDocument.class);
        } catch (Exception ignored) {
            throw invalid();
        }
        validate(document);
        require(scope.equals(document.scope()) && expectedId.equals(document.id()));
        Metadata expected = encode(document).metadata();
        require(expected.toMap().keySet().equals(segment.metadata().toMap().keySet()));
        for (var entry : expected.toMap().entrySet()) {
            Object actual = segment.metadata().toMap().get(entry.getKey());
            if (entry.getValue() instanceof Number expectedNumber) {
                require(actual instanceof Number
                        && Double.compare(((Number) actual).doubleValue(), expectedNumber.doubleValue()) == 0
                        && ((Number) actual).longValue() == expectedNumber.longValue());
            } else {
                require(entry.getValue().equals(actual));
            }
        }
        return document;
    }

    public static String encodeManifest(MemoryManifest manifest) {
        return json(manifest);
    }

    public static MemoryManifest decodeManifest(String json) {
        require(json != null && MemoryBounds.bytes(json) <= MemoryBounds.VECTOR_RECORD_BYTES);
        try {
            MemoryManifest manifest = MAPPER.readValue(json, MemoryManifest.class);
            require(manifest != null);
            return manifest;
        } catch (Exception ignored) {
            throw invalid();
        }
    }

    public void validateSources(MemoryDocument document, Set<Long> allowedSourceIds) {
        for (MemoryDocument.Fact fact : document.userFacts()) {
            require(allowedSourceIds.containsAll(fact.sourceIds()));
        }
        for (MemoryDocument.Fact fact : document.characterDeltas()) {
            require(allowedSourceIds.containsAll(fact.sourceIds()));
        }
    }

    public static String hash(String text) {
        try {
            return HexFormat.of()
                    .formatHex(MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException error) {
            throw new IllegalStateException("SHA-256 is unavailable");
        }
    }

    public static boolean canonicalUuid(String value) {
        if (value == null || value.length() != 36) {
            return false;
        }
        try {
            return UUID.fromString(value).toString().equals(value);
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    private void validate(MemoryDocument document) {
        require(document != null);
        require(canonicalUuid(document.id()) && canonicalUuid(document.commitId()));
        require(document.scope() != null && document.kind() != null && document.observedAt() != null);
        require(document.fingerprint() != null
                && HASH.matcher(document.fingerprint()).matches());
        require(document.sourceStartId() >= 0 && document.sourceEndId() >= document.sourceStartId());
        require(validText(document.summary()));
        require(document.kind() == MemoryDocument.Kind.WINDOW_SUMMARY || !document.archive());
        if (document.kind() == MemoryDocument.Kind.PROFILE_SNAPSHOT) {
            require(document.summary().isEmpty());
        } else {
            require(!document.summary().isBlank());
            bounds.requireText(document.summary(), properties.getSummaryMaxTokens(), MemoryBounds.COMPACT_TEXT_BYTES);
        }
        if (document.kind() == MemoryDocument.Kind.WINDOW_SUMMARY
                || document.kind() == MemoryDocument.Kind.EPISODE_SUMMARY) {
            require(document.userFacts().isEmpty() && document.characterDeltas().isEmpty());
            require(document.sourceStartId() > 0);
        }
        validateFacts(document.userFacts(), document);
        validateFacts(document.characterDeltas(), document);
    }

    private void validateFacts(List<MemoryDocument.Fact> facts, MemoryDocument document) {
        require(facts.size() <= properties.getProfileMaxFacts());
        Set<String> keys = new HashSet<>();
        for (MemoryDocument.Fact fact : facts) {
            require(fact.key() != null && FACT_KEY.matcher(fact.key()).matches() && keys.add(fact.key()));
            require(validText(fact.value()) && !fact.value().isBlank());
            require(fact.observedAt() != null && !fact.observedAt().isAfter(document.observedAt()));
            require(!fact.sourceIds().isEmpty() && fact.sourceIds().size() <= MemoryBounds.MAX_MANIFEST_RECORDS);
            require(fact.sourceIds().stream().allMatch(id -> id > 0));
            require(new HashSet<>(fact.sourceIds()).size() == fact.sourceIds().size());
        }
        bounds.requireText(json(facts), properties.getProfileMaxTokens(), MemoryBounds.COMPACT_TEXT_BYTES);
    }

    private static boolean validText(String text) {
        if (text == null) {
            return false;
        }
        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if (Character.isHighSurrogate(current)) {
                if (++index >= text.length() || !Character.isLowSurrogate(text.charAt(index))) {
                    return false;
                }
            } else if (Character.isLowSurrogate(current)) {
                return false;
            }
        }
        return true;
    }

    private static String json(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception ignored) {
            throw invalid();
        }
    }

    private static void require(boolean condition) {
        if (!condition) {
            throw invalid();
        }
    }

    private static IllegalArgumentException invalid() {
        return new IllegalArgumentException("Invalid memory document");
    }
}
