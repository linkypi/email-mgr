-- 删除重复的邮件管理菜单
-- 执行时间：2024年

-- 1. 删除旧的邮件管理菜单（如果存在重复的）
-- 注意：这里只删除可能重复的菜单，保留原有的邮件管理菜单（2020）

-- 查找并删除可能重复的邮件管理菜单（除了menu_id=2020的）
DELETE FROM sys_menu 
WHERE menu_name = '邮件管理' 
AND menu_id != 2020 
AND parent_id = 0;

-- 2. 删除可能存在的重复子菜单
-- 删除可能重复的任务管理菜单（除了新添加的3001）
DELETE FROM sys_menu 
WHERE menu_name = '任务管理' 
AND menu_id != 3001 
AND parent_id != 3000;

-- 删除可能重复的邮件跟踪记录菜单（除了新添加的3010）
DELETE FROM sys_menu 
WHERE menu_name IN ('邮件跟踪记录', '邮件追踪') 
AND menu_id != 3010 
AND parent_id != 3000;

-- 删除可能重复的邮件服务监控菜单（除了新添加的3020）
DELETE FROM sys_menu 
WHERE menu_name = '邮件服务监控' 
AND menu_id != 3020 
AND parent_id != 3000;

-- 3. 验证删除结果
-- 查询所有邮件相关的菜单
SELECT menu_id, menu_name, parent_id, order_num, path 
FROM sys_menu 
WHERE menu_name LIKE '%邮件%' 
ORDER BY parent_id, order_num;

-- 4. 查询邮件监控菜单结构
SELECT menu_id, menu_name, order_num, path 
FROM sys_menu 
WHERE parent_id = 3000 
ORDER BY order_num;
