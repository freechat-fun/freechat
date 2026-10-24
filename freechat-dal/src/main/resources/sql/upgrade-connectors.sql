USE `freechat`;

-- Private remote connector configuration only; no runtime state or plaintext header values.
CREATE TABLE IF NOT EXISTS `connector_info` (
  `connector_id` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `user_id` varchar(32) COLLATE utf8mb4_bin NOT NULL,
  `revision` varchar(32) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `name` varchar(64) NOT NULL,
  `transport` varchar(8) CHARACTER SET ascii COLLATE ascii_bin NOT NULL,
  `url` varchar(2048) NOT NULL,
  `timeout` int NOT NULL DEFAULT 60000,
  `headers` json NOT NULL COMMENT 'header names mapped to encrypted values',
  `header_names` json NOT NULL COMMENT 'names only, readable without decryption',
  `active` tinyint NOT NULL DEFAULT 1,
  `gmt_create` datetime(6) NOT NULL,
  `gmt_modified` datetime(6) NOT NULL,
  PRIMARY KEY (`connector_id`),
  KEY `idx_connector_owner` (`user_id`, `gmt_modified`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
