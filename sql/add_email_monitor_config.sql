-- 添加邮件监控相关配置参数
-- 执行此脚本以添加IMAP和SMTP监控的配置参数

-- IMAP重试延迟时间（毫秒）
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`) 
VALUES ('IMAP重试延迟时间', 'email.imap.retry.delay', '5000', 'N', 'admin', NOW(), 'IMAP连接失败时的重试延迟时间，单位毫秒，范围5-30秒');

-- IMAP心跳检测间隔（毫秒）
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`) 
VALUES ('IMAP心跳检测间隔', 'email.imap.heartbeat.interval', '10000', 'N', 'admin', NOW(), 'IMAP连接心跳检测间隔，单位毫秒，范围5-30秒');

-- SMTP重试延迟时间（毫秒）
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`) 
VALUES ('SMTP重试延迟时间', 'email.smtp.retry.delay', '5000', 'N', 'admin', NOW(), 'SMTP连接失败时的重试延迟时间，单位毫秒，范围5-30秒');

-- SMTP心跳检测间隔（毫秒）
INSERT INTO `sys_config` (`config_name`, `config_key`, `config_value`, `config_type`, `create_by`, `create_time`, `remark`) 
VALUES ('SMTP心跳检测间隔', 'email.smtp.heartbeat.interval', '10000', 'N', 'admin', NOW(), 'SMTP连接心跳检测间隔，单位毫秒，范围5-30秒');

-- 查询已添加的配置
SELECT config_name, config_key, config_value, remark FROM sys_config WHERE config_key LIKE 'email.%';

