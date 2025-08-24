-- 修复重复的邮件监控菜单问题
-- 执行时间：2024年

-- 1. 删除可能存在的重复邮件监控菜单
-- 删除所有名为"邮件监控"的菜单（除了menu_id=3000的）
DELETE FROM sys_menu 
WHERE menu_name = '邮件监控' 
AND menu_id != 3000;

-- 2. 删除可能存在的重复子菜单
-- 删除任务管理菜单（除了menu_id=3001的）
DELETE FROM sys_menu 
WHERE menu_name = '任务管理' 
AND menu_id != 3001;

-- 删除邮件追踪菜单（除了menu_id=3010的）
DELETE FROM sys_menu 
WHERE menu_name = '邮件追踪' 
AND menu_id != 3010;

-- 删除邮件服务监控菜单（除了menu_id=3020的）
DELETE FROM sys_menu 
WHERE menu_name = '邮件服务监控' 
AND menu_id != 3020;

-- 3. 确保邮件监控主菜单存在且正确
-- 如果邮件监控主菜单不存在，则添加它
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, perms, type, visible, status, is_frame, is_cache, menu_type, visible, status, icon, create_by, create_time, update_by, update_time, remark)
SELECT 3000, '邮件监控', 0, 3, 'email-monitor', NULL, '', '', 'M', '0', '0', 1, 0, '', 'monitor', 'admin', NOW(), '', NULL, '邮件监控管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3000);

-- 4. 确保任务管理子菜单存在且正确
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, perms, type, visible, status, is_frame, is_cache, menu_type, visible, status, icon, create_by, create_time, update_by, update_time, remark)
SELECT 3001, '任务管理', 3000, 1, 'task', 'email/task/index', '', 'email:task:list', 'C', '0', '0', 1, 0, '', 'list', 'admin', NOW(), '', NULL, '邮件任务管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3001);

-- 5. 确保邮件追踪子菜单存在且正确
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, perms, type, visible, status, is_frame, is_cache, menu_type, visible, status, icon, create_by, create_time, update_by, update_time, remark)
SELECT 3010, '邮件追踪', 3000, 2, 'track-record', 'email/trackRecord/index', '', 'email:track:list', 'C', '0', '0', 1, 0, '', 'track', 'admin', NOW(), '', NULL, '邮件追踪记录管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3010);

-- 6. 确保邮件服务监控子菜单存在且正确
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, perms, type, visible, status, is_frame, is_cache, menu_type, visible, status, icon, create_by, create_time, update_by, update_time, remark)
SELECT 3020, '邮件服务监控', 3000, 3, 'monitor', 'email/monitor/index', '', 'email:monitor:list', 'C', '0', '0', 1, 0, '', 'monitor', 'admin', NOW(), '', NULL, '邮件服务监控管理'
WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 3020);

-- 7. 验证修复结果
-- 查询所有邮件相关的菜单
SELECT menu_id, menu_name, parent_id, order_num, path 
FROM sys_menu 
WHERE menu_name LIKE '%邮件%' 
ORDER BY parent_id, order_num;

-- 8. 查询邮件监控菜单结构
SELECT menu_id, menu_name, order_num, path 
FROM sys_menu 
WHERE parent_id = 3000 
ORDER BY order_num;
