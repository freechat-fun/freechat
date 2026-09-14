package fun.freechat.mapper;

import fun.freechat.model.ChatMemoryCommit;
import fun.freechat.model.ChatMemoryState;
import java.time.LocalDateTime;
import java.util.Optional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Select;

/** Handwritten locking/clock supplement to the generated memory mappers. */
@Mapper
public interface ChatMemoryCoordinationMapper {
    @Insert("""
            INSERT INTO chat_memory_state
                (chat_id, user_id, character_uid, store_type, generation, fingerprint,
                 last_activity, gmt_create, gmt_modified)
            VALUES (#{chatId}, #{userId}, #{characterUid}, #{storeType}, #{generation}, #{fingerprint},
                    UTC_TIMESTAMP(6), UTC_TIMESTAMP(6), UTC_TIMESTAMP(6))
            ON DUPLICATE KEY UPDATE chat_id = chat_id
            """)
    int initialize(ChatMemoryState state);

    @Select("SELECT * FROM chat_memory_state WHERE chat_id = #{chatId} FOR UPDATE")
    @ResultMap("fun.freechat.mapper.ChatMemoryStateMapper.ChatMemoryStateResult")
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    Optional<ChatMemoryState> lock(@Param("chatId") String chatId);

    @Insert("""
            INSERT INTO chat_memory_commit
                (attempt_id, chat_id, generation, operation, source_start_id, source_end_id,
                 expected_cursor, lease_token, status, fingerprint, schema_version, model_id,
                 manifest, progress, lease_until, gc_after, gmt_create, gmt_modified)
            VALUES (#{attemptId}, #{chatId}, #{generation}, #{operation}, 0, 0, 0, #{leaseToken},
                    #{status}, #{fingerprint}, #{schemaVersion}, 'none', #{manifest}, #{progress},
                    #{leaseUntil}, #{gcAfter}, #{gmtCreate}, #{gmtModified})
            ON DUPLICATE KEY UPDATE attempt_id = attempt_id
            """)
    int retainControl(ChatMemoryCommit row);

    @Select("SELECT progress FROM chat_memory_commit WHERE attempt_id = #{id} AND operation = 'SCOPE' AND status = 'control'")
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    Optional<String> scopeProgress(@Param("id") String id);

    @Select("""
            SELECT COUNT(*) FROM chat_memory_commit
            WHERE chat_id = #{chatId} AND generation = #{generation}
              AND operation = 'RECONCILE' AND status = 'reconciling'
            """)
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    long reconciling(@Param("chatId") String chatId, @Param("generation") long generation);

    // Evict the previous payload page rather than retaining an entire transcript in the transaction cache.
    @org.apache.ibatis.annotations.SelectProvider(type = org.mybatis.dynamic.sql.util.SqlProviderAdapter.class, method = "select")
    @ResultMap("fun.freechat.mapper.ChatHistoryMapper.ChatHistoryResult")
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    java.util.List<fun.freechat.model.ChatHistory> selectHistoryPage(
            org.mybatis.dynamic.sql.select.render.SelectStatementProvider statement);

    // A single snapshot avoids double-counting history while a provider-call receipt is being inserted.
    @Select("""
            SELECT token_usage FROM chat_memory_commit
            WHERE chat_id = #{chatId} AND operation = 'CHAT_USAGE' AND status = 'usage'
            UNION ALL
            SELECT h.ext FROM chat_history h
            WHERE h.memory_id = #{chatId} AND JSON_UNQUOTE(JSON_EXTRACT(h.message, '$.type')) = 'AI'
              AND NOT EXISTS (
                SELECT 1 FROM chat_memory_commit c
                WHERE c.chat_id = h.memory_id AND c.operation = 'CHAT_USAGE' AND c.status = 'usage'
                  AND c.lease_token = h.turn_id)
            """)
    @org.apache.ibatis.annotations.ResultType(String.class)
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE,
            fetchSize = Integer.MIN_VALUE, resultSetType = org.apache.ibatis.mapping.ResultSetType.FORWARD_ONLY)
    void conversationUsage(@Param("chatId") String chatId, org.apache.ibatis.session.ResultHandler<String> handler);

    // Never serve the clock from MyBatis's transaction-local or second-level cache.
    @Select("SELECT UTC_TIMESTAMP(6)")
    @Options(useCache = false, flushCache = Options.FlushCachePolicy.TRUE)
    LocalDateTime databaseNow();
}
