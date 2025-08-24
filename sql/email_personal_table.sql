-- 个人邮件表
DROP TABLE IF EXISTS `email_personal`;
CREATE TABLE `email_personal` (
  `email_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '邮件ID',
  `from_address` varchar(255) NOT NULL COMMENT '发件人邮箱',
  `to_address` varchar(255) NOT NULL COMMENT '收件人邮箱',
  `subject` varchar(500) DEFAULT NULL COMMENT '邮件主题',
  `content` text COMMENT '邮件内容',
  `html_content` longtext COMMENT 'HTML内容',
  `status` varchar(20) DEFAULT 'unread' COMMENT '邮件状态(unread=未读,read=已读,starred=星标,deleted=已删除)',
  `is_starred` tinyint(1) DEFAULT 0 COMMENT '是否星标(0=否,1=是)',
  `is_important` tinyint(1) DEFAULT 0 COMMENT '是否重要(0=否,1=是)',
  `receive_time` datetime DEFAULT NULL COMMENT '接收时间',
  `send_time` datetime DEFAULT NULL COMMENT '发送时间',
  `email_type` varchar(20) DEFAULT 'inbox' COMMENT '邮件类型(inbox=收件箱,sent=发件箱,starred=星标,deleted=已删除)',
  `attachment_count` int(11) DEFAULT 0 COMMENT '附件数量',
  `delete_time` datetime DEFAULT NULL COMMENT '删除时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`email_id`),
  KEY `idx_from_address` (`from_address`),
  KEY `idx_to_address` (`to_address`),
  KEY `idx_status` (`status`),
  KEY `idx_email_type` (`email_type`),
  KEY `idx_receive_time` (`receive_time`),
  KEY `idx_send_time` (`send_time`),
  KEY `idx_create_by` (`create_by`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='个人邮件表';

-- 插入测试数据 - 收件箱邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `is_starred`, `is_important`, `receive_time`, `email_type`, `create_by`, `create_time`) VALUES
('hr@company.com', 'admin@company.com', '2023年带薪休假政策更新通知', '尊敬的员工：根据最新国家政策和公司规定，我们对2023年带薪休假政策进行了以下更新...', '<p>尊敬的员工：根据最新国家政策和公司规定，我们对2023年带薪休假政策进行了以下更新...</p>', 'unread', 0, 0, '2024-01-15 10:30:00', 'inbox', 'admin', NOW()),
('tech@company.com', 'admin@company.com', '生产环境系统升级提醒', '各位同事：计划于今晚23:00至次日凌晨3:00进行生产环境系统升级，升级期间将暂停...', '<p>各位同事：计划于今晚23:00至次日凌晨3:00进行生产环境系统升级，升级期间将暂停...</p>', 'unread', 1, 1, '2024-01-15 09:15:00', 'inbox', 'admin', NOW()),
('project@company.com', 'admin@company.com', '【周报】数字化项目进度报告 - 第25周', '项目整体进度：完成86%，本周完成需求分析会议3次，核心模块...', '<p>项目整体进度：完成86%，本周完成需求分析会议3次，核心模块...</p>', 'read', 1, 0, '2024-01-14 16:20:00', 'inbox', 'admin', NOW()),
('marketing@company.com', 'admin@company.com', '市场活动策划方案', '关于下季度市场活动的策划方案，请查收...', '<p>关于下季度市场活动的策划方案，请查收...</p>', 'read', 0, 1, '2024-01-14 14:20:00', 'inbox', 'admin', NOW()),
('support@company.com', 'admin@company.com', '技术支持回复', '您提交的技术问题已解决，详情请查看...', '<p>您提交的技术问题已解决，详情请查看...</p>', 'read', 0, 0, '2024-01-13 11:30:00', 'inbox', 'admin', NOW());

-- 插入测试数据 - 发件箱邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `is_starred`, `is_important`, `send_time`, `email_type`, `create_by`, `create_time`) VALUES
('admin@company.com', 'hr@company.com', '员工福利申请', '关于员工福利申请的相关事宜...', '<p>关于员工福利申请的相关事宜...</p>', 'sent', 0, 0, '2024-01-15 14:30:00', 'sent', 'admin', NOW()),
('admin@company.com', 'tech@company.com', '系统维护通知', '系统维护相关通知...', '<p>系统维护相关通知...</p>', 'sent', 0, 0, '2024-01-15 11:20:00', 'sent', 'admin', NOW()),
('admin@company.com', 'project@company.com', '项目进度汇报', '关于当前项目进度的详细汇报...', '<p>关于当前项目进度的详细汇报...</p>', 'sent', 1, 0, '2024-01-14 16:45:00', 'sent', 'admin', NOW()),
('admin@company.com', 'finance@company.com', '预算申请', '关于下季度预算的申请报告...', '<p>关于下季度预算的申请报告...</p>', 'sent', 0, 1, '2024-01-13 09:15:00', 'sent', 'admin', NOW()),
('admin@company.com', 'support@company.com', '技术问题咨询', '关于系统使用中遇到的技术问题...', '<p>关于系统使用中遇到的技术问题...</p>', 'sent', 0, 0, '2024-01-12 15:20:00', 'sent', 'admin', NOW());

-- 插入测试数据 - 已删除邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `is_starred`, `is_important`, `receive_time`, `send_time`, `email_type`, `delete_time`, `create_by`, `create_time`) VALUES
('spam@example.com', 'admin@company.com', '垃圾邮件示例', '这是一封垃圾邮件的内容，包含一些不必要的信息...', '<p>这是一封垃圾邮件的内容，包含一些不必要的信息...</p><p>请忽略此邮件。</p>', 'deleted', 0, 0, '2024-01-15 16:30:00', NULL, 'inbox', '2024-01-15 16:30:00', 'admin', NOW()),
('admin@company.com', 'old@company.com', '过期的会议通知', '关于过期会议的通知内容，会议已经结束...', '<p>关于过期会议的通知内容，会议已经结束...</p><p>此邮件已过期，可以删除。</p>', 'deleted', 0, 0, NULL, '2024-01-14 10:15:00', 'sent', '2024-01-14 10:15:00', 'admin', NOW()),
('test@company.com', 'admin@company.com', '测试邮件', '这是一封测试邮件，已被删除...', '<p>这是一封测试邮件，已被删除...</p><p>用于测试邮件系统功能。</p>', 'deleted', 1, 0, '2024-01-13 14:20:00', NULL, 'inbox', '2024-01-13 14:20:00', 'admin', NOW()),
('notification@company.com', 'admin@company.com', '系统维护通知', '系统将于今晚进行维护，请提前保存工作...', '<p>系统将于今晚进行维护，请提前保存工作...</p><p>维护时间：22:00-02:00</p>', 'deleted', 0, 1, '2024-01-12 09:45:00', NULL, 'inbox', '2024-01-12 09:45:00', 'admin', NOW()),
('admin@company.com', 'support@company.com', '技术支持请求', '关于系统使用的问题咨询...', '<p>关于系统使用的问题咨询...</p><p>问题已解决，可以删除。</p>', 'deleted', 0, 0, NULL, '2024-01-11 15:20:00', 'sent', '2024-01-11 15:20:00', 'admin', NOW());
