-- 邮件管理系统数据库表结构
-- 创建时间: 2024-01-01
-- 说明: 基于若依框架的邮件管理系统

-- 1. 联系人表
CREATE TABLE `email_contact` (
  `contact_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '联系人ID',
  `name` varchar(100) NOT NULL COMMENT '姓名',
  `email` varchar(255) NOT NULL COMMENT '邮箱',
  `company` varchar(200) DEFAULT NULL COMMENT '企业',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `age` int(3) DEFAULT NULL COMMENT '年龄',
  `gender` char(1) DEFAULT NULL COMMENT '性别(0男 1女)',
  `social_media` varchar(500) DEFAULT NULL COMMENT '社交媒体账号',
  `followers_count` int(11) DEFAULT 0 COMMENT '粉丝数',
  `group_id` bigint(20) DEFAULT NULL COMMENT '群组ID',
  `level` varchar(20) DEFAULT '普通' COMMENT '等级',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`contact_id`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人表';

-- 2. 联系人群组表
CREATE TABLE `email_contact_group` (
  `group_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '群组ID',
  `group_name` varchar(100) NOT NULL COMMENT '群组名称',
  `description` varchar(500) DEFAULT NULL COMMENT '群组描述',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='联系人群组表';

-- 3. 销售数据表
CREATE TABLE `email_sales_data` (
  `sales_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '销售数据ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `sales_amount` decimal(10,2) DEFAULT 0.00 COMMENT '销售金额',
  `sales_date` date DEFAULT NULL COMMENT '销售日期',
  `product_name` varchar(200) DEFAULT NULL COMMENT '产品名称',
  `sales_status` varchar(20) DEFAULT '待跟进' COMMENT '销售状态',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`sales_id`),
  KEY `idx_contact_id` (`contact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='销售数据表';

-- 4. 邮件账号表
CREATE TABLE `email_account` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '账号ID',
  `account_name` varchar(100) NOT NULL COMMENT '账号名称',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `password` varchar(255) NOT NULL COMMENT '密码(加密)',
  `smtp_host` varchar(100) DEFAULT NULL COMMENT 'SMTP服务器',
  `smtp_port` int(5) DEFAULT 587 COMMENT 'SMTP端口',
  `daily_limit` int(5) DEFAULT 50 COMMENT '每日发送上限',
  `current_count` int(5) DEFAULT 0 COMMENT '当前发送数量',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_email_address` (`email_address`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件账号表';

-- 5. 邮件模板表
CREATE TABLE `email_template` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '模板ID',
  `template_name` varchar(100) NOT NULL COMMENT '模板名称',
  `subject` varchar(500) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `template_type` varchar(20) DEFAULT '普通' COMMENT '模板类型',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件模板表';

-- 6. 邮件发送任务表
CREATE TABLE `email_send_task` (
  `task_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `task_name` varchar(100) NOT NULL COMMENT '任务名称',
  `template_id` bigint(20) DEFAULT NULL COMMENT '模板ID',
  `subject` varchar(500) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `account_id` bigint(20) NOT NULL COMMENT '发送账号ID',
  `send_limit` int(5) DEFAULT 50 COMMENT '发送数量限制',
  `time_interval` int(5) DEFAULT 10 COMMENT '发送间隔(秒)',
  `start_time` datetime DEFAULT NULL COMMENT '开始发送时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束发送时间',
  `total_count` int(5) DEFAULT 0 COMMENT '总发送数量',
  `sent_count` int(5) DEFAULT 0 COMMENT '已发送数量',
  `delivered_count` int(5) DEFAULT 0 COMMENT '送达数量',
  `opened_count` int(5) DEFAULT 0 COMMENT '打开数量',
  `replied_count` int(5) DEFAULT 0 COMMENT '回复数量',
  `task_status` varchar(20) DEFAULT '待发送' COMMENT '任务状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送任务表';

-- 7. 邮件发送记录表
CREATE TABLE `email_send_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `account_id` bigint(20) NOT NULL COMMENT '发送账号ID',
  `email_address` varchar(255) NOT NULL COMMENT '收件人邮箱',
  `subject` varchar(500) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `delivered_time` datetime DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime DEFAULT NULL COMMENT '回复时间',
  `send_status` varchar(20) DEFAULT '待发送' COMMENT '发送状态',
  `is_delivered` char(1) DEFAULT '0' COMMENT '是否送达(0否 1是)',
  `is_opened` char(1) DEFAULT '0' COMMENT '是否打开(0否 1是)',
  `is_replied` char(1) DEFAULT '0' COMMENT '是否回复(0否 1是)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`record_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件发送记录表';

-- 8. 邮件往来记录表
CREATE TABLE `email_conversation` (
  `conversation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '往来记录ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `email_address` varchar(255) NOT NULL COMMENT '邮箱地址',
  `subject` varchar(500) NOT NULL COMMENT '邮件主题',
  `content` text NOT NULL COMMENT '邮件内容',
  `direction` char(1) NOT NULL COMMENT '方向(0发送 1接收)',
  `message_id` varchar(255) DEFAULT NULL COMMENT '邮件消息ID',
  `parent_id` bigint(20) DEFAULT NULL COMMENT '父邮件ID',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `receive_time` datetime DEFAULT NULL COMMENT '接收时间',
  `status` varchar(20) DEFAULT '正常' COMMENT '状态',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`conversation_id`),
  KEY `idx_contact_id` (`contact_id`),
  KEY `idx_message_id` (`message_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件往来记录表';

-- 9. 发送回复标签表
CREATE TABLE `email_send_reply_tag` (
  `tag_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `contact_id` bigint(20) NOT NULL COMMENT '联系人ID',
  `tag_name` varchar(100) NOT NULL COMMENT '标签名称',
  `tag_type` varchar(20) DEFAULT '发送' COMMENT '标签类型(发送/回复)',
  `send_count` int(5) DEFAULT 0 COMMENT '发送邮件数量',
  `reply_count` int(5) DEFAULT 0 COMMENT '回复邮件数量',
  `open_count` int(5) DEFAULT 0 COMMENT '打开邮件数量',
  `last_send_time` datetime DEFAULT NULL COMMENT '最后发送时间',
  `last_reply_time` datetime DEFAULT NULL COMMENT '最后回复时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`tag_id`),
  KEY `idx_contact_id` (`contact_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发送回复标签表';

-- 10. 系统角色扩展表
CREATE TABLE `sys_role_email` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `can_manage_contacts` char(1) DEFAULT '0' COMMENT '可管理联系人(0否 1是)',
  `can_send_emails` char(1) DEFAULT '0' COMMENT '可发送邮件(0否 1是)',
  `can_manage_accounts` char(1) DEFAULT '0' COMMENT '可管理账号(0否 1是)',
  `can_view_reports` char(1) DEFAULT '0' COMMENT '可查看报表(0否 1是)',
  `can_manage_users` char(1) DEFAULT '0' COMMENT '可管理用户(0否 1是)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统角色邮件权限表';

-- 插入初始数据
INSERT INTO `email_contact_group` (`group_name`, `description`, `status`, `create_by`, `create_time`) VALUES 
('VIP客户', '重要客户群组', '0', 'admin', NOW()),
('潜在客户', '潜在客户群组', '0', 'admin', NOW()),
('普通客户', '普通客户群组', '0', 'admin', NOW());

INSERT INTO `email_template` (`template_name`, `subject`, `content`, `template_type`, `status`, `create_by`, `create_time`) VALUES 
('欢迎邮件', '欢迎加入我们', '亲爱的{name}，\n\n欢迎您加入我们的大家庭！\n\n我们很高兴能够为您提供服务。\n\n如有任何问题，请随时联系我们。\n\n祝好！\n{company}', '普通', '0', 'admin', NOW()),
('产品介绍', '我们的产品介绍', '尊敬的{name}，\n\n感谢您对我们产品的关注。\n\n我们为您准备了详细的产品介绍，希望能够满足您的需求。\n\n期待与您的进一步合作！\n\n{company}', '普通', '0', 'admin', NOW());

-- 更新系统角色权限
INSERT INTO `sys_role_email` (`role_id`, `can_manage_contacts`, `can_send_emails`, `can_manage_accounts`, `can_view_reports`, `can_manage_users`, `create_by`, `create_time`) VALUES 
(1, '1', '1', '1', '1', '1', 'admin', NOW()),
(2, '1', '1', '0', '1', '0', 'admin', NOW()),
(3, '0', '0', '0', '1', '0', 'admin', NOW());

