-- =============================================
-- 邮件管理系统 - 个人邮件表测试数据
-- 版本: V1.0.1
-- 创建时间: 2024-01-15
-- 描述: 插入个人邮件表的测试数据
-- =============================================

-- 插入测试数据 - 收件箱邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `is_starred`, `is_important`, `receive_time`, `email_type`, `create_by`, `create_time`) VALUES
('tech@company.com', 'admin@company.com', '生产环境系统升级提醒', '各位同事：计划于今晚23:00至次日凌晨3:00进行生产环境系统升级，升级期间将暂停...', '<p>各位同事：计划于今晚23:00至次日凌晨3:00进行生产环境系统升级，升级期间将暂停...</p>', 'read', 1, 1, '2024-01-15 09:15:00', 'inbox', 'admin', NOW()),
('project@company.com', 'admin@company.com', '【周报】数字化项目进度报告 - 第25周', '项目整体进度：完成86%，本周完成需求分析会议3次，核心模块...', '<p>项目整体进度：完成86%，本周完成需求分析会议3次，核心模块...</p>', 'read', 1, 0, '2024-01-14 16:20:00', 'inbox', 'admin', NOW()),
('hr@company.com', 'admin@company.com', '员工福利更新通知', '各位员工：公司福利政策有所调整，请查看附件了解详情...', '<p>各位员工：公司福利政策有所调整，请查看附件了解详情...</p>', 'unread', 0, 0, '2024-01-15 10:30:00', 'inbox', 'admin', NOW()),
('boss@company.com', 'admin@company.com', '重要会议通知', '明天上午9:00在会议室A召开重要会议，请准时参加...', '<p>明天上午9:00在会议室A召开重要会议，请准时参加...</p>', 'unread', 0, 1, '2024-01-15 11:00:00', 'inbox', 'admin', NOW());

-- 插入测试数据 - 发件箱邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `send_time`, `email_type`, `create_by`, `create_time`) VALUES
('admin@company.com', 'hr@company.com', '员工福利申请', '关于员工福利申请的相关事宜...', '<p>关于员工福利申请的相关事宜...</p>', 'sent', '2024-01-15 14:30:00', 'sent', 'admin', NOW()),
('admin@company.com', 'tech@company.com', '系统维护通知', '系统维护相关通知...', '<p>系统维护相关通知...</p>', 'sent', '2024-01-15 11:20:00', 'sent', 'admin', NOW());

-- 插入测试数据 - 星标邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `is_starred`, `receive_time`, `email_type`, `create_by`, `create_time`) VALUES
('boss@company.com', 'admin@company.com', '重要项目通知', '关于重要项目的相关通知...', '<p>关于重要项目的相关通知...</p>', 'starred', 1, '2024-01-15 10:30:00', 'inbox', 'admin', NOW()),
('hr@company.com', 'admin@company.com', '年终奖金通知', '年终奖金发放相关通知...', '<p>年终奖金发放相关通知...</p>', 'starred', 1, '2024-01-14 16:20:00', 'inbox', 'admin', NOW());

-- 插入测试数据 - 已删除邮件
INSERT INTO `email_personal` (`from_address`, `to_address`, `subject`, `content`, `html_content`, `status`, `delete_time`, `email_type`, `create_by`, `create_time`) VALUES
('spam@example.com', 'admin@company.com', '垃圾邮件', '这是一封垃圾邮件...', '<p>这是一封垃圾邮件...</p>', 'deleted', '2024-01-15 10:30:00', 'inbox', 'admin', NOW()),
('old@company.com', 'admin@company.com', '过期通知', '这是一封过期通知...', '<p>这是一封过期通知...</p>', 'deleted', '2024-01-14 16:20:00', 'inbox', 'admin', NOW());

