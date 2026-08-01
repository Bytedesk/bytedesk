# ************************************************************
# Sequel Ace SQL dump
# 版本号： 20080
#
# https://sequel-ace.com/
# https://github.com/Sequel-Ace/Sequel-Ace
#
# 主机: 124.220.58.234 (MySQL 8.0.42-0ubuntu0.24.04.1)
# 数据库: bytedesk
# 生成时间: 2025-10-20 03:31:19 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE='NO_AUTO_VALUE_ON_ZERO', SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# 转储表 aliases
# ------------------------------------------------------------

DROP TABLE IF EXISTS `aliases`;

CREATE TABLE `aliases` (
  `sticky` int DEFAULT NULL,
  `alias` varchar(128) DEFAULT NULL,
  `command` varchar(4096) DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  KEY `alias1` (`alias`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





# 转储表 calls
# ------------------------------------------------------------

DROP TABLE IF EXISTS `calls`;

CREATE TABLE `calls` (
  `call_uuid` varchar(255) DEFAULT NULL,
  `call_created` varchar(128) DEFAULT NULL,
  `call_created_epoch` int DEFAULT NULL,
  `caller_uuid` varchar(256) DEFAULT NULL,
  `callee_uuid` varchar(256) DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  KEY `callsidx1` (`hostname`),
  KEY `eruuindex` (`caller_uuid`,`hostname`),
  KEY `eeuuindex` (`callee_uuid`),
  KEY `eeuuindex2` (`call_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 carrier_gateway
# ------------------------------------------------------------

DROP TABLE IF EXISTS `carrier_gateway`;

CREATE TABLE `carrier_gateway` (
  `id` int NOT NULL AUTO_INCREMENT,
  `carrier_id` int NOT NULL,
  `prefix` varchar(32) DEFAULT '',
  `suffix` varchar(32) DEFAULT '',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `codec` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_cg_carrier` (`carrier_id`),
  CONSTRAINT `fk_cg_carrier` FOREIGN KEY (`carrier_id`) REFERENCES `carriers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 carriers
# ------------------------------------------------------------

DROP TABLE IF EXISTS `carriers`;

CREATE TABLE `carriers` (
  `id` int NOT NULL AUTO_INCREMENT,
  `carrier_name` varchar(255) NOT NULL,
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 cdr
# ------------------------------------------------------------

DROP TABLE IF EXISTS `cdr`;

CREATE TABLE `cdr` (
  `caller_id_name` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `caller_id_number` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `destination_number` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `context` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `start_stamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `answer_stamp` timestamp NULL DEFAULT NULL,
  `end_stamp` timestamp NULL DEFAULT NULL,
  `duration` int NOT NULL DEFAULT '0',
  `billsec` int NOT NULL DEFAULT '0',
  `hangup_cause` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT '',
  `uuid` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bleg_uuid` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `account_code` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `domain_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `read_codec` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `write_codec` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `sip_hangup_disposition` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `ani` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `aniii` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `network_addr` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `json_cdr` mediumtext COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`uuid`),
  KEY `cdr_start_stamp` (`start_stamp`),
  KEY `cdr_caller_id_number` (`caller_id_number`),
  KEY `cdr_destination_number` (`destination_number`),
  KEY `cdr_context` (`context`),
  KEY `cdr_hangup_cause` (`hangup_cause`),
  KEY `cdr_account_code` (`account_code`),
  KEY `cdr_domain_name` (`domain_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


# 转储表 channels
# ------------------------------------------------------------

DROP TABLE IF EXISTS `channels`;

CREATE TABLE `channels` (
  `uuid` varchar(256) DEFAULT NULL,
  `direction` varchar(32) DEFAULT NULL,
  `created` varchar(128) DEFAULT NULL,
  `created_epoch` int DEFAULT NULL,
  `name` varchar(1024) DEFAULT NULL,
  `state` varchar(64) DEFAULT NULL,
  `cid_name` varchar(1024) DEFAULT NULL,
  `cid_num` varchar(256) DEFAULT NULL,
  `ip_addr` varchar(256) DEFAULT NULL,
  `dest` varchar(1024) DEFAULT NULL,
  `application` varchar(128) DEFAULT NULL,
  `application_data` text,
  `dialplan` varchar(128) DEFAULT NULL,
  `context` varchar(128) DEFAULT NULL,
  `read_codec` varchar(128) DEFAULT NULL,
  `read_rate` varchar(32) DEFAULT NULL,
  `read_bit_rate` varchar(32) DEFAULT NULL,
  `write_codec` varchar(128) DEFAULT NULL,
  `write_rate` varchar(32) DEFAULT NULL,
  `write_bit_rate` varchar(32) DEFAULT NULL,
  `secure` varchar(64) DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  `presence_id` varchar(4096) DEFAULT NULL,
  `presence_data` text,
  `accountcode` varchar(256) DEFAULT NULL,
  `callstate` varchar(64) DEFAULT NULL,
  `callee_name` varchar(1024) DEFAULT NULL,
  `callee_num` varchar(256) DEFAULT NULL,
  `callee_direction` varchar(5) DEFAULT NULL,
  `call_uuid` varchar(256) DEFAULT NULL,
  `sent_callee_name` varchar(1024) DEFAULT NULL,
  `sent_callee_num` varchar(256) DEFAULT NULL,
  `initial_cid_name` varchar(1024) DEFAULT NULL,
  `initial_cid_num` varchar(256) DEFAULT NULL,
  `initial_ip_addr` varchar(256) DEFAULT NULL,
  `initial_dest` varchar(1024) DEFAULT NULL,
  `initial_dialplan` varchar(128) DEFAULT NULL,
  `initial_context` varchar(128) DEFAULT NULL,
  KEY `chidx1` (`hostname`),
  KEY `uuindex` (`uuid`,`hostname`),
  KEY `uuindex2` (`call_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 complete
# ------------------------------------------------------------

DROP TABLE IF EXISTS `complete`;

CREATE TABLE `complete` (
  `sticky` int DEFAULT NULL,
  `a1` varchar(128) DEFAULT NULL,
  `a2` varchar(128) DEFAULT NULL,
  `a3` varchar(128) DEFAULT NULL,
  `a4` varchar(128) DEFAULT NULL,
  `a5` varchar(128) DEFAULT NULL,
  `a6` varchar(128) DEFAULT NULL,
  `a7` varchar(128) DEFAULT NULL,
  `a8` varchar(128) DEFAULT NULL,
  `a9` varchar(128) DEFAULT NULL,
  `a10` varchar(128) DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  KEY `complete1` (`a1`,`hostname`),
  KEY `complete2` (`a2`,`hostname`),
  KEY `complete3` (`a3`,`hostname`),
  KEY `complete4` (`a4`,`hostname`),
  KEY `complete5` (`a5`,`hostname`),
  KEY `complete6` (`a6`,`hostname`),
  KEY `complete7` (`a7`,`hostname`),
  KEY `complete8` (`a8`,`hostname`),
  KEY `complete9` (`a9`,`hostname`),
  KEY `complete10` (`a10`,`hostname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 db_data
# ------------------------------------------------------------

DROP TABLE IF EXISTS `db_data`;

CREATE TABLE `db_data` (
  `hostname` varchar(255) DEFAULT NULL,
  `realm` varchar(255) DEFAULT NULL,
  `data_key` varchar(255) DEFAULT NULL,
  `data` varchar(255) DEFAULT NULL,
  UNIQUE KEY `dd_data_key_realm` (`data_key`,`realm`),
  KEY `dd_realm` (`realm`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;





# 转储表 directory
# ------------------------------------------------------------

DROP TABLE IF EXISTS `directory`;

CREATE TABLE `directory` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `domain` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'localhost',
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `vm_password` varchar(10) COLLATE utf8mb4_unicode_ci DEFAULT '1234',
  `mailbox` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `email_addr` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `vm_email_all_messages` enum('true','false') COLLATE utf8mb4_unicode_ci DEFAULT 'false',
  `vm_attach_file` enum('true','false') COLLATE utf8mb4_unicode_ci DEFAULT 'true',
  `vm_keep_local_after_email` enum('true','false') COLLATE utf8mb4_unicode_ci DEFAULT 'true',
  `user_context` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'default',
  `effective_caller_id_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `effective_caller_id_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `outbound_caller_id_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `outbound_caller_id_number` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `enabled` enum('true','false') COLLATE utf8mb4_unicode_ci DEFAULT 'true',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_domain` (`username`,`domain`),
  KEY `idx_domain` (`domain`),
  KEY `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# 转储表 directory_params
# ------------------------------------------------------------

DROP TABLE IF EXISTS `directory_params`;

CREATE TABLE `directory_params` (
  `id` int NOT NULL AUTO_INCREMENT,
  `directory_id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `value` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_directory_id` (`directory_id`),
  KEY `idx_name` (`name`),
  CONSTRAINT `fk_directory_params` FOREIGN KEY (`directory_id`) REFERENCES `directory` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# 转储表 directory_search
# ------------------------------------------------------------

DROP TABLE IF EXISTS `directory_search`;

CREATE TABLE `directory_search` (
  `hostname` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `extension` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `full_name_digit` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `first_name_digit` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `last_name_digit` varchar(255) DEFAULT NULL,
  `name_visible` int DEFAULT NULL,
  `exten_visible` int DEFAULT NULL,
  KEY `idx_dir_uuid` (`uuid`),
  KEY `idx_dir_extension` (`extension`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 directory_vars
# ------------------------------------------------------------

DROP TABLE IF EXISTS `directory_vars`;

CREATE TABLE `directory_vars` (
  `id` int NOT NULL AUTO_INCREMENT,
  `directory_id` int NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `value` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_directory_id` (`directory_id`),
  KEY `idx_name` (`name`),
  CONSTRAINT `fk_directory_vars` FOREIGN KEY (`directory_id`) REFERENCES `directory` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# 转储表 fifo_bridge
# ------------------------------------------------------------

DROP TABLE IF EXISTS `fifo_bridge`;

CREATE TABLE `fifo_bridge` (
  `fifo_name` varchar(1024) NOT NULL,
  `caller_uuid` varchar(255) NOT NULL,
  `caller_caller_id_name` varchar(255) DEFAULT NULL,
  `caller_caller_id_number` varchar(255) DEFAULT NULL,
  `consumer_uuid` varchar(255) NOT NULL,
  `consumer_outgoing_uuid` varchar(255) DEFAULT NULL,
  `bridge_start` int DEFAULT NULL,
  KEY `idx_fifo_bridge_fifo` (`fifo_name`(191)),
  KEY `idx_fifo_bridge_caller` (`caller_uuid`),
  KEY `idx_fifo_bridge_consumer` (`consumer_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 fifo_callers
# ------------------------------------------------------------

DROP TABLE IF EXISTS `fifo_callers`;

CREATE TABLE `fifo_callers` (
  `fifo_name` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `caller_caller_id_name` varchar(255) DEFAULT NULL,
  `caller_caller_id_number` varchar(255) DEFAULT NULL,
  `timestamp` int DEFAULT NULL,
  KEY `idx_fifo_callers_fifo` (`fifo_name`),
  KEY `idx_fifo_callers_uuid` (`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 fifo_outbound
# ------------------------------------------------------------

DROP TABLE IF EXISTS `fifo_outbound`;

CREATE TABLE `fifo_outbound` (
  `uuid` varchar(255) DEFAULT NULL,
  `fifo_name` varchar(255) DEFAULT NULL,
  `originate_string` varchar(255) DEFAULT NULL,
  `simo_count` int DEFAULT NULL,
  `use_count` int DEFAULT NULL,
  `timeout` int DEFAULT NULL,
  `lag` int DEFAULT NULL,
  `next_avail` int NOT NULL DEFAULT '0',
  `expires` int NOT NULL DEFAULT '0',
  `static` int NOT NULL DEFAULT '0',
  `outbound_call_count` int NOT NULL DEFAULT '0',
  `outbound_fail_count` int NOT NULL DEFAULT '0',
  `hostname` varchar(255) DEFAULT NULL,
  `taking_calls` int NOT NULL DEFAULT '1',
  `status` varchar(255) DEFAULT NULL,
  `outbound_call_total_count` int NOT NULL DEFAULT '0',
  `outbound_fail_total_count` int NOT NULL DEFAULT '0',
  `active_time` int NOT NULL DEFAULT '0',
  `inactive_time` int NOT NULL DEFAULT '0',
  `manual_calls_out_count` int NOT NULL DEFAULT '0',
  `manual_calls_in_count` int NOT NULL DEFAULT '0',
  `manual_calls_out_total_count` int NOT NULL DEFAULT '0',
  `manual_calls_in_total_count` int NOT NULL DEFAULT '0',
  `ring_count` int NOT NULL DEFAULT '0',
  `start_time` int NOT NULL DEFAULT '0',
  `stop_time` int NOT NULL DEFAULT '0',
  KEY `idx_fifo_name` (`fifo_name`),
  KEY `idx_fifo_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 group_data
# ------------------------------------------------------------

DROP TABLE IF EXISTS `group_data`;

CREATE TABLE `group_data` (
  `hostname` varchar(255) DEFAULT NULL,
  `groupname` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  KEY `gd_groupname` (`groupname`),
  KEY `gd_url` (`url`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 interfaces
# ------------------------------------------------------------

DROP TABLE IF EXISTS `interfaces`;

CREATE TABLE `interfaces` (
  `type` varchar(128) DEFAULT NULL,
  `name` varchar(1024) DEFAULT NULL,
  `description` varchar(4096) DEFAULT NULL,
  `ikey` varchar(1024) DEFAULT NULL,
  `filename` varchar(4096) DEFAULT NULL,
  `syntax` varchar(4096) DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 json_store
# ------------------------------------------------------------

DROP TABLE IF EXISTS `json_store`;

CREATE TABLE `json_store` (
  `key` varchar(255) DEFAULT NULL,
  `value` text
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 lcr
# ------------------------------------------------------------

DROP TABLE IF EXISTS `lcr`;

CREATE TABLE `lcr` (
  `id` int NOT NULL AUTO_INCREMENT,
  `carrier_id` int NOT NULL,
  `digits` varchar(32) NOT NULL,
  `rate` decimal(10,5) NOT NULL DEFAULT '0.00000',
  `quality` int NOT NULL DEFAULT '0',
  `reliability` int NOT NULL DEFAULT '0',
  `lead_strip` int NOT NULL DEFAULT '0',
  `trail_strip` int NOT NULL DEFAULT '0',
  `prefix` varchar(32) DEFAULT '',
  `suffix` varchar(32) DEFAULT '',
  `enabled` tinyint(1) NOT NULL DEFAULT '1',
  `date_start` datetime NOT NULL DEFAULT '1970-01-01 00:00:00',
  `date_end` datetime NOT NULL DEFAULT '2099-12-31 23:59:59',
  `cid` varchar(32) DEFAULT NULL,
  `lrn` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `fk_lcr_carrier` (`carrier_id`),
  KEY `digits` (`digits`),
  CONSTRAINT `fk_lcr_carrier` FOREIGN KEY (`carrier_id`) REFERENCES `carriers` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 limit_data
# ------------------------------------------------------------

DROP TABLE IF EXISTS `limit_data`;

CREATE TABLE `limit_data` (
  `hostname` varchar(255) DEFAULT NULL,
  `realm` varchar(255) DEFAULT NULL,
  `id` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  KEY `ld_hostname` (`hostname`),
  KEY `ld_uuid` (`uuid`),
  KEY `ld_realm` (`realm`),
  KEY `ld_id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 nat
# ------------------------------------------------------------

DROP TABLE IF EXISTS `nat`;

CREATE TABLE `nat` (
  `sticky` int DEFAULT NULL,
  `port` int DEFAULT NULL,
  `proto` int DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  KEY `nat_map_port_proto` (`port`,`proto`,`hostname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 recovery
# ------------------------------------------------------------

DROP TABLE IF EXISTS `recovery`;

CREATE TABLE `recovery` (
  `runtime_uuid` varchar(255) DEFAULT NULL,
  `technology` varchar(255) DEFAULT NULL,
  `profile_name` varchar(255) DEFAULT NULL,
  `hostname` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `metadata` text,
  KEY `recovery1` (`technology`),
  KEY `recovery2` (`profile_name`),
  KEY `recovery3` (`uuid`),
  KEY `recovery4` (`runtime_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 registrations
# ------------------------------------------------------------

DROP TABLE IF EXISTS `registrations`;

CREATE TABLE `registrations` (
  `reg_user` varchar(256) DEFAULT NULL,
  `realm` varchar(256) DEFAULT NULL,
  `token` varchar(256) DEFAULT NULL,
  `url` text,
  `expires` int DEFAULT NULL,
  `network_ip` varchar(256) DEFAULT NULL,
  `network_port` varchar(256) DEFAULT NULL,
  `network_proto` varchar(256) DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  `metadata` varchar(256) DEFAULT NULL,
  KEY `regindex1` (`reg_user`,`realm`,`hostname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 sip_registrations
# ------------------------------------------------------------

DROP TABLE IF EXISTS `sip_registrations`;

CREATE TABLE `sip_registrations` (
  `call_id` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sip_user` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sip_host` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `presence_hosts` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `contact` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `ping_status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `ping_count` int DEFAULT '0',
  `ping_time` bigint DEFAULT '0',
  `force_ping` int DEFAULT '0',
  `hostname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `network_ip` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `network_port` varchar(6) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `sip_username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `sip_realm` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `mwi_user` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `mwi_host` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `orig_server_host` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `orig_hostname` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  `sub_host` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT '',
  PRIMARY KEY (`call_id`),
  KEY `sr_sip_user` (`sip_user`),
  KEY `sr_sip_host` (`sip_host`),
  KEY `sr_sub_host` (`sub_host`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;



# 转储表 tasks
# ------------------------------------------------------------

DROP TABLE IF EXISTS `tasks`;

CREATE TABLE `tasks` (
  `task_id` int DEFAULT NULL,
  `task_desc` varchar(4096) DEFAULT NULL,
  `task_group` varchar(1024) DEFAULT NULL,
  `task_runtime` bigint DEFAULT NULL,
  `task_sql_manager` int DEFAULT NULL,
  `hostname` varchar(256) DEFAULT NULL,
  KEY `tasks1` (`hostname`,`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 voicemail_msgs
# ------------------------------------------------------------

DROP TABLE IF EXISTS `voicemail_msgs`;

CREATE TABLE `voicemail_msgs` (
  `created_epoch` int DEFAULT NULL,
  `read_epoch` int DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  `cid_name` varchar(255) DEFAULT NULL,
  `cid_number` varchar(255) DEFAULT NULL,
  `in_folder` varchar(255) DEFAULT NULL,
  `file_path` varchar(255) DEFAULT NULL,
  `message_len` int DEFAULT NULL,
  `flags` varchar(255) DEFAULT NULL,
  `read_flags` varchar(255) DEFAULT NULL,
  `forwarded_by` varchar(255) DEFAULT NULL,
  KEY `voicemail_msgs_idx1` (`created_epoch`),
  KEY `voicemail_msgs_idx2` (`username`),
  KEY `voicemail_msgs_idx3` (`domain`),
  KEY `voicemail_msgs_idx4` (`uuid`),
  KEY `voicemail_msgs_idx5` (`in_folder`),
  KEY `voicemail_msgs_idx6` (`read_flags`),
  KEY `voicemail_msgs_idx7` (`forwarded_by`),
  KEY `voicemail_msgs_idx8` (`read_epoch`),
  KEY `voicemail_msgs_idx9` (`flags`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 转储表 voicemail_prefs
# ------------------------------------------------------------

DROP TABLE IF EXISTS `voicemail_prefs`;

CREATE TABLE `voicemail_prefs` (
  `username` varchar(255) DEFAULT NULL,
  `domain` varchar(255) DEFAULT NULL,
  `name_path` varchar(255) DEFAULT NULL,
  `greeting_path` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  KEY `voicemail_prefs_idx1` (`username`),
  KEY `voicemail_prefs_idx2` (`domain`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;



# 导出视图 basic_calls
# ------------------------------------------------------------

DROP TABLE IF EXISTS `basic_calls`; DROP VIEW IF EXISTS `basic_calls`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `basic_calls`
AS SELECT
   `a`.`uuid` AS `uuid`,
   `a`.`direction` AS `direction`,
   `a`.`created` AS `created`,
   `a`.`created_epoch` AS `created_epoch`,
   `a`.`name` AS `name`,
   `a`.`state` AS `state`,
   `a`.`cid_name` AS `cid_name`,
   `a`.`cid_num` AS `cid_num`,
   `a`.`ip_addr` AS `ip_addr`,
   `a`.`dest` AS `dest`,
   `a`.`presence_id` AS `presence_id`,
   `a`.`presence_data` AS `presence_data`,
   `a`.`accountcode` AS `accountcode`,
   `a`.`callstate` AS `callstate`,
   `a`.`callee_name` AS `callee_name`,
   `a`.`callee_num` AS `callee_num`,
   `a`.`callee_direction` AS `callee_direction`,
   `a`.`call_uuid` AS `call_uuid`,
   `a`.`hostname` AS `hostname`,
   `a`.`sent_callee_name` AS `sent_callee_name`,
   `a`.`sent_callee_num` AS `sent_callee_num`,
   `b`.`uuid` AS `b_uuid`,
   `b`.`direction` AS `b_direction`,
   `b`.`created` AS `b_created`,
   `b`.`created_epoch` AS `b_created_epoch`,
   `b`.`name` AS `b_name`,
   `b`.`state` AS `b_state`,
   `b`.`cid_name` AS `b_cid_name`,
   `b`.`cid_num` AS `b_cid_num`,
   `b`.`ip_addr` AS `b_ip_addr`,
   `b`.`dest` AS `b_dest`,
   `b`.`presence_id` AS `b_presence_id`,
   `b`.`presence_data` AS `b_presence_data`,
   `b`.`accountcode` AS `b_accountcode`,
   `b`.`callstate` AS `b_callstate`,
   `b`.`callee_name` AS `b_callee_name`,
   `b`.`callee_num` AS `b_callee_num`,
   `b`.`callee_direction` AS `b_callee_direction`,
   `b`.`sent_callee_name` AS `b_sent_callee_name`,
   `b`.`sent_callee_num` AS `b_sent_callee_num`,
   `c`.`call_created_epoch` AS `call_created_epoch`
FROM ((`channels` `a` left join `calls` `c` on(((`a`.`uuid` = `c`.`caller_uuid`) and (`a`.`hostname` = `c`.`hostname`)))) left join `channels` `b` on(((`b`.`uuid` = `c`.`callee_uuid`) and (`b`.`hostname` = `c`.`hostname`)))) where ((`a`.`uuid` = `c`.`caller_uuid`) or `a`.`uuid` in (select `calls`.`callee_uuid` from `calls`) is false);

# 导出视图 detailed_calls
# ------------------------------------------------------------

DROP TABLE IF EXISTS `detailed_calls`; DROP VIEW IF EXISTS `detailed_calls`;

CREATE ALGORITHM=UNDEFINED DEFINER=`root`@`%` SQL SECURITY DEFINER VIEW `detailed_calls`
AS SELECT
   `a`.`uuid` AS `uuid`,
   `a`.`direction` AS `direction`,
   `a`.`created` AS `created`,
   `a`.`created_epoch` AS `created_epoch`,
   `a`.`name` AS `name`,
   `a`.`state` AS `state`,
   `a`.`cid_name` AS `cid_name`,
   `a`.`cid_num` AS `cid_num`,
   `a`.`ip_addr` AS `ip_addr`,
   `a`.`dest` AS `dest`,
   `a`.`application` AS `application`,
   `a`.`application_data` AS `application_data`,
   `a`.`dialplan` AS `dialplan`,
   `a`.`context` AS `context`,
   `a`.`read_codec` AS `read_codec`,
   `a`.`read_rate` AS `read_rate`,
   `a`.`read_bit_rate` AS `read_bit_rate`,
   `a`.`write_codec` AS `write_codec`,
   `a`.`write_rate` AS `write_rate`,
   `a`.`write_bit_rate` AS `write_bit_rate`,
   `a`.`secure` AS `secure`,
   `a`.`hostname` AS `hostname`,
   `a`.`presence_id` AS `presence_id`,
   `a`.`presence_data` AS `presence_data`,
   `a`.`accountcode` AS `accountcode`,
   `a`.`callstate` AS `callstate`,
   `a`.`callee_name` AS `callee_name`,
   `a`.`callee_num` AS `callee_num`,
   `a`.`callee_direction` AS `callee_direction`,
   `a`.`call_uuid` AS `call_uuid`,
   `a`.`sent_callee_name` AS `sent_callee_name`,
   `a`.`sent_callee_num` AS `sent_callee_num`,
   `b`.`uuid` AS `b_uuid`,
   `b`.`direction` AS `b_direction`,
   `b`.`created` AS `b_created`,
   `b`.`created_epoch` AS `b_created_epoch`,
   `b`.`name` AS `b_name`,
   `b`.`state` AS `b_state`,
   `b`.`cid_name` AS `b_cid_name`,
   `b`.`cid_num` AS `b_cid_num`,
   `b`.`ip_addr` AS `b_ip_addr`,
   `b`.`dest` AS `b_dest`,
   `b`.`application` AS `b_application`,
   `b`.`application_data` AS `b_application_data`,
   `b`.`dialplan` AS `b_dialplan`,
   `b`.`context` AS `b_context`,
   `b`.`read_codec` AS `b_read_codec`,
   `b`.`read_rate` AS `b_read_rate`,
   `b`.`read_bit_rate` AS `b_read_bit_rate`,
   `b`.`write_codec` AS `b_write_codec`,
   `b`.`write_rate` AS `b_write_rate`,
   `b`.`write_bit_rate` AS `b_write_bit_rate`,
   `b`.`secure` AS `b_secure`,
   `b`.`hostname` AS `b_hostname`,
   `b`.`presence_id` AS `b_presence_id`,
   `b`.`presence_data` AS `b_presence_data`,
   `b`.`accountcode` AS `b_accountcode`,
   `b`.`callstate` AS `b_callstate`,
   `b`.`callee_name` AS `b_callee_name`,
   `b`.`callee_num` AS `b_callee_num`,
   `b`.`callee_direction` AS `b_callee_direction`,
   `b`.`call_uuid` AS `b_call_uuid`,
   `b`.`sent_callee_name` AS `b_sent_callee_name`,
   `b`.`sent_callee_num` AS `b_sent_callee_num`,
   `c`.`call_created_epoch` AS `call_created_epoch`
FROM ((`channels` `a` left join `calls` `c` on(((`a`.`uuid` = `c`.`caller_uuid`) and (`a`.`hostname` = `c`.`hostname`)))) left join `channels` `b` on(((`b`.`uuid` = `c`.`callee_uuid`) and (`b`.`hostname` = `c`.`hostname`)))) where ((`a`.`uuid` = `c`.`caller_uuid`) or `a`.`uuid` in (select `calls`.`callee_uuid` from `calls`) is false);


/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
