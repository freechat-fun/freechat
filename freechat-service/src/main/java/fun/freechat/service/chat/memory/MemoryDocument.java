package fun.freechat.service.chat.memory;

import java.time.Instant;
import java.util.List;

public record MemoryDocument(
        String id,
        MemoryScope scope,
        String commitId,
        Kind kind,
        boolean archive,
        long sourceStartId,
        long sourceEndId,
        Instant observedAt,
        String fingerprint,
        String summary,
        List<Fact> userFacts,
        List<Fact> characterDeltas) {
    public MemoryDocument {
        userFacts = List.copyOf(userFacts);
        characterDeltas = List.copyOf(characterDeltas);
    }

    public boolean discoverable() {
        return kind == Kind.EPISODE_SUMMARY || kind == Kind.WINDOW_SUMMARY && archive;
    }

    public enum Kind {
        WINDOW_SUMMARY,
        EPISODE_SUMMARY,
        PROFILE_SNAPSHOT,
        EXTRACTION_CHECKPOINT
    }

    public record Fact(String key, String value, List<Long> sourceIds, Instant observedAt, boolean fictional) {
        public Fact {
            sourceIds = List.copyOf(sourceIds);
        }
    }
}
