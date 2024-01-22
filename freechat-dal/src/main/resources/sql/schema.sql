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
  `picture` varchar(256) DEFAULT NULL,
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

CREATE TABLE IF NOT EXISTS `ai_model_info` (
  `model_id` varchar(98) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `name` varchar(64) NOT NULL,
  `description` varchar(128) DEFAULT NULL,
  `provider` varchar(32) DEFAULT 'unknown'  COMMENT 'hugging_face | open_ai | local_ai | in_process | dash_scope | unknown',
  `type` varchar(24) DEFAULT 'text2text' COMMENT 'text2text | text2chat | text2image | embedding | moderation | unknown',
  PRIMARY KEY (`model_id`),
  UNIQUE KEY `uk_model_id` (`model_id`),
  INDEX `idx_name` (`name`),
  INDEX `idx_provider` (`provider`),
  INDEX `idx_name_provider` (`name`, `provider`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ai model info table'
;

CREATE TABLE IF NOT EXISTS `prompt_info` (
  `prompt_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `parent_id` varchar(32) DEFAULT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL,
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
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='prompt info table'
;

CREATE TABLE IF NOT EXISTS `flow_info` (
  `flow_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `parent_id` varchar(32) DEFAULT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL,
  `description` text DEFAULT NULL,
  `config` json DEFAULT NULL,
  `format` varchar(16) DEFAULT 'langflow',
  `example` text DEFAULT NULL,
  `parameters` json DEFAULT NULL,
  `version` int unsigned DEFAULT 1,
  `ext` json DEFAULT NULL,
  `draft` text DEFAULT NULL,
  PRIMARY KEY (`flow_id`),
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='flow info table'
;

CREATE TABLE IF NOT EXISTS `plugin_info` (
  `plugin_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL,
  `provider` text DEFAULT NULL COMMENT 'provider information',
  `manifest_info` text DEFAULT NULL COMMENT 'url or json',
  `manifest_format` varchar(16) DEFAULT 'dash_scope' COMMENT 'dash_scope | open_ai',
  `api_info` text DEFAULT NULL COMMENT 'url or json',
  `api_format` varchar(24) DEFAULT 'openapi_v3' COMMENT 'openapi_v3 | open_ai',
  `ext` json DEFAULT NULL,
  PRIMARY KEY (`plugin_id`),
  INDEX `idx_visibility` (`visibility`),
  INDEX `idx_visibility_name` (`visibility`, `name`),
  INDEX `idx_user` (`user_id`),
  INDEX `idx_user_name` (`user_id`, `name`),
  INDEX `idx_modified` (`gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='plugin info table'
;

CREATE TABLE IF NOT EXISTS `character_info` (
  `character_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) NOT NULL,
  `parent_id` varchar(32) DEFAULT NULL,
  `visibility` varchar(16) DEFAULT 'private',
  `name` varchar(128) NOT NULL,
  `description` text DEFAULT NULL,
  `avatar` varchar(256) DEFAULT NULL,
  `picture` varchar(256) DEFAULT NULL,
  `gender` varchar(16) DEFAULT 'other' COMMENT 'male | female | other',
  `lang` varchar(64) DEFAULT 'English' COMMENT 'English | Chinese (Simplified) | ...',
  `profile` text DEFAULT NULL,
  `greeting` text DEFAULT NULL,
  `chat_style` text DEFAULT NULL,
  `chat_example` text DEFAULT NULL,
  `experience` text DEFAULT NULL,
  `version` int unsigned DEFAULT 1,
  `ext` json DEFAULT NULL,
  `draft` text DEFAULT NULL,
  PRIMARY KEY (`character_id`),
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
  `character_id` varchar(32) NOT NULL,
  `is_default` tinyint NOT NULL DEFAULT 0,
  `chat_prompt_task_id` varchar(32) NOT NULL,
  `greeting_prompt_task_id` varchar(32) DEFAULT NULL,
  `experience_prompt_task_id` varchar(32) DEFAULT NULL,
  `moderation_model_id` varchar(98) DEFAULT NULL,
  `moderation_api_key_name` varchar(64) DEFAULT NULL,
  `moderation_params` json DEFAULT NULL,
  `forward_to_user` tinyint NOT NULL DEFAULT 0,
  `message_window_size` int NOT NULL DEFAULT 100,
  PRIMARY KEY (`backend_id`),
  INDEX `idx_character` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='character backend table'
;

CREATE TABLE IF NOT EXISTS `prompt_task` (
  `task_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `gmt_executed` datetime DEFAULT NULL,
  `prompt_id` varchar(32) DEFAULT NULL,
  `variables` json DEFAULT NULL COMMENT 'variables applied to the prompt template',
  `draft` tinyint NOT NULL DEFAULT 0 COMMENT 'whether to use the prompt draft content',
  `model_id` varchar(98) NOT NULL,
  `api_key_name` varchar(64) NOT NULL,
  `params` json NOT NULL COMMENT 'model call parameters, the actual supported fields are related to modelId, depending on the model provider',
  `cron` varchar(32) DEFAULT NULL COMMENT 'cron expression for scheduled task',
  `status` varchar(16) DEFAULT NUll COMMENT 'pending | running | succeeded | failed | unknown',
  PRIMARY KEY (`task_id`),
  INDEX `idx_prompt` (`prompt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='prompt task table'
;

CREATE TABLE IF NOT EXISTS `chat_context` (
  `chat_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `user_id` varchar(32) DEFAULT NULL,
  `user_nickname` varchar(128) DEFAULT NULL,
  `user_profile` text DEFAULT NULL,
  `character_nickname` varchar(128) DEFAULT NULL,
  `backend_id` varchar(32) NOT NULL,
  `ext` json DEFAULT NULL,
  PRIMARY KEY (`chat_id`),
  INDEX `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='chat context table'
;

CREATE TABLE IF NOT EXISTS `chat_history` (
  `id` bigint unsigned NOT NULL AUTO_INCREMENT,
  `memory_id` varchar(32) NOT NULL,
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `message` json DEFAULT NULL,
  `enabled` tinyint NOT NULL DEFAULT 1,
  PRIMARY KEY (`id`),
  INDEX `idx_memory_enabled` (`memory_id`, `enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='chat message table'
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

CREATE TABLE IF NOT EXISTS `non_real_time_config` (
  `gmt_create` datetime NOT NULL,
  `gmt_modified` datetime NOT NULL,
  `name` varchar(32) NOT NULL,
  `format` varchar(16) DEFAULT 'kv' COMMENT 'kv | json | yaml',
  `content` text DEFAULT NULl,
  `version` int unsigned DEFAULT 1,
  `user_id` varchar(32) NOT NULL,
  PRIMARY KEY (`name`, `version`),
  INDEX `idx_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=265 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='Non-real-time configurations, delayed by a few minutes for publishing.'
;
