/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80406 (8.4.6)
 Source Host           : localhost:3306
 Source Schema         : email_mgr

 Target Server Type    : MySQL
 Target Server Version : 80406 (8.4.6)
 File Encoding         : 65001

 Date: 25/08/2025 20:53:34
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for email_account
-- ----------------------------
DROP TABLE IF EXISTS `email_account`;
CREATE TABLE `email_account`  (
  `account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `account_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '账号名称',
  `email_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
  `password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱密码(加密)',
  `smtp_host` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT 'SMTP服务器',
  `smtp_port` int NOT NULL COMMENT 'SMTP端口',
  `smtp_ssl` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否启用SSL(0否 1是)',
  `imap_host` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IMAP服务器',
  `imap_port` int NULL DEFAULT NULL COMMENT 'IMAP端口',
  `imap_ssl` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT 'IMAP是否启用SSL(0否 1是)',
  `imap_username` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IMAP用户名(通常与邮箱地址相同)',
  `imap_password` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IMAP密码(加密，通常与SMTP密码相同)',
  `webhook_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Webhook回调地址',
  `webhook_secret` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'Webhook密钥',
  `tracking_enabled` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '是否启用邮件跟踪(0否 1是)',
  `daily_limit` int NULL DEFAULT 100 COMMENT '每日发送限制',
  `used_count` int NULL DEFAULT 0 COMMENT '今日已发送数量',
  `last_send_time` datetime NULL DEFAULT NULL COMMENT '最后发送时间',
  `last_sync_time` datetime NULL DEFAULT NULL COMMENT '最后同步时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  `listener_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'stopped' COMMENT 'IMAP监听状态(running=运行中, stopped=已停止, error=错误)',
  `sync_count` int NULL DEFAULT 0 COMMENT '同步邮件数量',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `monitor_enabled` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否启用监控(0否 1是)',
  `auto_start_monitor` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '是否自动启动监听(0否 1是)',
  `monitor_priority` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'medium' COMMENT '监控优先级(high=高, medium=中, low=低)',
  `monitor_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'stopped' COMMENT '监控状态(running=运行中, stopped=已停止, error=错误, connecting=连接中)',
  `imap_monitor_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'stopped' COMMENT 'IMAP监控状态',
  `smtp_monitor_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'stopped' COMMENT 'SMTP监控状态',
  `last_monitor_time` datetime NULL DEFAULT NULL COMMENT '最后监控时间',
  `monitor_error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '监控错误信息',
  PRIMARY KEY (`account_id`) USING BTREE,
  UNIQUE INDEX `uk_email_address`(`email_address` ASC) USING BTREE,
  INDEX `idx_email_account_listener_status`(`listener_status` ASC) USING BTREE,
  INDEX `idx_email_account_email_address`(`email_address` ASC) USING BTREE,
  INDEX `idx_email_account_imap_host`(`imap_host` ASC) USING BTREE,
  INDEX `idx_email_account_tracking_enabled`(`tracking_enabled` ASC) USING BTREE,
  INDEX `idx_email_account_monitor_enabled`(`monitor_enabled` ASC) USING BTREE,
  INDEX `idx_email_account_monitor_status`(`monitor_status` ASC) USING BTREE,
  INDEX `idx_email_account_monitor_priority`(`monitor_priority` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮箱账号表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_account
-- ----------------------------
INSERT INTO `email_account` VALUES (1, 'Gmail测试账号', 'trouble.linky@gmail.com', 'hmle pvoe hfox qjgp', 'smtp.gmail.com', 587, '1', 'imap.gmail.com', 993, '1', 'trouble.linky@gmail.com', 'hmle pvoe hfox qjgp', 'https://your-domain.com/email/webhook/callback', NULL, '1', 100, 2, '2025-08-21 12:49:44', '2025-08-25 13:01:55', '0', 'Gmail测试账号', '', '2025-08-20 09:10:16', '', '2025-08-25 13:01:56', '0', 'stopped', 18, NULL, '0', '0', 'medium', 'stopped', 'stopped', 'stopped', NULL, NULL);
INSERT INTO `email_account` VALUES (6, 'my', '182867664@qq.com', 'xwoohyhjidmucafg', 'smtp.qq.com', 465, '1', 'imap.qq.com', 993, '1', '182867664@qq.com', 'xwoohyhjidmucafg', 'https://your-domain.com/email/webhook/callback', NULL, '1', 100, 9, '2025-08-25 00:12:56', '2025-08-25 14:01:44', '0', NULL, '', '2025-08-20 12:30:20', '', '2025-08-25 14:01:45', '0', 'stopped', 31, NULL, '0', '0', 'medium', 'stopped', 'stopped', 'stopped', NULL, NULL);

-- ----------------------------
-- Table structure for email_contact
-- ----------------------------
DROP TABLE IF EXISTS `email_contact`;
CREATE TABLE `email_contact`  (
  `contact_id` bigint NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '姓名',
  `email` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
  `company` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '企业名称',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '地址',
  `age` int NULL DEFAULT NULL COMMENT '年龄',
  `gender` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '性别(0未知 1男 2女)',
  `social_media` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '社交媒体账号',
  `followers` int NULL DEFAULT 0 COMMENT '粉丝数量',
  `level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '3' COMMENT '等级(1重要 2普通 3一般)',
  `group_id` bigint NULL DEFAULT NULL COMMENT '群组ID',
  `tags` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '标签(逗号分隔)',
  `send_count` int NULL DEFAULT 0 COMMENT '发送邮件数量',
  `reply_count` int NULL DEFAULT 0 COMMENT '回复邮件数量',
  `open_count` int NULL DEFAULT 0 COMMENT '打开邮件数量',
  `reply_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '回复率',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`contact_id`) USING BTREE,
  UNIQUE INDEX `uk_email`(`email` ASC) USING BTREE,
  INDEX `idx_group_id`(`group_id` ASC) USING BTREE,
  INDEX `idx_level`(`level` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件联系人表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_contact
-- ----------------------------
INSERT INTO `email_contact` VALUES (1, '哒哒', '182867664@qq.com', '', '', 1, '0', NULL, 0, '', NULL, '', 0, 0, 0, 0.00, '0', '', '', '2025-08-19 13:51:09', '', NULL, '0');
INSERT INTO `email_contact` VALUES (2, 'aa', 'trouble.linky@gmail.com', '', '', 1, '0', NULL, 0, '', 2, '', 0, 0, 0, 0.00, '0', '', '', '2025-08-21 12:37:06', '', NULL, '0');
INSERT INTO `email_contact` VALUES (3, 'nn', '1444927178@qq.com', '', '', 1, '0', NULL, 0, '', NULL, '', 0, 0, 0, 0.00, '0', '', '', '2025-08-25 00:12:32', '', NULL, '0');

-- ----------------------------
-- Table structure for email_contact_group
-- ----------------------------
DROP TABLE IF EXISTS `email_contact_group`;
CREATE TABLE `email_contact_group`  (
  `group_id` bigint NOT NULL AUTO_INCREMENT COMMENT '群组ID',
  `group_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '群组名称',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '群组描述',
  `contact_count` int NULL DEFAULT 0 COMMENT '联系人数量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`group_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '联系人群组表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_contact_group
-- ----------------------------
INSERT INTO `email_contact_group` VALUES (1, 'VIP客户', '重要客户群组', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_contact_group` VALUES (2, '普通客户', '普通客户群组', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_contact_group` VALUES (3, '潜在客户', '潜在客户群组', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');

-- ----------------------------
-- Table structure for email_contact_sales
-- ----------------------------
DROP TABLE IF EXISTS `email_contact_sales`;
CREATE TABLE `email_contact_sales`  (
  `sales_id` bigint NOT NULL AUTO_INCREMENT COMMENT '销售数据ID',
  `contact_id` bigint NOT NULL COMMENT '联系人ID',
  `sales_amount` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '销售金额',
  `sales_date` date NULL DEFAULT NULL COMMENT '销售日期',
  `product_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '产品名称',
  `sales_channel` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '销售渠道',
  `sales_notes` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '销售备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`sales_id`) USING BTREE,
  INDEX `idx_contact_id`(`contact_id` ASC) USING BTREE,
  INDEX `idx_sales_date`(`sales_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '联系人销售数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_contact_sales
-- ----------------------------

-- ----------------------------
-- Table structure for email_contact_tag
-- ----------------------------
DROP TABLE IF EXISTS `email_contact_tag`;
CREATE TABLE `email_contact_tag`  (
  `tag_id` bigint NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '#409EFF' COMMENT '标签颜色',
  `contact_count` int NULL DEFAULT 0 COMMENT '使用该标签的联系人数量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`tag_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '联系人标签表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_contact_tag
-- ----------------------------
INSERT INTO `email_contact_tag` VALUES (1, '重要', '#F56C6C', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_contact_tag` VALUES (2, '项目', '#67C23A', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_contact_tag` VALUES (3, '待处理', '#E6A23C', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_contact_tag` VALUES (4, '归档', '#909399', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');

-- ----------------------------
-- Table structure for email_personal
-- ----------------------------
DROP TABLE IF EXISTS `email_personal`;
CREATE TABLE `email_personal`  (
  `email_id` bigint NOT NULL AUTO_INCREMENT COMMENT '邮件ID',
  `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮件Message-ID',
  `from_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发件人邮箱',
  `to_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件人邮箱',
  `subject` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮件主题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '邮件内容',
  `html_content` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'HTML内容',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'unread' COMMENT '邮件状态(unread=未读,read=已读,starred=星标,deleted=已删除)',
  `is_starred` tinyint(1) NULL DEFAULT 0 COMMENT '是否星标(0=否,1=是)',
  `is_important` tinyint(1) NULL DEFAULT 0 COMMENT '是否重要(0=否,1=是)',
  `receive_time` datetime NULL DEFAULT NULL COMMENT '接收时间',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `email_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'inbox' COMMENT '邮件类型(inbox=收件箱,sent=发件箱,starred=星标,deleted=已删除)',
  `attachment_count` int NULL DEFAULT 0 COMMENT '附件数量',
  `is_replied` tinyint(1) NULL DEFAULT 0 COMMENT '是否已回复：0=否，1=是',
  `is_delivered` tinyint(1) NULL DEFAULT 0 COMMENT '是否已送达：0=否，1=是',
  `delete_time` datetime NULL DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`email_id`) USING BTREE,
  INDEX `idx_from_address`(`from_address` ASC) USING BTREE,
  INDEX `idx_to_address`(`to_address` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_email_type`(`email_type` ASC) USING BTREE,
  INDEX `idx_receive_time`(`receive_time` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE,
  INDEX `idx_create_by`(`create_by` ASC) USING BTREE,
  INDEX `idx_message_id`(`message_id` ASC) USING BTREE,
  INDEX `idx_is_replied`(`is_replied` ASC) USING BTREE,
  INDEX `idx_is_delivered`(`is_delivered` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个人邮件表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_personal
-- ----------------------------
INSERT INTO `email_personal` VALUES (2, NULL, 'tech@company.com', 'admin@company.com', '生产环境系统升级提醒', '各位同事：计划于今晚23:00至次日凌晨3:00进行生产环境系统升级，升级期间将暂停...', '<p>各位同事：计划于今晚23:00至次日凌晨3:00进行生产环境系统升级，升级期间将暂停...</p>', 'read', 1, 1, '2024-01-15 09:15:00', NULL, 'inbox', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (3, NULL, 'project@company.com', 'admin@company.com', '【周报】数字化项目进度报告 - 第25周', '项目整体进度：完成86%，本周完成需求分析会议3次，核心模块...', '<p>项目整体进度：完成86%，本周完成需求分析会议3次，核心模块...</p>', 'read', 1, 0, '2024-01-14 16:20:00', NULL, 'inbox', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (4, NULL, 'marketing@company.com', 'admin@company.com', '市场活动策划方案', '关于下季度市场活动的策划方案，请查收...', '<p>关于下季度市场活动的策划方案，请查收...</p>', 'read', 0, 1, '2024-01-14 14:20:00', NULL, 'inbox', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (5, NULL, 'support@company.com', 'admin@company.com', '技术支持回复', '您提交的技术问题已解决，详情请查看...', '<p>您提交的技术问题已解决，详情请查看...</p>', 'read', 0, 0, '2024-01-13 11:30:00', NULL, 'inbox', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (6, NULL, 'admin@company.com', 'hr@company.com', '员工福利申请', '关于员工福利申请的相关事宜...', '<p>关于员工福利申请的相关事宜...</p>', 'sent', 0, 0, NULL, '2024-01-15 14:30:00', 'sent', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (7, NULL, 'admin@company.com', 'tech@company.com', '系统维护通知', '系统维护相关通知...', '<p>系统维护相关通知...</p>', 'sent', 0, 0, NULL, '2024-01-15 11:20:00', 'sent', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (8, NULL, 'admin@company.com', 'project@company.com', '项目进度汇报', '关于当前项目进度的详细汇报...', '<p>关于当前项目进度的详细汇报...</p>', 'sent', 1, 0, NULL, '2024-01-14 16:45:00', 'sent', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (9, NULL, 'admin@company.com', 'finance@company.com', '预算申请', '关于下季度预算的申请报告...', '<p>关于下季度预算的申请报告...</p>', 'sent', 0, 1, NULL, '2024-01-13 09:15:00', 'sent', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (10, NULL, 'admin@company.com', 'support@company.com', '技术问题咨询', '关于系统使用中遇到的技术问题...', '<p>关于系统使用中遇到的技术问题...</p>', 'sent', 0, 0, NULL, '2024-01-12 15:20:00', 'sent', 0, 0, 0, NULL, 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (11, NULL, 'spam@example.com', 'admin@company.com', '垃圾邮件示例', '这是一封垃圾邮件的内容，包含一些不必要的信息...', '<p>这是一封垃圾邮件的内容，包含一些不必要的信息...</p><p>请忽略此邮件。</p>', 'deleted', 0, 0, '2024-01-15 16:30:00', NULL, 'inbox', 0, 0, 0, '2024-01-15 16:30:00', 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (12, NULL, 'admin@company.com', 'old@company.com', '过期的会议通知', '关于过期会议的通知内容，会议已经结束...', '<p>关于过期会议的通知内容，会议已经结束...</p><p>此邮件已过期，可以删除。</p>', 'deleted', 0, 0, NULL, '2024-01-14 10:15:00', 'sent', 0, 0, 0, '2024-01-14 10:15:00', 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (13, NULL, 'test@company.com', 'admin@company.com', '测试邮件', '这是一封测试邮件，已被删除...', '<p>这是一封测试邮件，已被删除...</p><p>用于测试邮件系统功能。</p>', 'deleted', 1, 0, '2024-01-13 14:20:00', NULL, 'inbox', 0, 0, 0, '2024-01-13 14:20:00', 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (14, NULL, 'notification@company.com', 'admin@company.com', '系统维护通知', '系统将于今晚进行维护，请提前保存工作...', '<p>系统将于今晚进行维护，请提前保存工作...</p><p>维护时间：22:00-02:00</p>', 'deleted', 0, 1, '2024-01-12 09:45:00', NULL, 'inbox', 0, 0, 0, '2024-01-12 09:45:00', 'admin', '2025-08-19 09:18:49', '', NULL, NULL);
INSERT INTO `email_personal` VALUES (15, NULL, 'admin@company.com', 'support@company.com', '技术支持请求', '关于系统使用的问题咨询...', '<p>关于系统使用的问题咨询...</p><p>问题已解决，可以删除。</p>', 'deleted', 0, 0, NULL, '2024-01-11 15:20:00', 'sent', 0, 0, 0, '2024-01-11 15:20:00', 'admin', '2025-08-19 09:18:49', '', NULL, NULL);

-- ----------------------------
-- Table structure for email_send_record
-- ----------------------------
DROP TABLE IF EXISTS `email_send_record`;
CREATE TABLE `email_send_record`  (
  `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `task_id` bigint NULL DEFAULT NULL COMMENT '任务ID',
  `contact_id` bigint NOT NULL COMMENT '联系人ID',
  `account_id` bigint NOT NULL COMMENT '发件账号ID',
  `email_address` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收件邮箱',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件主题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件内容',
  `send_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `delivered_time` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime NULL DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0待发送 1已发送 2已送达 3已打开 4已回复 5发送失败)',
  `error_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_contact_id`(`contact_id` ASC) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件发送记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_send_record
-- ----------------------------

-- ----------------------------
-- Table structure for email_send_task
-- ----------------------------
DROP TABLE IF EXISTS `email_send_task`;
CREATE TABLE `email_send_task`  (
  `task_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `template_id` bigint NULL DEFAULT NULL COMMENT '模板ID',
  `account_id` bigint NULL DEFAULT NULL COMMENT '发件人账号ID',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件主题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件内容',
  `recipient_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'all' COMMENT '收件人类型(all全部 group群组 tag标签 manual手动)',
  `recipient_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '收件人ID列表(JSON格式)',
  `group_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '群组ID列表(逗号分隔)',
  `tag_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '标签ID列表(逗号分隔)',
  `contact_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '联系人ID列表(逗号分隔)',
  `account_ids` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '发件账号ID列表(JSON格式)',
  `send_mode` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'immediate' COMMENT '发送模式(immediate立即发送 scheduled定时发送)',
  `send_interval` int NULL DEFAULT 10 COMMENT '发送间隔(秒)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始发送时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束发送时间',
  `total_count` int NULL DEFAULT 0 COMMENT '总发送数量',
  `sent_count` int NULL DEFAULT 0 COMMENT '已发送数量',
  `delivered_count` int NULL DEFAULT 0 COMMENT '送达数量',
  `opened_count` int NULL DEFAULT 0 COMMENT '打开数量',
  `replied_count` int NULL DEFAULT 0 COMMENT '回复数量',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0待发送 1发送中 2已完成 3已暂停 4已取消)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  `template_variables` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模板变量(JSON格式)',
  PRIMARY KEY (`task_id`) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 32 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '批量发送任务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_send_task
-- ----------------------------
INSERT INTO `email_send_task` VALUES (1, 'test', 2, NULL, '邀请您参加重要会议', '<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>', 'manual', NULL, NULL, NULL, NULL, NULL, 'immediate', 10, NULL, NULL, 0, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-21 09:16:10', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (2, 'test', 1, 6, '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '1', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-21 11:41:12', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (3, 'test2', 1, 6, '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '1', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-21 11:50:45', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (4, 'aaa', 2, 6, '邀请您参加重要会议', '<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>', 'manual', NULL, NULL, NULL, '1', NULL, 'immediate', 10, NULL, NULL, 1, 1, 1, 0, 0, '2', NULL, 'admin', '2025-08-21 12:30:52', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (5, 'gg', 1, 1, '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-21 12:39:34', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (6, 'gg4', 1, 1, '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-21 12:41:53', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (7, 'gg45', 1, 1, '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 2, 2, 0, 0, '2', NULL, 'admin', '2025-08-21 12:49:02', '', NULL, '0', NULL);
INSERT INTO `email_send_task` VALUES (8, 'test22', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '4', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 0, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-24 17:59:42', '', NULL, '0', '{\"name\":\"test\"}');
INSERT INTO `email_send_task` VALUES (9, 'haha', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '1', NULL, NULL, NULL, NULL, NULL, 'immediate', 10, NULL, NULL, 0, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-24 18:04:04', '', NULL, '0', '{\"name\":\"dad\"}');
INSERT INTO `email_send_task` VALUES (10, 'haha2', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '1', NULL, NULL, NULL, NULL, NULL, 'immediate', 10, NULL, NULL, 0, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-24 18:04:38', '', NULL, '0', '{\"name\":\"dad\"}');
INSERT INTO `email_send_task` VALUES (11, 'gaga', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'all', NULL, NULL, NULL, NULL, NULL, 'immediate', 10, NULL, NULL, 2, 1, 1, 0, 0, '2', NULL, 'admin', '2025-08-24 18:10:55', '', NULL, '0', '{\"name\":\"dada\"}');
INSERT INTO `email_send_task` VALUES (12, 'gg45_复制', 1, 1, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-24 20:30:56', '', NULL, '0', '{\"name\":\"嘎嘎\"}');
INSERT INTO `email_send_task` VALUES (13, 'gg45_复制_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 2, 2, 0, 0, '2', NULL, 'admin', '2025-08-24 20:53:38', '', NULL, '0', '{\"name\":\"翻页\"}');
INSERT INTO `email_send_task` VALUES (14, 'gg45_复制_复制_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 2, 2, 0, 0, '2', NULL, 'admin', '2025-08-24 21:22:05', '', NULL, '0', '{\"name\":\"行号\"}');
INSERT INTO `email_send_task` VALUES (15, 'gg45_22', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 2, 2, 0, 0, '2', NULL, 'admin', '2025-08-24 22:47:00', '', NULL, '0', '{\"name\":\"方法\"}');
INSERT INTO `email_send_task` VALUES (16, 'gg45_33', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 2, 2, 0, 0, '2', NULL, 'admin', '2025-08-24 22:48:55', '', NULL, '0', '{\"name\":\"天天\"}');
INSERT INTO `email_send_task` VALUES (17, 'gg45_44', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 1, 1, 0, 0, '2', NULL, 'admin', '2025-08-24 23:53:09', '', NULL, '0', '{\"name\":\"刚刚\"}');
INSERT INTO `email_send_task` VALUES (18, 'gg45_44_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 1, 1, 0, 0, '2', NULL, 'admin', '2025-08-25 00:04:35', '', NULL, '0', '{\"name\":\"方法\"}');
INSERT INTO `email_send_task` VALUES (19, 'gg45_55', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '2,1', NULL, 'immediate', 10, NULL, NULL, 2, 1, 1, 0, 0, '2', NULL, 'admin', '2025-08-25 00:10:55', '', NULL, '0', '{\"name\":\"报表\"}');
INSERT INTO `email_send_task` VALUES (20, 'gg45_66', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 1, 1, 0, 0, '2', NULL, 'admin', '2025-08-25 00:12:55', '', NULL, '0', '{\"name\":\"bb\"}');
INSERT INTO `email_send_task` VALUES (21, 'gg45_7', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-25 00:18:42', '', NULL, '0', '{\"name\":\"ffs\"}');
INSERT INTO `email_send_task` VALUES (22, 'gg45_7_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-25 00:27:01', '', NULL, '0', '{\"name\":\"hh\"}');
INSERT INTO `email_send_task` VALUES (23, 'gg45_7_复制_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-25 00:39:12', '', NULL, '0', '{\"name\":\"vv\"}');
INSERT INTO `email_send_task` VALUES (24, 'gg45_8', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-25 12:34:24', '', NULL, '0', '{\"name\":\"bb\"}');
INSERT INTO `email_send_task` VALUES (25, 'gg45_82', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-25 12:35:53', '', NULL, '0', '{\"name\":\"bb\"}');
INSERT INTO `email_send_task` VALUES (26, 'gg45_84', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-25 12:37:09', '', NULL, '0', '{\"name\":\"ff\"}');
INSERT INTO `email_send_task` VALUES (27, 'gg45_85', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-25 12:39:01', '', NULL, '0', '{\"name\":\"ff\"}');
INSERT INTO `email_send_task` VALUES (28, 'gg45_86', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-25 12:41:12', '', NULL, '0', '{\"name\":\"ff\"}');
INSERT INTO `email_send_task` VALUES (29, 'gg45_86_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-25 12:42:00', '', NULL, '0', '{\"name\":\"bb\"}');
INSERT INTO `email_send_task` VALUES (30, 'gg45_82_复制', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '2', NULL, 'admin', '2025-08-25 12:43:19', '', NULL, '0', '{\"name\":\"nn\"}');
INSERT INTO `email_send_task` VALUES (31, 'gg45_82_复制3', 1, 6, '<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', 'manual', NULL, NULL, NULL, '3', NULL, 'immediate', 10, NULL, NULL, 1, 0, 0, 0, 0, '4', NULL, 'admin', '2025-08-25 12:48:05', '', NULL, '0', '{\"name\":\"ff\"}');

-- ----------------------------
-- Table structure for email_service_monitor
-- ----------------------------
DROP TABLE IF EXISTS `email_service_monitor`;
CREATE TABLE `email_service_monitor`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint NOT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮箱地址',
  `imap_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'stopped' COMMENT 'IMAP服务状态：running/stopped/error',
  `smtp_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'stopped' COMMENT 'SMTP服务状态：running/stopped/error',
  `imap_last_check_time` datetime NULL DEFAULT NULL COMMENT 'IMAP最后检查时间',
  `smtp_last_check_time` datetime NULL DEFAULT NULL COMMENT 'SMTP最后检查时间',
  `imap_error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'IMAP错误信息',
  `smtp_error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'SMTP错误信息',
  `imap_error_time` datetime NULL DEFAULT NULL COMMENT 'IMAP错误时间',
  `smtp_error_time` datetime NULL DEFAULT NULL COMMENT 'SMTP错误时间',
  `monitor_enabled` tinyint(1) NULL DEFAULT 1 COMMENT '是否启用监控：0-禁用，1-启用',
  `monitor_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '监控状态：0-停止，1-运行中',
  `last_monitor_time` datetime NULL DEFAULT NULL COMMENT '最后监控时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_email_address`(`email_address` ASC) USING BTREE,
  INDEX `idx_imap_status`(`imap_status` ASC) USING BTREE,
  INDEX `idx_smtp_status`(`smtp_status` ASC) USING BTREE,
  INDEX `idx_monitor_status`(`monitor_status` ASC) USING BTREE,
  INDEX `idx_last_monitor_time`(`last_monitor_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件服务监控状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_service_monitor
-- ----------------------------
INSERT INTO `email_service_monitor` VALUES (1, 1, 'trouble.linky@gmail.com', '4', '2', '2025-08-25 20:51:44', '2025-08-25 14:26:25', '连接测试失败，请检查网络或服务器配置', '网络超时: Couldn\'t connect to host, port: smtp.gmail.com, 587; timeout 30000', '2025-08-25 20:51:44', '2025-08-25 14:17:53', 1, '0', NULL, '', '2025-08-22 14:25:39', 'system', '2025-08-25 20:51:44', NULL);
INSERT INTO `email_service_monitor` VALUES (2, 6, '182867664@qq.com', '4', '2', '2025-08-25 20:51:44', '2025-08-25 14:26:24', '连接测试失败，请检查网络或服务器配置', '端口错误: Could not connect to SMTP host: smtp.qq.com, port: 465, response: 451', '2025-08-25 20:51:44', '2025-08-25 14:04:19', 1, '0', NULL, '', '2025-08-22 14:25:39', 'system', '2025-08-25 20:51:44', NULL);

-- ----------------------------
-- Table structure for email_service_monitor_config
-- ----------------------------
DROP TABLE IF EXISTS `email_service_monitor_config`;
CREATE TABLE `email_service_monitor_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置值',
  `config_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '配置描述',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_config_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件服务监控配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_service_monitor_config
-- ----------------------------
INSERT INTO `email_service_monitor_config` VALUES (1, 'imap_check_interval', '30', 'IMAP服务检查间隔（秒）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (2, 'smtp_check_interval', '60', 'SMTP服务检查间隔（秒）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (3, 'connection_timeout', '10000', '连接超时时间（毫秒）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (4, 'read_timeout', '30000', '读取超时时间（毫秒）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (5, 'retry_count', '3', '连接失败重试次数', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (6, 'retry_delay', '5000', '重试延迟时间（毫秒）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (7, 'error_notification_enabled', '1', '是否启用错误通知：0-禁用，1-启用', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (8, 'error_notification_threshold', '3', '错误通知阈值（连续失败次数）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (9, 'auto_reconnect_enabled', '1', '是否启用自动重连：0-禁用，1-启用', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');
INSERT INTO `email_service_monitor_config` VALUES (10, 'auto_reconnect_delay', '30000', '自动重连延迟时间（毫秒）', 'system', '2025-08-24 09:24:39', 'system', '2025-08-24 09:24:39');

-- ----------------------------
-- Table structure for email_service_monitor_log
-- ----------------------------
DROP TABLE IF EXISTS `email_service_monitor_log`;
CREATE TABLE `email_service_monitor_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `account_id` bigint NULL DEFAULT NULL COMMENT '邮箱账号ID',
  `email_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `service_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '服务类型：IMAP/SMTP',
  `status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态：success/error',
  `message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '状态信息',
  `check_time` datetime NOT NULL COMMENT '检查时间',
  `response_time` int NULL DEFAULT NULL COMMENT '响应时间(毫秒)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_email_address`(`email_address` ASC) USING BTREE,
  INDEX `idx_service_type`(`service_type` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_check_time`(`check_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 272 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件服务监控日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_service_monitor_log
-- ----------------------------
INSERT INTO `email_service_monitor_log` VALUES (1, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-22 14:35:00', NULL, 'system', '2025-08-22 14:35:00', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (2, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-22 14:35:00', NULL, 'system', '2025-08-22 14:35:00', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (3, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-22 14:36:15', NULL, 'system', '2025-08-22 14:36:15', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (4, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-22 14:37:38', NULL, 'system', '2025-08-22 14:37:38', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (5, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-22 14:37:38', NULL, 'system', '2025-08-22 14:37:38', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (6, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-22 14:37:54', NULL, 'system', '2025-08-22 14:37:54', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (7, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-22 14:43:58', NULL, 'system', '2025-08-22 14:43:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (8, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-22 14:43:58', NULL, 'system', '2025-08-22 14:43:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (9, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 07:40:35', NULL, 'system', '2025-08-24 07:40:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (10, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 07:41:35', NULL, 'system', '2025-08-24 07:41:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (11, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 07:41:35', NULL, 'system', '2025-08-24 07:41:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (12, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 07:59:13', NULL, 'system', '2025-08-24 07:59:13', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (13, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 07:59:14', NULL, 'system', '2025-08-24 07:59:14', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (14, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 07:59:14', NULL, 'system', '2025-08-24 07:59:14', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (15, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 07:59:27', NULL, 'system', '2025-08-24 07:59:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (16, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 08:00:27', NULL, 'system', '2025-08-24 08:00:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (17, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 08:00:27', NULL, 'system', '2025-08-24 08:00:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (18, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 08:08:35', NULL, 'system', '2025-08-24 08:08:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (19, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 08:08:36', NULL, 'system', '2025-08-24 08:08:36', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (20, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 08:08:36', NULL, 'system', '2025-08-24 08:08:36', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (21, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 08:16:23', NULL, 'system', '2025-08-24 08:16:23', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (22, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 08:17:23', NULL, 'system', '2025-08-24 08:17:23', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (23, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 08:17:23', NULL, 'system', '2025-08-24 08:17:23', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (24, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 08:21:42', NULL, 'system', '2025-08-24 08:21:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (25, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 08:21:43', NULL, 'system', '2025-08-24 08:21:43', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (26, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 08:21:43', NULL, 'system', '2025-08-24 08:21:43', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (27, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 08:34:53', NULL, 'system', '2025-08-24 08:34:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (28, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 08:35:53', NULL, 'system', '2025-08-24 08:35:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (29, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 08:35:53', NULL, 'system', '2025-08-24 08:35:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (30, 6, NULL, 'SMTP', 'SUCCESS', 'SMTP连接测试成功', '2025-08-24 08:36:26', NULL, 'system', '2025-08-24 08:36:26', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (31, 1, NULL, 'SMTP', 'FAILED', 'Couldn\'t connect to host, port: smtp.gmail.com, 587; timeout 30000', '2025-08-24 08:36:52', NULL, 'system', '2025-08-24 08:36:52', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (32, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 09:23:46', NULL, 'system', '2025-08-24 09:23:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (33, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 09:24:46', NULL, 'system', '2025-08-24 09:24:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (34, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 09:24:46', NULL, 'system', '2025-08-24 09:24:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (35, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 09:25:36', NULL, 'system', '2025-08-24 09:25:36', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (36, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 09:25:46', NULL, 'system', '2025-08-24 09:25:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (37, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 09:37:46', NULL, 'system', '2025-08-24 09:37:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (38, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 09:38:46', NULL, 'system', '2025-08-24 09:38:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (39, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 09:38:46', NULL, 'system', '2025-08-24 09:38:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (40, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 09:52:16', NULL, 'system', '2025-08-24 09:52:16', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (41, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 09:53:16', NULL, 'system', '2025-08-24 09:53:16', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (42, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 09:53:16', NULL, 'system', '2025-08-24 09:53:16', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (43, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 10:06:47', NULL, 'system', '2025-08-24 10:06:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (44, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 10:06:47', NULL, 'system', '2025-08-24 10:06:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (45, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 10:06:47', NULL, 'system', '2025-08-24 10:06:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (46, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 10:07:02', NULL, 'system', '2025-08-24 10:07:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (47, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 10:08:02', NULL, 'system', '2025-08-24 10:08:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (48, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 10:08:02', NULL, 'system', '2025-08-24 10:08:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (49, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 10:14:26', NULL, 'system', '2025-08-24 10:14:26', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (50, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 10:14:27', NULL, 'system', '2025-08-24 10:14:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (51, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 10:14:27', NULL, 'system', '2025-08-24 10:14:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (52, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 10:14:41', NULL, 'system', '2025-08-24 10:14:41', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (53, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 10:15:41', NULL, 'system', '2025-08-24 10:15:41', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (54, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 10:15:41', NULL, 'system', '2025-08-24 10:15:41', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (55, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 10:28:30', NULL, 'system', '2025-08-24 10:28:30', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (56, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 10:28:30', NULL, 'system', '2025-08-24 10:28:30', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (57, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 10:28:30', NULL, 'system', '2025-08-24 10:28:30', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (58, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 10:28:48', NULL, 'system', '2025-08-24 10:28:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (59, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 10:29:48', NULL, 'system', '2025-08-24 10:29:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (60, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 10:29:48', NULL, 'system', '2025-08-24 10:29:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (61, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 10:55:31', NULL, 'system', '2025-08-24 10:55:31', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (62, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 10:55:32', NULL, 'system', '2025-08-24 10:55:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (63, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 10:55:32', NULL, 'system', '2025-08-24 10:55:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (64, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 10:55:47', NULL, 'system', '2025-08-24 10:55:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (65, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 10:56:47', NULL, 'system', '2025-08-24 10:56:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (66, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 10:56:47', NULL, 'system', '2025-08-24 10:56:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (67, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 11:27:17', NULL, 'system', '2025-08-24 11:27:17', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (68, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:28:18', NULL, 'system', '2025-08-24 11:28:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (69, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:28:18', NULL, 'system', '2025-08-24 11:28:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (70, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 11:32:18', NULL, 'system', '2025-08-24 11:32:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (71, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 11:32:18', NULL, 'system', '2025-08-24 11:32:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (72, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 11:32:18', NULL, 'system', '2025-08-24 11:32:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (73, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:33:17', NULL, 'system', '2025-08-24 11:33:17', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (74, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:33:18', NULL, 'system', '2025-08-24 11:33:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (75, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 11:36:42', NULL, 'system', '2025-08-24 11:36:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (76, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:37:42', NULL, 'system', '2025-08-24 11:37:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (77, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:37:42', NULL, 'system', '2025-08-24 11:37:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (78, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 11:38:49', NULL, 'system', '2025-08-24 11:38:49', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (79, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 11:38:49', NULL, 'system', '2025-08-24 11:38:49', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (80, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 11:38:51', NULL, 'system', '2025-08-24 11:38:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (81, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 11:39:19', NULL, 'system', '2025-08-24 11:39:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (82, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:39:42', NULL, 'system', '2025-08-24 11:39:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (83, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:39:42', NULL, 'system', '2025-08-24 11:39:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (84, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 11:40:32', NULL, 'system', '2025-08-24 11:40:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (85, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 11:40:33', NULL, 'system', '2025-08-24 11:40:33', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (86, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 11:40:35', NULL, 'system', '2025-08-24 11:40:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (87, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:40:42', NULL, 'system', '2025-08-24 11:40:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (88, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:40:42', NULL, 'system', '2025-08-24 11:40:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (89, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 11:43:48', NULL, 'system', '2025-08-24 11:43:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (90, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:44:48', NULL, 'system', '2025-08-24 11:44:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (91, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:44:48', NULL, 'system', '2025-08-24 11:44:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (92, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 11:45:19', NULL, 'system', '2025-08-24 11:45:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (93, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 11:45:20', NULL, 'system', '2025-08-24 11:45:20', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (94, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 11:45:22', NULL, 'system', '2025-08-24 11:45:22', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (95, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:45:48', NULL, 'system', '2025-08-24 11:45:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (96, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:45:48', NULL, 'system', '2025-08-24 11:45:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (97, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 11:48:53', NULL, 'system', '2025-08-24 11:48:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (98, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:49:53', NULL, 'system', '2025-08-24 11:49:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (99, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:49:53', NULL, 'system', '2025-08-24 11:49:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (100, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 11:50:20', NULL, 'system', '2025-08-24 11:50:20', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (101, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 11:50:20', NULL, 'system', '2025-08-24 11:50:20', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (102, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 11:50:22', NULL, 'system', '2025-08-24 11:50:22', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (103, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 11:50:53', NULL, 'system', '2025-08-24 11:50:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (104, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 11:50:53', NULL, 'system', '2025-08-24 11:50:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (105, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 12:01:27', NULL, 'system', '2025-08-24 12:01:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (106, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 12:02:27', NULL, 'system', '2025-08-24 12:02:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (107, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 12:02:27', NULL, 'system', '2025-08-24 12:02:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (108, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 12:06:24', NULL, 'system', '2025-08-24 12:06:24', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (109, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 12:06:25', NULL, 'system', '2025-08-24 12:06:25', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (110, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 12:06:27', NULL, 'system', '2025-08-24 12:06:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (111, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 12:06:27', NULL, 'system', '2025-08-24 12:06:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (112, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 12:06:27', NULL, 'system', '2025-08-24 12:06:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (113, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 12:16:51', NULL, 'system', '2025-08-24 12:16:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (114, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 12:17:51', NULL, 'system', '2025-08-24 12:17:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (115, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 12:17:51', NULL, 'system', '2025-08-24 12:17:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (116, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 12:18:21', NULL, 'system', '2025-08-24 12:18:21', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (117, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 12:18:21', NULL, 'system', '2025-08-24 12:18:21', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (118, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 12:18:23', NULL, 'system', '2025-08-24 12:18:23', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (119, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 12:18:51', NULL, 'system', '2025-08-24 12:18:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (120, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 12:18:51', NULL, 'system', '2025-08-24 12:18:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (121, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 12:19:59', NULL, 'system', '2025-08-24 12:19:59', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (122, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 12:21:57', NULL, 'system', '2025-08-24 12:21:57', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (123, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 12:21:58', NULL, 'system', '2025-08-24 12:21:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (124, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 12:21:59', NULL, 'system', '2025-08-24 12:21:59', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (125, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 12:21:59', NULL, 'system', '2025-08-24 12:21:59', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (126, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 12:22:00', NULL, 'system', '2025-08-24 12:22:00', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (127, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:05:19', NULL, 'system', '2025-08-24 17:05:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (128, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:05:19', NULL, 'system', '2025-08-24 17:05:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (129, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:05:19', NULL, 'system', '2025-08-24 17:05:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (130, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:06:43', NULL, 'system', '2025-08-24 17:06:43', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (131, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:07:18', NULL, 'system', '2025-08-24 17:07:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (132, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 17:08:18', NULL, 'system', '2025-08-24 17:08:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (133, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:08:18', NULL, 'system', '2025-08-24 17:08:18', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (134, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:08:19', NULL, 'system', '2025-08-24 17:08:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (135, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:08:19', NULL, 'system', '2025-08-24 17:08:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (136, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 17:08:21', NULL, 'system', '2025-08-24 17:08:21', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (137, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:09:19', NULL, 'system', '2025-08-24 17:09:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (138, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:09:19', NULL, 'system', '2025-08-24 17:09:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (139, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:13:50', NULL, 'system', '2025-08-24 17:13:50', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (140, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:13:50', NULL, 'system', '2025-08-24 17:13:50', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (141, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:13:50', NULL, 'system', '2025-08-24 17:13:50', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (142, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 17:14:40', NULL, 'system', '2025-08-24 17:14:40', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (143, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:14:41', NULL, 'system', '2025-08-24 17:14:41', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (144, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 17:14:43', NULL, 'system', '2025-08-24 17:14:43', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (145, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:14:50', NULL, 'system', '2025-08-24 17:14:50', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (146, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:14:50', NULL, 'system', '2025-08-24 17:14:50', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (147, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:21:12', NULL, 'system', '2025-08-24 17:21:12', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (148, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:21:12', NULL, 'system', '2025-08-24 17:21:12', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (149, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:21:12', NULL, 'system', '2025-08-24 17:21:12', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (150, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:21:56', NULL, 'system', '2025-08-24 17:21:56', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (151, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:21:56', NULL, 'system', '2025-08-24 17:21:56', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (152, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:21:56', NULL, 'system', '2025-08-24 17:21:56', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (153, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:24:22', NULL, 'system', '2025-08-24 17:24:22', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (154, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:24:43', NULL, 'system', '2025-08-24 17:24:43', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (155, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 17:30:20', NULL, 'system', '2025-08-24 17:30:20', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (156, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:30:29', NULL, 'system', '2025-08-24 17:30:29', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (157, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 17:30:31', NULL, 'system', '2025-08-24 17:30:31', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (158, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:31:35', NULL, 'system', '2025-08-24 17:31:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (159, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:31:35', NULL, 'system', '2025-08-24 17:31:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (160, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:31:35', NULL, 'system', '2025-08-24 17:31:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (161, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 17:39:39', NULL, 'system', '2025-08-24 17:39:39', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (162, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:39:40', NULL, 'system', '2025-08-24 17:39:40', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (163, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 17:39:42', NULL, 'system', '2025-08-24 17:39:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (164, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:41:25', NULL, 'system', '2025-08-24 17:41:25', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (165, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:41:25', NULL, 'system', '2025-08-24 17:41:25', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (166, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:41:25', NULL, 'system', '2025-08-24 17:41:25', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (167, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 17:56:08', NULL, 'system', '2025-08-24 17:56:08', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (168, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 17:56:09', NULL, 'system', '2025-08-24 17:56:09', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (169, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 17:56:11', NULL, 'system', '2025-08-24 17:56:11', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (170, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 17:58:04', NULL, 'system', '2025-08-24 17:58:04', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (171, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 17:58:04', NULL, 'system', '2025-08-24 17:58:04', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (172, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 17:58:04', NULL, 'system', '2025-08-24 17:58:04', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (173, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 18:10:27', NULL, 'system', '2025-08-24 18:10:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (174, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 18:10:27', NULL, 'system', '2025-08-24 18:10:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (175, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 18:10:27', NULL, 'system', '2025-08-24 18:10:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (176, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 18:15:46', NULL, 'system', '2025-08-24 18:15:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (177, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 18:15:46', NULL, 'system', '2025-08-24 18:15:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (178, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 18:15:46', NULL, 'system', '2025-08-24 18:15:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (179, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 20:36:55', NULL, 'system', '2025-08-24 20:36:55', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (180, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 20:36:56', NULL, 'system', '2025-08-24 20:36:56', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (181, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 20:36:58', NULL, 'system', '2025-08-24 20:36:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (182, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 20:37:15', NULL, 'system', '2025-08-24 20:37:15', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (183, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 20:37:16', NULL, 'system', '2025-08-24 20:37:16', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (184, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 20:37:16', NULL, 'system', '2025-08-24 20:37:16', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (185, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 21:16:56', NULL, 'system', '2025-08-24 21:16:56', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (186, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 21:16:56', NULL, 'system', '2025-08-24 21:16:56', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (187, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 21:20:52', NULL, 'system', '2025-08-24 21:20:52', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (188, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 21:20:53', NULL, 'system', '2025-08-24 21:20:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (189, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 21:20:53', NULL, 'system', '2025-08-24 21:20:53', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (190, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 21:29:29', NULL, 'system', '2025-08-24 21:29:29', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (191, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 21:29:30', NULL, 'system', '2025-08-24 21:29:30', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (192, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 21:29:32', NULL, 'system', '2025-08-24 21:29:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (193, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 21:44:46', NULL, 'system', '2025-08-24 21:44:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (194, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 21:44:47', NULL, 'system', '2025-08-24 21:44:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (195, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 21:44:47', NULL, 'system', '2025-08-24 21:44:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (196, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 21:55:02', NULL, 'system', '2025-08-24 21:55:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (197, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 21:55:02', NULL, 'system', '2025-08-24 21:55:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (198, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 21:55:02', NULL, 'system', '2025-08-24 21:55:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (199, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 22:10:22', NULL, 'system', '2025-08-24 22:10:22', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (200, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 22:10:22', NULL, 'system', '2025-08-24 22:10:22', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (201, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 22:10:22', NULL, 'system', '2025-08-24 22:10:22', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (202, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 22:14:42', NULL, 'system', '2025-08-24 22:14:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (203, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 22:14:42', NULL, 'system', '2025-08-24 22:14:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (204, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 22:14:42', NULL, 'system', '2025-08-24 22:14:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (205, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 22:44:16', NULL, 'system', '2025-08-24 22:44:16', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (206, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 22:44:17', NULL, 'system', '2025-08-24 22:44:17', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (207, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 22:44:17', NULL, 'system', '2025-08-24 22:44:17', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (208, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 23:31:46', NULL, 'system', '2025-08-24 23:31:46', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (209, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 23:31:47', NULL, 'system', '2025-08-24 23:31:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (210, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 23:31:49', NULL, 'system', '2025-08-24 23:31:49', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (211, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 23:43:51', NULL, 'system', '2025-08-24 23:43:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (212, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 23:43:51', NULL, 'system', '2025-08-24 23:43:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (213, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 23:43:51', NULL, 'system', '2025-08-24 23:43:51', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (214, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 23:44:01', NULL, 'system', '2025-08-24 23:44:01', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (215, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 23:44:02', NULL, 'system', '2025-08-24 23:44:02', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (216, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 23:44:04', NULL, 'system', '2025-08-24 23:44:04', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (217, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-24 23:51:32', NULL, 'system', '2025-08-24 23:51:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (218, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-24 23:51:32', NULL, 'system', '2025-08-24 23:51:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (219, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-24 23:51:32', NULL, 'system', '2025-08-24 23:51:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (220, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-24 23:58:24', NULL, 'system', '2025-08-24 23:58:24', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (221, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-24 23:58:25', NULL, 'system', '2025-08-24 23:58:25', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (222, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-24 23:58:27', NULL, 'system', '2025-08-24 23:58:27', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (223, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 00:01:47', NULL, 'system', '2025-08-25 00:01:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (224, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 00:01:47', NULL, 'system', '2025-08-25 00:01:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (225, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 00:01:47', NULL, 'system', '2025-08-25 00:01:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (226, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 00:02:07', NULL, 'system', '2025-08-25 00:02:07', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (227, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 00:02:07', NULL, 'system', '2025-08-25 00:02:07', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (228, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 00:02:07', NULL, 'system', '2025-08-25 00:02:07', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (229, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 00:09:44', NULL, 'system', '2025-08-25 00:09:44', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (230, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 00:09:44', NULL, 'system', '2025-08-25 00:09:44', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (231, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 00:09:44', NULL, 'system', '2025-08-25 00:09:44', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (232, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 00:17:48', NULL, 'system', '2025-08-25 00:17:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (233, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 00:17:48', NULL, 'system', '2025-08-25 00:17:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (234, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 00:17:48', NULL, 'system', '2025-08-25 00:17:48', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (235, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-25 00:23:29', NULL, 'system', '2025-08-25 00:23:29', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (236, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-25 00:23:30', NULL, 'system', '2025-08-25 00:23:30', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (237, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-25 00:23:32', NULL, 'system', '2025-08-25 00:23:32', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (238, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 00:25:58', NULL, 'system', '2025-08-25 00:25:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (239, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 00:25:58', NULL, 'system', '2025-08-25 00:25:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (240, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 00:25:58', NULL, 'system', '2025-08-25 00:25:58', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (241, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 00:38:45', NULL, 'system', '2025-08-25 00:38:45', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (242, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 00:38:45', NULL, 'system', '2025-08-25 00:38:45', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (243, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 00:38:45', NULL, 'system', '2025-08-25 00:38:45', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (244, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 12:24:47', NULL, 'system', '2025-08-25 12:24:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (245, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 12:24:47', NULL, 'system', '2025-08-25 12:24:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (246, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 12:24:47', NULL, 'system', '2025-08-25 12:24:47', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (247, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 12:46:19', NULL, 'system', '2025-08-25 12:46:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (248, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 12:46:19', NULL, 'system', '2025-08-25 12:46:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (249, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 12:46:19', NULL, 'system', '2025-08-25 12:46:19', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (250, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-25 12:58:36', NULL, 'system', '2025-08-25 12:58:36', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (251, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-25 12:58:42', NULL, 'system', '2025-08-25 12:58:42', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (252, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-25 12:58:44', NULL, 'system', '2025-08-25 12:58:44', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (253, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 12:59:05', NULL, 'system', '2025-08-25 12:59:05', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (254, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 12:59:05', NULL, 'system', '2025-08-25 12:59:05', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (255, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 12:59:05', NULL, 'system', '2025-08-25 12:59:05', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (256, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 13:44:39', NULL, 'system', '2025-08-25 13:44:39', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (257, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 13:44:39', NULL, 'system', '2025-08-25 13:44:39', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (258, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 13:44:39', NULL, 'system', '2025-08-25 13:44:39', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (259, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-25 13:50:12', NULL, 'system', '2025-08-25 13:50:12', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (260, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 13:50:33', NULL, 'system', '2025-08-25 13:50:33', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (261, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 13:50:33', NULL, 'system', '2025-08-25 13:50:33', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (262, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 13:50:33', NULL, 'system', '2025-08-25 13:50:33', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (263, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 13:59:35', NULL, 'system', '2025-08-25 13:59:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (264, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 13:59:35', NULL, 'system', '2025-08-25 13:59:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (265, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 13:59:35', NULL, 'system', '2025-08-25 13:59:35', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (266, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控停止成功', '2025-08-25 14:18:04', NULL, 'system', '2025-08-25 14:18:04', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (267, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控停止成功', '2025-08-25 14:18:05', NULL, 'system', '2025-08-25 14:18:05', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (268, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控停止成功', '2025-08-25 14:18:07', NULL, 'system', '2025-08-25 14:18:07', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (269, 6, NULL, 'ACCOUNT', 'SUCCESS', '账户 182867664@qq.com 监控启动成功', '2025-08-25 14:26:24', NULL, 'system', '2025-08-25 14:26:24', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (270, 1, NULL, 'ACCOUNT', 'SUCCESS', '账户 trouble.linky@gmail.com 监控启动成功', '2025-08-25 14:26:25', NULL, 'system', '2025-08-25 14:26:25', '', NULL, NULL);
INSERT INTO `email_service_monitor_log` VALUES (271, NULL, NULL, 'GLOBAL', 'SUCCESS', '全局监控启动成功', '2025-08-25 14:26:25', NULL, 'system', '2025-08-25 14:26:25', '', NULL, NULL);

-- ----------------------------
-- Table structure for email_service_status_enum
-- ----------------------------
DROP TABLE IF EXISTS `email_service_status_enum`;
CREATE TABLE `email_service_status_enum`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `status_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态代码',
  `status_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '状态名称',
  `status_desc` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '状态描述',
  `css_class` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'CSS样式类',
  `is_error` tinyint(1) NULL DEFAULT 0 COMMENT '是否为错误状态：0-否，1-是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_status_code`(`status_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件服务状态枚举表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_service_status_enum
-- ----------------------------
INSERT INTO `email_service_status_enum` VALUES (1, '0', 'STOPPED', '服务停止状态', 'info', 0, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (2, '1', 'RUNNING', '服务正常运行状态', 'success', 0, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (3, '2', 'CONNECTING', '正在建立网络连接', 'warning', 0, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (4, '3', 'CONNECTED', '网络连接已建立', 'primary', 0, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (5, '4', 'TIMEOUT', '网络连接超时', 'danger', 1, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (6, '5', 'AUTH_FAILED', '用户名或密码认证失败', 'danger', 1, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (7, '6', 'SSL_ERROR', 'SSL/TLS连接错误', 'danger', 1, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (8, '7', 'PORT_ERROR', '端口配置错误或端口被占用', 'danger', 1, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (9, '8', 'HOST_UNREACHABLE', '邮件服务器主机不可达', 'danger', 1, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (10, '9', 'FIREWALL_BLOCKED', '防火墙阻止连接', 'danger', 1, '2025-08-24 09:24:39');
INSERT INTO `email_service_status_enum` VALUES (11, '10', 'SERVICE_ERROR', '其他服务异常', 'danger', 1, '2025-08-24 09:24:39');

-- ----------------------------
-- Table structure for email_statistics
-- ----------------------------
DROP TABLE IF EXISTS `email_statistics`;
CREATE TABLE `email_statistics`  (
  `stat_id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '统计类型(1日统计 2月统计 3年统计)',
  `total_sent` int NULL DEFAULT 0 COMMENT '总发送数',
  `total_delivered` int NULL DEFAULT 0 COMMENT '总送达数',
  `total_opened` int NULL DEFAULT 0 COMMENT '总打开数',
  `total_replied` int NULL DEFAULT 0 COMMENT '总回复数',
  `delivery_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '送达率',
  `open_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '打开率',
  `reply_rate` decimal(5, 2) NULL DEFAULT 0.00 COMMENT '回复率',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮件Message-ID',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮件状态',
  `delivered_time` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime NULL DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `clicked_time` datetime NULL DEFAULT NULL COMMENT '点击时间',
  PRIMARY KEY (`stat_id`) USING BTREE,
  UNIQUE INDEX `uk_date_type`(`stat_date` ASC, `stat_type` ASC) USING BTREE,
  INDEX `idx_stat_date`(`stat_date` ASC) USING BTREE,
  INDEX `idx_stat_type`(`stat_type` ASC) USING BTREE,
  INDEX `idx_email_statistics_message_id`(`message_id` ASC) USING BTREE,
  INDEX `idx_email_statistics_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 46 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件统计表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_statistics
-- ----------------------------
INSERT INTO `email_statistics` VALUES (1, '2018-11-07', '1', 1, 1, 0, 0, 100.00, 0.00, 0.00, '2025-08-21 23:27:02', NULL, '<88.97.02998.B2DC1EB5@twitter.com>', 'received', '2018-11-07 01:19:39', NULL, NULL, NULL);
INSERT INTO `email_statistics` VALUES (2, '2025-08-20', '1', 1, 1, 0, 0, 100.00, 0.00, 0.00, '2025-08-21 23:36:50', NULL, '<1689941518.0.1755653400926@localhost>', 'received', '2025-08-20 09:30:00', NULL, NULL, NULL);
INSERT INTO `email_statistics` VALUES (35, '2025-08-21', '1', 1, 1, 0, 0, 100.00, 0.00, 0.00, '2025-08-21 23:37:21', NULL, '<4757988b-334e-4d00-bda4-9ce9f1be77c2@teamviewer.com>', 'received', '2025-08-21 06:58:43', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for email_task_execution
-- ----------------------------
DROP TABLE IF EXISTS `email_task_execution`;
CREATE TABLE `email_task_execution`  (
  `execution_id` bigint NOT NULL AUTO_INCREMENT COMMENT '执行ID',
  `task_id` bigint NOT NULL COMMENT '任务ID',
  `execution_status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '0' COMMENT '执行状态(0未开始 1执行中 2已完成 3执行失败 4执行中断)',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `execution_user` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行人',
  `execution_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '执行IP',
  `total_count` int NULL DEFAULT 0 COMMENT '总发送数量',
  `sent_count` int NULL DEFAULT 0 COMMENT '已发送数量',
  `success_count` int NULL DEFAULT 0 COMMENT '成功数量',
  `failed_count` int NULL DEFAULT 0 COMMENT '失败数量',
  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误信息',
  `execution_log` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '执行日志',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`execution_id`) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_execution_status`(`execution_status` ASC) USING BTREE,
  INDEX `idx_start_time`(`start_time` ASC) USING BTREE,
  INDEX `idx_execution_user`(`execution_user` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件任务执行表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_task_execution
-- ----------------------------

-- ----------------------------
-- Table structure for email_template
-- ----------------------------
DROP TABLE IF EXISTS `email_template`;
CREATE TABLE `email_template`  (
  `template_id` bigint NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '模板名称',
  `template_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '1' COMMENT '模板类型(1普通 2营销 3通知)',
  `subject` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件主题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件内容',
  `variables` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '变量列表(JSON格式)',
  `use_count` int NULL DEFAULT 0 COMMENT '使用次数',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`template_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件模板表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_template
-- ----------------------------
INSERT INTO `email_template` VALUES (1, '产品推广邮件模板', '2', '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '[\"name\",\"company\"]', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_template` VALUES (2, '会议邀请模板', '3', '邀请您参加重要会议', '<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>', '[\"name\",\"meeting_time\",\"meeting_location\"]', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');
INSERT INTO `email_template` VALUES (3, '月度报表通知', '3', '月度报表已生成', '<p>尊敬的{{name}}：</p><p>您的月度报表已经生成，请查收...</p>', '[\"name\",\"report_month\"]', 0, '0', NULL, 'admin', '2025-08-15 21:49:28', '', NULL, '0');

-- ----------------------------
-- Table structure for email_track_record
-- ----------------------------
DROP TABLE IF EXISTS `email_track_record`;
CREATE TABLE `email_track_record`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_id` bigint NULL DEFAULT NULL COMMENT '任务ID',
  `message_id` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '邮件Message-ID',
  `subject` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮件主题',
  `recipient` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收件人',
  `sender` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '发件人',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '邮件内容',
  `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PENDING' COMMENT '邮件状态',
  `sent_time` datetime NULL DEFAULT NULL COMMENT '发送时间',
  `delivered_time` datetime NULL DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime NULL DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime NULL DEFAULT NULL COMMENT '回复时间',
  `clicked_time` datetime NULL DEFAULT NULL COMMENT '点击时间',
  `retry_count` int NULL DEFAULT 0 COMMENT '重试次数',
  `error_logs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '错误日志',
  `account_id` bigint NULL DEFAULT NULL COMMENT '邮箱账号ID',
  `tracking_pixel_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跟踪像素URL',
  `tracking_link_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '跟踪链接URL',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_message_id`(`message_id` ASC) USING BTREE,
  INDEX `idx_task_id`(`task_id` ASC) USING BTREE,
  INDEX `idx_account_id`(`account_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_sent_time`(`sent_time` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE,
  INDEX `idx_deleted`(`deleted` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '邮件跟踪记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_track_record
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table`  (
  `table_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column`  (
  `column_id` bigint NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint NULL DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '代码生成业务表字段' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob NULL COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Blob类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`, `calendar_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '日历信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'Cron类型的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint NOT NULL COMMENT '触发的时间',
  `sched_time` bigint NOT NULL COMMENT '定时器制定的时间',
  `priority` int NOT NULL COMMENT '优先级',
  `state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`, `entry_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '已触发的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '任务详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`, `lock_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '存储的悲观锁信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`, `trigger_group`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '暂停的触发器表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`, `instance_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '调度器状态表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '简单触发器的信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int NULL DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint NULL DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13, 4) NULL DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '同步机制的行锁表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers`  (
  `sched_name` varchar(120) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint NULL DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint NULL DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int NULL DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint NOT NULL COMMENT '开始时间',
  `end_time` bigint NULL DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint NULL DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob NULL COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`, `trigger_name`, `trigger_group`) USING BTREE,
  INDEX `sched_name`(`sched_name` ASC, `job_name` ASC, `job_group` ASC) USING BTREE,
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '触发器详细信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `config_id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 105 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '参数配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES (1, '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES (2, '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '初始化密码 123456');
INSERT INTO `sys_config` VALUES (3, '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES (4, '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (5, '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES (6, '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES (7, '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (8, '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-08-03 19:54:27', '', NULL, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES (100, '邮件IMAP同步最大邮件数量', 'email.imap.sync.max.messages', '2000', 'Y', 'admin', '2025-08-21 23:23:12', 'admin', '2025-08-21 23:23:12', '邮件IMAP同步的最大邮件数量，默认为1000封');
INSERT INTO `sys_config` VALUES (101, 'IMAP重试延迟时间', 'email.imap.retry.delay', '5000', 'N', 'admin', '2025-08-24 22:39:25', '', NULL, 'IMAP连接失败时的重试延迟时间，单位毫秒，范围5-30秒');
INSERT INTO `sys_config` VALUES (102, 'IMAP心跳检测间隔', 'email.imap.heartbeat.interval', '10000', 'N', 'admin', '2025-08-24 22:39:25', '', NULL, 'IMAP连接心跳检测间隔，单位毫秒，范围5-30秒');
INSERT INTO `sys_config` VALUES (103, 'SMTP重试延迟时间', 'email.smtp.retry.delay', '5000', 'N', 'admin', '2025-08-24 22:39:25', '', NULL, 'SMTP连接失败时的重试延迟时间，单位毫秒，范围5-30秒');
INSERT INTO `sys_config` VALUES (104, 'SMTP心跳检测间隔', 'email.smtp.heartbeat.interval', '10000', 'N', 'admin', '2025-08-24 22:39:25', '', NULL, 'SMTP连接心跳检测间隔，单位毫秒，范围5-30秒');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `dept_id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父部门id',
  `ancestors` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 200 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES (100, 0, '0', '若依科技', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (101, 100, '0,100', '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (105, 101, '0,100,101', '测试部门', 3, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (108, 102, '0,100,102', '市场部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);
INSERT INTO `sys_dept` VALUES (109, 102, '0,100,102', '财务部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `dict_code` bigint NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int NULL DEFAULT 0 COMMENT '字典排序',
  `dict_label` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 134 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES (1, 1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2, 2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3, 3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4, 1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '显示菜单');
INSERT INTO `sys_dict_data` VALUES (5, 2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES (6, 1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (7, 2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (8, 1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (9, 2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (10, 1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '默认分组');
INSERT INTO `sys_dict_data` VALUES (11, 2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '系统分组');
INSERT INTO `sys_dict_data` VALUES (12, 1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '系统默认是');
INSERT INTO `sys_dict_data` VALUES (13, 2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '系统默认否');
INSERT INTO `sys_dict_data` VALUES (14, 1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '通知');
INSERT INTO `sys_dict_data` VALUES (15, 2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '公告');
INSERT INTO `sys_dict_data` VALUES (16, 1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (17, 2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '关闭状态');
INSERT INTO `sys_dict_data` VALUES (18, 99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '其他操作');
INSERT INTO `sys_dict_data` VALUES (19, 1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '新增操作');
INSERT INTO `sys_dict_data` VALUES (20, 2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '修改操作');
INSERT INTO `sys_dict_data` VALUES (21, 3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '删除操作');
INSERT INTO `sys_dict_data` VALUES (22, 4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '授权操作');
INSERT INTO `sys_dict_data` VALUES (23, 5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '导出操作');
INSERT INTO `sys_dict_data` VALUES (24, 6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '导入操作');
INSERT INTO `sys_dict_data` VALUES (25, 7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '强退操作');
INSERT INTO `sys_dict_data` VALUES (26, 8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '生成操作');
INSERT INTO `sys_dict_data` VALUES (27, 9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '清空操作');
INSERT INTO `sys_dict_data` VALUES (28, 1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '正常状态');
INSERT INTO `sys_dict_data` VALUES (29, 2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '停用状态');
INSERT INTO `sys_dict_data` VALUES (103, 1, '停止', '0', 'monitor_status', '', 'info', 'Y', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '监控停止状态');
INSERT INTO `sys_dict_data` VALUES (104, 2, '运行中', '1', 'monitor_status', '', 'success', 'N', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '监控正常运行状态');
INSERT INTO `sys_dict_data` VALUES (105, 1, 'IMAP', 'IMAP', 'service_type', '', 'primary', 'Y', '0', 'admin', '2025-08-24 07:59:05', '', NULL, 'IMAP服务');
INSERT INTO `sys_dict_data` VALUES (106, 2, 'SMTP', 'SMTP', 'service_type', '', 'success', 'N', '0', 'admin', '2025-08-24 07:59:05', '', NULL, 'SMTP服务');
INSERT INTO `sys_dict_data` VALUES (107, 1, '启动', 'START', 'operation_type', '', 'success', 'Y', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '启动操作');
INSERT INTO `sys_dict_data` VALUES (108, 2, '停止', 'STOP', 'operation_type', '', 'danger', 'N', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '停止操作');
INSERT INTO `sys_dict_data` VALUES (109, 3, '重启', 'RESTART', 'operation_type', '', 'warning', 'N', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '重启操作');
INSERT INTO `sys_dict_data` VALUES (110, 4, '测试', 'TEST', 'operation_type', '', 'primary', 'N', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '测试操作');
INSERT INTO `sys_dict_data` VALUES (111, 5, '监控', 'MONITOR', 'operation_type', '', 'info', 'N', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '监控操作');
INSERT INTO `sys_dict_data` VALUES (123, 1, '停止', '0', 'email_service_status', '', 'info', 'Y', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '服务停止状态');
INSERT INTO `sys_dict_data` VALUES (124, 2, '运行中', '1', 'email_service_status', '', 'success', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '服务正常运行状态');
INSERT INTO `sys_dict_data` VALUES (125, 3, '连接中', '2', 'email_service_status', '', 'warning', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '正在建立网络连接');
INSERT INTO `sys_dict_data` VALUES (126, 4, '已连接', '3', 'email_service_status', '', 'primary', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '网络连接已建立');
INSERT INTO `sys_dict_data` VALUES (127, 5, '网络超时', '4', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '网络连接超时');
INSERT INTO `sys_dict_data` VALUES (128, 6, '认证失败', '5', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '用户名或密码认证失败');
INSERT INTO `sys_dict_data` VALUES (129, 7, 'SSL错误', '6', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, 'SSL/TLS连接错误');
INSERT INTO `sys_dict_data` VALUES (130, 8, '端口错误', '7', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '端口配置错误或端口被占用');
INSERT INTO `sys_dict_data` VALUES (131, 9, '主机不可达', '8', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '邮件服务器主机不可达');
INSERT INTO `sys_dict_data` VALUES (132, 10, '防火墙阻止', '9', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '防火墙阻止连接');
INSERT INTO `sys_dict_data` VALUES (133, 11, '服务异常', '10', 'email_service_status', '', 'danger', 'N', '0', 'admin', '2025-08-24 09:51:34', '', NULL, '其他服务异常');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `dict_id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '字典类型',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`) USING BTREE,
  UNIQUE INDEX `dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 104 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES (1, '用户性别', 'sys_user_sex', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2, '菜单状态', 'sys_show_hide', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3, '系统开关', 'sys_normal_disable', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (4, '任务状态', 'sys_job_status', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES (5, '任务分组', 'sys_job_group', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES (6, '系统是否', 'sys_yes_no', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES (7, '通知类型', 'sys_notice_type', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES (8, '通知状态', 'sys_notice_status', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES (9, '操作类型', 'sys_oper_type', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES (10, '系统状态', 'sys_common_status', '0', 'admin', '2025-08-03 19:54:27', '', NULL, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES (100, '邮件服务状态', 'email_service_status', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '邮件服务状态字典');
INSERT INTO `sys_dict_type` VALUES (101, '监控状态', 'monitor_status', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '监控状态字典');
INSERT INTO `sys_dict_type` VALUES (102, '服务类型', 'service_type', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '服务类型字典');
INSERT INTO `sys_dict_type` VALUES (103, '操作类型', 'operation_type', '0', 'admin', '2025-08-24 07:59:05', '', NULL, '操作类型字典');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job`  (
  `job_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`, `job_name`, `job_group`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1006 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES (1, '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2025-08-03 19:54:27', '', NULL, '');
INSERT INTO `sys_job` VALUES (2, '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2025-08-03 19:54:27', '', NULL, '');
INSERT INTO `sys_job` VALUES (3, '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2025-08-03 19:54:27', '', NULL, '');
INSERT INTO `sys_job` VALUES (100, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', '0 * * * * ?', '1', '1', '0', 'admin', '2025-08-21 13:38:50', 'admin', '2025-08-21 13:38:50', '邮件发送任务检查定时任务 - 每分钟检查一次即将启动的发送任务');
INSERT INTO `sys_job` VALUES (101, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', '0 */5 * * * ?', '1', '1', '0', 'admin', '2025-08-21 13:38:50', 'admin', '2025-08-21 13:38:50', '邮件统计数据同步定时任务 - 每5分钟同步一次邮件统计数据');
INSERT INTO `sys_job` VALUES (1001, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '0 0 * * * ?', '1', '1', '0', 'admin', '2025-08-21 14:25:47', 'admin', '2025-08-21 14:25:47', '每小时同步一次邮件统计数据，通过IMAP协议获取送达、打开、回复等统计信息');
INSERT INTO `sys_job` VALUES (1002, '每日邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateDailyStatistics', '0 0 2 * * ?', '1', '1', '0', 'admin', '2025-08-21 14:25:47', 'admin', '2025-08-21 14:25:47', '每天凌晨2点生成前一天的邮件统计数据');
INSERT INTO `sys_job` VALUES (1003, '月度邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateMonthlyStatistics', '0 0 3 1 * ?', '1', '1', '0', 'admin', '2025-08-21 14:25:47', 'admin', '2025-08-21 14:25:47', '每月1号凌晨3点生成上个月的邮件统计数据');
INSERT INTO `sys_job` VALUES (1004, '年度邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateYearlyStatistics', '0 0 4 1 1 ?', '1', '1', '0', 'admin', '2025-08-21 14:25:47', 'admin', '2025-08-21 14:25:47', '每年1月1号凌晨4点生成上一年的邮件统计数据');
INSERT INTO `sys_job` VALUES (1005, '过期邮件统计清理', 'DEFAULT', 'emailStatisticsTask.cleanExpiredStatistics', '0 0 1 ? * SUN', '1', '1', '0', 'admin', '2025-08-21 14:25:47', 'admin', '2025-08-21 14:25:47', '每周日凌晨1点清理过期的邮件统计数据');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log`  (
  `job_log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '日志信息',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '异常信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1593 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '定时任务调度日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------
INSERT INTO `sys_job_log` VALUES (1, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：6毫秒', '0', '', '2025-08-21 16:40:00');
INSERT INTO `sys_job_log` VALUES (2, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：131毫秒', '0', '', '2025-08-21 16:40:00');
INSERT INTO `sys_job_log` VALUES (3, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:41:00');
INSERT INTO `sys_job_log` VALUES (4, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-21 16:42:00');
INSERT INTO `sys_job_log` VALUES (5, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-21 16:43:00');
INSERT INTO `sys_job_log` VALUES (6, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：99毫秒', '0', '', '2025-08-21 16:44:00');
INSERT INTO `sys_job_log` VALUES (7, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 16:45:00');
INSERT INTO `sys_job_log` VALUES (8, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-21 16:45:00');
INSERT INTO `sys_job_log` VALUES (9, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:46:00');
INSERT INTO `sys_job_log` VALUES (10, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：66毫秒', '0', '', '2025-08-21 16:47:00');
INSERT INTO `sys_job_log` VALUES (11, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：11毫秒', '0', '', '2025-08-21 16:48:00');
INSERT INTO `sys_job_log` VALUES (12, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 16:50:37');
INSERT INTO `sys_job_log` VALUES (13, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:50:37');
INSERT INTO `sys_job_log` VALUES (14, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 16:50:37');
INSERT INTO `sys_job_log` VALUES (15, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9毫秒', '0', '', '2025-08-21 16:52:00');
INSERT INTO `sys_job_log` VALUES (16, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-21 16:53:00');
INSERT INTO `sys_job_log` VALUES (17, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:54:00');
INSERT INTO `sys_job_log` VALUES (18, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-21 16:55:00');
INSERT INTO `sys_job_log` VALUES (19, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:55:00');
INSERT INTO `sys_job_log` VALUES (20, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-21 16:56:00');
INSERT INTO `sys_job_log` VALUES (21, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:57:00');
INSERT INTO `sys_job_log` VALUES (22, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 16:58:00');
INSERT INTO `sys_job_log` VALUES (23, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-21 16:59:00');
INSERT INTO `sys_job_log` VALUES (24, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 17:00:00');
INSERT INTO `sys_job_log` VALUES (25, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 17:00:00');
INSERT INTO `sys_job_log` VALUES (26, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：6048毫秒', '0', '', '2025-08-21 17:00:06');
INSERT INTO `sys_job_log` VALUES (27, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 17:01:00');
INSERT INTO `sys_job_log` VALUES (28, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 17:02:00');
INSERT INTO `sys_job_log` VALUES (29, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 17:03:00');
INSERT INTO `sys_job_log` VALUES (30, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:04:00');
INSERT INTO `sys_job_log` VALUES (31, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 17:05:00');
INSERT INTO `sys_job_log` VALUES (32, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:05:00');
INSERT INTO `sys_job_log` VALUES (33, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:06:00');
INSERT INTO `sys_job_log` VALUES (34, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 17:07:00');
INSERT INTO `sys_job_log` VALUES (35, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:08:00');
INSERT INTO `sys_job_log` VALUES (36, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:09:00');
INSERT INTO `sys_job_log` VALUES (37, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-21 17:10:00');
INSERT INTO `sys_job_log` VALUES (38, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:10:00');
INSERT INTO `sys_job_log` VALUES (39, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:11:00');
INSERT INTO `sys_job_log` VALUES (40, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:12:00');
INSERT INTO `sys_job_log` VALUES (41, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:13:00');
INSERT INTO `sys_job_log` VALUES (42, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:14:00');
INSERT INTO `sys_job_log` VALUES (43, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 17:15:00');
INSERT INTO `sys_job_log` VALUES (44, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:15:00');
INSERT INTO `sys_job_log` VALUES (45, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 17:16:00');
INSERT INTO `sys_job_log` VALUES (46, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:17:00');
INSERT INTO `sys_job_log` VALUES (47, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:18:00');
INSERT INTO `sys_job_log` VALUES (48, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 17:19:00');
INSERT INTO `sys_job_log` VALUES (49, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-21 17:20:00');
INSERT INTO `sys_job_log` VALUES (50, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 17:20:00');
INSERT INTO `sys_job_log` VALUES (51, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：28毫秒', '0', '', '2025-08-21 17:47:00');
INSERT INTO `sys_job_log` VALUES (52, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 17:48:00');
INSERT INTO `sys_job_log` VALUES (53, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 17:49:00');
INSERT INTO `sys_job_log` VALUES (54, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：31毫秒', '0', '', '2025-08-21 18:31:06');
INSERT INTO `sys_job_log` VALUES (55, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-21 18:32:00');
INSERT INTO `sys_job_log` VALUES (56, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 18:33:00');
INSERT INTO `sys_job_log` VALUES (57, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 18:34:00');
INSERT INTO `sys_job_log` VALUES (58, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 18:35:00');
INSERT INTO `sys_job_log` VALUES (59, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-21 18:35:00');
INSERT INTO `sys_job_log` VALUES (60, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 18:36:00');
INSERT INTO `sys_job_log` VALUES (61, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 18:37:00');
INSERT INTO `sys_job_log` VALUES (62, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：93毫秒', '0', '', '2025-08-21 22:59:00');
INSERT INTO `sys_job_log` VALUES (63, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：36毫秒', '0', '', '2025-08-21 23:02:00');
INSERT INTO `sys_job_log` VALUES (64, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：35毫秒', '0', '', '2025-08-21 23:03:00');
INSERT INTO `sys_job_log` VALUES (65, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 23:04:00');
INSERT INTO `sys_job_log` VALUES (66, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 23:05:00');
INSERT INTO `sys_job_log` VALUES (67, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 23:05:00');
INSERT INTO `sys_job_log` VALUES (68, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 23:06:00');
INSERT INTO `sys_job_log` VALUES (69, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 23:07:00');
INSERT INTO `sys_job_log` VALUES (70, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 23:08:00');
INSERT INTO `sys_job_log` VALUES (71, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 23:09:00');
INSERT INTO `sys_job_log` VALUES (72, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 23:10:00');
INSERT INTO `sys_job_log` VALUES (73, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 23:10:00');
INSERT INTO `sys_job_log` VALUES (74, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：32毫秒', '0', '', '2025-08-21 23:11:00');
INSERT INTO `sys_job_log` VALUES (75, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-21 23:12:00');
INSERT INTO `sys_job_log` VALUES (76, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：14毫秒', '0', '', '2025-08-21 23:13:00');
INSERT INTO `sys_job_log` VALUES (77, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：49毫秒', '0', '', '2025-08-21 23:14:00');
INSERT INTO `sys_job_log` VALUES (78, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：61毫秒', '0', '', '2025-08-21 23:16:16');
INSERT INTO `sys_job_log` VALUES (79, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-21 23:17:00');
INSERT INTO `sys_job_log` VALUES (80, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：50毫秒', '0', '', '2025-08-21 23:19:06');
INSERT INTO `sys_job_log` VALUES (81, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-21 23:24:18');
INSERT INTO `sys_job_log` VALUES (82, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：13毫秒', '0', '', '2025-08-21 23:24:18');
INSERT INTO `sys_job_log` VALUES (83, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-21 23:24:18');
INSERT INTO `sys_job_log` VALUES (84, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：50毫秒', '0', '', '2025-08-21 23:37:08');
INSERT INTO `sys_job_log` VALUES (85, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-21 23:37:08');
INSERT INTO `sys_job_log` VALUES (86, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：36毫秒', '0', '', '2025-08-21 23:38:00');
INSERT INTO `sys_job_log` VALUES (87, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-21 23:39:00');
INSERT INTO `sys_job_log` VALUES (88, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：71毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (89, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：42毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (90, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (91, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：10毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (92, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (93, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (94, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (95, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (96, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (97, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (98, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (99, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (100, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (101, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (102, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (103, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (104, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (105, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (106, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (107, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (108, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (109, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (110, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (111, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (112, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (113, '每日邮件统计生成', 'DEFAULT', 'emailStatisticsTask.generateDailyStatistics', '每日邮件统计生成 总共耗时：238毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (114, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：16毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (115, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (116, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (117, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (118, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (119, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (120, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (121, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (122, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (123, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (124, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (125, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (126, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (127, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (128, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (129, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (130, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (131, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (132, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (133, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (134, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (135, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (136, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (137, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (138, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (139, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (140, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (141, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (142, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (143, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (144, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (145, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (146, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (147, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (148, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (149, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (150, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (151, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (152, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (153, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (154, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (155, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (156, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (157, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (158, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (159, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (160, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (161, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (162, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (163, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (164, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (165, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (166, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (167, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (168, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (169, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (170, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (171, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (172, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (173, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (174, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (175, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (176, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (177, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (178, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (179, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (180, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (181, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (182, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (183, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (184, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (185, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (186, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (187, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (188, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (189, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (190, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (191, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (192, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:52');
INSERT INTO `sys_job_log` VALUES (193, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:46:56');
INSERT INTO `sys_job_log` VALUES (194, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：3697毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (195, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (196, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (197, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (198, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (199, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (200, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (201, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:46:57');
INSERT INTO `sys_job_log` VALUES (202, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:02');
INSERT INTO `sys_job_log` VALUES (203, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:04');
INSERT INTO `sys_job_log` VALUES (204, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2739毫秒', '0', '', '2025-08-22 08:47:04');
INSERT INTO `sys_job_log` VALUES (205, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3220毫秒', '0', '', '2025-08-22 08:47:08');
INSERT INTO `sys_job_log` VALUES (206, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:08');
INSERT INTO `sys_job_log` VALUES (207, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:08');
INSERT INTO `sys_job_log` VALUES (208, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:08');
INSERT INTO `sys_job_log` VALUES (209, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:10');
INSERT INTO `sys_job_log` VALUES (210, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：428毫秒', '0', '', '2025-08-22 08:47:10');
INSERT INTO `sys_job_log` VALUES (211, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (212, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (213, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (214, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (215, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (216, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (217, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (218, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (219, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (220, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (221, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (222, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (223, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (224, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:11');
INSERT INTO `sys_job_log` VALUES (225, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:25');
INSERT INTO `sys_job_log` VALUES (226, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-22 08:47:25');
INSERT INTO `sys_job_log` VALUES (227, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:26');
INSERT INTO `sys_job_log` VALUES (228, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:26');
INSERT INTO `sys_job_log` VALUES (229, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:38');
INSERT INTO `sys_job_log` VALUES (230, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:47:38');
INSERT INTO `sys_job_log` VALUES (231, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:40');
INSERT INTO `sys_job_log` VALUES (232, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1013毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (233, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (234, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (235, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (236, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (237, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (238, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (239, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (240, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (241, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (242, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (243, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (244, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (245, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (246, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (247, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (248, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (249, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (250, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (251, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (252, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (253, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (254, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (255, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (256, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (257, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (258, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (259, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (260, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (261, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (262, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (263, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (264, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (265, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (266, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (267, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (268, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (269, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (270, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (271, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (272, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (273, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (274, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (275, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (276, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (277, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (278, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (279, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (280, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (281, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (282, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (283, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (284, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (285, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (286, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (287, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (288, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (289, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (290, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (291, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (292, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (293, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (294, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (295, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (296, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (297, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (298, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (299, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (300, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (301, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (302, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (303, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (304, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:47:41');
INSERT INTO `sys_job_log` VALUES (305, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9868毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (306, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (307, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (308, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (309, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (310, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (311, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (312, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (313, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (314, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (315, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (316, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (317, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (318, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (319, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (320, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (321, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (322, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (323, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (324, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (325, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (326, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (327, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (328, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (329, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (330, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (331, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (332, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (333, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (334, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (335, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (336, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (337, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (338, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (339, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (340, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (341, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (342, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (343, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (344, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (345, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (346, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (347, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (348, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (349, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (350, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (351, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (352, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (353, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-22 08:48:00');
INSERT INTO `sys_job_log` VALUES (354, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：3毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (355, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (356, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：45毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (357, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (358, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (359, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：22毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (360, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (361, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (362, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (363, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 10:02:36');
INSERT INTO `sys_job_log` VALUES (364, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：42毫秒', '0', '', '2025-08-22 12:33:00');
INSERT INTO `sys_job_log` VALUES (365, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:34:00');
INSERT INTO `sys_job_log` VALUES (366, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 12:35:00');
INSERT INTO `sys_job_log` VALUES (367, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:35:00');
INSERT INTO `sys_job_log` VALUES (368, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:36:00');
INSERT INTO `sys_job_log` VALUES (369, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:37:00');
INSERT INTO `sys_job_log` VALUES (370, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：17毫秒', '0', '', '2025-08-22 12:39:00');
INSERT INTO `sys_job_log` VALUES (371, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 12:40:00');
INSERT INTO `sys_job_log` VALUES (372, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-22 12:40:00');
INSERT INTO `sys_job_log` VALUES (373, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:41:00');
INSERT INTO `sys_job_log` VALUES (374, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:42:00');
INSERT INTO `sys_job_log` VALUES (375, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:43:00');
INSERT INTO `sys_job_log` VALUES (376, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:44:00');
INSERT INTO `sys_job_log` VALUES (377, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 12:45:00');
INSERT INTO `sys_job_log` VALUES (378, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-22 12:45:00');
INSERT INTO `sys_job_log` VALUES (379, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:46:00');
INSERT INTO `sys_job_log` VALUES (380, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:47:00');
INSERT INTO `sys_job_log` VALUES (381, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:48:00');
INSERT INTO `sys_job_log` VALUES (382, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:49:00');
INSERT INTO `sys_job_log` VALUES (383, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 12:50:00');
INSERT INTO `sys_job_log` VALUES (384, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:50:00');
INSERT INTO `sys_job_log` VALUES (385, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:51:00');
INSERT INTO `sys_job_log` VALUES (386, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-22 12:52:00');
INSERT INTO `sys_job_log` VALUES (387, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:53:00');
INSERT INTO `sys_job_log` VALUES (388, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:54:00');
INSERT INTO `sys_job_log` VALUES (389, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 12:55:00');
INSERT INTO `sys_job_log` VALUES (390, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:55:00');
INSERT INTO `sys_job_log` VALUES (391, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:56:00');
INSERT INTO `sys_job_log` VALUES (392, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 12:57:00');
INSERT INTO `sys_job_log` VALUES (393, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:58:00');
INSERT INTO `sys_job_log` VALUES (394, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-22 12:59:00');
INSERT INTO `sys_job_log` VALUES (395, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 13:00:00');
INSERT INTO `sys_job_log` VALUES (396, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9毫秒', '0', '', '2025-08-22 13:00:00');
INSERT INTO `sys_job_log` VALUES (397, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：21934毫秒', '0', '', '2025-08-22 13:00:21');
INSERT INTO `sys_job_log` VALUES (398, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：15毫秒', '0', '', '2025-08-22 14:18:13');
INSERT INTO `sys_job_log` VALUES (399, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-22 14:19:00');
INSERT INTO `sys_job_log` VALUES (400, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-22 14:20:55');
INSERT INTO `sys_job_log` VALUES (401, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：16毫秒', '0', '', '2025-08-22 14:20:55');
INSERT INTO `sys_job_log` VALUES (402, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：38毫秒', '0', '', '2025-08-22 14:31:15');
INSERT INTO `sys_job_log` VALUES (403, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：13毫秒', '0', '', '2025-08-22 14:32:00');
INSERT INTO `sys_job_log` VALUES (404, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-22 14:35:00');
INSERT INTO `sys_job_log` VALUES (405, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：13毫秒', '0', '', '2025-08-22 14:35:00');
INSERT INTO `sys_job_log` VALUES (406, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：239毫秒', '0', '', '2025-08-24 07:41:00');
INSERT INTO `sys_job_log` VALUES (407, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 07:42:00');
INSERT INTO `sys_job_log` VALUES (408, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-24 07:43:00');
INSERT INTO `sys_job_log` VALUES (409, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 07:44:00');
INSERT INTO `sys_job_log` VALUES (410, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 07:45:00');
INSERT INTO `sys_job_log` VALUES (411, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-24 07:45:00');
INSERT INTO `sys_job_log` VALUES (412, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 07:46:00');
INSERT INTO `sys_job_log` VALUES (413, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:47:00');
INSERT INTO `sys_job_log` VALUES (414, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:48:00');
INSERT INTO `sys_job_log` VALUES (415, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:49:00');
INSERT INTO `sys_job_log` VALUES (416, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 07:50:00');
INSERT INTO `sys_job_log` VALUES (417, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:50:00');
INSERT INTO `sys_job_log` VALUES (418, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:51:00');
INSERT INTO `sys_job_log` VALUES (419, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:52:00');
INSERT INTO `sys_job_log` VALUES (420, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 07:53:00');
INSERT INTO `sys_job_log` VALUES (421, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:54:00');
INSERT INTO `sys_job_log` VALUES (422, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 07:55:00');
INSERT INTO `sys_job_log` VALUES (423, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 07:55:00');
INSERT INTO `sys_job_log` VALUES (424, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:56:00');
INSERT INTO `sys_job_log` VALUES (425, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 07:57:00');
INSERT INTO `sys_job_log` VALUES (426, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:58:00');
INSERT INTO `sys_job_log` VALUES (427, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 07:59:00');
INSERT INTO `sys_job_log` VALUES (428, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：6毫秒', '0', '', '2025-08-24 08:00:00');
INSERT INTO `sys_job_log` VALUES (429, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：11毫秒', '0', '', '2025-08-24 08:00:00');
INSERT INTO `sys_job_log` VALUES (430, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 08:01:00');
INSERT INTO `sys_job_log` VALUES (431, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：11毫秒', '0', '', '2025-08-24 08:02:03');
INSERT INTO `sys_job_log` VALUES (432, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：145172毫秒', '0', '', '2025-08-24 08:02:25');
INSERT INTO `sys_job_log` VALUES (433, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:03:00');
INSERT INTO `sys_job_log` VALUES (434, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:04:00');
INSERT INTO `sys_job_log` VALUES (435, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 08:05:00');
INSERT INTO `sys_job_log` VALUES (436, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:05:00');
INSERT INTO `sys_job_log` VALUES (437, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:06:00');
INSERT INTO `sys_job_log` VALUES (438, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:07:00');
INSERT INTO `sys_job_log` VALUES (439, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:08:00');
INSERT INTO `sys_job_log` VALUES (440, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 08:17:00');
INSERT INTO `sys_job_log` VALUES (441, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:18:00');
INSERT INTO `sys_job_log` VALUES (442, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:19:00');
INSERT INTO `sys_job_log` VALUES (443, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 08:20:00');
INSERT INTO `sys_job_log` VALUES (444, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:20:00');
INSERT INTO `sys_job_log` VALUES (445, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:21:00');
INSERT INTO `sys_job_log` VALUES (446, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：3毫秒', '0', '', '2025-08-24 08:35:00');
INSERT INTO `sys_job_log` VALUES (447, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：14毫秒', '0', '', '2025-08-24 08:35:00');
INSERT INTO `sys_job_log` VALUES (448, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:36:00');
INSERT INTO `sys_job_log` VALUES (449, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:37:00');
INSERT INTO `sys_job_log` VALUES (450, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:38:00');
INSERT INTO `sys_job_log` VALUES (451, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:39:00');
INSERT INTO `sys_job_log` VALUES (452, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 08:40:00');
INSERT INTO `sys_job_log` VALUES (453, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:40:00');
INSERT INTO `sys_job_log` VALUES (454, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:41:00');
INSERT INTO `sys_job_log` VALUES (455, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:42:00');
INSERT INTO `sys_job_log` VALUES (456, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:43:00');
INSERT INTO `sys_job_log` VALUES (457, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 08:44:00');
INSERT INTO `sys_job_log` VALUES (458, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 08:45:00');
INSERT INTO `sys_job_log` VALUES (459, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:45:00');
INSERT INTO `sys_job_log` VALUES (460, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:46:00');
INSERT INTO `sys_job_log` VALUES (461, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:47:00');
INSERT INTO `sys_job_log` VALUES (462, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:48:00');
INSERT INTO `sys_job_log` VALUES (463, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:49:00');
INSERT INTO `sys_job_log` VALUES (464, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 08:50:00');
INSERT INTO `sys_job_log` VALUES (465, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:50:00');
INSERT INTO `sys_job_log` VALUES (466, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 08:51:00');
INSERT INTO `sys_job_log` VALUES (467, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:52:00');
INSERT INTO `sys_job_log` VALUES (468, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:53:00');
INSERT INTO `sys_job_log` VALUES (469, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:54:00');
INSERT INTO `sys_job_log` VALUES (470, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 08:55:00');
INSERT INTO `sys_job_log` VALUES (471, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:55:00');
INSERT INTO `sys_job_log` VALUES (472, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:56:00');
INSERT INTO `sys_job_log` VALUES (473, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:57:00');
INSERT INTO `sys_job_log` VALUES (474, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 08:58:00');
INSERT INTO `sys_job_log` VALUES (475, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 08:59:00');
INSERT INTO `sys_job_log` VALUES (476, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 09:00:00');
INSERT INTO `sys_job_log` VALUES (477, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:00:00');
INSERT INTO `sys_job_log` VALUES (478, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:01:00');
INSERT INTO `sys_job_log` VALUES (479, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:02:57');
INSERT INTO `sys_job_log` VALUES (480, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:03:00');
INSERT INTO `sys_job_log` VALUES (481, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：193071毫秒', '0', '', '2025-08-24 09:03:13');
INSERT INTO `sys_job_log` VALUES (482, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:04:00');
INSERT INTO `sys_job_log` VALUES (483, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:05:00');
INSERT INTO `sys_job_log` VALUES (484, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:05:00');
INSERT INTO `sys_job_log` VALUES (485, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:06:00');
INSERT INTO `sys_job_log` VALUES (486, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:07:00');
INSERT INTO `sys_job_log` VALUES (487, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:08:00');
INSERT INTO `sys_job_log` VALUES (488, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:09:00');
INSERT INTO `sys_job_log` VALUES (489, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:10:00');
INSERT INTO `sys_job_log` VALUES (490, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:10:00');
INSERT INTO `sys_job_log` VALUES (491, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:11:00');
INSERT INTO `sys_job_log` VALUES (492, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:12:00');
INSERT INTO `sys_job_log` VALUES (493, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:13:00');
INSERT INTO `sys_job_log` VALUES (494, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:14:00');
INSERT INTO `sys_job_log` VALUES (495, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 09:15:00');
INSERT INTO `sys_job_log` VALUES (496, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:15:00');
INSERT INTO `sys_job_log` VALUES (497, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:16:00');
INSERT INTO `sys_job_log` VALUES (498, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:17:00');
INSERT INTO `sys_job_log` VALUES (499, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:18:00');
INSERT INTO `sys_job_log` VALUES (500, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:19:00');
INSERT INTO `sys_job_log` VALUES (501, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 09:20:00');
INSERT INTO `sys_job_log` VALUES (502, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:20:00');
INSERT INTO `sys_job_log` VALUES (503, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:21:00');
INSERT INTO `sys_job_log` VALUES (504, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:22:00');
INSERT INTO `sys_job_log` VALUES (505, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:23:00');
INSERT INTO `sys_job_log` VALUES (506, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：67毫秒', '0', '', '2025-08-24 09:24:00');
INSERT INTO `sys_job_log` VALUES (507, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:25:00');
INSERT INTO `sys_job_log` VALUES (508, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 09:25:00');
INSERT INTO `sys_job_log` VALUES (509, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:26:00');
INSERT INTO `sys_job_log` VALUES (510, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:27:00');
INSERT INTO `sys_job_log` VALUES (511, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:28:00');
INSERT INTO `sys_job_log` VALUES (512, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 09:29:00');
INSERT INTO `sys_job_log` VALUES (513, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:30:00');
INSERT INTO `sys_job_log` VALUES (514, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:30:00');
INSERT INTO `sys_job_log` VALUES (515, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:31:00');
INSERT INTO `sys_job_log` VALUES (516, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:32:00');
INSERT INTO `sys_job_log` VALUES (517, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:33:00');
INSERT INTO `sys_job_log` VALUES (518, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:34:00');
INSERT INTO `sys_job_log` VALUES (519, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:35:00');
INSERT INTO `sys_job_log` VALUES (520, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 09:35:00');
INSERT INTO `sys_job_log` VALUES (521, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：84毫秒', '0', '', '2025-08-24 09:38:00');
INSERT INTO `sys_job_log` VALUES (522, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 09:39:00');
INSERT INTO `sys_job_log` VALUES (523, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:40:00');
INSERT INTO `sys_job_log` VALUES (524, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:40:00');
INSERT INTO `sys_job_log` VALUES (525, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:41:00');
INSERT INTO `sys_job_log` VALUES (526, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:42:00');
INSERT INTO `sys_job_log` VALUES (527, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:43:00');
INSERT INTO `sys_job_log` VALUES (528, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:44:00');
INSERT INTO `sys_job_log` VALUES (529, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 09:45:00');
INSERT INTO `sys_job_log` VALUES (530, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:45:00');
INSERT INTO `sys_job_log` VALUES (531, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:46:00');
INSERT INTO `sys_job_log` VALUES (532, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:47:00');
INSERT INTO `sys_job_log` VALUES (533, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:48:00');
INSERT INTO `sys_job_log` VALUES (534, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:49:00');
INSERT INTO `sys_job_log` VALUES (535, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:50:00');
INSERT INTO `sys_job_log` VALUES (536, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:50:00');
INSERT INTO `sys_job_log` VALUES (537, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 09:51:00');
INSERT INTO `sys_job_log` VALUES (538, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:52:00');
INSERT INTO `sys_job_log` VALUES (539, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-24 09:53:00');
INSERT INTO `sys_job_log` VALUES (540, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:54:00');
INSERT INTO `sys_job_log` VALUES (541, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 09:55:00');
INSERT INTO `sys_job_log` VALUES (542, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:55:00');
INSERT INTO `sys_job_log` VALUES (543, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:56:00');
INSERT INTO `sys_job_log` VALUES (544, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 09:57:00');
INSERT INTO `sys_job_log` VALUES (545, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:58:00');
INSERT INTO `sys_job_log` VALUES (546, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 09:59:00');
INSERT INTO `sys_job_log` VALUES (547, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:00:00');
INSERT INTO `sys_job_log` VALUES (548, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-24 10:00:00');
INSERT INTO `sys_job_log` VALUES (549, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:01:00');
INSERT INTO `sys_job_log` VALUES (550, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：109673毫秒', '0', '', '2025-08-24 10:01:49');
INSERT INTO `sys_job_log` VALUES (551, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:02:00');
INSERT INTO `sys_job_log` VALUES (552, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:03:00');
INSERT INTO `sys_job_log` VALUES (553, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:04:00');
INSERT INTO `sys_job_log` VALUES (554, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:05:00');
INSERT INTO `sys_job_log` VALUES (555, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:05:00');
INSERT INTO `sys_job_log` VALUES (556, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:06:00');
INSERT INTO `sys_job_log` VALUES (557, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 10:08:00');
INSERT INTO `sys_job_log` VALUES (558, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:09:00');
INSERT INTO `sys_job_log` VALUES (559, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:10:00');
INSERT INTO `sys_job_log` VALUES (560, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:10:00');
INSERT INTO `sys_job_log` VALUES (561, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:11:00');
INSERT INTO `sys_job_log` VALUES (562, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:12:00');
INSERT INTO `sys_job_log` VALUES (563, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:13:00');
INSERT INTO `sys_job_log` VALUES (564, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:14:00');
INSERT INTO `sys_job_log` VALUES (565, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：5毫秒', '0', '', '2025-08-24 10:15:00');
INSERT INTO `sys_job_log` VALUES (566, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：43毫秒', '0', '', '2025-08-24 10:15:00');
INSERT INTO `sys_job_log` VALUES (567, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:16:00');
INSERT INTO `sys_job_log` VALUES (568, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:17:00');
INSERT INTO `sys_job_log` VALUES (569, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:18:00');
INSERT INTO `sys_job_log` VALUES (570, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:19:00');
INSERT INTO `sys_job_log` VALUES (571, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 10:20:00');
INSERT INTO `sys_job_log` VALUES (572, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:20:00');
INSERT INTO `sys_job_log` VALUES (573, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:21:00');
INSERT INTO `sys_job_log` VALUES (574, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:22:00');
INSERT INTO `sys_job_log` VALUES (575, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:23:00');
INSERT INTO `sys_job_log` VALUES (576, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:24:00');
INSERT INTO `sys_job_log` VALUES (577, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:25:00');
INSERT INTO `sys_job_log` VALUES (578, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:25:00');
INSERT INTO `sys_job_log` VALUES (579, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:26:00');
INSERT INTO `sys_job_log` VALUES (580, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:27:00');
INSERT INTO `sys_job_log` VALUES (581, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:28:00');
INSERT INTO `sys_job_log` VALUES (582, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：10毫秒', '0', '', '2025-08-24 10:29:00');
INSERT INTO `sys_job_log` VALUES (583, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:30:00');
INSERT INTO `sys_job_log` VALUES (584, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:30:00');
INSERT INTO `sys_job_log` VALUES (585, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 10:31:00');
INSERT INTO `sys_job_log` VALUES (586, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:32:00');
INSERT INTO `sys_job_log` VALUES (587, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:33:00');
INSERT INTO `sys_job_log` VALUES (588, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:34:00');
INSERT INTO `sys_job_log` VALUES (589, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:35:00');
INSERT INTO `sys_job_log` VALUES (590, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:35:00');
INSERT INTO `sys_job_log` VALUES (591, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:36:00');
INSERT INTO `sys_job_log` VALUES (592, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:37:00');
INSERT INTO `sys_job_log` VALUES (593, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:38:00');
INSERT INTO `sys_job_log` VALUES (594, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:39:00');
INSERT INTO `sys_job_log` VALUES (595, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 10:40:00');
INSERT INTO `sys_job_log` VALUES (596, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:40:00');
INSERT INTO `sys_job_log` VALUES (597, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:41:00');
INSERT INTO `sys_job_log` VALUES (598, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:42:00');
INSERT INTO `sys_job_log` VALUES (599, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:43:00');
INSERT INTO `sys_job_log` VALUES (600, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:44:00');
INSERT INTO `sys_job_log` VALUES (601, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 10:45:00');
INSERT INTO `sys_job_log` VALUES (602, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:45:00');
INSERT INTO `sys_job_log` VALUES (603, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:46:00');
INSERT INTO `sys_job_log` VALUES (604, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:47:00');
INSERT INTO `sys_job_log` VALUES (605, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:48:00');
INSERT INTO `sys_job_log` VALUES (606, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:49:00');
INSERT INTO `sys_job_log` VALUES (607, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:50:00');
INSERT INTO `sys_job_log` VALUES (608, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:50:00');
INSERT INTO `sys_job_log` VALUES (609, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:51:00');
INSERT INTO `sys_job_log` VALUES (610, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:52:00');
INSERT INTO `sys_job_log` VALUES (611, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 10:53:00');
INSERT INTO `sys_job_log` VALUES (612, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:54:00');
INSERT INTO `sys_job_log` VALUES (613, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 10:55:00');
INSERT INTO `sys_job_log` VALUES (614, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:55:00');
INSERT INTO `sys_job_log` VALUES (615, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：18毫秒', '0', '', '2025-08-24 10:56:00');
INSERT INTO `sys_job_log` VALUES (616, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 10:57:00');
INSERT INTO `sys_job_log` VALUES (617, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 10:58:00');
INSERT INTO `sys_job_log` VALUES (618, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 10:59:00');
INSERT INTO `sys_job_log` VALUES (619, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 11:00:00');
INSERT INTO `sys_job_log` VALUES (620, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:00:00');
INSERT INTO `sys_job_log` VALUES (621, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:01:00');
INSERT INTO `sys_job_log` VALUES (622, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：107170毫秒', '0', '', '2025-08-24 11:01:47');
INSERT INTO `sys_job_log` VALUES (623, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:02:00');
INSERT INTO `sys_job_log` VALUES (624, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:03:00');
INSERT INTO `sys_job_log` VALUES (625, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:04:00');
INSERT INTO `sys_job_log` VALUES (626, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:05:00');
INSERT INTO `sys_job_log` VALUES (627, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:05:00');
INSERT INTO `sys_job_log` VALUES (628, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:06:00');
INSERT INTO `sys_job_log` VALUES (629, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:07:00');
INSERT INTO `sys_job_log` VALUES (630, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:08:00');
INSERT INTO `sys_job_log` VALUES (631, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:09:00');
INSERT INTO `sys_job_log` VALUES (632, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 11:10:00');
INSERT INTO `sys_job_log` VALUES (633, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:10:00');
INSERT INTO `sys_job_log` VALUES (634, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:11:00');
INSERT INTO `sys_job_log` VALUES (635, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:12:00');
INSERT INTO `sys_job_log` VALUES (636, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 11:13:00');
INSERT INTO `sys_job_log` VALUES (637, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:14:00');
INSERT INTO `sys_job_log` VALUES (638, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:15:00');
INSERT INTO `sys_job_log` VALUES (639, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:15:00');
INSERT INTO `sys_job_log` VALUES (640, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:16:00');
INSERT INTO `sys_job_log` VALUES (641, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:17:00');
INSERT INTO `sys_job_log` VALUES (642, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:18:00');
INSERT INTO `sys_job_log` VALUES (643, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:19:00');
INSERT INTO `sys_job_log` VALUES (644, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:20:00');
INSERT INTO `sys_job_log` VALUES (645, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:20:00');
INSERT INTO `sys_job_log` VALUES (646, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:21:00');
INSERT INTO `sys_job_log` VALUES (647, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:22:00');
INSERT INTO `sys_job_log` VALUES (648, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:23:00');
INSERT INTO `sys_job_log` VALUES (649, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:24:00');
INSERT INTO `sys_job_log` VALUES (650, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 11:25:00');
INSERT INTO `sys_job_log` VALUES (651, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:25:00');
INSERT INTO `sys_job_log` VALUES (652, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:26:00');
INSERT INTO `sys_job_log` VALUES (653, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：20毫秒', '0', '', '2025-08-24 11:28:00');
INSERT INTO `sys_job_log` VALUES (654, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:29:00');
INSERT INTO `sys_job_log` VALUES (655, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:30:00');
INSERT INTO `sys_job_log` VALUES (656, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:30:00');
INSERT INTO `sys_job_log` VALUES (657, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:31:00');
INSERT INTO `sys_job_log` VALUES (658, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:32:00');
INSERT INTO `sys_job_log` VALUES (659, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:33:00');
INSERT INTO `sys_job_log` VALUES (660, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:34:00');
INSERT INTO `sys_job_log` VALUES (661, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:35:00');
INSERT INTO `sys_job_log` VALUES (662, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:35:00');
INSERT INTO `sys_job_log` VALUES (663, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:36:00');
INSERT INTO `sys_job_log` VALUES (664, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：44毫秒', '0', '', '2025-08-24 11:37:00');
INSERT INTO `sys_job_log` VALUES (665, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:38:00');
INSERT INTO `sys_job_log` VALUES (666, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:39:00');
INSERT INTO `sys_job_log` VALUES (667, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:40:00');
INSERT INTO `sys_job_log` VALUES (668, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:40:00');
INSERT INTO `sys_job_log` VALUES (669, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:41:00');
INSERT INTO `sys_job_log` VALUES (670, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:42:00');
INSERT INTO `sys_job_log` VALUES (671, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:43:00');
INSERT INTO `sys_job_log` VALUES (672, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：37毫秒', '0', '', '2025-08-24 11:44:00');
INSERT INTO `sys_job_log` VALUES (673, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:45:00');
INSERT INTO `sys_job_log` VALUES (674, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:45:00');
INSERT INTO `sys_job_log` VALUES (675, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:46:00');
INSERT INTO `sys_job_log` VALUES (676, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:47:00');
INSERT INTO `sys_job_log` VALUES (677, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 11:48:00');
INSERT INTO `sys_job_log` VALUES (678, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：39毫秒', '0', '', '2025-08-24 11:49:00');
INSERT INTO `sys_job_log` VALUES (679, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:50:00');
INSERT INTO `sys_job_log` VALUES (680, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:50:00');
INSERT INTO `sys_job_log` VALUES (681, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:51:00');
INSERT INTO `sys_job_log` VALUES (682, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:52:00');
INSERT INTO `sys_job_log` VALUES (683, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:53:00');
INSERT INTO `sys_job_log` VALUES (684, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9毫秒', '0', '', '2025-08-24 11:54:00');
INSERT INTO `sys_job_log` VALUES (685, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 11:55:00');
INSERT INTO `sys_job_log` VALUES (686, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:55:00');
INSERT INTO `sys_job_log` VALUES (687, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 11:56:00');
INSERT INTO `sys_job_log` VALUES (688, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:57:00');
INSERT INTO `sys_job_log` VALUES (689, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 11:58:00');
INSERT INTO `sys_job_log` VALUES (690, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 11:59:00');
INSERT INTO `sys_job_log` VALUES (691, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:01:00');
INSERT INTO `sys_job_log` VALUES (692, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:02:00');
INSERT INTO `sys_job_log` VALUES (693, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:03:00');
INSERT INTO `sys_job_log` VALUES (694, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:04:00');
INSERT INTO `sys_job_log` VALUES (695, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:05:00');
INSERT INTO `sys_job_log` VALUES (696, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:05:00');
INSERT INTO `sys_job_log` VALUES (697, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:06:00');
INSERT INTO `sys_job_log` VALUES (698, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:07:00');
INSERT INTO `sys_job_log` VALUES (699, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:08:00');
INSERT INTO `sys_job_log` VALUES (700, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:09:00');
INSERT INTO `sys_job_log` VALUES (701, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:10:00');
INSERT INTO `sys_job_log` VALUES (702, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:10:00');
INSERT INTO `sys_job_log` VALUES (703, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 12:11:00');
INSERT INTO `sys_job_log` VALUES (704, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:12:00');
INSERT INTO `sys_job_log` VALUES (705, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：58毫秒', '0', '', '2025-08-24 12:15:00');
INSERT INTO `sys_job_log` VALUES (706, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：7毫秒', '0', '', '2025-08-24 12:15:00');
INSERT INTO `sys_job_log` VALUES (707, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:16:00');
INSERT INTO `sys_job_log` VALUES (708, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:17:00');
INSERT INTO `sys_job_log` VALUES (709, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:18:00');
INSERT INTO `sys_job_log` VALUES (710, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:19:00');
INSERT INTO `sys_job_log` VALUES (711, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:20:00');
INSERT INTO `sys_job_log` VALUES (712, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:20:00');
INSERT INTO `sys_job_log` VALUES (713, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:21:00');
INSERT INTO `sys_job_log` VALUES (714, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-24 12:23:00');
INSERT INTO `sys_job_log` VALUES (715, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 12:24:00');
INSERT INTO `sys_job_log` VALUES (716, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:25:00');
INSERT INTO `sys_job_log` VALUES (717, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:25:00');
INSERT INTO `sys_job_log` VALUES (718, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:26:00');
INSERT INTO `sys_job_log` VALUES (719, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:27:00');
INSERT INTO `sys_job_log` VALUES (720, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:28:00');
INSERT INTO `sys_job_log` VALUES (721, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:29:00');
INSERT INTO `sys_job_log` VALUES (722, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:30:00');
INSERT INTO `sys_job_log` VALUES (723, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:30:00');
INSERT INTO `sys_job_log` VALUES (724, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:31:00');
INSERT INTO `sys_job_log` VALUES (725, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:32:00');
INSERT INTO `sys_job_log` VALUES (726, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:33:00');
INSERT INTO `sys_job_log` VALUES (727, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:34:00');
INSERT INTO `sys_job_log` VALUES (728, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:35:00');
INSERT INTO `sys_job_log` VALUES (729, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:35:00');
INSERT INTO `sys_job_log` VALUES (730, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:36:00');
INSERT INTO `sys_job_log` VALUES (731, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:37:00');
INSERT INTO `sys_job_log` VALUES (732, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:38:00');
INSERT INTO `sys_job_log` VALUES (733, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 12:39:00');
INSERT INTO `sys_job_log` VALUES (734, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：67毫秒', '0', '', '2025-08-24 12:40:00');
INSERT INTO `sys_job_log` VALUES (735, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：5毫秒', '0', '', '2025-08-24 12:40:00');
INSERT INTO `sys_job_log` VALUES (736, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 12:41:00');
INSERT INTO `sys_job_log` VALUES (737, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 12:42:00');
INSERT INTO `sys_job_log` VALUES (738, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:43:00');
INSERT INTO `sys_job_log` VALUES (739, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:44:00');
INSERT INTO `sys_job_log` VALUES (740, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:45:00');
INSERT INTO `sys_job_log` VALUES (741, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:45:00');
INSERT INTO `sys_job_log` VALUES (742, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：20毫秒', '0', '', '2025-08-24 12:49:00');
INSERT INTO `sys_job_log` VALUES (743, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:50:00');
INSERT INTO `sys_job_log` VALUES (744, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:50:00');
INSERT INTO `sys_job_log` VALUES (745, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 12:51:00');
INSERT INTO `sys_job_log` VALUES (746, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：11毫秒', '0', '', '2025-08-24 12:54:00');
INSERT INTO `sys_job_log` VALUES (747, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 12:55:00');
INSERT INTO `sys_job_log` VALUES (748, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 12:55:00');
INSERT INTO `sys_job_log` VALUES (749, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:56:00');
INSERT INTO `sys_job_log` VALUES (750, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 12:57:00');
INSERT INTO `sys_job_log` VALUES (751, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：23毫秒', '0', '', '2025-08-24 12:58:00');
INSERT INTO `sys_job_log` VALUES (752, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 12:59:00');
INSERT INTO `sys_job_log` VALUES (753, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:00:00');
INSERT INTO `sys_job_log` VALUES (754, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:00:00');
INSERT INTO `sys_job_log` VALUES (755, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:01:00');
INSERT INTO `sys_job_log` VALUES (756, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：100981毫秒', '0', '', '2025-08-24 13:01:40');
INSERT INTO `sys_job_log` VALUES (757, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:02:00');
INSERT INTO `sys_job_log` VALUES (758, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:03:00');
INSERT INTO `sys_job_log` VALUES (759, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:04:00');
INSERT INTO `sys_job_log` VALUES (760, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:05:00');
INSERT INTO `sys_job_log` VALUES (761, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:05:00');
INSERT INTO `sys_job_log` VALUES (762, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:06:00');
INSERT INTO `sys_job_log` VALUES (763, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:07:00');
INSERT INTO `sys_job_log` VALUES (764, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:08:00');
INSERT INTO `sys_job_log` VALUES (765, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:10:00');
INSERT INTO `sys_job_log` VALUES (766, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 13:10:00');
INSERT INTO `sys_job_log` VALUES (767, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:11:00');
INSERT INTO `sys_job_log` VALUES (768, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:12:00');
INSERT INTO `sys_job_log` VALUES (769, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:13:00');
INSERT INTO `sys_job_log` VALUES (770, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:14:00');
INSERT INTO `sys_job_log` VALUES (771, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:15:00');
INSERT INTO `sys_job_log` VALUES (772, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:15:00');
INSERT INTO `sys_job_log` VALUES (773, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:16:00');
INSERT INTO `sys_job_log` VALUES (774, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:17:00');
INSERT INTO `sys_job_log` VALUES (775, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:18:00');
INSERT INTO `sys_job_log` VALUES (776, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:19:00');
INSERT INTO `sys_job_log` VALUES (777, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:20:00');
INSERT INTO `sys_job_log` VALUES (778, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 13:20:00');
INSERT INTO `sys_job_log` VALUES (779, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:21:00');
INSERT INTO `sys_job_log` VALUES (780, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:22:00');
INSERT INTO `sys_job_log` VALUES (781, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:23:00');
INSERT INTO `sys_job_log` VALUES (782, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:24:00');
INSERT INTO `sys_job_log` VALUES (783, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 13:25:00');
INSERT INTO `sys_job_log` VALUES (784, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:25:00');
INSERT INTO `sys_job_log` VALUES (785, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 13:26:00');
INSERT INTO `sys_job_log` VALUES (786, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-24 13:27:00');
INSERT INTO `sys_job_log` VALUES (787, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-24 13:28:00');
INSERT INTO `sys_job_log` VALUES (788, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:29:00');
INSERT INTO `sys_job_log` VALUES (789, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 13:30:00');
INSERT INTO `sys_job_log` VALUES (790, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:30:00');
INSERT INTO `sys_job_log` VALUES (791, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 13:31:00');
INSERT INTO `sys_job_log` VALUES (792, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:32:00');
INSERT INTO `sys_job_log` VALUES (793, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:33:00');
INSERT INTO `sys_job_log` VALUES (794, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:34:00');
INSERT INTO `sys_job_log` VALUES (795, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9毫秒', '0', '', '2025-08-24 13:44:00');
INSERT INTO `sys_job_log` VALUES (796, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:45:00');
INSERT INTO `sys_job_log` VALUES (797, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:45:00');
INSERT INTO `sys_job_log` VALUES (798, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 13:48:00');
INSERT INTO `sys_job_log` VALUES (799, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 13:49:00');
INSERT INTO `sys_job_log` VALUES (800, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:50:00');
INSERT INTO `sys_job_log` VALUES (801, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:50:00');
INSERT INTO `sys_job_log` VALUES (802, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 13:51:00');
INSERT INTO `sys_job_log` VALUES (803, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:52:00');
INSERT INTO `sys_job_log` VALUES (804, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:53:00');
INSERT INTO `sys_job_log` VALUES (805, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:54:00');
INSERT INTO `sys_job_log` VALUES (806, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 13:55:00');
INSERT INTO `sys_job_log` VALUES (807, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:55:00');
INSERT INTO `sys_job_log` VALUES (808, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:56:00');
INSERT INTO `sys_job_log` VALUES (809, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 13:57:00');
INSERT INTO `sys_job_log` VALUES (810, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 13:58:00');
INSERT INTO `sys_job_log` VALUES (811, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 13:59:00');
INSERT INTO `sys_job_log` VALUES (812, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:00:00');
INSERT INTO `sys_job_log` VALUES (813, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 14:00:00');
INSERT INTO `sys_job_log` VALUES (814, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:01:00');
INSERT INTO `sys_job_log` VALUES (815, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:02:00');
INSERT INTO `sys_job_log` VALUES (816, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：121806毫秒', '0', '', '2025-08-24 14:02:01');
INSERT INTO `sys_job_log` VALUES (817, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:03:00');
INSERT INTO `sys_job_log` VALUES (818, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:04:00');
INSERT INTO `sys_job_log` VALUES (819, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 14:05:00');
INSERT INTO `sys_job_log` VALUES (820, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:05:00');
INSERT INTO `sys_job_log` VALUES (821, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:06:00');
INSERT INTO `sys_job_log` VALUES (822, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:07:00');
INSERT INTO `sys_job_log` VALUES (823, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:08:00');
INSERT INTO `sys_job_log` VALUES (824, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:09:00');
INSERT INTO `sys_job_log` VALUES (825, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 14:10:00');
INSERT INTO `sys_job_log` VALUES (826, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:10:00');
INSERT INTO `sys_job_log` VALUES (827, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:11:00');
INSERT INTO `sys_job_log` VALUES (828, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:12:00');
INSERT INTO `sys_job_log` VALUES (829, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:13:00');
INSERT INTO `sys_job_log` VALUES (830, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:14:00');
INSERT INTO `sys_job_log` VALUES (831, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:15:00');
INSERT INTO `sys_job_log` VALUES (832, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:15:00');
INSERT INTO `sys_job_log` VALUES (833, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:16:00');
INSERT INTO `sys_job_log` VALUES (834, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:17:00');
INSERT INTO `sys_job_log` VALUES (835, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:18:00');
INSERT INTO `sys_job_log` VALUES (836, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 14:19:00');
INSERT INTO `sys_job_log` VALUES (837, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:20:00');
INSERT INTO `sys_job_log` VALUES (838, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:20:00');
INSERT INTO `sys_job_log` VALUES (839, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:21:00');
INSERT INTO `sys_job_log` VALUES (840, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 14:22:00');
INSERT INTO `sys_job_log` VALUES (841, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:23:00');
INSERT INTO `sys_job_log` VALUES (842, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:24:00');
INSERT INTO `sys_job_log` VALUES (843, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:25:00');
INSERT INTO `sys_job_log` VALUES (844, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:25:00');
INSERT INTO `sys_job_log` VALUES (845, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:26:00');
INSERT INTO `sys_job_log` VALUES (846, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:27:00');
INSERT INTO `sys_job_log` VALUES (847, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:28:00');
INSERT INTO `sys_job_log` VALUES (848, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:29:00');
INSERT INTO `sys_job_log` VALUES (849, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:30:00');
INSERT INTO `sys_job_log` VALUES (850, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:30:00');
INSERT INTO `sys_job_log` VALUES (851, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:31:00');
INSERT INTO `sys_job_log` VALUES (852, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:32:00');
INSERT INTO `sys_job_log` VALUES (853, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:33:00');
INSERT INTO `sys_job_log` VALUES (854, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:34:00');
INSERT INTO `sys_job_log` VALUES (855, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 14:35:00');
INSERT INTO `sys_job_log` VALUES (856, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:35:00');
INSERT INTO `sys_job_log` VALUES (857, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:36:00');
INSERT INTO `sys_job_log` VALUES (858, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:37:00');
INSERT INTO `sys_job_log` VALUES (859, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:38:00');
INSERT INTO `sys_job_log` VALUES (860, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:39:00');
INSERT INTO `sys_job_log` VALUES (861, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:40:00');
INSERT INTO `sys_job_log` VALUES (862, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:40:00');
INSERT INTO `sys_job_log` VALUES (863, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:41:00');
INSERT INTO `sys_job_log` VALUES (864, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:42:00');
INSERT INTO `sys_job_log` VALUES (865, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:43:00');
INSERT INTO `sys_job_log` VALUES (866, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:44:00');
INSERT INTO `sys_job_log` VALUES (867, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 14:45:00');
INSERT INTO `sys_job_log` VALUES (868, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:45:00');
INSERT INTO `sys_job_log` VALUES (869, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:46:00');
INSERT INTO `sys_job_log` VALUES (870, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:47:00');
INSERT INTO `sys_job_log` VALUES (871, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:48:00');
INSERT INTO `sys_job_log` VALUES (872, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:49:00');
INSERT INTO `sys_job_log` VALUES (873, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:50:00');
INSERT INTO `sys_job_log` VALUES (874, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:50:00');
INSERT INTO `sys_job_log` VALUES (875, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:51:00');
INSERT INTO `sys_job_log` VALUES (876, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:52:00');
INSERT INTO `sys_job_log` VALUES (877, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 14:53:00');
INSERT INTO `sys_job_log` VALUES (878, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:54:00');
INSERT INTO `sys_job_log` VALUES (879, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 14:55:00');
INSERT INTO `sys_job_log` VALUES (880, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:55:00');
INSERT INTO `sys_job_log` VALUES (881, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:56:00');
INSERT INTO `sys_job_log` VALUES (882, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:57:00');
INSERT INTO `sys_job_log` VALUES (883, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:58:00');
INSERT INTO `sys_job_log` VALUES (884, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 14:59:00');
INSERT INTO `sys_job_log` VALUES (885, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:00:00');
INSERT INTO `sys_job_log` VALUES (886, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:00:00');
INSERT INTO `sys_job_log` VALUES (887, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:01:00');
INSERT INTO `sys_job_log` VALUES (888, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：115137毫秒', '0', '', '2025-08-24 15:01:55');
INSERT INTO `sys_job_log` VALUES (889, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:02:00');
INSERT INTO `sys_job_log` VALUES (890, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:03:00');
INSERT INTO `sys_job_log` VALUES (891, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:04:00');
INSERT INTO `sys_job_log` VALUES (892, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:05:00');
INSERT INTO `sys_job_log` VALUES (893, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:05:00');
INSERT INTO `sys_job_log` VALUES (894, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:06:00');
INSERT INTO `sys_job_log` VALUES (895, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 15:07:00');
INSERT INTO `sys_job_log` VALUES (896, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 15:08:00');
INSERT INTO `sys_job_log` VALUES (897, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:09:00');
INSERT INTO `sys_job_log` VALUES (898, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:10:00');
INSERT INTO `sys_job_log` VALUES (899, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:10:00');
INSERT INTO `sys_job_log` VALUES (900, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:11:00');
INSERT INTO `sys_job_log` VALUES (901, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 15:12:00');
INSERT INTO `sys_job_log` VALUES (902, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 15:13:00');
INSERT INTO `sys_job_log` VALUES (903, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:14:00');
INSERT INTO `sys_job_log` VALUES (904, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:15:00');
INSERT INTO `sys_job_log` VALUES (905, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:15:00');
INSERT INTO `sys_job_log` VALUES (906, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:16:00');
INSERT INTO `sys_job_log` VALUES (907, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:17:00');
INSERT INTO `sys_job_log` VALUES (908, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:18:00');
INSERT INTO `sys_job_log` VALUES (909, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 15:19:00');
INSERT INTO `sys_job_log` VALUES (910, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 15:20:00');
INSERT INTO `sys_job_log` VALUES (911, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:20:00');
INSERT INTO `sys_job_log` VALUES (912, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:21:00');
INSERT INTO `sys_job_log` VALUES (913, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:22:00');
INSERT INTO `sys_job_log` VALUES (914, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:23:00');
INSERT INTO `sys_job_log` VALUES (915, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:24:00');
INSERT INTO `sys_job_log` VALUES (916, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 15:25:00');
INSERT INTO `sys_job_log` VALUES (917, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:25:00');
INSERT INTO `sys_job_log` VALUES (918, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:26:00');
INSERT INTO `sys_job_log` VALUES (919, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:27:00');
INSERT INTO `sys_job_log` VALUES (920, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:28:00');
INSERT INTO `sys_job_log` VALUES (921, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:29:00');
INSERT INTO `sys_job_log` VALUES (922, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 15:30:00');
INSERT INTO `sys_job_log` VALUES (923, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:30:00');
INSERT INTO `sys_job_log` VALUES (924, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:31:00');
INSERT INTO `sys_job_log` VALUES (925, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:32:00');
INSERT INTO `sys_job_log` VALUES (926, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:33:00');
INSERT INTO `sys_job_log` VALUES (927, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:34:00');
INSERT INTO `sys_job_log` VALUES (928, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:35:00');
INSERT INTO `sys_job_log` VALUES (929, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:35:00');
INSERT INTO `sys_job_log` VALUES (930, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:36:00');
INSERT INTO `sys_job_log` VALUES (931, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:37:00');
INSERT INTO `sys_job_log` VALUES (932, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:38:00');
INSERT INTO `sys_job_log` VALUES (933, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:39:00');
INSERT INTO `sys_job_log` VALUES (934, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 15:40:00');
INSERT INTO `sys_job_log` VALUES (935, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:40:00');
INSERT INTO `sys_job_log` VALUES (936, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:41:00');
INSERT INTO `sys_job_log` VALUES (937, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:42:00');
INSERT INTO `sys_job_log` VALUES (938, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:43:00');
INSERT INTO `sys_job_log` VALUES (939, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:44:00');
INSERT INTO `sys_job_log` VALUES (940, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:45:00');
INSERT INTO `sys_job_log` VALUES (941, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:45:00');
INSERT INTO `sys_job_log` VALUES (942, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:46:00');
INSERT INTO `sys_job_log` VALUES (943, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:47:00');
INSERT INTO `sys_job_log` VALUES (944, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:48:00');
INSERT INTO `sys_job_log` VALUES (945, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:49:00');
INSERT INTO `sys_job_log` VALUES (946, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:50:00');
INSERT INTO `sys_job_log` VALUES (947, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:50:00');
INSERT INTO `sys_job_log` VALUES (948, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:51:00');
INSERT INTO `sys_job_log` VALUES (949, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:52:00');
INSERT INTO `sys_job_log` VALUES (950, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:53:00');
INSERT INTO `sys_job_log` VALUES (951, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:54:00');
INSERT INTO `sys_job_log` VALUES (952, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 15:55:00');
INSERT INTO `sys_job_log` VALUES (953, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:55:00');
INSERT INTO `sys_job_log` VALUES (954, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:56:00');
INSERT INTO `sys_job_log` VALUES (955, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:57:00');
INSERT INTO `sys_job_log` VALUES (956, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 15:58:00');
INSERT INTO `sys_job_log` VALUES (957, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 15:59:00');
INSERT INTO `sys_job_log` VALUES (958, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:00:00');
INSERT INTO `sys_job_log` VALUES (959, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:00:00');
INSERT INTO `sys_job_log` VALUES (960, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:01:00');
INSERT INTO `sys_job_log` VALUES (961, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：116756毫秒', '0', '', '2025-08-24 16:01:56');
INSERT INTO `sys_job_log` VALUES (962, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:02:00');
INSERT INTO `sys_job_log` VALUES (963, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:03:00');
INSERT INTO `sys_job_log` VALUES (964, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:04:00');
INSERT INTO `sys_job_log` VALUES (965, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:05:00');
INSERT INTO `sys_job_log` VALUES (966, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:05:00');
INSERT INTO `sys_job_log` VALUES (967, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:06:00');
INSERT INTO `sys_job_log` VALUES (968, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:07:00');
INSERT INTO `sys_job_log` VALUES (969, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:08:00');
INSERT INTO `sys_job_log` VALUES (970, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:09:00');
INSERT INTO `sys_job_log` VALUES (971, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:10:00');
INSERT INTO `sys_job_log` VALUES (972, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:10:00');
INSERT INTO `sys_job_log` VALUES (973, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:11:00');
INSERT INTO `sys_job_log` VALUES (974, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:12:00');
INSERT INTO `sys_job_log` VALUES (975, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:13:00');
INSERT INTO `sys_job_log` VALUES (976, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:14:00');
INSERT INTO `sys_job_log` VALUES (977, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:15:00');
INSERT INTO `sys_job_log` VALUES (978, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:15:00');
INSERT INTO `sys_job_log` VALUES (979, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:16:00');
INSERT INTO `sys_job_log` VALUES (980, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 16:17:00');
INSERT INTO `sys_job_log` VALUES (981, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:18:00');
INSERT INTO `sys_job_log` VALUES (982, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:19:00');
INSERT INTO `sys_job_log` VALUES (983, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 16:20:00');
INSERT INTO `sys_job_log` VALUES (984, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:20:00');
INSERT INTO `sys_job_log` VALUES (985, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:21:00');
INSERT INTO `sys_job_log` VALUES (986, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:22:00');
INSERT INTO `sys_job_log` VALUES (987, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:23:00');
INSERT INTO `sys_job_log` VALUES (988, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:24:00');
INSERT INTO `sys_job_log` VALUES (989, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:25:00');
INSERT INTO `sys_job_log` VALUES (990, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:25:00');
INSERT INTO `sys_job_log` VALUES (991, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:26:00');
INSERT INTO `sys_job_log` VALUES (992, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:27:00');
INSERT INTO `sys_job_log` VALUES (993, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:28:00');
INSERT INTO `sys_job_log` VALUES (994, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:29:00');
INSERT INTO `sys_job_log` VALUES (995, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 16:30:00');
INSERT INTO `sys_job_log` VALUES (996, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:30:00');
INSERT INTO `sys_job_log` VALUES (997, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 16:31:00');
INSERT INTO `sys_job_log` VALUES (998, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:32:00');
INSERT INTO `sys_job_log` VALUES (999, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:33:00');
INSERT INTO `sys_job_log` VALUES (1000, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:34:00');
INSERT INTO `sys_job_log` VALUES (1001, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:35:00');
INSERT INTO `sys_job_log` VALUES (1002, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:35:00');
INSERT INTO `sys_job_log` VALUES (1003, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:36:00');
INSERT INTO `sys_job_log` VALUES (1004, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:37:00');
INSERT INTO `sys_job_log` VALUES (1005, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:38:00');
INSERT INTO `sys_job_log` VALUES (1006, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:39:00');
INSERT INTO `sys_job_log` VALUES (1007, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：4毫秒', '0', '', '2025-08-24 16:40:00');
INSERT INTO `sys_job_log` VALUES (1008, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：35毫秒', '0', '', '2025-08-24 16:40:00');
INSERT INTO `sys_job_log` VALUES (1009, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:41:00');
INSERT INTO `sys_job_log` VALUES (1010, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:42:00');
INSERT INTO `sys_job_log` VALUES (1011, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:43:00');
INSERT INTO `sys_job_log` VALUES (1012, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:44:00');
INSERT INTO `sys_job_log` VALUES (1013, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:45:00');
INSERT INTO `sys_job_log` VALUES (1014, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:45:00');
INSERT INTO `sys_job_log` VALUES (1015, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:46:00');
INSERT INTO `sys_job_log` VALUES (1016, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:47:00');
INSERT INTO `sys_job_log` VALUES (1017, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:48:00');
INSERT INTO `sys_job_log` VALUES (1018, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 16:49:00');
INSERT INTO `sys_job_log` VALUES (1019, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:50:00');
INSERT INTO `sys_job_log` VALUES (1020, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:50:00');
INSERT INTO `sys_job_log` VALUES (1021, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:51:00');
INSERT INTO `sys_job_log` VALUES (1022, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 16:52:00');
INSERT INTO `sys_job_log` VALUES (1023, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：8毫秒', '0', '', '2025-08-24 16:53:00');
INSERT INTO `sys_job_log` VALUES (1024, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 16:54:00');
INSERT INTO `sys_job_log` VALUES (1025, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 16:55:00');
INSERT INTO `sys_job_log` VALUES (1026, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 16:55:00');
INSERT INTO `sys_job_log` VALUES (1027, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:56:00');
INSERT INTO `sys_job_log` VALUES (1028, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:57:00');
INSERT INTO `sys_job_log` VALUES (1029, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 16:58:00');
INSERT INTO `sys_job_log` VALUES (1030, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-24 17:01:00');
INSERT INTO `sys_job_log` VALUES (1031, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:02:00');
INSERT INTO `sys_job_log` VALUES (1032, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:03:00');
INSERT INTO `sys_job_log` VALUES (1033, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：23毫秒', '0', '', '2025-08-24 17:06:00');
INSERT INTO `sys_job_log` VALUES (1034, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:07:00');
INSERT INTO `sys_job_log` VALUES (1035, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:08:00');
INSERT INTO `sys_job_log` VALUES (1036, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:09:00');
INSERT INTO `sys_job_log` VALUES (1037, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:10:00');
INSERT INTO `sys_job_log` VALUES (1038, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:10:00');
INSERT INTO `sys_job_log` VALUES (1039, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:11:00');
INSERT INTO `sys_job_log` VALUES (1040, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:12:00');
INSERT INTO `sys_job_log` VALUES (1041, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：18毫秒', '0', '', '2025-08-24 17:14:00');
INSERT INTO `sys_job_log` VALUES (1042, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:15:00');
INSERT INTO `sys_job_log` VALUES (1043, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:15:00');
INSERT INTO `sys_job_log` VALUES (1044, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:16:00');
INSERT INTO `sys_job_log` VALUES (1045, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:17:00');
INSERT INTO `sys_job_log` VALUES (1046, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:18:00');
INSERT INTO `sys_job_log` VALUES (1047, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:19:00');
INSERT INTO `sys_job_log` VALUES (1048, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 17:20:00');
INSERT INTO `sys_job_log` VALUES (1049, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:20:00');
INSERT INTO `sys_job_log` VALUES (1050, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：48毫秒', '0', '', '2025-08-24 17:22:01');
INSERT INTO `sys_job_log` VALUES (1051, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:23:00');
INSERT INTO `sys_job_log` VALUES (1052, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:24:00');
INSERT INTO `sys_job_log` VALUES (1053, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:25:00');
INSERT INTO `sys_job_log` VALUES (1054, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:25:00');
INSERT INTO `sys_job_log` VALUES (1055, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:26:00');
INSERT INTO `sys_job_log` VALUES (1056, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:27:00');
INSERT INTO `sys_job_log` VALUES (1057, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:28:00');
INSERT INTO `sys_job_log` VALUES (1058, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:29:00');
INSERT INTO `sys_job_log` VALUES (1059, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:30:00');
INSERT INTO `sys_job_log` VALUES (1060, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:30:00');
INSERT INTO `sys_job_log` VALUES (1061, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：23毫秒', '0', '', '2025-08-24 17:32:00');
INSERT INTO `sys_job_log` VALUES (1062, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 17:33:00');
INSERT INTO `sys_job_log` VALUES (1063, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:34:00');
INSERT INTO `sys_job_log` VALUES (1064, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:35:00');
INSERT INTO `sys_job_log` VALUES (1065, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:35:00');
INSERT INTO `sys_job_log` VALUES (1066, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:36:00');
INSERT INTO `sys_job_log` VALUES (1067, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:37:00');
INSERT INTO `sys_job_log` VALUES (1068, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:38:00');
INSERT INTO `sys_job_log` VALUES (1069, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 17:39:00');
INSERT INTO `sys_job_log` VALUES (1070, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 17:42:00');
INSERT INTO `sys_job_log` VALUES (1071, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:43:00');
INSERT INTO `sys_job_log` VALUES (1072, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:44:00');
INSERT INTO `sys_job_log` VALUES (1073, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:45:00');
INSERT INTO `sys_job_log` VALUES (1074, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:45:00');
INSERT INTO `sys_job_log` VALUES (1075, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:46:00');
INSERT INTO `sys_job_log` VALUES (1076, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:47:00');
INSERT INTO `sys_job_log` VALUES (1077, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:48:00');
INSERT INTO `sys_job_log` VALUES (1078, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:49:00');
INSERT INTO `sys_job_log` VALUES (1079, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 17:50:00');
INSERT INTO `sys_job_log` VALUES (1080, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:50:00');
INSERT INTO `sys_job_log` VALUES (1081, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:51:00');
INSERT INTO `sys_job_log` VALUES (1082, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:52:00');
INSERT INTO `sys_job_log` VALUES (1083, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:53:00');
INSERT INTO `sys_job_log` VALUES (1084, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:54:00');
INSERT INTO `sys_job_log` VALUES (1085, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 17:55:00');
INSERT INTO `sys_job_log` VALUES (1086, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 17:55:00');
INSERT INTO `sys_job_log` VALUES (1087, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 17:56:00');
INSERT INTO `sys_job_log` VALUES (1088, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：12毫秒', '0', '', '2025-08-24 17:59:00');
INSERT INTO `sys_job_log` VALUES (1089, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:00:00');
INSERT INTO `sys_job_log` VALUES (1090, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:00:00');
INSERT INTO `sys_job_log` VALUES (1091, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:01:00');
INSERT INTO `sys_job_log` VALUES (1092, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：96121毫秒', '0', '', '2025-08-24 18:01:36');
INSERT INTO `sys_job_log` VALUES (1093, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:02:00');
INSERT INTO `sys_job_log` VALUES (1094, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:03:00');
INSERT INTO `sys_job_log` VALUES (1095, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:04:00');
INSERT INTO `sys_job_log` VALUES (1096, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:10:07');
INSERT INTO `sys_job_log` VALUES (1097, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 18:10:07');
INSERT INTO `sys_job_log` VALUES (1098, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:10:07');
INSERT INTO `sys_job_log` VALUES (1099, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 18:10:07');
INSERT INTO `sys_job_log` VALUES (1100, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9毫秒', '0', '', '2025-08-24 18:10:07');
INSERT INTO `sys_job_log` VALUES (1101, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:10:07');
INSERT INTO `sys_job_log` VALUES (1102, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：9毫秒', '0', '', '2025-08-24 18:11:04');
INSERT INTO `sys_job_log` VALUES (1103, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:12:00');
INSERT INTO `sys_job_log` VALUES (1104, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:13:00');
INSERT INTO `sys_job_log` VALUES (1105, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:14:00');
INSERT INTO `sys_job_log` VALUES (1106, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:15:00');
INSERT INTO `sys_job_log` VALUES (1107, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 18:15:00');
INSERT INTO `sys_job_log` VALUES (1108, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：46毫秒', '0', '', '2025-08-24 18:16:00');
INSERT INTO `sys_job_log` VALUES (1109, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 18:17:00');
INSERT INTO `sys_job_log` VALUES (1110, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 18:18:00');
INSERT INTO `sys_job_log` VALUES (1111, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:19:00');
INSERT INTO `sys_job_log` VALUES (1112, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:20:00');
INSERT INTO `sys_job_log` VALUES (1113, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:20:00');
INSERT INTO `sys_job_log` VALUES (1114, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:21:00');
INSERT INTO `sys_job_log` VALUES (1115, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:22:00');
INSERT INTO `sys_job_log` VALUES (1116, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:23:00');
INSERT INTO `sys_job_log` VALUES (1117, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:24:00');
INSERT INTO `sys_job_log` VALUES (1118, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:25:00');
INSERT INTO `sys_job_log` VALUES (1119, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:25:00');
INSERT INTO `sys_job_log` VALUES (1120, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 18:26:00');
INSERT INTO `sys_job_log` VALUES (1121, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:27:00');
INSERT INTO `sys_job_log` VALUES (1122, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:28:00');
INSERT INTO `sys_job_log` VALUES (1123, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 18:29:00');
INSERT INTO `sys_job_log` VALUES (1124, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 18:30:00');
INSERT INTO `sys_job_log` VALUES (1125, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:30:00');
INSERT INTO `sys_job_log` VALUES (1126, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 18:31:00');
INSERT INTO `sys_job_log` VALUES (1127, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 18:32:00');
INSERT INTO `sys_job_log` VALUES (1128, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:33:00');
INSERT INTO `sys_job_log` VALUES (1129, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 18:34:00');
INSERT INTO `sys_job_log` VALUES (1130, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:35:00');
INSERT INTO `sys_job_log` VALUES (1131, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:35:00');
INSERT INTO `sys_job_log` VALUES (1132, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:36:00');
INSERT INTO `sys_job_log` VALUES (1133, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:37:00');
INSERT INTO `sys_job_log` VALUES (1134, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:38:00');
INSERT INTO `sys_job_log` VALUES (1135, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:39:00');
INSERT INTO `sys_job_log` VALUES (1136, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:40:00');
INSERT INTO `sys_job_log` VALUES (1137, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:40:00');
INSERT INTO `sys_job_log` VALUES (1138, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:41:00');
INSERT INTO `sys_job_log` VALUES (1139, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:42:00');
INSERT INTO `sys_job_log` VALUES (1140, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:43:00');
INSERT INTO `sys_job_log` VALUES (1141, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:44:00');
INSERT INTO `sys_job_log` VALUES (1142, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:45:00');
INSERT INTO `sys_job_log` VALUES (1143, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 18:45:00');
INSERT INTO `sys_job_log` VALUES (1144, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:46:00');
INSERT INTO `sys_job_log` VALUES (1145, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:47:00');
INSERT INTO `sys_job_log` VALUES (1146, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:48:00');
INSERT INTO `sys_job_log` VALUES (1147, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:49:00');
INSERT INTO `sys_job_log` VALUES (1148, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 18:50:00');
INSERT INTO `sys_job_log` VALUES (1149, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:50:00');
INSERT INTO `sys_job_log` VALUES (1150, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 18:51:00');
INSERT INTO `sys_job_log` VALUES (1151, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:52:00');
INSERT INTO `sys_job_log` VALUES (1152, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:53:00');
INSERT INTO `sys_job_log` VALUES (1153, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:54:00');
INSERT INTO `sys_job_log` VALUES (1154, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 18:55:00');
INSERT INTO `sys_job_log` VALUES (1155, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:55:00');
INSERT INTO `sys_job_log` VALUES (1156, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:56:00');
INSERT INTO `sys_job_log` VALUES (1157, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 18:57:00');
INSERT INTO `sys_job_log` VALUES (1158, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:58:00');
INSERT INTO `sys_job_log` VALUES (1159, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 18:59:00');
INSERT INTO `sys_job_log` VALUES (1160, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 19:00:00');
INSERT INTO `sys_job_log` VALUES (1161, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:00:00');
INSERT INTO `sys_job_log` VALUES (1162, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：18628毫秒', '0', '', '2025-08-24 19:00:18');
INSERT INTO `sys_job_log` VALUES (1163, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:01:00');
INSERT INTO `sys_job_log` VALUES (1164, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:02:00');
INSERT INTO `sys_job_log` VALUES (1165, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:03:00');
INSERT INTO `sys_job_log` VALUES (1166, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:04:00');
INSERT INTO `sys_job_log` VALUES (1167, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:05:00');
INSERT INTO `sys_job_log` VALUES (1168, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:05:00');
INSERT INTO `sys_job_log` VALUES (1169, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:06:00');
INSERT INTO `sys_job_log` VALUES (1170, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:07:00');
INSERT INTO `sys_job_log` VALUES (1171, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:08:00');
INSERT INTO `sys_job_log` VALUES (1172, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 19:09:00');
INSERT INTO `sys_job_log` VALUES (1173, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:10:00');
INSERT INTO `sys_job_log` VALUES (1174, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:10:00');
INSERT INTO `sys_job_log` VALUES (1175, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:11:00');
INSERT INTO `sys_job_log` VALUES (1176, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:12:00');
INSERT INTO `sys_job_log` VALUES (1177, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:13:00');
INSERT INTO `sys_job_log` VALUES (1178, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:14:00');
INSERT INTO `sys_job_log` VALUES (1179, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 19:15:00');
INSERT INTO `sys_job_log` VALUES (1180, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:15:00');
INSERT INTO `sys_job_log` VALUES (1181, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:16:00');
INSERT INTO `sys_job_log` VALUES (1182, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:17:00');
INSERT INTO `sys_job_log` VALUES (1183, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:18:00');
INSERT INTO `sys_job_log` VALUES (1184, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:19:00');
INSERT INTO `sys_job_log` VALUES (1185, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:20:00');
INSERT INTO `sys_job_log` VALUES (1186, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:20:00');
INSERT INTO `sys_job_log` VALUES (1187, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:21:00');
INSERT INTO `sys_job_log` VALUES (1188, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:22:00');
INSERT INTO `sys_job_log` VALUES (1189, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:23:00');
INSERT INTO `sys_job_log` VALUES (1190, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:24:00');
INSERT INTO `sys_job_log` VALUES (1191, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:25:00');
INSERT INTO `sys_job_log` VALUES (1192, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:25:00');
INSERT INTO `sys_job_log` VALUES (1193, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:26:00');
INSERT INTO `sys_job_log` VALUES (1194, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:27:00');
INSERT INTO `sys_job_log` VALUES (1195, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:28:00');
INSERT INTO `sys_job_log` VALUES (1196, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:29:00');
INSERT INTO `sys_job_log` VALUES (1197, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 19:30:00');
INSERT INTO `sys_job_log` VALUES (1198, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:30:00');
INSERT INTO `sys_job_log` VALUES (1199, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:31:00');
INSERT INTO `sys_job_log` VALUES (1200, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 19:32:00');
INSERT INTO `sys_job_log` VALUES (1201, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:33:00');
INSERT INTO `sys_job_log` VALUES (1202, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:34:00');
INSERT INTO `sys_job_log` VALUES (1203, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:35:00');
INSERT INTO `sys_job_log` VALUES (1204, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：16毫秒', '0', '', '2025-08-24 19:35:00');
INSERT INTO `sys_job_log` VALUES (1205, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:36:00');
INSERT INTO `sys_job_log` VALUES (1206, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:37:00');
INSERT INTO `sys_job_log` VALUES (1207, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:38:00');
INSERT INTO `sys_job_log` VALUES (1208, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:39:00');
INSERT INTO `sys_job_log` VALUES (1209, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 19:40:00');
INSERT INTO `sys_job_log` VALUES (1210, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:40:00');
INSERT INTO `sys_job_log` VALUES (1211, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:41:00');
INSERT INTO `sys_job_log` VALUES (1212, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:42:00');
INSERT INTO `sys_job_log` VALUES (1213, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:43:00');
INSERT INTO `sys_job_log` VALUES (1214, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:44:00');
INSERT INTO `sys_job_log` VALUES (1215, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:45:00');
INSERT INTO `sys_job_log` VALUES (1216, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 19:45:00');
INSERT INTO `sys_job_log` VALUES (1217, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:46:00');
INSERT INTO `sys_job_log` VALUES (1218, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:47:00');
INSERT INTO `sys_job_log` VALUES (1219, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 19:48:00');
INSERT INTO `sys_job_log` VALUES (1220, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 19:49:00');
INSERT INTO `sys_job_log` VALUES (1221, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:50:00');
INSERT INTO `sys_job_log` VALUES (1222, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:50:00');
INSERT INTO `sys_job_log` VALUES (1223, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:51:00');
INSERT INTO `sys_job_log` VALUES (1224, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:52:00');
INSERT INTO `sys_job_log` VALUES (1225, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:53:00');
INSERT INTO `sys_job_log` VALUES (1226, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:54:00');
INSERT INTO `sys_job_log` VALUES (1227, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 19:55:00');
INSERT INTO `sys_job_log` VALUES (1228, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:55:00');
INSERT INTO `sys_job_log` VALUES (1229, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:56:00');
INSERT INTO `sys_job_log` VALUES (1230, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:57:00');
INSERT INTO `sys_job_log` VALUES (1231, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 19:58:00');
INSERT INTO `sys_job_log` VALUES (1232, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 19:59:00');
INSERT INTO `sys_job_log` VALUES (1233, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:00:00');
INSERT INTO `sys_job_log` VALUES (1234, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:00:00');
INSERT INTO `sys_job_log` VALUES (1235, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:01:00');
INSERT INTO `sys_job_log` VALUES (1236, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：119173毫秒', '0', '', '2025-08-24 20:01:59');
INSERT INTO `sys_job_log` VALUES (1237, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:02:00');
INSERT INTO `sys_job_log` VALUES (1238, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:03:00');
INSERT INTO `sys_job_log` VALUES (1239, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:04:00');
INSERT INTO `sys_job_log` VALUES (1240, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:05:00');
INSERT INTO `sys_job_log` VALUES (1241, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:05:00');
INSERT INTO `sys_job_log` VALUES (1242, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:06:00');
INSERT INTO `sys_job_log` VALUES (1243, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:07:00');
INSERT INTO `sys_job_log` VALUES (1244, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:08:00');
INSERT INTO `sys_job_log` VALUES (1245, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:09:00');
INSERT INTO `sys_job_log` VALUES (1246, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:10:00');
INSERT INTO `sys_job_log` VALUES (1247, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:10:00');
INSERT INTO `sys_job_log` VALUES (1248, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:11:00');
INSERT INTO `sys_job_log` VALUES (1249, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:12:00');
INSERT INTO `sys_job_log` VALUES (1250, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:13:00');
INSERT INTO `sys_job_log` VALUES (1251, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:14:00');
INSERT INTO `sys_job_log` VALUES (1252, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:15:00');
INSERT INTO `sys_job_log` VALUES (1253, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:15:00');
INSERT INTO `sys_job_log` VALUES (1254, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:16:00');
INSERT INTO `sys_job_log` VALUES (1255, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:17:00');
INSERT INTO `sys_job_log` VALUES (1256, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:18:00');
INSERT INTO `sys_job_log` VALUES (1257, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:19:00');
INSERT INTO `sys_job_log` VALUES (1258, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:20:00');
INSERT INTO `sys_job_log` VALUES (1259, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:20:00');
INSERT INTO `sys_job_log` VALUES (1260, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:21:00');
INSERT INTO `sys_job_log` VALUES (1261, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:22:00');
INSERT INTO `sys_job_log` VALUES (1262, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:23:00');
INSERT INTO `sys_job_log` VALUES (1263, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:24:00');
INSERT INTO `sys_job_log` VALUES (1264, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:25:00');
INSERT INTO `sys_job_log` VALUES (1265, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:25:00');
INSERT INTO `sys_job_log` VALUES (1266, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:26:00');
INSERT INTO `sys_job_log` VALUES (1267, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:27:00');
INSERT INTO `sys_job_log` VALUES (1268, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:28:00');
INSERT INTO `sys_job_log` VALUES (1269, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:29:00');
INSERT INTO `sys_job_log` VALUES (1270, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:30:00');
INSERT INTO `sys_job_log` VALUES (1271, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:30:00');
INSERT INTO `sys_job_log` VALUES (1272, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:31:00');
INSERT INTO `sys_job_log` VALUES (1273, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:32:00');
INSERT INTO `sys_job_log` VALUES (1274, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:33:00');
INSERT INTO `sys_job_log` VALUES (1275, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:34:00');
INSERT INTO `sys_job_log` VALUES (1276, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:35:00');
INSERT INTO `sys_job_log` VALUES (1277, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:35:00');
INSERT INTO `sys_job_log` VALUES (1278, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:36:00');
INSERT INTO `sys_job_log` VALUES (1279, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 20:38:00');
INSERT INTO `sys_job_log` VALUES (1280, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:39:00');
INSERT INTO `sys_job_log` VALUES (1281, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:40:00');
INSERT INTO `sys_job_log` VALUES (1282, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 20:40:00');
INSERT INTO `sys_job_log` VALUES (1283, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:41:00');
INSERT INTO `sys_job_log` VALUES (1284, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:42:00');
INSERT INTO `sys_job_log` VALUES (1285, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:43:00');
INSERT INTO `sys_job_log` VALUES (1286, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:44:00');
INSERT INTO `sys_job_log` VALUES (1287, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 20:45:00');
INSERT INTO `sys_job_log` VALUES (1288, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:45:00');
INSERT INTO `sys_job_log` VALUES (1289, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:46:00');
INSERT INTO `sys_job_log` VALUES (1290, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 20:47:00');
INSERT INTO `sys_job_log` VALUES (1291, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:48:00');
INSERT INTO `sys_job_log` VALUES (1292, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:49:00');
INSERT INTO `sys_job_log` VALUES (1293, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:50:00');
INSERT INTO `sys_job_log` VALUES (1294, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 20:50:00');
INSERT INTO `sys_job_log` VALUES (1295, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:51:00');
INSERT INTO `sys_job_log` VALUES (1296, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:52:00');
INSERT INTO `sys_job_log` VALUES (1297, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:53:00');
INSERT INTO `sys_job_log` VALUES (1298, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:54:00');
INSERT INTO `sys_job_log` VALUES (1299, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 20:55:00');
INSERT INTO `sys_job_log` VALUES (1300, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:55:00');
INSERT INTO `sys_job_log` VALUES (1301, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:56:00');
INSERT INTO `sys_job_log` VALUES (1302, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 20:57:00');
INSERT INTO `sys_job_log` VALUES (1303, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 20:58:00');
INSERT INTO `sys_job_log` VALUES (1304, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 20:59:00');
INSERT INTO `sys_job_log` VALUES (1305, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 21:00:00');
INSERT INTO `sys_job_log` VALUES (1306, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 21:00:00');
INSERT INTO `sys_job_log` VALUES (1307, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:01:00');
INSERT INTO `sys_job_log` VALUES (1308, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 21:02:00');
INSERT INTO `sys_job_log` VALUES (1309, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：133949毫秒', '0', '', '2025-08-24 21:02:13');
INSERT INTO `sys_job_log` VALUES (1310, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:03:00');
INSERT INTO `sys_job_log` VALUES (1311, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 21:04:00');
INSERT INTO `sys_job_log` VALUES (1312, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 21:05:00');
INSERT INTO `sys_job_log` VALUES (1313, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:05:00');
INSERT INTO `sys_job_log` VALUES (1314, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:06:00');
INSERT INTO `sys_job_log` VALUES (1315, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:07:00');
INSERT INTO `sys_job_log` VALUES (1316, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:08:00');
INSERT INTO `sys_job_log` VALUES (1317, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:09:00');
INSERT INTO `sys_job_log` VALUES (1318, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 21:10:00');
INSERT INTO `sys_job_log` VALUES (1319, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:10:00');
INSERT INTO `sys_job_log` VALUES (1320, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 21:11:00');
INSERT INTO `sys_job_log` VALUES (1321, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:12:00');
INSERT INTO `sys_job_log` VALUES (1322, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:13:00');
INSERT INTO `sys_job_log` VALUES (1323, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:14:00');
INSERT INTO `sys_job_log` VALUES (1324, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 21:15:00');
INSERT INTO `sys_job_log` VALUES (1325, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 21:15:00');
INSERT INTO `sys_job_log` VALUES (1326, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 21:16:00');
INSERT INTO `sys_job_log` VALUES (1327, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：33毫秒', '0', '', '2025-08-24 21:21:00');
INSERT INTO `sys_job_log` VALUES (1328, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-24 21:22:00');
INSERT INTO `sys_job_log` VALUES (1329, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:23:00');
INSERT INTO `sys_job_log` VALUES (1330, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 21:24:00');
INSERT INTO `sys_job_log` VALUES (1331, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 21:25:00');
INSERT INTO `sys_job_log` VALUES (1332, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:25:00');
INSERT INTO `sys_job_log` VALUES (1333, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 21:26:00');
INSERT INTO `sys_job_log` VALUES (1334, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 21:27:00');
INSERT INTO `sys_job_log` VALUES (1335, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 21:28:00');
INSERT INTO `sys_job_log` VALUES (1336, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 21:29:00');
INSERT INTO `sys_job_log` VALUES (1337, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 21:56:00');
INSERT INTO `sys_job_log` VALUES (1338, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 21:57:00');
INSERT INTO `sys_job_log` VALUES (1339, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：15毫秒', '0', '', '2025-08-24 22:02:34');
INSERT INTO `sys_job_log` VALUES (1340, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：41毫秒', '0', '', '2025-08-24 22:02:34');
INSERT INTO `sys_job_log` VALUES (1341, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:02:34');
INSERT INTO `sys_job_log` VALUES (1342, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:02:34');
INSERT INTO `sys_job_log` VALUES (1343, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：27毫秒', '0', '', '2025-08-24 22:02:34');
INSERT INTO `sys_job_log` VALUES (1344, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：7毫秒', '0', '', '2025-08-24 22:15:00');
INSERT INTO `sys_job_log` VALUES (1345, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：35毫秒', '0', '', '2025-08-24 22:15:00');
INSERT INTO `sys_job_log` VALUES (1346, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:16:00');
INSERT INTO `sys_job_log` VALUES (1347, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 22:35:17');
INSERT INTO `sys_job_log` VALUES (1348, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 22:35:17');
INSERT INTO `sys_job_log` VALUES (1349, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：42毫秒', '0', '', '2025-08-24 22:35:17');
INSERT INTO `sys_job_log` VALUES (1350, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 22:35:17');
INSERT INTO `sys_job_log` VALUES (1351, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：24毫秒', '0', '', '2025-08-24 22:35:17');
INSERT INTO `sys_job_log` VALUES (1352, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：27毫秒', '0', '', '2025-08-24 22:35:17');
INSERT INTO `sys_job_log` VALUES (1353, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:35:18');
INSERT INTO `sys_job_log` VALUES (1354, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:35:18');
INSERT INTO `sys_job_log` VALUES (1355, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 22:35:18');
INSERT INTO `sys_job_log` VALUES (1356, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：2毫秒', '0', '', '2025-08-24 22:45:00');
INSERT INTO `sys_job_log` VALUES (1357, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-24 22:45:00');
INSERT INTO `sys_job_log` VALUES (1358, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:46:00');
INSERT INTO `sys_job_log` VALUES (1359, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 22:47:00');
INSERT INTO `sys_job_log` VALUES (1360, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:48:00');
INSERT INTO `sys_job_log` VALUES (1361, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:49:00');
INSERT INTO `sys_job_log` VALUES (1362, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 22:50:00');
INSERT INTO `sys_job_log` VALUES (1363, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:50:00');
INSERT INTO `sys_job_log` VALUES (1364, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 22:51:00');
INSERT INTO `sys_job_log` VALUES (1365, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:52:00');
INSERT INTO `sys_job_log` VALUES (1366, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:53:00');
INSERT INTO `sys_job_log` VALUES (1367, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:54:00');
INSERT INTO `sys_job_log` VALUES (1368, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 22:55:00');
INSERT INTO `sys_job_log` VALUES (1369, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 22:55:00');
INSERT INTO `sys_job_log` VALUES (1370, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:56:00');
INSERT INTO `sys_job_log` VALUES (1371, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 22:57:00');
INSERT INTO `sys_job_log` VALUES (1372, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 22:58:00');
INSERT INTO `sys_job_log` VALUES (1373, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 22:59:00');
INSERT INTO `sys_job_log` VALUES (1374, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 23:00:00');
INSERT INTO `sys_job_log` VALUES (1375, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：16毫秒', '0', '', '2025-08-24 23:00:00');
INSERT INTO `sys_job_log` VALUES (1376, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:01:00');
INSERT INTO `sys_job_log` VALUES (1377, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：111459毫秒', '0', '', '2025-08-24 23:01:51');
INSERT INTO `sys_job_log` VALUES (1378, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:02:00');
INSERT INTO `sys_job_log` VALUES (1379, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:03:00');
INSERT INTO `sys_job_log` VALUES (1380, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:04:00');
INSERT INTO `sys_job_log` VALUES (1381, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 23:05:00');
INSERT INTO `sys_job_log` VALUES (1382, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:05:00');
INSERT INTO `sys_job_log` VALUES (1383, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:06:00');
INSERT INTO `sys_job_log` VALUES (1384, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:07:00');
INSERT INTO `sys_job_log` VALUES (1385, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:08:00');
INSERT INTO `sys_job_log` VALUES (1386, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:09:00');
INSERT INTO `sys_job_log` VALUES (1387, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 23:10:00');
INSERT INTO `sys_job_log` VALUES (1388, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:10:00');
INSERT INTO `sys_job_log` VALUES (1389, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:11:00');
INSERT INTO `sys_job_log` VALUES (1390, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:12:00');
INSERT INTO `sys_job_log` VALUES (1391, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:13:00');
INSERT INTO `sys_job_log` VALUES (1392, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:14:00');
INSERT INTO `sys_job_log` VALUES (1393, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 23:15:00');
INSERT INTO `sys_job_log` VALUES (1394, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:15:00');
INSERT INTO `sys_job_log` VALUES (1395, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:16:00');
INSERT INTO `sys_job_log` VALUES (1396, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:17:00');
INSERT INTO `sys_job_log` VALUES (1397, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:18:00');
INSERT INTO `sys_job_log` VALUES (1398, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:19:00');
INSERT INTO `sys_job_log` VALUES (1399, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-24 23:20:00');
INSERT INTO `sys_job_log` VALUES (1400, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:20:00');
INSERT INTO `sys_job_log` VALUES (1401, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:21:00');
INSERT INTO `sys_job_log` VALUES (1402, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:22:00');
INSERT INTO `sys_job_log` VALUES (1403, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:23:00');
INSERT INTO `sys_job_log` VALUES (1404, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 23:24:00');
INSERT INTO `sys_job_log` VALUES (1405, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 23:25:00');
INSERT INTO `sys_job_log` VALUES (1406, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:25:00');
INSERT INTO `sys_job_log` VALUES (1407, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:26:00');
INSERT INTO `sys_job_log` VALUES (1408, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:27:00');
INSERT INTO `sys_job_log` VALUES (1409, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:28:00');
INSERT INTO `sys_job_log` VALUES (1410, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:29:00');
INSERT INTO `sys_job_log` VALUES (1411, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 23:30:00');
INSERT INTO `sys_job_log` VALUES (1412, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-24 23:30:00');
INSERT INTO `sys_job_log` VALUES (1413, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:31:00');
INSERT INTO `sys_job_log` VALUES (1414, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：25毫秒', '0', '', '2025-08-24 23:52:00');
INSERT INTO `sys_job_log` VALUES (1415, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:53:00');
INSERT INTO `sys_job_log` VALUES (1416, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:54:00');
INSERT INTO `sys_job_log` VALUES (1417, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-24 23:55:00');
INSERT INTO `sys_job_log` VALUES (1418, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:55:00');
INSERT INTO `sys_job_log` VALUES (1419, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-24 23:56:00');
INSERT INTO `sys_job_log` VALUES (1420, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-24 23:57:00');
INSERT INTO `sys_job_log` VALUES (1421, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-24 23:58:00');
INSERT INTO `sys_job_log` VALUES (1422, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：12毫秒', '0', '', '2025-08-25 00:03:00');
INSERT INTO `sys_job_log` VALUES (1423, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:04:11');
INSERT INTO `sys_job_log` VALUES (1424, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 00:05:00');
INSERT INTO `sys_job_log` VALUES (1425, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:05:00');
INSERT INTO `sys_job_log` VALUES (1426, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:06:00');
INSERT INTO `sys_job_log` VALUES (1427, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:07:00');
INSERT INTO `sys_job_log` VALUES (1428, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-25 00:08:00');
INSERT INTO `sys_job_log` VALUES (1429, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:09:00');
INSERT INTO `sys_job_log` VALUES (1430, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：5毫秒', '0', '', '2025-08-25 00:10:00');
INSERT INTO `sys_job_log` VALUES (1431, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：26毫秒', '0', '', '2025-08-25 00:10:00');
INSERT INTO `sys_job_log` VALUES (1432, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:11:00');
INSERT INTO `sys_job_log` VALUES (1433, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:12:00');
INSERT INTO `sys_job_log` VALUES (1434, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:13:00');
INSERT INTO `sys_job_log` VALUES (1435, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:14:00');
INSERT INTO `sys_job_log` VALUES (1436, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-25 00:15:00');
INSERT INTO `sys_job_log` VALUES (1437, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:15:00');
INSERT INTO `sys_job_log` VALUES (1438, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:16:00');
INSERT INTO `sys_job_log` VALUES (1439, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:17:00');
INSERT INTO `sys_job_log` VALUES (1440, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：26毫秒', '0', '', '2025-08-25 00:18:00');
INSERT INTO `sys_job_log` VALUES (1441, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 00:20:13');
INSERT INTO `sys_job_log` VALUES (1442, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-25 00:20:13');
INSERT INTO `sys_job_log` VALUES (1443, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:20:13');
INSERT INTO `sys_job_log` VALUES (1444, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:21:00');
INSERT INTO `sys_job_log` VALUES (1445, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 00:22:00');
INSERT INTO `sys_job_log` VALUES (1446, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:23:00');
INSERT INTO `sys_job_log` VALUES (1447, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：36毫秒', '0', '', '2025-08-25 00:26:02');
INSERT INTO `sys_job_log` VALUES (1448, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:27:00');
INSERT INTO `sys_job_log` VALUES (1449, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:28:00');
INSERT INTO `sys_job_log` VALUES (1450, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:29:00');
INSERT INTO `sys_job_log` VALUES (1451, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 00:30:00');
INSERT INTO `sys_job_log` VALUES (1452, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:30:00');
INSERT INTO `sys_job_log` VALUES (1453, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-25 00:31:00');
INSERT INTO `sys_job_log` VALUES (1454, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 00:32:00');
INSERT INTO `sys_job_log` VALUES (1455, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 00:33:00');
INSERT INTO `sys_job_log` VALUES (1456, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 00:34:00');
INSERT INTO `sys_job_log` VALUES (1457, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：12毫秒', '0', '', '2025-08-25 00:39:00');
INSERT INTO `sys_job_log` VALUES (1458, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：5毫秒', '0', '', '2025-08-25 12:25:00');
INSERT INTO `sys_job_log` VALUES (1459, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：36毫秒', '0', '', '2025-08-25 12:25:00');
INSERT INTO `sys_job_log` VALUES (1460, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-25 12:26:00');
INSERT INTO `sys_job_log` VALUES (1461, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 12:27:00');
INSERT INTO `sys_job_log` VALUES (1462, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:28:00');
INSERT INTO `sys_job_log` VALUES (1463, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 12:29:00');
INSERT INTO `sys_job_log` VALUES (1464, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 12:30:00');
INSERT INTO `sys_job_log` VALUES (1465, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 12:30:00');
INSERT INTO `sys_job_log` VALUES (1466, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 12:31:00');
INSERT INTO `sys_job_log` VALUES (1467, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 12:32:00');
INSERT INTO `sys_job_log` VALUES (1468, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:33:00');
INSERT INTO `sys_job_log` VALUES (1469, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:34:00');
INSERT INTO `sys_job_log` VALUES (1470, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：535毫秒', '0', '', '2025-08-25 12:35:15');
INSERT INTO `sys_job_log` VALUES (1471, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：531毫秒', '0', '', '2025-08-25 12:35:15');
INSERT INTO `sys_job_log` VALUES (1472, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:36:00');
INSERT INTO `sys_job_log` VALUES (1473, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:37:00');
INSERT INTO `sys_job_log` VALUES (1474, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：7毫秒', '0', '', '2025-08-25 12:38:11');
INSERT INTO `sys_job_log` VALUES (1475, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:39:00');
INSERT INTO `sys_job_log` VALUES (1476, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：430毫秒', '0', '', '2025-08-25 12:40:11');
INSERT INTO `sys_job_log` VALUES (1477, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：847毫秒', '0', '', '2025-08-25 12:40:11');
INSERT INTO `sys_job_log` VALUES (1478, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:41:00');
INSERT INTO `sys_job_log` VALUES (1479, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:42:02');
INSERT INTO `sys_job_log` VALUES (1480, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:43:00');
INSERT INTO `sys_job_log` VALUES (1481, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 12:45:55');
INSERT INTO `sys_job_log` VALUES (1482, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:45:55');
INSERT INTO `sys_job_log` VALUES (1483, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 12:45:55');
INSERT INTO `sys_job_log` VALUES (1484, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：26毫秒', '0', '', '2025-08-25 12:47:00');
INSERT INTO `sys_job_log` VALUES (1485, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:48:00');
INSERT INTO `sys_job_log` VALUES (1486, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 12:49:00');
INSERT INTO `sys_job_log` VALUES (1487, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 12:50:00');
INSERT INTO `sys_job_log` VALUES (1488, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:50:00');
INSERT INTO `sys_job_log` VALUES (1489, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:51:00');
INSERT INTO `sys_job_log` VALUES (1490, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-25 12:52:59');
INSERT INTO `sys_job_log` VALUES (1491, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:53:00');
INSERT INTO `sys_job_log` VALUES (1492, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:54:00');
INSERT INTO `sys_job_log` VALUES (1493, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 12:55:00');
INSERT INTO `sys_job_log` VALUES (1494, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:55:00');
INSERT INTO `sys_job_log` VALUES (1495, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 12:56:00');
INSERT INTO `sys_job_log` VALUES (1496, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:57:00');
INSERT INTO `sys_job_log` VALUES (1497, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 12:58:00');
INSERT INTO `sys_job_log` VALUES (1498, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:00:00');
INSERT INTO `sys_job_log` VALUES (1499, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-25 13:00:00');
INSERT INTO `sys_job_log` VALUES (1500, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:01:00');
INSERT INTO `sys_job_log` VALUES (1501, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：116219毫秒', '0', '', '2025-08-25 13:01:56');
INSERT INTO `sys_job_log` VALUES (1502, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:02:00');
INSERT INTO `sys_job_log` VALUES (1503, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:03:00');
INSERT INTO `sys_job_log` VALUES (1504, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 13:04:00');
INSERT INTO `sys_job_log` VALUES (1505, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:05:00');
INSERT INTO `sys_job_log` VALUES (1506, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 13:05:00');
INSERT INTO `sys_job_log` VALUES (1507, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:06:00');
INSERT INTO `sys_job_log` VALUES (1508, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:07:00');
INSERT INTO `sys_job_log` VALUES (1509, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 13:08:00');
INSERT INTO `sys_job_log` VALUES (1510, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:09:00');
INSERT INTO `sys_job_log` VALUES (1511, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1512, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：12毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1513, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1514, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：5毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1515, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1516, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1517, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1518, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1519, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1520, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1521, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1522, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1523, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1524, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1525, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1526, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1527, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1528, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1529, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1530, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1531, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1532, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1533, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1534, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1535, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1536, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1537, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1538, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1539, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：10毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1540, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1541, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1542, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:25');
INSERT INTO `sys_job_log` VALUES (1543, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1544, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1545, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1546, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：10毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1547, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1548, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1549, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1550, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1551, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：1毫秒', '0', '', '2025-08-25 13:43:26');
INSERT INTO `sys_job_log` VALUES (1552, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：4毫秒', '0', '', '2025-08-25 13:45:00');
INSERT INTO `sys_job_log` VALUES (1553, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：36毫秒', '0', '', '2025-08-25 13:45:00');
INSERT INTO `sys_job_log` VALUES (1554, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:46:00');
INSERT INTO `sys_job_log` VALUES (1555, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:47:00');
INSERT INTO `sys_job_log` VALUES (1556, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 13:48:00');
INSERT INTO `sys_job_log` VALUES (1557, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:49:00');
INSERT INTO `sys_job_log` VALUES (1558, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:50:00');
INSERT INTO `sys_job_log` VALUES (1559, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:50:00');
INSERT INTO `sys_job_log` VALUES (1560, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：25毫秒', '0', '', '2025-08-25 13:51:00');
INSERT INTO `sys_job_log` VALUES (1561, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 13:52:00');
INSERT INTO `sys_job_log` VALUES (1562, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:53:00');
INSERT INTO `sys_job_log` VALUES (1563, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 13:54:00');
INSERT INTO `sys_job_log` VALUES (1564, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 13:55:00');
INSERT INTO `sys_job_log` VALUES (1565, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:55:00');
INSERT INTO `sys_job_log` VALUES (1566, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 13:56:00');
INSERT INTO `sys_job_log` VALUES (1567, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:57:00');
INSERT INTO `sys_job_log` VALUES (1568, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:58:00');
INSERT INTO `sys_job_log` VALUES (1569, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 13:59:00');
INSERT INTO `sys_job_log` VALUES (1570, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：1毫秒', '0', '', '2025-08-25 14:00:00');
INSERT INTO `sys_job_log` VALUES (1571, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：23毫秒', '0', '', '2025-08-25 14:00:00');
INSERT INTO `sys_job_log` VALUES (1572, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 14:01:00');
INSERT INTO `sys_job_log` VALUES (1573, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 14:02:00');
INSERT INTO `sys_job_log` VALUES (1574, '邮件统计数据同步', 'DEFAULT', 'emailStatisticsTask.syncEmailStatistics', '邮件统计数据同步 总共耗时：125770毫秒', '0', '', '2025-08-25 14:02:05');
INSERT INTO `sys_job_log` VALUES (1575, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:03:00');
INSERT INTO `sys_job_log` VALUES (1576, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:04:00');
INSERT INTO `sys_job_log` VALUES (1577, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 14:05:00');
INSERT INTO `sys_job_log` VALUES (1578, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 14:05:00');
INSERT INTO `sys_job_log` VALUES (1579, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：6毫秒', '0', '', '2025-08-25 14:06:00');
INSERT INTO `sys_job_log` VALUES (1580, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:07:00');
INSERT INTO `sys_job_log` VALUES (1581, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:08:00');
INSERT INTO `sys_job_log` VALUES (1582, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 14:09:00');
INSERT INTO `sys_job_log` VALUES (1583, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 14:10:00');
INSERT INTO `sys_job_log` VALUES (1584, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 14:10:00');
INSERT INTO `sys_job_log` VALUES (1585, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:11:00');
INSERT INTO `sys_job_log` VALUES (1586, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：2毫秒', '0', '', '2025-08-25 14:12:00');
INSERT INTO `sys_job_log` VALUES (1587, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:13:00');
INSERT INTO `sys_job_log` VALUES (1588, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:14:00');
INSERT INTO `sys_job_log` VALUES (1589, 'EmailStatisticsSync', 'DEFAULT', 'emailTaskScheduler.syncEmailStatistics()', 'EmailStatisticsSync 总共耗时：0毫秒', '0', '', '2025-08-25 14:15:00');
INSERT INTO `sys_job_log` VALUES (1590, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:15:00');
INSERT INTO `sys_job_log` VALUES (1591, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：3毫秒', '0', '', '2025-08-25 14:16:00');
INSERT INTO `sys_job_log` VALUES (1592, 'EmailTaskScheduler', 'DEFAULT', 'emailTaskScheduler.checkPendingEmailTasks()', 'EmailTaskScheduler 总共耗时：4毫秒', '0', '', '2025-08-25 14:17:00');

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor`  (
  `info_id` bigint NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作系统',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '提示消息',
  `login_time` datetime NULL DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`) USING BTREE,
  INDEX `idx_sys_logininfor_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_logininfor_lt`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 153 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '系统访问记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES (100, 'adminmanta', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2025-08-10 21:58:06');
INSERT INTO `sys_logininfor` VALUES (101, 'adminmanta', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2025-08-10 21:58:20');
INSERT INTO `sys_logininfor` VALUES (102, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-10 21:58:30');
INSERT INTO `sys_logininfor` VALUES (103, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-10 23:42:55');
INSERT INTO `sys_logininfor` VALUES (104, 'adminmanta', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '用户不存在/密码错误', '2025-08-14 09:08:43');
INSERT INTO `sys_logininfor` VALUES (105, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '验证码错误', '2025-08-14 09:08:53');
INSERT INTO `sys_logininfor` VALUES (106, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-14 09:08:57');
INSERT INTO `sys_logininfor` VALUES (107, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-14 11:21:14');
INSERT INTO `sys_logininfor` VALUES (108, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-14 15:00:45');
INSERT INTO `sys_logininfor` VALUES (109, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-14 22:23:36');
INSERT INTO `sys_logininfor` VALUES (110, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '验证码已失效', '2025-08-15 16:29:21');
INSERT INTO `sys_logininfor` VALUES (111, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-15 16:29:24');
INSERT INTO `sys_logininfor` VALUES (112, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-15 20:22:49');
INSERT INTO `sys_logininfor` VALUES (113, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-16 10:35:03');
INSERT INTO `sys_logininfor` VALUES (114, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '验证码已失效', '2025-08-16 15:52:23');
INSERT INTO `sys_logininfor` VALUES (115, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-16 15:52:27');
INSERT INTO `sys_logininfor` VALUES (116, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-17 20:37:22');
INSERT INTO `sys_logininfor` VALUES (117, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '验证码已失效', '2025-08-17 22:37:28');
INSERT INTO `sys_logininfor` VALUES (118, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-17 22:37:35');
INSERT INTO `sys_logininfor` VALUES (119, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '1', '验证码错误', '2025-08-18 00:32:40');
INSERT INTO `sys_logininfor` VALUES (120, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 00:32:45');
INSERT INTO `sys_logininfor` VALUES (121, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 12:32:08');
INSERT INTO `sys_logininfor` VALUES (122, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 13:04:29');
INSERT INTO `sys_logininfor` VALUES (123, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 14:13:22');
INSERT INTO `sys_logininfor` VALUES (124, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 21:20:28');
INSERT INTO `sys_logininfor` VALUES (125, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 22:47:45');
INSERT INTO `sys_logininfor` VALUES (126, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-18 23:43:02');
INSERT INTO `sys_logininfor` VALUES (127, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-19 08:52:51');
INSERT INTO `sys_logininfor` VALUES (128, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-19 09:46:14');
INSERT INTO `sys_logininfor` VALUES (129, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-19 10:27:45');
INSERT INTO `sys_logininfor` VALUES (130, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-19 10:59:06');
INSERT INTO `sys_logininfor` VALUES (131, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-20 08:58:26');
INSERT INTO `sys_logininfor` VALUES (132, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-20 12:22:05');
INSERT INTO `sys_logininfor` VALUES (133, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '退出成功', '2025-08-20 12:51:06');
INSERT INTO `sys_logininfor` VALUES (134, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-20 12:53:18');
INSERT INTO `sys_logininfor` VALUES (135, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '退出成功', '2025-08-20 13:06:04');
INSERT INTO `sys_logininfor` VALUES (136, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-20 13:06:19');
INSERT INTO `sys_logininfor` VALUES (137, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-20 17:55:04');
INSERT INTO `sys_logininfor` VALUES (138, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 08:32:42');
INSERT INTO `sys_logininfor` VALUES (139, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '退出成功', '2025-08-21 08:43:42');
INSERT INTO `sys_logininfor` VALUES (140, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 08:43:46');
INSERT INTO `sys_logininfor` VALUES (141, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 10:52:40');
INSERT INTO `sys_logininfor` VALUES (142, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 16:48:05');
INSERT INTO `sys_logininfor` VALUES (143, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 16:48:40');
INSERT INTO `sys_logininfor` VALUES (144, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '退出成功', '2025-08-21 16:51:36');
INSERT INTO `sys_logininfor` VALUES (145, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 16:51:43');
INSERT INTO `sys_logininfor` VALUES (146, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-21 23:01:40');
INSERT INTO `sys_logininfor` VALUES (147, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-22 12:33:16');
INSERT INTO `sys_logininfor` VALUES (148, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-24 07:41:05');
INSERT INTO `sys_logininfor` VALUES (149, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '退出成功', '2025-08-24 19:13:31');
INSERT INTO `sys_logininfor` VALUES (150, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-24 19:13:43');
INSERT INTO `sys_logininfor` VALUES (151, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-24 21:55:36');
INSERT INTO `sys_logininfor` VALUES (152, 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2025-08-25 12:25:29');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父菜单ID',
  `order_num` int NULL DEFAULT 0 COMMENT '显示顺序',
  `path` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '路由名称',
  `is_frame` int NULL DEFAULT 1 COMMENT '是否为外链（0是 1否）',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3026 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, '系统管理', 0, 6, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-08-03 19:54:26', '', NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES (2, '系统监控', 0, 7, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2025-08-03 19:54:26', '', NULL, '系统监控目录');
INSERT INTO `sys_menu` VALUES (3, '系统工具', 0, 8, 'tool', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2025-08-03 19:54:26', '', NULL, '系统工具目录');
INSERT INTO `sys_menu` VALUES (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-08-03 19:54:26', '', NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-08-03 19:54:26', '', NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-08-03 19:54:26', '', NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-08-03 19:54:26', '', NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-08-03 19:54:26', '', NULL, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2025-08-03 19:54:26', '', NULL, '字典管理菜单');
INSERT INTO `sys_menu` VALUES (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-08-03 19:54:26', '', NULL, '参数设置菜单');
INSERT INTO `sys_menu` VALUES (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2025-08-03 19:54:26', '', NULL, '通知公告菜单');
INSERT INTO `sys_menu` VALUES (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', '2025-08-03 19:54:26', '', NULL, '日志管理菜单');
INSERT INTO `sys_menu` VALUES (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-08-03 19:54:26', '', NULL, '在线用户菜单');
INSERT INTO `sys_menu` VALUES (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-08-03 19:54:26', '', NULL, '定时任务菜单');
INSERT INTO `sys_menu` VALUES (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-08-03 19:54:26', '', NULL, '数据监控菜单');
INSERT INTO `sys_menu` VALUES (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-08-03 19:54:26', '', NULL, '服务监控菜单');
INSERT INTO `sys_menu` VALUES (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-08-03 19:54:26', '', NULL, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES (114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-08-03 19:54:26', '', NULL, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES (115, '表单构建', 3, 1, 'build', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-08-03 19:54:26', '', NULL, '表单构建菜单');
INSERT INTO `sys_menu` VALUES (116, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-08-03 19:54:26', '', NULL, '代码生成菜单');
INSERT INTO `sys_menu` VALUES (117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-08-03 19:54:26', '', NULL, '系统接口菜单');
INSERT INTO `sys_menu` VALUES (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-08-03 19:54:26', '', NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-08-03 19:54:26', '', NULL, '登录日志菜单');
INSERT INTO `sys_menu` VALUES (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1019, '部门删除', 103, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1055, '生成查询', 116, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1056, '生成修改', 116, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1057, '生成删除', 116, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1058, '导入代码', 116, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1059, '预览代码', 116, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (1060, '生成代码', 116, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2000, '个人邮件', 0, 2, 'personal', NULL, '', 'personal', 1, 0, 'M', '0', '0', '', 'email', 'admin', '2025-08-21 09:10:25', '', NULL, '个人邮件管理');
INSERT INTO `sys_menu` VALUES (2001, '收件箱', 2000, 1, 'inbox', 'email/personal/inbox', '', 'inbox', 1, 0, 'C', '0', '0', 'email:personal:inbox:list', 'message', 'admin', '2025-08-21 09:10:25', '', NULL, '收件箱管理');
INSERT INTO `sys_menu` VALUES (2002, '收件箱查询', 2001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:inbox:query', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2003, '收件箱删除', 2001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:inbox:remove', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2004, '发件箱', 2000, 2, 'sent', 'email/personal/sent', '', 'sent', 1, 0, 'C', '0', '0', 'email:personal:sent:list', 'upload', 'admin', '2025-08-21 09:10:25', '', NULL, '发件箱管理');
INSERT INTO `sys_menu` VALUES (2005, '发件箱查询', 2004, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:sent:query', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2006, '发件箱删除', 2004, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:sent:remove', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2007, '星标邮件', 2000, 3, 'starred', 'email/personal/starred', '', 'starred', 1, 0, 'C', '0', '0', 'email:personal:starred:list', 'star', 'admin', '2025-08-21 09:10:25', '', NULL, '星标邮件管理');
INSERT INTO `sys_menu` VALUES (2008, '星标邮件查询', 2007, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:starred:query', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2009, '星标邮件删除', 2007, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:starred:remove', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2010, '已删除', 2000, 4, 'deleted', 'email/personal/deleted', '', 'deleted', 1, 0, 'C', '0', '0', 'email:personal:deleted:list', 'edit', 'admin', '2025-08-21 09:10:25', '', NULL, '已删除邮件管理');
INSERT INTO `sys_menu` VALUES (2011, '已删除查询', 2010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:deleted:query', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2012, '已删除恢复', 2010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:deleted:restore', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2013, '已删除彻底删除', 2010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:personal:deleted:delete', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2020, '邮件管理', 0, 4, 'management', NULL, '', 'management', 1, 0, 'M', '0', '0', '', 'system', 'admin', '2025-08-21 09:10:25', '', NULL, '邮件管理');
INSERT INTO `sys_menu` VALUES (2021, '发件人管理', 2020, 1, 'account', 'email/account/index', '', 'account', 1, 0, 'C', '0', '0', 'email:account:list', 'user', 'admin', '2025-08-21 09:10:25', '', NULL, '发件人管理');
INSERT INTO `sys_menu` VALUES (2022, '发件人查询', 2021, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:query', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2023, '发件人新增', 2021, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:add', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2024, '发件人修改', 2021, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:edit', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2025, '发件人删除', 2021, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:remove', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2026, '发件人导出', 2021, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:account:export', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2027, '收件人管理', 2020, 2, 'contact', 'email/contact/index', '', 'contact', 1, 0, 'C', '0', '0', 'email:contact:list', 'people', 'admin', '2025-08-21 09:10:25', '', NULL, '收件人管理');
INSERT INTO `sys_menu` VALUES (2028, '收件人查询', 2027, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:query', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2029, '收件人新增', 2027, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:add', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2030, '收件人修改', 2027, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:edit', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2031, '收件人删除', 2027, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:remove', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2032, '收件人导出', 2027, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:export', '#', 'admin', '2025-08-21 09:10:25', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2033, '收件人导入', 2027, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:contact:import', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2040, '群组管理', 2020, 3, 'group', 'email/group/index', '', 'group', 1, 0, 'C', '0', '0', 'email:group:list', 'peoples', 'admin', '2025-08-21 09:10:26', '', NULL, '群组管理');
INSERT INTO `sys_menu` VALUES (2041, '群组查询', 2040, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:query', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2042, '群组新增', 2040, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:add', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2043, '群组修改', 2040, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:edit', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2044, '群组删除', 2040, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:remove', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2045, '群组导出', 2040, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:group:export', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2050, '标签管理', 2020, 4, 'tag', 'email/tag/index', '', 'tag', 1, 0, 'C', '0', '0', 'email:tag:list', 'list', 'admin', '2025-08-21 09:10:26', '', NULL, '标签管理');
INSERT INTO `sys_menu` VALUES (2051, '标签查询', 2050, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:query', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2052, '标签新增', 2050, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:add', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2053, '标签修改', 2050, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:edit', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2054, '标签删除', 2050, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:remove', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2055, '标签导出', 2050, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:tag:export', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2060, '邮件模板', 2020, 5, 'template', 'email/template/index', '', 'template', 1, 0, 'C', '0', '0', 'email:template:list', 'documentation', 'admin', '2025-08-21 09:10:26', '', NULL, '邮件模板管理');
INSERT INTO `sys_menu` VALUES (2061, '模板查询', 2060, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:query', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2062, '模板新增', 2060, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:add', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2063, '模板修改', 2060, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:edit', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2064, '模板删除', 2060, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:remove', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2065, '模板导出', 2060, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:export', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2066, '模板预览', 2060, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:template:preview', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2070, '批量操作', 0, 3, 'batch', NULL, '', 'batch', 1, 0, 'M', '0', '0', '', 'tool', 'admin', '2025-08-21 09:10:26', '', NULL, '批量操作');
INSERT INTO `sys_menu` VALUES (2071, '导入收件人', 2070, 1, 'import', 'email/batch/import', '', 'import', 1, 0, 'C', '0', '0', 'email:batch:import:list', 'upload', 'admin', '2025-08-21 09:10:26', '', NULL, '批量导入收件人');
INSERT INTO `sys_menu` VALUES (2072, '导入查询', 2071, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:query', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2073, '导入新增', 2071, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:add', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2074, '导入删除', 2071, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:remove', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2075, '导入导出', 2071, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:import:export', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2080, '邮件批量发送', 2070, 2, 'send', 'email/batch/send', '', 'send', 1, 0, 'C', '0', '0', 'email:batch:send:list', 'upload', 'admin', '2025-08-21 09:10:26', '', NULL, '邮件批量发送');
INSERT INTO `sys_menu` VALUES (2081, '开始发送', 2080, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:batch:send:create', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2091, '统计查询', 2090, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:query', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (2092, '统计导出', 2090, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:statistics:export', '#', 'admin', '2025-08-21 09:10:26', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3000, '邮件监控', 0, 5, 'email-monitor', NULL, '', 'email-monitor', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', '2025-08-24 18:33:01', '', NULL, '邮件监控管理');
INSERT INTO `sys_menu` VALUES (3001, '任务管理', 3000, 1, 'task', 'email/task/index', '', 'task', 1, 0, 'C', '0', '0', 'email:task:list', 'list', 'admin', '2025-08-24 18:33:01', '', NULL, '邮件任务管理');
INSERT INTO `sys_menu` VALUES (3002, '任务管理查询', 3001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:query', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3003, '任务管理新增', 3001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:add', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3004, '任务管理修改', 3001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:edit', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3005, '任务管理删除', 3001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:remove', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3006, '任务重新执行', 3001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:restart', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3007, '任务停止', 3001, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:stop', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3008, '任务复制', 3001, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:copy', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3009, '执行记录', 3001, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:execution', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3010, '邮件追踪', 3000, 2, 'track-record', 'email/trackRecord/index', '', 'track-record', 1, 0, 'C', '0', '0', 'email:track:list', 'search', 'admin', '2025-08-24 18:33:01', '', NULL, '邮件追踪记录管理');
INSERT INTO `sys_menu` VALUES (3011, '邮件追踪查询', 3010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:query', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3012, '邮件追踪新增', 3010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:add', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3013, '邮件追踪修改', 3010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:edit', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3014, '邮件追踪删除', 3010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:remove', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3015, '邮件追踪导出', 3010, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:export', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3020, '邮件服务监控', 3000, 3, 'monitor', 'email/monitor/index', '', 'monitor', 1, 0, 'C', '0', '0', 'email:monitor:list', 'monitor', 'admin', '2025-08-24 18:33:01', '', NULL, '邮件服务监控管理');
INSERT INTO `sys_menu` VALUES (3021, '邮件服务监控查询', 3020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:query', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3022, '邮件服务监控新增', 3020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:add', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3023, '邮件服务监控修改', 3020, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:edit', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3024, '邮件服务监控删除', 3020, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:remove', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');
INSERT INTO `sys_menu` VALUES (3025, '邮件服务监控导出', 3020, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:export', '#', 'admin', '2025-08-24 18:33:01', '', NULL, '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice`  (
  `notice_id` int NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob NULL COMMENT '公告内容',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '通知公告表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES (1, '温馨提醒：2018-07-01 若依新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-08-03 19:54:27', '', NULL, '管理员');
INSERT INTO `sys_notice` VALUES (2, '维护通知：2018-07-01 若依系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2025-08-03 19:54:27', '', NULL, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log`  (
  `oper_id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '模块标题',
  `business_type` int NULL DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求方式',
  `operator_type` int NULL DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '返回参数',
  `status` int NULL DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint NULL DEFAULT 0 COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`) USING BTREE,
  INDEX `idx_sys_oper_log_bt`(`business_type` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_s`(`status` ASC) USING BTREE,
  INDEX `idx_sys_oper_log_ot`(`oper_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 193 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES (100, '联系人', 1, 'com.ruoyi.web.controller.email.EmailContactController.add()', 'POST', 1, 'admin', '研发部门', '/email/contact', '127.0.0.1', '内网IP', '{\"age\":4,\"email\":\"18286766@qq.com\",\"followersCount\":0,\"name\":\"test\",\"params\":{},\"status\":\"0\"}', NULL, 1, 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'deleted\' in \'class com.ruoyi.system.domain.email.EmailContact\'', '2025-08-15 16:39:18', 35);
INSERT INTO `sys_oper_log` VALUES (101, '邮件模板', 1, 'com.ruoyi.web.controller.email.EmailTemplateController.add()', 'POST', 1, 'admin', '研发部门', '/email/template', '127.0.0.1', '内网IP', '{\"content\":\"这是推广邮件\",\"params\":{},\"subject\":\"test\",\"templateName\":\"test\",\"templateType\":\"product\"}', NULL, 1, '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'template_type\' at row 1\r\n### The error may exist in file [D:\\vue\\email-manager-backend\\ruoyi-system\\target\\classes\\mapper\\email\\EmailTemplateMapper.xml]\r\n### The error may involve com.ruoyi.system.mapper.email.EmailTemplateMapper.insertEmailTemplate-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into email_template          ( template_name,             template_type,             subject,             content )           values ( ?,             ?,             ?,             ? )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'template_type\' at row 1\n; Data truncation: Data too long for column \'template_type\' at row 1; nested exception is com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'template_type\' at row 1', '2025-08-18 12:34:37', 26);
INSERT INTO `sys_oper_log` VALUES (102, '标记邮件已读', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsRead()', 'PUT', 1, 'admin', '研发部门', '/email/personal/read/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 09:19:57', 88);
INSERT INTO `sys_oper_log` VALUES (103, '标记邮件已读', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsRead()', 'PUT', 1, 'admin', '研发部门', '/email/personal/read/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 09:20:00', 39);
INSERT INTO `sys_oper_log` VALUES (104, '标记邮件重要', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsImportant()', 'PUT', 1, 'admin', '研发部门', '/email/personal/important/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 11:30:41', 32);
INSERT INTO `sys_oper_log` VALUES (105, '标记邮件重要', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsImportant()', 'PUT', 1, 'admin', '研发部门', '/email/personal/important/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 11:30:43', 3);
INSERT INTO `sys_oper_log` VALUES (106, '标记邮件重要', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsImportant()', 'PUT', 1, 'admin', '研发部门', '/email/personal/important/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 11:30:50', 3);
INSERT INTO `sys_oper_log` VALUES (107, '标记邮件重要', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsImportant()', 'PUT', 1, 'admin', '研发部门', '/email/personal/important/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 11:30:58', 3);
INSERT INTO `sys_oper_log` VALUES (108, '标记邮件星标', 2, 'com.ruoyi.web.controller.email.EmailPersonalController.markAsStarred()', 'PUT', 1, 'admin', '研发部门', '/email/personal/star/8', '127.0.0.1', '内网IP', '8', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 11:31:19', 3);
INSERT INTO `sys_oper_log` VALUES (109, '个人邮件', 3, 'com.ruoyi.web.controller.email.EmailPersonalController.remove()', 'DELETE', 1, 'admin', '研发部门', '/email/personal/1', '127.0.0.1', '内网IP', '[1]', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 11:32:45', 24);
INSERT INTO `sys_oper_log` VALUES (110, '邮件联系人', 1, 'com.ruoyi.web.controller.email.EmailContactController.add()', 'POST', 1, 'admin', '研发部门', '/email/contact', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":1,\"company\":\"\",\"createTime\":\"2025-08-19 11:43:37\",\"email\":\"182867664@qq.com\",\"gender\":\"0\",\"groupId\":1,\"level\":\"1\",\"name\":\"哒哒\",\"params\":{},\"remark\":\"\",\"replyRate\":null,\"tags\":\"\"}', NULL, 1, 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'socialMedia\' in \'class com.ruoyi.system.domain.email.EmailContact\'', '2025-08-19 11:43:37', 68);
INSERT INTO `sys_oper_log` VALUES (111, '邮件联系人', 1, 'com.ruoyi.web.controller.email.EmailContactController.add()', 'POST', 1, 'admin', '研发部门', '/email/contact', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":1,\"company\":\"\",\"createTime\":\"2025-08-19 11:45:01\",\"email\":\"182867664@qq.com\",\"gender\":\"0\",\"groupId\":1,\"level\":\"1\",\"name\":\"哒哒\",\"params\":{},\"remark\":\"\",\"replyRate\":null,\"tags\":\"\"}', NULL, 1, 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'socialMedia\' in \'class com.ruoyi.system.domain.email.EmailContact\'', '2025-08-19 11:45:01', 47);
INSERT INTO `sys_oper_log` VALUES (112, '邮件联系人', 1, 'com.ruoyi.web.controller.email.EmailContactController.add()', 'POST', 1, 'admin', '研发部门', '/email/contact', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":1,\"company\":\"\",\"contactId\":1,\"createTime\":\"2025-08-19 13:51:09\",\"email\":\"182867664@qq.com\",\"gender\":\"0\",\"level\":\"\",\"name\":\"哒哒\",\"params\":{},\"remark\":\"\",\"replyRate\":null,\"tags\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-19 13:51:09', 78);
INSERT INTO `sys_oper_log` VALUES (113, '邮箱账号', 1, 'com.ruoyi.web.controller.email.EmailAccountController.add()', 'POST', 1, 'admin', '研发部门', '/email/account', '127.0.0.1', '内网IP', '{\"accountId\":6,\"accountName\":\"my\",\"createTime\":\"2025-08-20 12:30:20\",\"dailyLimit\":100,\"emailAddress\":\"182867664@qq.com\",\"params\":{},\"smtpHost\":\"smtp.qq.com\",\"smtpPort\":465,\"smtpSsl\":\"1\",\"status\":\"0\",\"usedCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-20 12:30:20', 138);
INSERT INTO `sys_oper_log` VALUES (114, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 09:04:49\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"\",\"taskName\":\"test\",\"templateId\":3}', '{\"msg\":\"创建发送任务失败：\\r\\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'recipient_type\' at row 1\\r\\n### The error may exist in file [D:\\\\vue\\\\email-manager-backend\\\\ruoyi-system\\\\target\\\\classes\\\\mapper\\\\system\\\\email\\\\EmailSendTaskMapper.xml]\\r\\n### The error may involve com.ruoyi.system.mapper.email.EmailSendTaskMapper.insertEmailSendTask-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into email_send_task          ( task_name,             template_id,                          content,             recipient_type,                                       send_interval,                                                                                                                     create_by,             create_time )           values ( ?,             ?,                          ?,             ?,                                       ?,                                                                                                                     ?,             ? )\\r\\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'recipient_type\' at row 1\\n; Data truncation: Data too long for column \'recipient_type\' at row 1; nested exception is com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'recipient_type\' at row 1\",\"code\":500}', 0, NULL, '2025-08-21 09:04:49', 191);
INSERT INTO `sys_oper_log` VALUES (115, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 09:11:20\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"\",\"taskName\":\"test\",\"templateId\":2}', '{\"msg\":\"创建发送任务失败：\\r\\n### Error updating database.  Cause: java.sql.SQLException: Field \'subject\' doesn\'t have a default value\\r\\n### The error may exist in file [D:\\\\vue\\\\email-manager-backend\\\\ruoyi-system\\\\target\\\\classes\\\\mapper\\\\system\\\\email\\\\EmailSendTaskMapper.xml]\\r\\n### The error may involve com.ruoyi.system.mapper.email.EmailSendTaskMapper.insertEmailSendTask-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into email_send_task          ( task_name,             template_id,                          content,             recipient_type,                                       send_mode,             send_interval,                                                                                                                     create_by,             create_time )           values ( ?,             ?,                          ?,             ?,                                       ?,             ?,                                                                                                                     ?,             ? )\\r\\n### Cause: java.sql.SQLException: Field \'subject\' doesn\'t have a default value\\n; Field \'subject\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'subject\' doesn\'t have a default value\",\"code\":500}', 0, NULL, '2025-08-21 09:11:21', 148);
INSERT INTO `sys_oper_log` VALUES (116, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 09:16:09\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"邀请您参加重要会议\",\"taskId\":1,\"taskName\":\"test\",\"templateId\":2}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 09:16:09\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"邀请您参加重要会议\",\"taskId\":1,\"taskName\":\"test\",\"templateId\":2}}', 0, NULL, '2025-08-21 09:16:10', 127);
INSERT INTO `sys_oper_log` VALUES (117, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 11:41:11\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":2,\"taskName\":\"test\",\"templateId\":1}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 11:41:11\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":2,\"taskName\":\"test\",\"templateId\":1}}', 0, NULL, '2025-08-21 11:41:12', 217);
INSERT INTO `sys_oper_log` VALUES (118, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"test\",\"templateId\":1}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-21 11:50:38', 42);
INSERT INTO `sys_oper_log` VALUES (119, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 11:50:45\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":3,\"taskName\":\"test2\",\"templateId\":1}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 11:50:45\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":3,\"taskName\":\"test2\",\"templateId\":1}}', 0, NULL, '2025-08-21 11:52:05', 78130);
INSERT INTO `sys_oper_log` VALUES (120, '邮箱账号', 2, 'com.ruoyi.web.controller.email.EmailAccountController.edit()', 'PUT', 1, 'admin', '研发部门', '/email/account', '127.0.0.1', '内网IP', '{\"accountId\":6,\"accountName\":\"my\",\"createBy\":\"\",\"createTime\":\"2025-08-20 12:30:20\",\"dailyLimit\":100,\"emailAddress\":\"182867664@qq.com\",\"params\":{},\"smtpHost\":\"smtp.qq.com\",\"smtpPort\":465,\"smtpSsl\":\"1\",\"status\":\"0\",\"updateBy\":\"\",\"updateTime\":\"2025-08-21 12:29:57\",\"usedCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-21 12:29:58', 80);
INSERT INTO `sys_oper_log` VALUES (121, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:30:52\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"邀请您参加重要会议\",\"taskId\":4,\"taskName\":\"aaa\",\"templateId\":2}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"1\",\"content\":\"<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:30:52\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"邀请您参加重要会议\",\"taskId\":4,\"taskName\":\"aaa\",\"templateId\":2}}', 0, NULL, '2025-08-21 12:31:12', 12696);
INSERT INTO `sys_oper_log` VALUES (122, '邮箱账号', 2, 'com.ruoyi.web.controller.email.EmailAccountController.edit()', 'PUT', 1, 'admin', '研发部门', '/email/account', '127.0.0.1', '内网IP', '{\"accountId\":1,\"accountName\":\"Gmail测试账号\",\"createBy\":\"\",\"createTime\":\"2025-08-20 09:10:16\",\"dailyLimit\":100,\"emailAddress\":\"trouble.linky@gmail.com\",\"params\":{},\"remark\":\"Gmail测试账号\",\"smtpHost\":\"smtp.gmail.com\",\"smtpPort\":587,\"smtpSsl\":\"1\",\"status\":\"0\",\"updateBy\":\"\",\"updateTime\":\"2025-08-21 12:35:51\",\"usedCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-21 12:35:51', 12);
INSERT INTO `sys_oper_log` VALUES (123, '邮件联系人', 1, 'com.ruoyi.web.controller.email.EmailContactController.add()', 'POST', 1, 'admin', '研发部门', '/email/contact', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":1,\"company\":\"\",\"contactId\":2,\"createTime\":\"2025-08-21 12:37:06\",\"email\":\"trouble.linky@gmail.com\",\"gender\":\"0\",\"groupId\":2,\"level\":\"\",\"name\":\"aa\",\"params\":{},\"remark\":\"\",\"replyRate\":null,\"tags\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-21 12:37:06', 13);
INSERT INTO `sys_oper_log` VALUES (124, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:39:33\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":5,\"taskName\":\"gg\",\"templateId\":1}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:39:33\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":5,\"taskName\":\"gg\",\"templateId\":1}}', 0, NULL, '2025-08-21 12:39:44', 10434);
INSERT INTO `sys_oper_log` VALUES (125, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:41:52\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":6,\"taskName\":\"gg4\",\"templateId\":1}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:41:52\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":6,\"taskName\":\"gg4\",\"templateId\":1}}', 0, NULL, '2025-08-21 12:43:17', 15308);
INSERT INTO `sys_oper_log` VALUES (126, '邮箱账号', 2, 'com.ruoyi.web.controller.email.EmailAccountController.edit()', 'PUT', 1, 'admin', '研发部门', '/email/account', '127.0.0.1', '内网IP', '{\"accountId\":1,\"accountName\":\"Gmail测试账号\",\"createBy\":\"\",\"createTime\":\"2025-08-20 09:10:16\",\"dailyLimit\":100,\"emailAddress\":\"trouble.linky@gmail.com\",\"params\":{},\"remark\":\"Gmail测试账号\",\"smtpHost\":\"smtp.gmail.com\",\"smtpPort\":587,\"smtpSsl\":\"1\",\"status\":\"0\",\"updateBy\":\"\",\"updateTime\":\"2025-08-21 12:48:34\",\"usedCount\":0}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-21 12:48:34', 26);
INSERT INTO `sys_oper_log` VALUES (127, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:49:01\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":7,\"taskName\":\"gg45\",\"templateId\":1}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-21 12:49:01\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskId\":7,\"taskName\":\"gg45\",\"templateId\":1}}', 0, NULL, '2025-08-21 12:49:04', 50);
INSERT INTO `sys_oper_log` VALUES (128, '测试SMTP服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.testSmtpService()', 'POST', 1, 'admin', '研发部门', '/email/monitor/test/smtp/6', '127.0.0.1', '内网IP', '6', '{\"msg\":\"SMTP服务测试完成\",\"code\":200}', 0, NULL, '2025-08-24 08:36:25', 740);
INSERT INTO `sys_oper_log` VALUES (129, '测试SMTP服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.testSmtpService()', 'POST', 1, 'admin', '研发部门', '/email/monitor/test/smtp/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"SMTP服务测试完成\",\"code\":200}', 0, NULL, '2025-08-24 08:36:51', 21071);
INSERT INTO `sys_oper_log` VALUES (130, '停止账号监控', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopAccountMonitor()', 'POST', 1, 'admin', '研发部门', '/email/monitor/stop/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"账号监控停止成功\",\"code\":200}', 0, NULL, '2025-08-24 09:25:36', 1475);
INSERT INTO `sys_oper_log` VALUES (131, '重启邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.restartEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/restart', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务重启失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@3462a66d rejected from java.util.concurrent.ScheduledThreadPoolExecutor@5ea03335[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 14]\",\"code\":500}', 0, NULL, '2025-08-24 10:21:41', 14);
INSERT INTO `sys_oper_log` VALUES (132, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 10:51:42', 22);
INSERT INTO `sys_oper_log` VALUES (133, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:21:26', 14);
INSERT INTO `sys_oper_log` VALUES (134, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:32:18', 1287);
INSERT INTO `sys_oper_log` VALUES (135, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:38:51', 3355);
INSERT INTO `sys_oper_log` VALUES (136, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@3f99e43 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@32b01b3d[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 4]\",\"code\":500}', 0, NULL, '2025-08-24 11:39:12', 6);
INSERT INTO `sys_oper_log` VALUES (137, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动成功\",\"code\":200}', 0, NULL, '2025-08-24 11:39:19', 10);
INSERT INTO `sys_oper_log` VALUES (138, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:40:34', 7805);
INSERT INTO `sys_oper_log` VALUES (139, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@716f812a rejected from java.util.concurrent.ScheduledThreadPoolExecutor@32b01b3d[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 4]\",\"code\":500}', 0, NULL, '2025-08-24 11:40:58', 3);
INSERT INTO `sys_oper_log` VALUES (140, '启动邮件服务全局监控', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startGlobalMonitor()', 'POST', 1, 'admin', '研发部门', '/email/monitor/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"全局监控启动成功\",\"code\":200}', 0, NULL, '2025-08-24 11:44:45', 13);
INSERT INTO `sys_oper_log` VALUES (141, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:45:21', 3201);
INSERT INTO `sys_oper_log` VALUES (142, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@30f19e62 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@10ba80a[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 3]\",\"code\":500}', 0, NULL, '2025-08-24 11:45:32', 5);
INSERT INTO `sys_oper_log` VALUES (143, '停止邮件服务全局监控', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopGlobalMonitor()', 'POST', 1, 'admin', '研发部门', '/email/monitor/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"全局监控停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:46:32', 1);
INSERT INTO `sys_oper_log` VALUES (144, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 11:50:22', 7930);
INSERT INTO `sys_oper_log` VALUES (145, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动成功\",\"code\":200}', 0, NULL, '2025-08-24 12:01:27', 35);
INSERT INTO `sys_oper_log` VALUES (146, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 12:06:26', 3482);
INSERT INTO `sys_oper_log` VALUES (147, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@117be7a3 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@6d8ac36a[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 13]\",\"code\":500}', 0, NULL, '2025-08-24 12:06:32', 5);
INSERT INTO `sys_oper_log` VALUES (148, '停止邮件服务全局监控', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopGlobalMonitor()', 'POST', 1, 'admin', '研发部门', '/email/monitor/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"全局监控停止成功\",\"code\":200}', 0, NULL, '2025-08-24 12:07:23', 1);
INSERT INTO `sys_oper_log` VALUES (149, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动成功\",\"code\":200}', 0, NULL, '2025-08-24 12:16:51', 50);
INSERT INTO `sys_oper_log` VALUES (150, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 12:18:23', 7835);
INSERT INTO `sys_oper_log` VALUES (151, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@7c164349 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@4d205e02[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 7]\",\"code\":500}', 0, NULL, '2025-08-24 12:18:41', 4);
INSERT INTO `sys_oper_log` VALUES (152, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动成功\",\"code\":200}', 0, NULL, '2025-08-24 12:19:58', 12);
INSERT INTO `sys_oper_log` VALUES (153, '邮箱账号', 2, 'com.ruoyi.web.controller.email.EmailAccountController.edit()', 'PUT', 1, 'admin', '研发部门', '/email/account', '127.0.0.1', '内网IP', '{\"accountId\":6,\"accountName\":\"my\",\"autoStartMonitor\":\"1\",\"createBy\":\"\",\"createTime\":\"2025-08-20 12:30:20\",\"dailyLimit\":100,\"emailAddress\":\"182867664@qq.com\",\"imapHost\":\"imap.qq.com\",\"imapMonitorStatus\":\"stopped\",\"imapPassword\":\"xwoohyhjidmucafg\",\"imapPort\":993,\"imapSsl\":\"1\",\"imapUsername\":\"182867664@qq.com\",\"lastSendTime\":\"2025-08-21 12:31:17\",\"lastSyncTime\":\"2025-08-24 11:01:37\",\"listenerStatus\":\"stopped\",\"monitorEnabled\":\"1\",\"monitorPriority\":\"medium\",\"monitorStatus\":\"stopped\",\"params\":{},\"smtpHost\":\"smtp.qq.com\",\"smtpMonitorStatus\":\"stopped\",\"smtpPort\":465,\"smtpSsl\":\"1\",\"status\":\"0\",\"syncCount\":31,\"trackingEnabled\":\"1\",\"updateBy\":\"\",\"updateTime\":\"2025-08-24 11:01:37\",\"usedCount\":1,\"webhookUrl\":\"https://your-domain.com/email/webhook/callback\"}', NULL, 1, '密码加密失败', '2025-08-24 12:49:57', 58);
INSERT INTO `sys_oper_log` VALUES (154, '批量更新邮箱账号状态', 2, 'com.ruoyi.web.controller.email.EmailAccountController.batchUpdateStatus()', 'PUT', 1, 'admin', '研发部门', '/email/account/batch/status', '127.0.0.1', '内网IP', '{\"accountIds\":[6],\"params\":{},\"status\":\"1\",\"updateTime\":\"2025-08-24 17:06:42\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-24 17:06:43', 1348);
INSERT INTO `sys_oper_log` VALUES (155, '批量更新邮箱账号状态', 2, 'com.ruoyi.web.controller.email.EmailAccountController.batchUpdateStatus()', 'PUT', 1, 'admin', '研发部门', '/email/account/batch/status', '127.0.0.1', '内网IP', '{\"accountIds\":[6],\"params\":{},\"status\":\"0\",\"updateTime\":\"2025-08-24 17:07:17\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-24 17:07:18', 54);
INSERT INTO `sys_oper_log` VALUES (156, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 17:08:20', 4375);
INSERT INTO `sys_oper_log` VALUES (157, '启动邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.startEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/start', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务启动失败: Task java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask@4b3d4a1 rejected from java.util.concurrent.ScheduledThreadPoolExecutor@440b2a9f[Terminated, pool size = 0, active threads = 0, queued tasks = 0, completed tasks = 8]\",\"code\":500}', 0, NULL, '2025-08-24 17:10:32', 7);
INSERT INTO `sys_oper_log` VALUES (158, '停止邮件监听服务', 2, 'com.ruoyi.web.controller.email.EmailServiceMonitorController.stopEmailListener()', 'POST', 1, 'admin', '研发部门', '/email/monitor/listener/stop', '127.0.0.1', '内网IP', '', '{\"msg\":\"邮件监听服务停止成功\",\"code\":200}', 0, NULL, '2025-08-24 17:14:42', 7878);
INSERT INTO `sys_oper_log` VALUES (159, '批量更新邮箱账号状态', 2, 'com.ruoyi.web.controller.email.EmailAccountController.batchUpdateStatus()', 'PUT', 1, 'admin', '研发部门', '/email/account/batch/status', '127.0.0.1', '内网IP', '{\"accountIds\":[6],\"params\":{},\"status\":\"1\",\"updateTime\":\"2025-08-24 17:24:21\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-24 17:24:22', 981);
INSERT INTO `sys_oper_log` VALUES (160, '批量更新邮箱账号状态', 2, 'com.ruoyi.web.controller.email.EmailAccountController.batchUpdateStatus()', 'PUT', 1, 'admin', '研发部门', '/email/account/batch/status', '127.0.0.1', '内网IP', '{\"accountIds\":[6],\"params\":{},\"status\":\"0\",\"updateTime\":\"2025-08-24 17:24:42\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-24 17:24:42', 50);
INSERT INTO `sys_oper_log` VALUES (161, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"4\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"test2\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"test\\\"}\"}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-24 17:58:41', 38);
INSERT INTO `sys_oper_log` VALUES (162, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"4\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"test2\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"test\\\"}\"}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-24 17:58:47', 3);
INSERT INTO `sys_oper_log` VALUES (163, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 17:59:41\",\"params\":{},\"recipientType\":\"4\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":8,\"taskName\":\"test22\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"test\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 17:59:41\",\"params\":{},\"recipientType\":\"4\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":8,\"taskName\":\"test22\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"test\\\"}\"}}', 0, NULL, '2025-08-24 17:59:47', 5634);
INSERT INTO `sys_oper_log` VALUES (164, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 18:04:03\",\"params\":{},\"recipientType\":\"1\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":9,\"taskName\":\"haha\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dad\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 18:04:03\",\"params\":{},\"recipientType\":\"1\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":9,\"taskName\":\"haha\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dad\\\"}\"}}', 0, NULL, '2025-08-24 18:04:28', 24164);
INSERT INTO `sys_oper_log` VALUES (165, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"1\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"haha\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dad\\\"}\"}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-24 18:04:32', 2);
INSERT INTO `sys_oper_log` VALUES (166, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 18:04:38\",\"params\":{},\"recipientType\":\"1\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":10,\"taskName\":\"haha2\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dad\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 18:04:38\",\"params\":{},\"recipientType\":\"1\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":10,\"taskName\":\"haha2\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dad\\\"}\"}}', 0, NULL, '2025-08-24 18:10:07', 329382);
INSERT INTO `sys_oper_log` VALUES (167, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 18:10:55\",\"params\":{},\"recipientType\":\"all\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":11,\"taskName\":\"gaga\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dada\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 18:10:55\",\"params\":{},\"recipientType\":\"all\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":11,\"taskName\":\"gaga\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"dada\\\"}\"}}', 0, NULL, '2025-08-24 18:11:04', 9930);
INSERT INTO `sys_oper_log` VALUES (168, '菜单管理', 3, 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', 1, 'admin', '研发部门', '/system/menu/2200', '127.0.0.1', '内网IP', '2200', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', 0, NULL, '2025-08-24 18:16:35', 49);
INSERT INTO `sys_oper_log` VALUES (169, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 20:30:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":12,\"taskName\":\"gg45_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"嘎嘎\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":1,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 20:30:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":12,\"taskName\":\"gg45_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"嘎嘎\\\"}\"}}', 0, NULL, '2025-08-24 20:30:55', 89);
INSERT INTO `sys_oper_log` VALUES (170, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 20:53:37\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":13,\"taskName\":\"gg45_复制_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"翻页\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 20:53:37\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":13,\"taskName\":\"gg45_复制_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"翻页\\\"}\"}}', 0, NULL, '2025-08-24 20:53:37', 104);
INSERT INTO `sys_oper_log` VALUES (171, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 21:22:04\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":14,\"taskName\":\"gg45_复制_复制_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"行号\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 21:22:04\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":14,\"taskName\":\"gg45_复制_复制_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"行号\\\"}\"}}', 0, NULL, '2025-08-24 21:22:05', 110);
INSERT INTO `sys_oper_log` VALUES (172, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 22:46:59\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":15,\"taskName\":\"gg45_22\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"方法\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 22:46:59\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":15,\"taskName\":\"gg45_22\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"方法\\\"}\"}}', 0, NULL, '2025-08-24 22:47:00', 118);
INSERT INTO `sys_oper_log` VALUES (173, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 22:48:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":16,\"taskName\":\"gg45_33\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"天天\\\"}\"}', '{\"msg\":\"发送任务已创建\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 22:48:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":16,\"taskName\":\"gg45_33\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"天天\\\"}\"}}', 0, NULL, '2025-08-24 22:48:55', 36);
INSERT INTO `sys_oper_log` VALUES (174, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 23:53:08\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":17,\"taskName\":\"gg45_44\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"刚刚\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-24 23:53:08\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":17,\"taskName\":\"gg45_44\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"刚刚\\\"}\"}}', 0, NULL, '2025-08-24 23:53:09', 246);
INSERT INTO `sys_oper_log` VALUES (175, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:04:35\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":18,\"taskName\":\"gg45_44_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"方法\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:04:35\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":18,\"taskName\":\"gg45_44_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"方法\\\"}\"}}', 0, NULL, '2025-08-25 00:04:35', 244);
INSERT INTO `sys_oper_log` VALUES (176, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:10:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":19,\"taskName\":\"gg45_55\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"报表\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"2,1\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:10:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":19,\"taskName\":\"gg45_55\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"报表\\\"}\"}}', 0, NULL, '2025-08-25 00:10:55', 306);
INSERT INTO `sys_oper_log` VALUES (177, '邮件联系人', 1, 'com.ruoyi.web.controller.email.EmailContactController.add()', 'POST', 1, 'admin', '研发部门', '/email/contact', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":1,\"company\":\"\",\"contactId\":3,\"createTime\":\"2025-08-25 00:12:32\",\"email\":\"1444927178@qq.com\",\"gender\":\"0\",\"level\":\"\",\"name\":\"nn\",\"params\":{},\"remark\":\"\",\"replyRate\":null,\"tags\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', 0, NULL, '2025-08-25 00:12:32', 23);
INSERT INTO `sys_oper_log` VALUES (178, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:12:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":20,\"taskName\":\"gg45_66\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:12:55\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":20,\"taskName\":\"gg45_66\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}}', 0, NULL, '2025-08-25 00:12:55', 180);
INSERT INTO `sys_oper_log` VALUES (179, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:18:41\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":21,\"taskName\":\"gg45_7\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ffs\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:18:41\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":21,\"taskName\":\"gg45_7\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ffs\\\"}\"}}', 0, NULL, '2025-08-25 00:20:14', 92812);
INSERT INTO `sys_oper_log` VALUES (180, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:27:00\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":22,\"taskName\":\"gg45_7_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"hh\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:27:00\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":22,\"taskName\":\"gg45_7_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"hh\\\"}\"}}', 0, NULL, '2025-08-25 00:27:22', 21815);
INSERT INTO `sys_oper_log` VALUES (181, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:39:12\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":23,\"taskName\":\"gg45_7_复制_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"vv\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 00:39:12\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":23,\"taskName\":\"gg45_7_复制_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"vv\\\"}\"}}', 0, NULL, '2025-08-25 00:39:18', 6085);
INSERT INTO `sys_oper_log` VALUES (182, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:34:24\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":24,\"taskName\":\"gg45_8\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:34:24\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":24,\"taskName\":\"gg45_8\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}}', 0, NULL, '2025-08-25 12:35:27', 63894);
INSERT INTO `sys_oper_log` VALUES (183, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"gg45_8\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-25 12:35:45', 5);
INSERT INTO `sys_oper_log` VALUES (184, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:35:53\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":25,\"taskName\":\"gg45_82\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:35:53\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":25,\"taskName\":\"gg45_82\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}}', 0, NULL, '2025-08-25 12:36:23', 30097);
INSERT INTO `sys_oper_log` VALUES (185, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:37:09\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":26,\"taskName\":\"gg45_84\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:37:09\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":26,\"taskName\":\"gg45_84\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}}', 0, NULL, '2025-08-25 12:38:11', 62534);
INSERT INTO `sys_oper_log` VALUES (186, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"gg45_84\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-25 12:38:54', 3);
INSERT INTO `sys_oper_log` VALUES (187, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:39:00\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":27,\"taskName\":\"gg45_85\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:39:00\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":27,\"taskName\":\"gg45_85\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}}', 0, NULL, '2025-08-25 12:40:46', 106136);
INSERT INTO `sys_oper_log` VALUES (188, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"关于我们最新产品的介绍\",\"taskName\":\"gg45_85\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}', '{\"msg\":\"任务名称已存在，请使用不同的任务名称\",\"code\":500}', 0, NULL, '2025-08-25 12:41:04', 3);
INSERT INTO `sys_oper_log` VALUES (189, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:41:12\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":28,\"taskName\":\"gg45_86\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:41:12\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":28,\"taskName\":\"gg45_86\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}}', 0, NULL, '2025-08-25 12:41:17', 4900);
INSERT INTO `sys_oper_log` VALUES (190, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:41:59\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":29,\"taskName\":\"gg45_86_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:41:59\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":29,\"taskName\":\"gg45_86_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"bb\\\"}\"}}', 0, NULL, '2025-08-25 12:42:03', 3710);
INSERT INTO `sys_oper_log` VALUES (191, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:43:19\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":30,\"taskName\":\"gg45_82_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"nn\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:43:19\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":30,\"taskName\":\"gg45_82_复制\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"nn\\\"}\"}}', 0, NULL, '2025-08-25 12:43:20', 591);
INSERT INTO `sys_oper_log` VALUES (192, '创建邮件发送任务', 1, 'com.ruoyi.web.controller.email.EmailSendTaskController.createSendTask()', 'POST', 1, 'admin', '研发部门', '/email/task/create', '127.0.0.1', '内网IP', '{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:48:04\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":31,\"taskName\":\"gg45_82_复制3\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}', '{\"msg\":\"发送任务已创建并开始执行\",\"code\":200,\"data\":{\"accountId\":6,\"contactIds\":\"3\",\"content\":\"<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"createBy\":\"admin\",\"createTime\":\"2025-08-25 12:48:04\",\"params\":{},\"recipientType\":\"manual\",\"sendInterval\":10,\"sendMode\":\"immediate\",\"subject\":\"<p>尊敬的测试用户：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>\",\"taskId\":31,\"taskName\":\"gg45_82_复制3\",\"templateId\":1,\"templateVariables\":\"{\\\"name\\\":\\\"ff\\\"}\"}}', 0, NULL, '2025-08-25 12:48:14', 9861);

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int NOT NULL COMMENT '显示顺序',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES (1, 'ceo', '董事长', 1, '0', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_post` VALUES (2, 'se', '项目经理', 2, '0', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_post` VALUES (3, 'hr', '人力资源', 3, '0', 'admin', '2025-08-03 19:54:26', '', NULL, '');
INSERT INTO `sys_post` VALUES (4, 'user', '普通员工', 4, '0', 'admin', '2025-08-03 19:54:26', '', NULL, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) NULL DEFAULT 1 COMMENT '部门树选择项是否关联显示',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, '超级管理员', 'admin', 1, '1', 1, 1, '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL, '超级管理员');
INSERT INTO `sys_role` VALUES (2, '普通角色', 'common', 2, '2', 1, 1, '0', '0', 'admin', '2025-08-03 19:54:26', '', NULL, '普通角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `dept_id` bigint NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`, `dept_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和部门关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES (2, 100);
INSERT INTO `sys_role_dept` VALUES (2, 101);
INSERT INTO `sys_role_dept` VALUES (2, 105);

-- ----------------------------
-- Table structure for sys_role_email
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_email`;
CREATE TABLE `sys_role_email`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `can_manage_contacts` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '可管理联系人(0否 1是)',
  `can_send_emails` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '可发送邮件(0否 1是)',
  `can_manage_accounts` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '可管理账号(0否 1是)',
  `can_view_reports` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '可查看报表(0否 1是)',
  `can_manage_users` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '可管理用户(0否 1是)',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统角色邮件权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_email
-- ----------------------------
INSERT INTO `sys_role_email` VALUES (1, '1', '1', '1', '1', '1', 'admin', '2025-08-13 09:10:54', '', NULL, '0');
INSERT INTO `sys_role_email` VALUES (2, '1', '1', '0', '1', '0', 'admin', '2025-08-13 09:10:54', '', NULL, '0');
INSERT INTO `sys_role_email` VALUES (3, '0', '0', '0', '1', '0', 'admin', '2025-08-13 09:10:54', '', NULL, '0');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `menu_id` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色和菜单关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (2, 1);
INSERT INTO `sys_role_menu` VALUES (2, 2);
INSERT INTO `sys_role_menu` VALUES (2, 3);
INSERT INTO `sys_role_menu` VALUES (2, 4);
INSERT INTO `sys_role_menu` VALUES (2, 100);
INSERT INTO `sys_role_menu` VALUES (2, 101);
INSERT INTO `sys_role_menu` VALUES (2, 102);
INSERT INTO `sys_role_menu` VALUES (2, 103);
INSERT INTO `sys_role_menu` VALUES (2, 104);
INSERT INTO `sys_role_menu` VALUES (2, 105);
INSERT INTO `sys_role_menu` VALUES (2, 106);
INSERT INTO `sys_role_menu` VALUES (2, 107);
INSERT INTO `sys_role_menu` VALUES (2, 108);
INSERT INTO `sys_role_menu` VALUES (2, 109);
INSERT INTO `sys_role_menu` VALUES (2, 110);
INSERT INTO `sys_role_menu` VALUES (2, 111);
INSERT INTO `sys_role_menu` VALUES (2, 112);
INSERT INTO `sys_role_menu` VALUES (2, 113);
INSERT INTO `sys_role_menu` VALUES (2, 114);
INSERT INTO `sys_role_menu` VALUES (2, 115);
INSERT INTO `sys_role_menu` VALUES (2, 116);
INSERT INTO `sys_role_menu` VALUES (2, 117);
INSERT INTO `sys_role_menu` VALUES (2, 500);
INSERT INTO `sys_role_menu` VALUES (2, 501);
INSERT INTO `sys_role_menu` VALUES (2, 1000);
INSERT INTO `sys_role_menu` VALUES (2, 1001);
INSERT INTO `sys_role_menu` VALUES (2, 1002);
INSERT INTO `sys_role_menu` VALUES (2, 1003);
INSERT INTO `sys_role_menu` VALUES (2, 1004);
INSERT INTO `sys_role_menu` VALUES (2, 1005);
INSERT INTO `sys_role_menu` VALUES (2, 1006);
INSERT INTO `sys_role_menu` VALUES (2, 1007);
INSERT INTO `sys_role_menu` VALUES (2, 1008);
INSERT INTO `sys_role_menu` VALUES (2, 1009);
INSERT INTO `sys_role_menu` VALUES (2, 1010);
INSERT INTO `sys_role_menu` VALUES (2, 1011);
INSERT INTO `sys_role_menu` VALUES (2, 1012);
INSERT INTO `sys_role_menu` VALUES (2, 1013);
INSERT INTO `sys_role_menu` VALUES (2, 1014);
INSERT INTO `sys_role_menu` VALUES (2, 1015);
INSERT INTO `sys_role_menu` VALUES (2, 1016);
INSERT INTO `sys_role_menu` VALUES (2, 1017);
INSERT INTO `sys_role_menu` VALUES (2, 1018);
INSERT INTO `sys_role_menu` VALUES (2, 1019);
INSERT INTO `sys_role_menu` VALUES (2, 1020);
INSERT INTO `sys_role_menu` VALUES (2, 1021);
INSERT INTO `sys_role_menu` VALUES (2, 1022);
INSERT INTO `sys_role_menu` VALUES (2, 1023);
INSERT INTO `sys_role_menu` VALUES (2, 1024);
INSERT INTO `sys_role_menu` VALUES (2, 1025);
INSERT INTO `sys_role_menu` VALUES (2, 1026);
INSERT INTO `sys_role_menu` VALUES (2, 1027);
INSERT INTO `sys_role_menu` VALUES (2, 1028);
INSERT INTO `sys_role_menu` VALUES (2, 1029);
INSERT INTO `sys_role_menu` VALUES (2, 1030);
INSERT INTO `sys_role_menu` VALUES (2, 1031);
INSERT INTO `sys_role_menu` VALUES (2, 1032);
INSERT INTO `sys_role_menu` VALUES (2, 1033);
INSERT INTO `sys_role_menu` VALUES (2, 1034);
INSERT INTO `sys_role_menu` VALUES (2, 1035);
INSERT INTO `sys_role_menu` VALUES (2, 1036);
INSERT INTO `sys_role_menu` VALUES (2, 1037);
INSERT INTO `sys_role_menu` VALUES (2, 1038);
INSERT INTO `sys_role_menu` VALUES (2, 1039);
INSERT INTO `sys_role_menu` VALUES (2, 1040);
INSERT INTO `sys_role_menu` VALUES (2, 1041);
INSERT INTO `sys_role_menu` VALUES (2, 1042);
INSERT INTO `sys_role_menu` VALUES (2, 1043);
INSERT INTO `sys_role_menu` VALUES (2, 1044);
INSERT INTO `sys_role_menu` VALUES (2, 1045);
INSERT INTO `sys_role_menu` VALUES (2, 1046);
INSERT INTO `sys_role_menu` VALUES (2, 1047);
INSERT INTO `sys_role_menu` VALUES (2, 1048);
INSERT INTO `sys_role_menu` VALUES (2, 1049);
INSERT INTO `sys_role_menu` VALUES (2, 1050);
INSERT INTO `sys_role_menu` VALUES (2, 1051);
INSERT INTO `sys_role_menu` VALUES (2, 1052);
INSERT INTO `sys_role_menu` VALUES (2, 1053);
INSERT INTO `sys_role_menu` VALUES (2, 1054);
INSERT INTO `sys_role_menu` VALUES (2, 1055);
INSERT INTO `sys_role_menu` VALUES (2, 1056);
INSERT INTO `sys_role_menu` VALUES (2, 1057);
INSERT INTO `sys_role_menu` VALUES (2, 1058);
INSERT INTO `sys_role_menu` VALUES (2, 1059);
INSERT INTO `sys_role_menu` VALUES (2, 1060);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '手机号码',
  `sex` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '密码',
  `status` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '创建者',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '' COMMENT '更新者',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 100 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-08-25 12:25:30', '2025-08-03 19:54:26', 'admin', '2025-08-03 19:54:26', '', '2025-08-25 12:25:29', '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-08-03 19:54:26', '2025-08-03 19:54:26', 'admin', '2025-08-03 19:54:26', '', NULL, '测试员');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`, `post_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户与岗位关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES (1, 1);
INSERT INTO `sys_user_post` VALUES (2, 2);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户和角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);

-- ----------------------------
-- View structure for v_email_account_status
-- ----------------------------
DROP VIEW IF EXISTS `v_email_account_status`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_email_account_status` AS select `a`.`account_id` AS `account_id`,`a`.`account_name` AS `account_name`,`a`.`email_address` AS `email_address`,`a`.`status` AS `status`,`a`.`tracking_enabled` AS `tracking_enabled`,(case when (`a`.`status` = '0') then '启用发送邮件' when (`a`.`status` = '1') then '禁用发送邮件' else '未知状态' end) AS `status_desc`,(case when (`a`.`tracking_enabled` = '1') then '启用邮件跟踪' when (`a`.`tracking_enabled` = '0') then '禁用邮件跟踪' else '未知状态' end) AS `tracking_desc`,`m`.`imap_status` AS `imap_status`,`m`.`smtp_status` AS `smtp_status`,`m`.`imap_last_check_time` AS `imap_last_check_time`,`m`.`smtp_last_check_time` AS `smtp_last_check_time`,(case when ((`a`.`status` = '0') and (`m`.`account_id` is not null)) then '监控中' when ((`a`.`status` = '0') and (`m`.`account_id` is null)) then '未监控' else '不监控' end) AS `monitor_status` from (`email_account` `a` left join `email_service_monitor` `m` on((`a`.`account_id` = `m`.`account_id`))) where (`a`.`deleted` = '0');

-- ----------------------------
-- View structure for v_email_service_status_stats
-- ----------------------------
DROP VIEW IF EXISTS `v_email_service_status_stats`;
CREATE ALGORITHM = UNDEFINED SQL SECURITY DEFINER VIEW `v_email_service_status_stats` AS select 'IMAP' AS `service_type`,count(0) AS `total_accounts`,sum((case when (`email_service_monitor`.`imap_status` = '0') then 1 else 0 end)) AS `stopped_count`,sum((case when (`email_service_monitor`.`imap_status` = '1') then 1 else 0 end)) AS `running_count`,sum((case when (`email_service_monitor`.`imap_status` = '2') then 1 else 0 end)) AS `connecting_count`,sum((case when (`email_service_monitor`.`imap_status` = '3') then 1 else 0 end)) AS `connected_count`,sum((case when (`email_service_monitor`.`imap_status` in ('4','5','6','7','8','9','10')) then 1 else 0 end)) AS `error_count`,((sum((case when (`email_service_monitor`.`imap_status` in ('4','5','6','7','8','9','10')) then 1 else 0 end)) / count(0)) * 100) AS `error_rate` from `email_service_monitor` union all select 'SMTP' AS `service_type`,count(0) AS `total_accounts`,sum((case when (`email_service_monitor`.`smtp_status` = '0') then 1 else 0 end)) AS `stopped_count`,sum((case when (`email_service_monitor`.`smtp_status` = '1') then 1 else 0 end)) AS `running_count`,sum((case when (`email_service_monitor`.`smtp_status` = '2') then 1 else 0 end)) AS `connecting_count`,sum((case when (`email_service_monitor`.`smtp_status` = '3') then 1 else 0 end)) AS `connected_count`,sum((case when (`email_service_monitor`.`smtp_status` in ('4','5','6','7','8','9','10')) then 1 else 0 end)) AS `error_count`,((sum((case when (`email_service_monitor`.`smtp_status` in ('4','5','6','7','8','9','10')) then 1 else 0 end)) / count(0)) * 100) AS `error_rate` from `email_service_monitor`;

SET FOREIGN_KEY_CHECKS = 1;
