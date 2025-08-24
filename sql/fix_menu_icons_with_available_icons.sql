-- 修复菜单图标 - 使用现有可用的图标
-- 执行时间：2024年

-- 1. 主菜单图标设置
-- 1.1 首页/数据统计
UPDATE sys_menu SET icon = 'chart' WHERE menu_id = 0;

-- 1.2 个人邮件主菜单
UPDATE sys_menu SET icon = 'email' WHERE menu_id = 2000;

-- 1.3 批量操作主菜单
UPDATE sys_menu SET icon = 'tool' WHERE menu_id = 2070;

-- 1.4 邮件管理主菜单
UPDATE sys_menu SET icon = 'setting' WHERE menu_id = 2020;

-- 1.5 邮件监控主菜单
UPDATE sys_menu SET icon = 'monitor' WHERE menu_id = 3000;

-- 2. 个人邮件子菜单图标设置 - 使用现有图标
-- 2.1 收件箱 - 使用 message 图标
UPDATE sys_menu SET icon = 'message' WHERE menu_id = 2001;

-- 2.2 发件箱 - 使用 upload 图标
UPDATE sys_menu SET icon = 'upload' WHERE menu_id = 2004;

-- 2.3 星标邮件 - 使用 star 图标
UPDATE sys_menu SET icon = 'star' WHERE menu_id = 2007;

-- 2.4 已删除 - 使用 delete 图标（如果不存在，使用 edit 图标）
UPDATE sys_menu SET icon = 'edit' WHERE menu_id = 2010;

-- 3. 邮件管理子菜单图标设置
-- 3.1 发件人管理
UPDATE sys_menu SET icon = 'user' WHERE menu_id = 2021;

-- 3.2 收件人管理
UPDATE sys_menu SET icon = 'user' WHERE menu_id = 2027;

-- 3.3 群组管理
UPDATE sys_menu SET icon = 'peoples' WHERE menu_id = 2040;

-- 3.4 标签管理 - 使用 list 图标
UPDATE sys_menu SET icon = 'list' WHERE menu_id = 2050;

-- 3.5 邮件模板
UPDATE sys_menu SET icon = 'documentation' WHERE menu_id = 2060;

-- 4. 批量操作子菜单图标设置
-- 4.1 导入收件人
UPDATE sys_menu SET icon = 'upload' WHERE menu_id = 2071;

-- 4.2 邮件批量发送 - 使用 upload 图标
UPDATE sys_menu SET icon = 'upload' WHERE menu_id = 2080;

-- 5. 邮件监控子菜单图标设置
-- 5.1 任务管理
UPDATE sys_menu SET icon = 'list' WHERE menu_id = 3001;

-- 5.2 邮件追踪 - 使用 search 图标
UPDATE sys_menu SET icon = 'search' WHERE menu_id = 3010;

-- 5.3 邮件服务监控
UPDATE sys_menu SET icon = 'monitor' WHERE menu_id = 3020;

-- 6. 数据统计子菜单图标设置
-- 6.1 数据统计
UPDATE sys_menu SET icon = 'chart' WHERE menu_id = 2090;

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

-- 验证结果：查询所有没有图标的菜单
SELECT menu_id, menu_name, parent_id, icon, order_num
FROM sys_menu 
WHERE (icon IS NULL OR icon = '') AND menu_id IN (
    SELECT menu_id FROM sys_menu WHERE parent_id IN (2000, 2020, 2070, 3000, 2090)
    UNION
    SELECT menu_id FROM sys_menu WHERE parent_id = 0
)
ORDER BY parent_id, order_num;
