-- 扩展邮件服务状态定义
-- 执行时间：2024-01-01

-- 1. 更新邮件服务状态字典数据，增加更多状态
-- 先删除旧的状态数据
DELETE FROM sys_dict_data WHERE dict_type = 'email_service_status';

-- 重新插入扩展的状态数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES
(1, '停止', '0', 'email_service_status', '', 'info', 'Y', '0', 'admin', NOW(), '', NULL, '服务停止状态'),
(2, '运行中', '1', 'email_service_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '服务正常运行状态'),
(3, '连接中', '2', 'email_service_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '正在建立网络连接'),
(4, '已连接', '3', 'email_service_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '网络连接已建立'),
(5, '网络超时', '4', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '网络连接超时'),
(6, '认证失败', '5', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '用户名或密码认证失败'),
(7, 'SSL错误', '6', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, 'SSL/TLS连接错误'),
(8, '端口错误', '7', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '端口配置错误或端口被占用'),
(9, '主机不可达', '8', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '邮件服务器主机不可达'),
(10, '防火墙阻止', '9', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '防火墙阻止连接'),
(11, '服务异常', '10', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '其他服务异常');

-- 2. 创建邮件服务状态枚举表（用于后端代码参考）
CREATE TABLE IF NOT EXISTS `email_service_status_enum` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `status_code` varchar(10) NOT NULL COMMENT '状态代码',
  `status_name` varchar(50) NOT NULL COMMENT '状态名称',
  `status_desc` varchar(200) DEFAULT NULL COMMENT '状态描述',
  `css_class` varchar(50) DEFAULT NULL COMMENT 'CSS样式类',
  `is_error` tinyint(1) DEFAULT 0 COMMENT '是否为错误状态：0-否，1-是',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_status_code` (`status_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务状态枚举表';

-- 插入状态枚举数据
INSERT INTO `email_service_status_enum` (`status_code`, `status_name`, `status_desc`, `css_class`, `is_error`) VALUES
('0', 'STOPPED', '服务停止状态', 'info', 0),
('1', 'RUNNING', '服务正常运行状态', 'success', 0),
('2', 'CONNECTING', '正在建立网络连接', 'warning', 0),
('3', 'CONNECTED', '网络连接已建立', 'primary', 0),
('4', 'TIMEOUT', '网络连接超时', 'danger', 1),
('5', 'AUTH_FAILED', '用户名或密码认证失败', 'danger', 1),
('6', 'SSL_ERROR', 'SSL/TLS连接错误', 'danger', 1),
('7', 'PORT_ERROR', '端口配置错误或端口被占用', 'danger', 1),
('8', 'HOST_UNREACHABLE', '邮件服务器主机不可达', 'danger', 1),
('9', 'FIREWALL_BLOCKED', '防火墙阻止连接', 'danger', 1),
('10', 'SERVICE_ERROR', '其他服务异常', 'danger', 1);

-- 3. 创建邮件服务状态统计视图
CREATE OR REPLACE VIEW `v_email_service_status_stats` AS
SELECT 
  'IMAP' as service_type,
  COUNT(*) as total_accounts,
  SUM(CASE WHEN imap_status = '0' THEN 1 ELSE 0 END) as stopped_count,
  SUM(CASE WHEN imap_status = '1' THEN 1 ELSE 0 END) as running_count,
  SUM(CASE WHEN imap_status = '2' THEN 1 ELSE 0 END) as connecting_count,
  SUM(CASE WHEN imap_status = '3' THEN 1 ELSE 0 END) as connected_count,
  SUM(CASE WHEN imap_status IN ('4','5','6','7','8','9','10') THEN 1 ELSE 0 END) as error_count,
  SUM(CASE WHEN imap_status IN ('4','5','6','7','8','9','10') THEN 1 ELSE 0 END) / COUNT(*) * 100 as error_rate
FROM email_service_monitor
UNION ALL
SELECT 
  'SMTP' as service_type,
  COUNT(*) as total_accounts,
  SUM(CASE WHEN smtp_status = '0' THEN 1 ELSE 0 END) as stopped_count,
  SUM(CASE WHEN smtp_status = '1' THEN 1 ELSE 0 END) as running_count,
  SUM(CASE WHEN smtp_status = '2' THEN 1 ELSE 0 END) as connecting_count,
  SUM(CASE WHEN smtp_status = '3' THEN 1 ELSE 0 END) as connected_count,
  SUM(CASE WHEN smtp_status IN ('4','5','6','7','8','9','10') THEN 1 ELSE 0 END) as error_count,
  SUM(CASE WHEN smtp_status IN ('4','5','6','7','8','9','10') THEN 1 ELSE 0 END) / COUNT(*) * 100 as error_rate
FROM email_service_monitor;

-- 4. 创建邮件服务状态监控配置表
CREATE TABLE IF NOT EXISTS `email_service_monitor_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` varchar(100) NOT NULL COMMENT '配置键',
  `config_value` varchar(500) DEFAULT NULL COMMENT '配置值',
  `config_desc` varchar(200) DEFAULT NULL COMMENT '配置描述',
  `create_by` varchar(64) DEFAULT 'system' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT 'system' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件服务监控配置表';

-- 插入监控配置
INSERT INTO `email_service_monitor_config` (`config_key`, `config_value`, `config_desc`) VALUES
('imap_check_interval', '30', 'IMAP服务检查间隔（秒）'),
('smtp_check_interval', '60', 'SMTP服务检查间隔（秒）'),
('connection_timeout', '10000', '连接超时时间（毫秒）'),
('read_timeout', '30000', '读取超时时间（毫秒）'),
('retry_count', '3', '连接失败重试次数'),
('retry_delay', '5000', '重试延迟时间（毫秒）'),
('error_notification_enabled', '1', '是否启用错误通知：0-禁用，1-启用'),
('error_notification_threshold', '3', '错误通知阈值（连续失败次数）'),
('auto_reconnect_enabled', '1', '是否启用自动重连：0-禁用，1-启用'),
('auto_reconnect_delay', '30000', '自动重连延迟时间（毫秒）')
ON DUPLICATE KEY UPDATE 
  `config_value` = VALUES(`config_value`),
  `config_desc` = VALUES(`config_desc`),
  `update_time` = NOW();

-- 5. 验证数据插入成功
SELECT '邮件服务状态字典数据' as check_item, COUNT(*) as count FROM sys_dict_data WHERE dict_type = 'email_service_status'
UNION ALL
SELECT '状态枚举数据' as check_item, COUNT(*) as count FROM email_service_status_enum
UNION ALL
SELECT '监控配置数据' as check_item, COUNT(*) as count FROM email_service_monitor_config;

-- 6. 显示状态统计
SELECT * FROM v_email_service_status_stats;

