package fun.freechat.service.chat.memory;

import fun.freechat.mapper.ChatMemoryCoordinationMapper;
import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import fun.freechat.service.enums.EmbeddingStoreType;
import fun.freechat.service.util.InfoUtils;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

final class MemoryScopeLedger {
    private MemoryScopeLedger() {}

    static String id(String chatId, long generation) {
        return UUID.nameUUIDFromBytes(
                        ("freechat-memory-scope-v1\0" + chatId + "\0" + generation).getBytes(StandardCharsets.UTF_8))
                .toString();
    }

    static MemoryScope scope(ChatMemoryState state) {
        return new MemoryScope(
                state.getChatId(),
                state.getUserId(),
                state.getCharacterUid(),
                state.getGeneration(),
                EmbeddingStoreType.of(state.getStoreType()));
    }

    // Call while holding the state row, before overwriting any identity or creating vector IDs.
    static void retain(ChatMemoryCoordinationMapper coordination, ChatMemoryState state, LocalDateTime now) {
        MemoryScope scope = scope(state);
        var progress = InfoUtils.defaultMapper()
                .createObjectNode()
                .put("version", 1)
                .put("userId", scope.userId())
                .put("characterUid", scope.characterUid())
                .put("storeType", scope.storeType().text());
        String id = id(scope.chatId(), scope.generation());
        coordination.retainControl(new ChatMemoryCommit()
                .withAttemptId(id)
                .withChatId(scope.chatId())
                .withGeneration(scope.generation())
                .withOperation("SCOPE")
                .withStatus("control")
                .withLeaseToken(id)
                .withFingerprint(state.getFingerprint())
                .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                .withManifest("{}")
                .withProgress(progress.toString())
                .withLeaseUntil(now)
                .withGmtCreate(now)
                .withGmtModified(now));
        if (!scope.equals(decode(
                scope.chatId(),
                scope.generation(),
                coordination.scopeProgress(id).orElseThrow(MemoryScopeLedger::failed)))) {
            throw failed();
        }
    }

    static MemoryScope decode(String chatId, long generation, String json) {
        try {
            if (json == null || MemoryBounds.bytes(json) > 1024) {
                throw failed();
            }
            var progress = InfoUtils.defaultMapper().readTree(json);
            if (!progress.isObject()
                    || progress.size() != 4
                    || !progress.path("version").isIntegralNumber()
                    || !progress.path("version").canConvertToInt()
                    || progress.path("version").intValue() != 1
                    || !progress.path("userId").isTextual()
                    || !progress.path("characterUid").isTextual()
                    || !progress.path("storeType").isTextual()) {
                throw failed();
            }
            return new MemoryScope(
                    chatId,
                    progress.get("userId").textValue(),
                    progress.get("characterUid").textValue(),
                    generation,
                    EmbeddingStoreType.of(progress.get("storeType").textValue()));
        } catch (Exception ignored) {
            throw failed();
        }
    }

    static void markLegacy(
            ChatMemoryCoordinationMapper coordination, String chatId, LocalDateTime now, Duration grace) {
        String id = UUID.nameUUIDFromBytes(("freechat-memory-legacy-gc-v1\0" + chatId).getBytes(StandardCharsets.UTF_8))
                .toString();
        coordination.retainControl(new ChatMemoryCommit()
                .withAttemptId(id)
                .withChatId(chatId)
                .withGeneration(0L)
                .withOperation("LEGACY_GC")
                .withStatus("terminal")
                .withLeaseToken(id)
                .withFingerprint(MemoryDocumentCodec.hash("legacy-cleanup-v1"))
                .withSchemaVersion(MemoryScope.SCHEMA_VERSION)
                .withManifest("{}")
                .withProgress("{}")
                .withLeaseUntil(now)
                .withGcAfter(now.plus(grace))
                .withGmtCreate(now)
                .withGmtModified(now));
    }

    private static IllegalStateException failed() {
        return new IllegalStateException("Memory historical scope is unavailable");
    }
}
