-- 删除个人邮件菜单下的任务管理、IMAP监听和邮件统计菜单
-- 执行时间：2024年

-- 1. 删除任务管理菜单及其子菜单
DELETE FROM sys_menu WHERE menu_id IN (2200, 2201, 2202, 2203, 2204, 2205, 2206, 2207, 2208);

-- 2. 删除IMAP监听菜单及其子菜单
DELETE FROM sys_menu WHERE menu_id IN (2300, 2301, 2302, 2303, 2304, 2305);

-- 3. 删除邮件统计菜单及其子菜单
DELETE FROM sys_menu WHERE menu_id IN (2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107);

-- 4. 重新调整个人邮件菜单下剩余菜单的顺序
-- 收件箱 (2001) - order_num = 1
UPDATE sys_menu SET order_num = 1 WHERE menu_id = 2001;

-- 发件箱 (2004) - order_num = 2  
UPDATE sys_menu SET order_num = 2 WHERE menu_id = 2004;

-- 星标邮件 (2007) - order_num = 3
UPDATE sys_menu SET order_num = 3 WHERE menu_id = 2007;

-- 已删除 (2010) - order_num = 4
UPDATE sys_menu SET order_num = 4 WHERE menu_id = 2010;

-- 5. 验证删除结果
-- 查询个人邮件菜单下的所有子菜单
SELECT menu_id, menu_name, order_num, path 
FROM sys_menu 
WHERE parent_id = 2000 
ORDER BY order_num;

