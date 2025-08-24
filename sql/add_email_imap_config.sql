-- 添加邮件IMAP同步配置
-- 执行时间：2024-01-01

-- 添加邮件IMAP同步天数配置
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark) 
VALUES ('邮件IMAP同步天数', 'email.imap.sync.days', '7', 'Y', 'admin', NOW(), 'admin', NOW(), '邮件IMAP同步的天数，默认为7天，即只同步最近7天的邮件');

-- 添加邮件IMAP同步间隔配置（分钟）
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark) 
VALUES ('邮件IMAP同步间隔', 'email.imap.sync.interval', '30', 'Y', 'admin', NOW(), 'admin', NOW(), '邮件IMAP同步的间隔时间（分钟），默认为30分钟');

-- 添加邮件IMAP同步超时配置（秒）
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark) 
VALUES ('邮件IMAP同步超时', 'email.imap.sync.timeout', '60', 'Y', 'admin', NOW(), 'admin', NOW(), '邮件IMAP同步的超时时间（秒），默认为60秒');

-- 添加邮件IMAP同步批次大小配置
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark) 
VALUES ('邮件IMAP同步批次大小', 'email.imap.sync.batch.size', '100', 'Y', 'admin', NOW(), 'admin', NOW(), '邮件IMAP同步的批次大小，默认为100封邮件一批');

-- 添加邮件IMAP同步最大邮件数量配置
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark) 
VALUES ('邮件IMAP同步最大邮件数量', 'email.imap.sync.max.messages', '1000', 'Y', 'admin', NOW(), 'admin', NOW(), '邮件IMAP同步的最大邮件数量，默认为1000封');

-- 查看配置
SELECT config_name, config_key, config_value, remark FROM sys_config WHERE config_key LIKE 'email.imap.sync.%' ORDER BY config_key;
