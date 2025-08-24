-- 调整菜单顺序和修改首页内容
-- 执行时间：2024年

-- 1. 删除无用的菜单
-- 删除若依官网菜单
DELETE FROM sys_menu WHERE menu_id = 4;

-- 2. 调整主菜单顺序
-- 首页 (menu_id: 0) - order_num = 1
UPDATE sys_menu SET order_num = 1 WHERE menu_id = 0;

-- 个人邮件 (menu_id: 2000) - order_num = 2
UPDATE sys_menu SET order_num = 2 WHERE menu_id = 2000;

-- 批量操作 (menu_id: 2070) - order_num = 3
UPDATE sys_menu SET order_num = 3 WHERE menu_id = 2070;

-- 邮件管理 (menu_id: 2020) - order_num = 4
UPDATE sys_menu SET order_num = 4 WHERE menu_id = 2020;

-- 邮件监控 (menu_id: 3000) - order_num = 5
UPDATE sys_menu SET order_num = 5 WHERE menu_id = 3000;

-- 删除原来的数据统计菜单，因为首页已经变成了数据统计页面
DELETE FROM sys_menu WHERE menu_id = 2090;

-- 系统管理 (menu_id: 1) - order_num = 6
UPDATE sys_menu SET order_num = 6 WHERE menu_id = 1;

-- 系统监控 (menu_id: 2) - order_num = 7
UPDATE sys_menu SET order_num = 7 WHERE menu_id = 2;

-- 系统工具 (menu_id: 3) - order_num = 8
UPDATE sys_menu SET order_num = 8 WHERE menu_id = 3;

-- 3. 修改首页内容为数据统计页面
-- 更新首页的组件路径和标题
UPDATE sys_menu 
SET component = 'email/statistics/index',
    menu_name = '数据统计',
    icon = 'chart',
    perms = 'email:statistics:list',
    remark = '邮件数据统计'
WHERE menu_id = 0;

-- 4. 验证调整结果
-- 查询所有主菜单（parent_id = 0）按顺序排列
SELECT menu_id, menu_name, order_num, path, component, icon
FROM sys_menu 
WHERE parent_id = 0 
ORDER BY order_num;

-- 5. 查询数据统计页面信息
SELECT menu_id, menu_name, order_num, path, component, icon, perms, remark
FROM sys_menu 
WHERE menu_id = 0;
