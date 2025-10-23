-- Create CDR table for FreeSWITCH mod_odbc_cdr
-- This script runs automatically only on first container start when the MySQL data volume is empty.
-- If your DB already exists, run it manually (see README/notes).

CREATE TABLE IF NOT EXISTS `bytedesk_freeswitch`.`cdr` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(64) NOT NULL,
  `bleg_uuid` VARCHAR(64) DEFAULT NULL,
  `account_code` VARCHAR(64) DEFAULT NULL,
  `domain_name` VARCHAR(255) DEFAULT NULL,
  `caller_id_name` VARCHAR(255) DEFAULT NULL,
  `caller_id_number` VARCHAR(64) DEFAULT NULL,
  `destination_number` VARCHAR(64) DEFAULT NULL,
  `context` VARCHAR(255) DEFAULT NULL,
  `start_stamp` DATETIME DEFAULT NULL,
  `answer_stamp` DATETIME DEFAULT NULL,
  `end_stamp` DATETIME DEFAULT NULL,
  `duration` INT NOT NULL DEFAULT 0,
  `billsec` INT NOT NULL DEFAULT 0,
  `hangup_cause` VARCHAR(64) DEFAULT NULL,
  `read_codec` VARCHAR(64) DEFAULT NULL,
  `write_codec` VARCHAR(64) DEFAULT NULL,
  `sip_hangup_disposition` VARCHAR(64) DEFAULT NULL,
  `ani` VARCHAR(64) DEFAULT NULL,
  `aniii` VARCHAR(64) DEFAULT NULL,
  `network_addr` VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_uuid` (`uuid`),
  KEY `idx_bleg_uuid` (`bleg_uuid`),
  KEY `idx_start` (`start_stamp`),
  KEY `idx_dest` (`destination_number`),
  KEY `idx_account_code` (`account_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
