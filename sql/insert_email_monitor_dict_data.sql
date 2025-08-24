-- 插入邮件服务状态字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('邮件服务状态', 'email_service_status', '0', 'admin', NOW(), '', NULL, '邮件服务状态字典');

-- 插入邮件服务状态字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES
(1, '停止', '0', 'email_service_status', '', 'info', 'Y', '0', 'admin', NOW(), '', NULL, '服务停止状态'),
(2, '运行中', '1', 'email_service_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '服务正常运行状态'),
(3, '异常', '2', 'email_service_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '服务异常状态');

-- 插入监控状态字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('监控状态', 'monitor_status', '0', 'admin', NOW(), '', NULL, '监控状态字典');

-- 插入监控状态字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES
(1, '停止', '0', 'monitor_status', '', 'info', 'Y', '0', 'admin', NOW(), '', NULL, '监控停止状态'),
(2, '运行中', '1', 'monitor_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '监控正常运行状态');

-- 插入服务类型字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('服务类型', 'service_type', '0', 'admin', NOW(), '', NULL, '服务类型字典');

-- 插入服务类型字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES
(1, 'IMAP', 'IMAP', 'service_type', '', 'primary', 'Y', '0', 'admin', NOW(), '', NULL, 'IMAP服务'),
(2, 'SMTP', 'SMTP', 'service_type', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, 'SMTP服务');

-- 插入操作类型字典类型
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark) 
VALUES ('操作类型', 'operation_type', '0', 'admin', NOW(), '', NULL, '操作类型字典');

-- 插入操作类型字典数据
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) VALUES
(1, '启动', 'START', 'operation_type', '', 'success', 'Y', '0', 'admin', NOW(), '', NULL, '启动操作'),
(2, '停止', 'STOP', 'operation_type', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '停止操作'),
(3, '重启', 'RESTART', 'operation_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '重启操作'),
(4, '测试', 'TEST', 'operation_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '测试操作'),
(5, '监控', 'MONITOR', 'operation_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '监控操作');
