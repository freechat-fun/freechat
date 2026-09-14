package fun.freechat.service.chat.memory;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;
import static fun.freechat.service.enums.EmbeddingRecordMeta.MEMORY_ID;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.store.embedding.filter.Filter;
import fun.freechat.service.enums.EmbeddingStoreType;

public record MemoryScope(
        String chatId, String userId, String characterUid, long generation, EmbeddingStoreType storeType) {
    public static final int SCHEMA_VERSION = 1;

    public MemoryScope {
        requireIdentifier(chatId);
        requireIdentifier(userId);
        requireIdentifier(characterUid);
        if (chatId.endsWith("-assist") || generation <= 0 || !isMemoryStore(storeType)) {
            throw new IllegalArgumentException("Invalid persistent memory scope");
        }
    }

    public Metadata metadata() {
        return new Metadata()
                .put(MEMORY_ID.text(), chatId)
                .put("user_id", userId)
                .put("character_uid", characterUid)
                .put("generation", generation)
                .put("store_type", storeType.text())
                .put("schema_version", SCHEMA_VERSION);
    }

    public Filter filter() {
        return metadataKey(MEMORY_ID.text())
                .isEqualTo(chatId)
                .and(metadataKey("user_id").isEqualTo(userId))
                .and(metadataKey("character_uid").isEqualTo(characterUid))
                .and(metadataKey("generation").isEqualTo(generation))
                .and(metadataKey("store_type").isEqualTo(storeType.text()))
                .and(metadataKey("schema_version").isEqualTo(SCHEMA_VERSION));
    }

    public static boolean isMemoryStore(EmbeddingStoreType type) {
        return type == EmbeddingStoreType.EN_LONG_TERM_MEMORY
                || type == EmbeddingStoreType.ZH_LONG_TERM_MEMORY
                || type == EmbeddingStoreType.DEFAULT_LONG_TERM_MEMORY;
    }

    private static void requireIdentifier(String value) {
        if (value == null
                || value.isBlank()
                || value.length() > 32
                || value.chars().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException("Invalid memory scope identifier");
        }
    }
}
