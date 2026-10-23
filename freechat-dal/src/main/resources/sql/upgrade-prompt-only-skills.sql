-- Apply ONLY this additive migration to an existing database, never the full development schema.
-- Select the intended FreeChat database before running it. No existing tables or rows are modified.
CREATE TABLE IF NOT EXISTS `skill_info` (
  `skill_id` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `user_id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `revision` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `name` varchar(128) NOT NULL,
  `description` text NOT NULL,
  `digest` varchar(64) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `manifest` json NOT NULL COMMENT 'server-owned relative filenames, SHA-256 hashes and UTF-8 byte lengths',
  `gmt_create` datetime(6) NOT NULL,
  `gmt_modified` datetime(6) NOT NULL,
  PRIMARY KEY (`skill_id`),
  KEY `idx_skill_owner` (`user_id`, `gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS `skill_draft` (
  `draft_id` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `skill_id` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `user_id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `revision` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `expected_revision` varchar(32) CHARACTER SET ascii COLLATE ascii_bin DEFAULT NULL,
  `name` varchar(128) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `digest` varchar(64) CHARACTER SET ascii COLLATE ascii_bin DEFAULT NULL,
  `manifest` json NOT NULL,
  `status` varchar(16) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `expires_at` datetime(6) NOT NULL,
  `gc_after` datetime(6) DEFAULT NULL,
  `gmt_create` datetime(6) NOT NULL,
  `gmt_modified` datetime(6) NOT NULL,
  PRIMARY KEY (`draft_id`),
  UNIQUE KEY `uk_skill_revision` (`skill_id`, `revision`),
  KEY `idx_skill_attempts` (`user_id`, `gmt_create`),
  KEY `idx_skill_draft_owner` (`user_id`, `status`, `expires_at`),
  KEY `idx_skill_draft_gc` (`gc_after`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
