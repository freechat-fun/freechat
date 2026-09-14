-- Apply once to the baseline schema to add durable chat memory without changing existing history IDs or data.
USE `freechat`;

ALTER TABLE `chat_history`
  ADD COLUMN `turn_id` varchar(36) DEFAULT NULL COMMENT 'turn UUID shared by input, tool exchanges and final answer; null for unreconciled legacy rows',
  ADD COLUMN `record_kind` varchar(24) NOT NULL DEFAULT 'message' COMMENT 'message | turn-start | turn-complete | turn-abort; markers delimit durable turns',
  ADD COLUMN `source_message` json DEFAULT NULL COMMENT 'original user input before prompt transformation, retained as extraction evidence',
  ADD COLUMN `message_origin` varchar(24) DEFAULT NULL COMMENT 'user-input | assistant-output | tool | template-example | system; controls evidence eligibility',
  ADD COLUMN `system_message_ref` varchar(160) DEFAULT NULL COMMENT 'chat-scoped immutable system prompt FileStore reference; no prompt body',
  ADD COLUMN `episode` bigint NOT NULL DEFAULT 0 COMMENT 'idle-delimited episode number within the chat',
  ADD INDEX `idx_memory_enabled_id` (`memory_id`, `enabled`, `id`),
  ADD INDEX `idx_memory_turn_id` (`memory_id`, `turn_id`, `id`);

CREATE TABLE `chat_memory_state` (
  `chat_id` varchar(32) NOT NULL COMMENT 'persistent session scope; one memory state per chat_context.chat_id',
  `user_id` varchar(32) NOT NULL COMMENT 'trusted owner identity used to scope memory access',
  `character_uid` varchar(32) NOT NULL COMMENT 'trusted character identity used to scope memory access',
  `store_type` varchar(32) NOT NULL COMMENT 'embedding store type selecting the long-term memory collection',
  `generation` bigint NOT NULL DEFAULT 1 COMMENT 'scope generation; incremented to invalidate stale memory and writers',
  `version` bigint NOT NULL DEFAULT 0 COMMENT 'revision of published memory, finalized history or lifecycle state',
  `status` varchar(16) NOT NULL DEFAULT 'active' COMMENT 'active | disabled | deleted; deleted state is retained as a tombstone',
  `fingerprint` varchar(64) NOT NULL COMMENT 'configuration hash used to reject incompatible memory and extraction work',
  `last_activity` datetime(6) NOT NULL COMMENT 'latest chat activity time (UTC); lease renewal does not reset idle time',
  `latest_finalized_id` bigint NOT NULL DEFAULT 0 COMMENT 'highest finalized chat_history boundary, including completed and aborted turns',
  `episode` bigint NOT NULL DEFAULT 0 COMMENT 'current idle-delimited episode number within the chat',
  `overflow_through_id` bigint NOT NULL DEFAULT 0 COMMENT 'inclusive finalized-history cursor covered by overflow compression or abort skipping',
  `idle_through_id` bigint NOT NULL DEFAULT 0 COMMENT 'inclusive finalized-history cursor processed by idle consolidation; independent of overflow',
  `summary_id` varchar(36) DEFAULT NULL COMMENT 'committed rolling summary record UUID in Milvus; no summary body stored here',
  `profile_id` varchar(36) DEFAULT NULL COMMENT 'paired user-fact and character-delta snapshot UUID in Milvus; no profile bodies stored here',
  `profile_revalidation_pending` tinyint NOT NULL DEFAULT 0 COMMENT '1 hides the stored profile until it is revalidated against the current configuration',
  `turn_token` varchar(36) DEFAULT NULL COMMENT 'active invocation fencing UUID; null when no turn owns the session',
  `turn_lease_until` datetime(6) DEFAULT NULL COMMENT 'renewable turn lease expiry (UTC), bounded by turn_deadline',
  `turn_deadline` datetime(6) DEFAULT NULL COMMENT 'absolute turn expiry (UTC); never extended by renewal',
  `turn_revision` bigint NOT NULL DEFAULT 0 COMMENT 'revision advanced on turn admission, renewal, finalization or revocation',
  `due_at` datetime(6) DEFAULT NULL COMMENT 'earliest idle consolidation or profile revalidation time (UTC); null when not scheduled',
  `retry_at` datetime(6) DEFAULT NULL COMMENT 'retry backoff expiry (UTC); null when no backoff applies',
  `retry_attempts` int NOT NULL DEFAULT 0 COMMENT 'background extraction failure count checked against the configured retry limit',
  `claim_token` varchar(36) DEFAULT NULL COMMENT 'background extraction claim UUID; null when no worker owns the job',
  `claim_lease_until` datetime(6) DEFAULT NULL COMMENT 'renewable extraction claim expiry (UTC), bounded by claim_deadline',
  `claim_deadline` datetime(6) DEFAULT NULL COMMENT 'absolute extraction job expiry (UTC); never extended by renewal',
  `reconciled_through_id` bigint NOT NULL DEFAULT 0 COMMENT 'inclusive chat_history cursor scanned for durable turn and provenance reconciliation',
  `gmt_create` datetime(6) NOT NULL COMMENT 'state row creation time (UTC)',
  `gmt_modified` datetime(6) NOT NULL COMMENT 'state row last update time (UTC)',
  PRIMARY KEY (`chat_id`),
  INDEX `idx_status_due_chat` (`status`, `due_at`, `chat_id`),
  INDEX `idx_status_retry_chat` (`status`, `retry_at`, `chat_id`),
  INDEX `idx_claim_lease` (`claim_lease_until`),
  INDEX `idx_turn_lease` (`turn_lease_until`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='session memory scope, cursors and leases; derived memory bodies remain in Milvus'
;

CREATE TABLE `chat_memory_commit` (
  `attempt_id` varchar(36) NOT NULL COMMENT 'unique publication, checkpoint, provider-call receipt or control-record UUID',
  `chat_id` varchar(32) NOT NULL COMMENT 'persistent session whose memory or usage this record belongs to',
  `generation` bigint NOT NULL COMMENT 'historical scope generation retained for validation and safe vector cleanup',
  `operation` varchar(24) NOT NULL COMMENT 'OVERFLOW | IDLE | REVALIDATE | CHECKPOINT_* | SKIP_* | USAGE | CHAT_USAGE | RECONCILE | SCOPE | GC_IDS | LEGACY_GC',
  `source_start_id` bigint NOT NULL COMMENT 'inclusive source chat_history range start; zero for operations without a source range',
  `source_end_id` bigint NOT NULL COMMENT 'inclusive source boundary or operation-specific history reference; zero when not applicable',
  `expected_cursor` bigint NOT NULL COMMENT 'source cursor captured before work; publication requires it to remain unchanged',
  `expected_head` varchar(36) DEFAULT NULL COMMENT 'summary or profile head captured before work; compared during publication',
  `lease_token` varchar(36) NOT NULL COMMENT 'owning turn or extraction claim UUID; operation-specific identity for control records',
  `status` varchar(16) NOT NULL DEFAULT 'prepared' COMMENT 'prepared | committed | checkpoint | terminal | skipped | usage | control | reconciling | reconciled',
  `fingerprint` varchar(64) NOT NULL COMMENT 'configuration hash captured for this operation; checked before publishing or loading memory',
  `schema_version` int NOT NULL DEFAULT 1 COMMENT 'structured memory record schema version; separates current records from legacy vectors',
  `model_id` varchar(98) NOT NULL COMMENT 'provider model identifier for attribution; none for operations without a model call',
  `manifest` json NOT NULL COMMENT 'bounded vector UUID, kind and content-hash manifest; no memory bodies',
  `progress` json DEFAULT NULL COMMENT 'bounded source hashes, checkpoint positions or control metadata; no source or derived prose',
  `token_usage` json DEFAULT NULL COMMENT 'reported provider token counts; USAGE and CHAT_USAGE keep extraction and conversation costs separate',
  `error_category` varchar(64) DEFAULT NULL COMMENT 'sanitized failure or retirement code; never raw exception text or private payloads',
  `lease_until` datetime(6) NOT NULL COMMENT 'captured validation deadline (UTC) for prepared work; historical metadata for other statuses',
  `gc_after` datetime(6) DEFAULT NULL COMMENT 'earliest physical cleanup or repeated tombstone sweep time (UTC); null when not scheduled',
  `gmt_create` datetime(6) NOT NULL COMMENT 'ledger row creation time (UTC)',
  `gmt_modified` datetime(6) NOT NULL COMMENT 'ledger row last update time (UTC)',
  PRIMARY KEY (`attempt_id`),
  INDEX `idx_chat_generation_status` (`chat_id`, `generation`, `status`),
  INDEX `idx_status_gc_attempt` (`status`, `gc_after`, `attempt_id`),
  INDEX `idx_chat_generation_operation_end` (`chat_id`, `generation`, `operation`, `source_end_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='memory publication, checkpoint, usage and cleanup ledger; metadata only, no memory bodies'
;
