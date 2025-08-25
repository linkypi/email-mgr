-- 更新邮件服务状态字典数据
-- 执行时间：2024-01-01
-- 说明：更新邮件服务状态字典，支持11种不同的网络连接状态

-- 1. 删除旧的状态数据
DELETE FROM sys_dict_data WHERE dict_type = 'email_service_status';

-- 2. 重新插入扩展的状态数据
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

-- 3. 验证数据插入成功
SELECT '邮件服务状态字典数据' as check_item, COUNT(*) as count FROM sys_dict_data WHERE dict_type = 'email_service_status';

-- 4. 显示插入的状态数据
SELECT dict_sort, dict_label, dict_value, list_class, remark 
FROM sys_dict_data 
WHERE dict_type = 'email_service_status' 
ORDER BY dict_sort;

