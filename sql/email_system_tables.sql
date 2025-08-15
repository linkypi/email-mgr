-- 邮件管理系统数据库表结构
-- 基于若依框架设计

-- 1. 联系人表
CREATE TABLE `email_contact` (
  `contact_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `email` varchar(200) NOT NULL COMMENT '邮箱地址',
  `company` varchar(200) DEFAULT NULL COMMENT '企业名称',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `gender` char(1) DEFAULT '0' COMMENT '性别(0未知 1男 2女)',
  `social_media` varchar(200) DEFAULT NULL COMMENT '社交媒体账号',
  `followers` int(11) DEFAULT 0 COMMENT '粉丝数量',
  `level` char(1) DEFAULT '3' COMMENT '等级(1重要 2普通 3一般)',
  `group_id` bigint(20) DEFAULT NULL COMMENT '群组ID',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `send_count` int(11) DEFAULT 0 COMMENT '发送邮件数量',
  `reply_count` int(11) DEFAULT 0 COMMENT '回复邮件数量',
  `open_count` int(11) DEFAULT 0 COMMENT '打开邮件数量',
  `reply_rate` decimal(5,2) DEFAULT 0.00 COMMENT '回复率',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`contact_id`),
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_group_id` (`group_id`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件联系人表';

-- 2. 联系人群组表
CREATE TABLE `email_contact_group` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '群组ID',
  `group_name` varchar(100) NOT NULL COMMENT '群组名称',
  `description` varchar(500) DEFAULT NULL COMMENT '群组描述',
  `contact_count` int(11) DEFAULT 0 COMMENT '联系人数量',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人群组表';

-- 3. 联系人标签表
CREATE TABLE `email_contact_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `tag_name` varchar(50) NOT NULL COMMENT '标签名称',
  `tag_color` varchar(20) DEFAULT '#409EFF' COMMENT '标签颜色',
  `contact_count` int(11) DEFAULT 0 COMMENT '使用该标签的联系人数量',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人标签表';

-- 4. 联系人销售数据表
CREATE TABLE `email_contact_sales` (
  `sales_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '销售数据ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `sales_amount` decimal(10,2) DEFAULT 0.00 COMMENT '销售金额',
  `sales_date` date DEFAULT NULL COMMENT '销售日期',
  `product_name` varchar(200) DEFAULT NULL COMMENT '产品名称',
  `sales_channel` varchar(100) DEFAULT NULL COMMENT '销售渠道',
  `sales_notes` varchar(500) DEFAULT NULL COMMENT '销售备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`sales_id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_sales_date` (`sales_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人销售数据表';

-- 5. 邮箱账号表
CREATE TABLE `email_account` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `account_name` varchar(100) NOT NULL COMMENT '账号名称',
  `email_address` varchar(200) NOT NULL COMMENT '邮箱地址',
  `password` varchar(200) NOT NULL COMMENT '邮箱密码(加密)',
  `smtp_host` varchar(100) NOT NULL COMMENT 'SMTP服务器',
  `smtp_port` int(5) NOT NULL COMMENT 'SMTP端口',
  `smtp_ssl` char(1) DEFAULT '1' COMMENT '是否启用SSL(0否 1是)',
  `daily_limit` int(11) DEFAULT 100 COMMENT '每日发送限制',
  `used_count` int(11) DEFAULT 0 COMMENT '今日已发送数量',
  `last_send_time` datetime DEFAULT NULL COMMENT '最后发送时间',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_email_address` (`email_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮箱账号表';

-- 6. 邮件模板表
CREATE TABLE `email_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `template_type` char(1) DEFAULT '1' COMMENT '模板类型(1普通 2营销 3通知)',
  `subject` varchar(200) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `variables` varchar(500) DEFAULT NULL COMMENT '变量列表(JSON格式)',
  `use_count` int(11) DEFAULT 0 COMMENT '使用次数',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件模板表';

-- 7. 批量发送任务表
CREATE TABLE `email_send_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `template_id` bigint(20) DEFAULT NULL COMMENT '模板ID',
  `subject` varchar(200) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `recipient_type` char(1) DEFAULT '1' COMMENT '收件人类型(1全部 2群组 3标签 4手动)',
  `recipient_ids` text DEFAULT NULL COMMENT '收件人ID列表(JSON格式)',
  `account_ids` text DEFAULT NULL COMMENT '发件账号ID列表(JSON格式)',
  `send_interval` int(11) DEFAULT 10 COMMENT '发送间隔(秒)',
  `start_time` datetime DEFAULT NULL COMMENT '开始发送时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束发送时间',
  `total_count` int(11) DEFAULT 0 COMMENT '总发送数量',
  `sent_count` int(11) DEFAULT 0 COMMENT '已发送数量',
  `delivered_count` int(11) DEFAULT 0 COMMENT '送达数量',
  `opened_count` int(11) DEFAULT 0 COMMENT '打开数量',
  `replied_count` int(11) DEFAULT 0 COMMENT '回复数量',
  `status` char(1) DEFAULT '0' COMMENT '状态(0待发送 1发送中 2已完成 3已暂停 4已取消)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`task_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='批量发送任务表';

-- 8. 邮件发送记录表
CREATE TABLE `email_send_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `task_id` bigint(20) DEFAULT NULL COMMENT '任务ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `account_id` bigint(20) NOT NULL COMMENT '发件账号ID',
  `email_address` varchar(200) NOT NULL COMMENT '收件邮箱',
  `subject` varchar(200) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `delivered_time` datetime DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime DEFAULT NULL COMMENT '回复时间',
  `status` char(1) DEFAULT '0' COMMENT '状态(0待发送 1已发送 2已送达 3已打开 4已回复 5发送失败)',
  `error_message` varchar(500) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送记录表';

-- 9. 个人邮件表
CREATE TABLE `email_personal` (
  `email_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '邮件ID',
  `email_type` char(1) NOT NULL COMMENT '邮件类型(1收件 2发件)',
  `from_address` varchar(200) DEFAULT NULL COMMENT '发件人地址',
  `to_address` varchar(200) DEFAULT NULL COMMENT '收件人地址',
  `cc_address` varchar(500) DEFAULT NULL COMMENT '抄送地址',
  `bcc_address` varchar(500) DEFAULT NULL COMMENT '密送地址',
  `subject` varchar(200) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `attachments` text DEFAULT NULL COMMENT '附件列表(JSON格式)',
  `is_read` char(1) DEFAULT '0' COMMENT '是否已读(0未读 1已读)',
  `is_starred` char(1) DEFAULT '0' COMMENT '是否星标(0否 1是)',
  `is_important` char(1) DEFAULT '0' COMMENT '是否重要(0否 1是)',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `receive_time` datetime DEFAULT NULL COMMENT '接收时间',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1已删除)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`email_id`),
  KEY `idx_email_type` (`email_type`),
  KEY `idx_from_address` (`from_address`),
  KEY `idx_to_address` (`to_address`),
  KEY `idx_subject` (`subject`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_receive_time` (`receive_time`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_is_starred` (`is_starred`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='个人邮件表';

-- 10. 邮件统计表
CREATE TABLE `email_statistics` (
  `stat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` char(1) NOT NULL COMMENT '统计类型(1日统计 2月统计 3年统计)',
  `total_sent` int(11) DEFAULT 0 COMMENT '总发送数',
  `total_delivered` int(11) DEFAULT 0 COMMENT '总送达数',
  `total_opened` int(11) DEFAULT 0 COMMENT '总打开数',
  `total_replied` int(11) DEFAULT 0 COMMENT '总回复数',
  `delivery_rate` decimal(5,2) DEFAULT 0.00 COMMENT '送达率',
  `open_rate` decimal(5,2) DEFAULT 0.00 COMMENT '打开率',
  `reply_rate` decimal(5,2) DEFAULT 0.00 COMMENT '回复率',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`stat_id`),
  UNIQUE KEY `uk_date_type` (`stat_date`, `stat_type`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件统计表';

-- 初始化数据
INSERT INTO `email_contact_group` (`group_id`, `group_name`, `description`, `contact_count`, `status`, `create_by`, `create_time`) VALUES 
(1, 'VIP客户', '重要客户群组', 0, '0', 'admin', NOW()),
(2, '普通客户', '普通客户群组', 0, '0', 'admin', NOW()),
(3, '潜在客户', '潜在客户群组', 0, '0', 'admin', NOW());

INSERT INTO `email_contact_tag` (`tag_id`, `tag_name`, `tag_color`, `contact_count`, `status`, `create_by`, `create_time`) VALUES 
(1, '重要', '#F56C6C', 0, '0', 'admin', NOW()),
(2, '项目', '#67C23A', 0, '0', 'admin', NOW()),
(3, '待处理', '#E6A23C', 0, '0', 'admin', NOW()),
(4, '归档', '#909399', 0, '0', 'admin', NOW());

INSERT INTO `email_template` (`template_id`, `template_name`, `template_type`, `subject`, `content`, `variables`, `use_count`, `status`, `create_by`, `create_time`) VALUES 
(1, '产品推广邮件模板', '2', '关于我们最新产品的介绍', '<p>尊敬的{{name}}：</p><p>感谢您对我们公司的关注！</p><p>我们很高兴向您介绍我们的最新产品...</p>', '["name","company"]', 0, '0', 'admin', NOW()),
(2, '会议邀请模板', '3', '邀请您参加重要会议', '<p>尊敬的{{name}}：</p><p>诚邀您参加我们即将举行的会议...</p>', '["name","meeting_time","meeting_location"]', 0, '0', 'admin', NOW()),
(3, '月度报表通知', '3', '月度报表已生成', '<p>尊敬的{{name}}：</p><p>您的月度报表已经生成，请查收...</p>', '["name","report_month"]', 0, '0', 'admin', NOW());
