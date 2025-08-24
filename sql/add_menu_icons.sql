-- 为菜单添加图标
-- 执行时间：2024年

-- 1. 为邮件管理主菜单添加图标
UPDATE sys_menu 
SET icon = 'setting' 
WHERE menu_id = 2020 AND (icon IS NULL OR icon = '');

-- 2. 为邮件追踪菜单添加图标
UPDATE sys_menu 
SET icon = 'track' 
WHERE menu_id = 3010 AND (icon IS NULL OR icon = '');

-- 3. 为邮件服务监控菜单添加图标
UPDATE sys_menu 
SET icon = 'monitor' 
WHERE menu_id = 3020 AND (icon IS NULL OR icon = '');

-- 4. 为邮件模板菜单添加图标
UPDATE sys_menu 
SET icon = 'documentation' 
WHERE menu_id = 2060 AND (icon IS NULL OR icon = '');

-- 5. 为标签管理菜单添加图标
UPDATE sys_menu 
SET icon = 'collection-tag' 
WHERE menu_id = 2050 AND (icon IS NULL OR icon = '');

-- 6. 为发件人管理菜单添加图标
UPDATE sys_menu 
SET icon = 'user' 
WHERE menu_id = 2021 AND (icon IS NULL OR icon = '');

-- 7. 为收件人管理菜单添加图标
UPDATE sys_menu 
SET icon = 'user' 
WHERE menu_id = 2027 AND (icon IS NULL OR icon = '');

-- 8. 为群组管理菜单添加图标
UPDATE sys_menu 
SET icon = 'peoples' 
WHERE menu_id = 2040 AND (icon IS NULL OR icon = '');

-- 9. 为任务管理菜单添加图标
UPDATE sys_menu 
SET icon = 'list' 
WHERE menu_id = 3001 AND (icon IS NULL OR icon = '');

-- 10. 为导入收件人菜单添加图标
UPDATE sys_menu 
SET icon = 'upload' 
WHERE menu_id = 2071 AND (icon IS NULL OR icon = '');

-- 11. 为邮件批量发送菜单添加图标
UPDATE sys_menu 
SET icon = 's-promotion' 
WHERE menu_id = 2080 AND (icon IS NULL OR icon = '');

-- 12. 为数据统计菜单添加图标
UPDATE sys_menu 
SET icon = 'chart' 
WHERE menu_id = 2090 AND (icon IS NULL OR icon = '');

-- 13. 为个人邮件主菜单添加图标
UPDATE sys_menu 
SET icon = 'email' 
WHERE menu_id = 2000 AND (icon IS NULL OR icon = '');

-- 14. 为批量操作主菜单添加图标
UPDATE sys_menu 
SET icon = 'tool' 
WHERE menu_id = 2070 AND (icon IS NULL OR icon = '');

-- 15. 为邮件监控主菜单添加图标
UPDATE sys_menu 
SET icon = 'monitor' 
WHERE menu_id = 3000 AND (icon IS NULL OR icon = '');

-- 16. 为首页/数据统计菜单添加图标
UPDATE sys_menu 
SET icon = 'chart' 
WHERE menu_id = 0 AND (icon IS NULL OR icon = '');

-- 17. 为个人邮件子菜单添加图标
-- 17.1 收件箱
UPDATE sys_menu 
SET icon = 'inbox' 
WHERE menu_id = 2001 AND (icon IS NULL OR icon = '');

-- 17.2 发件箱
UPDATE sys_menu 
SET icon = 's-promotion' 
WHERE menu_id = 2004 AND (icon IS NULL OR icon = '');

-- 17.3 星标邮件
UPDATE sys_menu 
SET icon = 'star-on' 
WHERE menu_id = 2007 AND (icon IS NULL OR icon = '');

-- 17.4 已删除
UPDATE sys_menu 
SET icon = 'delete' 
WHERE menu_id = 2010 AND (icon IS NULL OR icon = '');

-- 验证结果：查询所有主菜单的图标设置
SELECT menu_id, menu_name, icon, order_num
FROM sys_menu 
WHERE parent_id = 0 
ORDER BY order_num;

-- 验证结果：查询所有子菜单的图标设置
SELECT menu_id, menu_name, parent_id, icon, order_num
FROM sys_menu 
WHERE parent_id IN (2000, 2020, 2070, 3000, 2090)
ORDER BY parent_id, order_num;
