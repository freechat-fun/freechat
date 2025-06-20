CREATE DATABASE IF NOT EXISTS `freechat` CHARACTER SET utf8mb4;
USE `freechat`;

CREATE TABLE IF NOT EXISTS `user` (
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL COMMENT 'immutable user identifier',
  `username` varchar(128) NOT NULL COMMENT 'mutable user identifier',
  `password` varchar(128) NOT NULL,
  `nickname` varchar(128) DEFAULT NULL,
  `given_name` varchar(128) DEFAULT NULL,
  `middle_name` varchar(128) DEFAULT NULL,
  `family_name` varchar(128) DEFAULT NULL,
  `preferred_username` varchar(128) DEFAULT NULL,
  `profile` varchar(256) DEFAULT NULL,
  `picture` varchar(512) DEFAULT NULL,
  `website` varchar(256) DEFAULT NULL,
  `email` varchar(128) DEFAULT NULL,
  `email_verified` TINYINT NOT NULL DEFAULT 0,
  `gender` varchar(16) DEFAULT NULL,
  `birthdate` datetime DEFAULT NULL,
  `zoneinfo` varchar(32) DEFAULT NULL,
  `locale` varchar(32) DEFAULT NULL,
  `phone_number` varchar(32) DEFAULT NULL,
  `phone_number_verified` TINYINT NOT NULL DEFAULT 0,
  `address` json DEFAULT NULL,
  `updated_at` datetime DEFAULT NULL,
  `platform` varchar(128) NOT NULL DEFAULT 'system',
  `enabled` tinyint NOT NULL DEFAULT 1,
  `locked` tinyint NOT NULL DEFAULT 0,
  `expires_at` datetime DEFAULT NULL,
  `password_expires_at` datetime DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_user_pass` (`username`,`password`),
  KEY `idx_user_platform` (`username`,`platform`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='user table'
;

CREATE TABLE IF NOT EXISTS `authority` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `scope` text NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_scope` (`user_id`,`scope`(128)),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='authority table'
;

CREATE TABLE IF NOT EXISTS `binding` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `platform` varchar(128) NOT NULL,
  `sub` varchar(256) NOT NULL,
  `iss` varchar(256) DEFAULT NULL,
  `aud` text DEFAULT NULL,
  `refresh_token` text DEFAULT NULL,
  `issued_at` datetime DEFAULT NULL,
  `expires_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='oauth2 binding table'
;

CREATE TABLE IF NOT EXISTS `api_token` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `token` varchar(64) NOT NULL,
  `type` varchar(16) NOT NULL DEFAULT 'access',
  `policy` text DEFAULT NULL,
  `issued_at` datetime NOT NULL,
  `expires_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='api token table'
;

CREATE TABLE IF NOT EXISTS `prompt_info` (
  `prompt_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'record identifier',
  `prompt_uid` varchar(32) NOT NULL COMMENT 'immutable prompt identifier',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `parent_uid` varchar(32) DEFAULT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL COMMENT 'mutable prompt identifier',
  `type` varchar(16) DEFAULT 'string' COMMENT 'string | chat',
  `description` text DEFAULT NULL,
  `template` text DEFAULT NULL,
  `format` varchar(16) DEFAULT 'mustache' COMMENT 'mustache | f_string',
  `lang` varchar(16) DEFAULT 'en' COMMENT 'en | zh_CN | ...',
  `example` text DEFAULT NULL,
  `inputs` json DEFAULT NULL,
  `version` int unsigned DEFAULT 1,
  `ext` json DEFAULT NULL,
  `draft` text DEFAULT NULL,
  PRIMARY KEY (`prompt_id`),
  INDEX `idx_uid` (`prompt_uid`),
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='prompt info table'
;

CREATE TABLE IF NOT EXISTS `agent_info` (
  `agent_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'record identifier',
  `agent_uid` varchar(32) NOT NULL COMMENT 'immutable agent identifier',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `parent_uid` varchar(32) DEFAULT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL COMMENT 'mutable agent identifier',
  `description` text DEFAULT NULL,
  `config` json DEFAULT NULL,
  `format` varchar(16) DEFAULT 'langflow',
  `example` text DEFAULT NULL,
  `parameters` json DEFAULT NULL,
  `version` int unsigned DEFAULT 1,
  `ext` json DEFAULT NULL,
  `draft` text DEFAULT NULL,
  PRIMARY KEY (`agent_id`),
  INDEX `idx_uid` (`agent_uid`),
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='agent info table'
;

CREATE TABLE IF NOT EXISTS `plugin_info` (
  `plugin_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'record identifier',
  `plugin_uid` varchar(32) NOT NULL COMMENT 'immutable plugin identifier',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL COMMENT 'mutable plugin identifier',
  `provider` text DEFAULT NULL COMMENT 'provider information',
  `manifest_info` text DEFAULT NULL COMMENT 'url or json',
  `manifest_format` varchar(16) DEFAULT 'dash_scope' COMMENT 'dash_scope | open_ai',
  `api_info` text DEFAULT NULL COMMENT 'url or json',
  `api_format` varchar(24) DEFAULT 'openapi_v3' COMMENT 'openapi_v3 | open_ai',
  `ext` json DEFAULT NULL,
  PRIMARY KEY (`plugin_id`),
  INDEX `idx_uid` (`plugin_uid`),
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='plugin info table'
;

CREATE TABLE IF NOT EXISTS `character_info` (
  `character_id` bigint unsigned NOT NULL AUTO_INCREMENT COMMENT 'record identifier',
  `character_uid` varchar(32) NOT NULL COMMENT 'immutable character identifier',
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `parent_uid` varchar(32) DEFAULT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL COMMENT 'mutable character identifier',
  `description` text DEFAULT NULL,
  `nickname` varchar(128) DEFAULT NULL,
  `avatar` varchar(512) DEFAULT NULL,
  `picture` varchar(512) DEFAULT NULL,
  `video` varchar(512) DEFAULT NULL,
  `gender` varchar(16) DEFAULT 'other' COMMENT 'male | female | other',
  `lang` varchar(64) DEFAULT 'en' COMMENT 'en | zh | ...',
  `profile` text DEFAULT NULL,
  `greeting` text DEFAULT NULL,
  `chat_style` text DEFAULT NULL,
  `chat_example` text DEFAULT NULL,
  `default_scene` text DEFAULT NULL,
  `version` int unsigned DEFAULT 1,
  `ext` json DEFAULT NULL,
  `draft` text DEFAULT NULL,
  `priority` int unsigned DEFAULT 1,
  PRIMARY KEY (`character_id`),
  INDEX `idx_uid` (`character_uid`),
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='character info table'
;

CREATE TABLE IF NOT EXISTS `character_backend` (
  `backend_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `character_uid` varchar(32) NOT NULL,
  `is_default` tinyint NOT NULL DEFAULT 0,
  `chat_prompt_task_id` varchar(32) NOT NULL,
  `greeting_prompt_task_id` varchar(32) DEFAULT NULL,
  `moderation_model_id` varchar(98) DEFAULT NULL,
  `moderation_api_key_name` varchar(64) DEFAULT NULL,
  `moderation_params` json DEFAULT NULL,
  `forward_to_user` tinyint NOT NULL DEFAULT 0,
  `message_window_size` int NOT NULL DEFAULT 100,
  `long_term_memory_window_size` int NOT NULL DEFAULT 0,
  `proactive_chat_waiting_time` int NOT NULL DEFAULT 0 COMMENT 'minutes, 0 for disabled',
  `init_quota` bigint unsigned DEFAULT 0,
  `quota_type` varchar(16) DEFAULT 'none' COMMENT 'messages | tokens | none',
  `enable_album_tool` tinyint NOT NULL DEFAULT 0,
  `enable_tts` tinyint NOT NULL DEFAULT 0,
  `tts_speaker_idx` varchar(32) DEFAULT NULL,
  `tts_speaker_wav` varchar(256) DEFAULT NULL,
  `tts_speaker_type` varchar(8) DEFAULT 'idx' COMMENT 'idx | wav',
  PRIMARY KEY (`backend_id`),
  INDEX `idx_character` (`character_uid`),
  INDEX `idx_chat_prompt_task` (`chat_prompt_task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='character backend table'
;

CREATE TABLE IF NOT EXISTS `prompt_task` (
  `task_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `gmt_start` datetime DEFAULT NULL,
  `gmt_end` datetime DEFAULT NULL,
  `prompt_uid` varchar(32) DEFAULT NULL,
  `variables` json DEFAULT NULL COMMENT 'variables applied to the prompt template',
  `draft` tinyint NOT NULL DEFAULT 0 COMMENT 'whether to use the prompt draft content',
  `model_id` varchar(98) DEFAULT NULL,
  `api_key_name` varchar(64) DEFAULT NULL,
  `api_key_value` text DEFAULT NULL,
  `params` json DEFAULT NULL COMMENT 'model call parameters, the actual supported fields are related to modelId, depending on the model provider',
  `cron` varchar(32) DEFAULT NULL COMMENT 'cron expression for scheduled task',
  `status` varchar(16) DEFAULT NUll COMMENT 'pending | running | succeeded | failed | canceled',
  `ext` json DEFAULT NULL,
  PRIMARY KEY (`task_id`),
  INDEX `idx_prompt` (`prompt_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='prompt task table'
;

CREATE TABLE IF NOT EXISTS `chat_context` (
  `chat_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `gmt_read` datetime DEFAULT NULL,
  `chat_type` varchar(16) NOT NULL DEFAULT 'u2c' COMMENT 'u2c | u2u',
  `user_id` varchar(32) DEFAULT NULL,
  `user_nickname` varchar(128) DEFAULT NULL,
  `user_profile` text DEFAULT NULL,
  `backend_id` varchar(32) NOT NULL,
  `character_nickname` varchar(128) DEFAULT NULL,
  `about` text DEFAULT NULL,
  `api_key_name` varchar(64) DEFAULT NULL,
  `api_key_value` text DEFAULT NULL,
  `quota` bigint unsigned DEFAULT 0,
  `quota_type` varchar(16) DEFAULT 'none' COMMENT 'messages | tokens | none',
  `ext` json DEFAULT NULL,
  PRIMARY KEY (`chat_id`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_backend` (`backend_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='chat context table'
;

CREATE TABLE IF NOT EXISTS `chat_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `memory_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `message` json DEFAULT NULL,
  `ext` json DEFAULT NULL,
  `enabled` tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `idx_memory` (`memory_id`),
  INDEX `idx_memory_enabled` (`memory_id`, `enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='chat message table'
;

CREATE TABLE IF NOT EXISTS `rag_task` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `gmt_start` datetime DEFAULT NULL,
  `gmt_end` datetime DEFAULT NULL,
  `character_uid` varchar(32) NOT NULL,
  `source_type` varchar(16) NOT NULL DEFAULT 'file' COMMENT 'file | url',
  `source` text NOT NULL,
  `max_segment_size` int NOT NULL DEFAULT 300,
  `max_overlap_size` int NOT NULL DEFAULT 30,
  `status` varchar(16) DEFAULT NUll COMMENT 'pending | running | succeeded | failed | canceled',
  `ext` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_character` (`character_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='rag task table'
;

CREATE TABLE IF NOT EXISTS `tag` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `content` varchar(32) NOT NULL,
  `refer_type` varchar(32) DEFAULT 'prompt',
  `refer_id` varchar(32) DEFAULT NULL,
  `user_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_content` (`content`),
  INDEX `idx_refer_type` (`refer_type`),
  INDEX `idx_refer` (`refer_type`, `refer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='tag table'
;

CREATE TABLE IF NOT EXISTS `ai_model` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `model_id` varchar(32) NOT NULL,
  `refer_type` varchar(32) DEFAULT 'prompt',
  `refer_id` varchar(32) DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_model_id` (`model_id`),
  INDEX `idx_refer_type` (`refer_type`),
  INDEX `idx_refer` (`refer_type`, `refer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ai model table'
;

CREATE TABLE IF NOT EXISTS `org_relationship` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `owner_id` varchar(32) NOT NULL,
  `is_virtual` tinyint NOT NULL DEFAULT 0,
  `enabled` tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_owner_id` (`owner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='organization relationship table'
;

CREATE TABLE IF NOT EXISTS `interactive_stats` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `refer_type` varchar(32) DEFAULT 'prompt',
  `refer_id` varchar(32) DEFAULT NULL,
  `view_count` bigint unsigned DEFAULT 0,
  `refer_count` bigint unsigned DEFAULT 0,
  `recommend_count` bigint unsigned DEFAULT 0,
  `score_count` bigint unsigned DEFAULT 0,
  `score` bigint unsigned DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `idx_refer_type` (`refer_type`),
  INDEX `idx_refer` (`refer_type`, `refer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='interactive statistics table'
;

CREATE TABLE IF NOT EXISTS `interactive_stats_score_details` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `refer_type` varchar(32) DEFAULT 'prompt',
  `refer_id` varchar(32) DEFAULT NULL,
  `user_id` varchar(32) NOT NULL,
  `score` bigint unsigned DEFAULT 0,
  PRIMARY KEY (`id`),
  INDEX `idx_refer` (`refer_type`, `refer_id`),
  INDEX `idx_user_refer` (`user_id`, `refer_type`, `refer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='interactive statistics score details table'
;

CREATE TABLE IF NOT EXISTS `ai_api_key` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `gmt_used` datetime DEFAULT NULL,
  `name` varchar(64) NOT NULL,
  `provider` varchar(32) DEFAULT 'unknown',
  `token` text NOT NULL,
  `enabled` tinyint NOT NULL DEFAULT 1,
  `user_id` varchar(32) NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `idx_user_provider` (`user_id`, `provider`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='AI provider API-KEY info'
;

CREATE TABLE IF NOT EXISTS `short_link` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `token` varchar(32) NOT NULL,
  `path` varchar(256) NOT NULL,
  `expires_at` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_token` (`token`),
  INDEX `idx_path` (`path`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='short link info'
;