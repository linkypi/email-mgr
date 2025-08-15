-- 邮件管理系统菜单数据
-- 创建时间: 2024-01-01

-- 插入邮件管理主菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件管理', 0, 4, 'email', NULL, NULL, 1, 0, 'M', '0', '0', NULL, 'email', 'admin', NOW(), '', NULL, '邮件管理目录');

-- 获取邮件管理菜单ID
SET @emailMenuId = LAST_INSERT_ID();

-- 插入联系人管理子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('联系人管理', @emailMenuId, 1, 'contact', 'email/contact/index', NULL, 1, 0, 'C', '0', '0', 'email:contact:list', 'user', 'admin', NOW(), '', NULL, '联系人管理菜单');

-- 获取联系人管理菜单ID
SET @contactMenuId = LAST_INSERT_ID();

-- 插入联系人管理按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('联系人查询', @contactMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:contact:query', '#', 'admin', NOW(), '', NULL, ''),
('联系人新增', @contactMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:contact:add', '#', 'admin', NOW(), '', NULL, ''),
('联系人修改', @contactMenuId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:contact:edit', '#', 'admin', NOW(), '', NULL, ''),
('联系人删除', @contactMenuId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:contact:remove', '#', 'admin', NOW(), '', NULL, ''),
('联系人导出', @contactMenuId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:contact:export', '#', 'admin', NOW(), '', NULL, ''),
('联系人导入', @contactMenuId, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:contact:import', '#', 'admin', NOW(), '', NULL, '');

-- 插入联系人群组管理子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('联系人群组', @emailMenuId, 2, 'group', 'email/group/index', NULL, 1, 0, 'C', '0', '0', 'email:group:list', 'peoples', 'admin', NOW(), '', NULL, '联系人群组管理菜单');

-- 获取联系人群组管理菜单ID
SET @groupMenuId = LAST_INSERT_ID();

-- 插入联系人群组管理按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('联系人群组查询', @groupMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:group:query', '#', 'admin', NOW(), '', NULL, ''),
('联系人群组新增', @groupMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:group:add', '#', 'admin', NOW(), '', NULL, ''),
('联系人群组修改', @groupMenuId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:group:edit', '#', 'admin', NOW(), '', NULL, ''),
('联系人群组删除', @groupMenuId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:group:remove', '#', 'admin', NOW(), '', NULL, ''),
('联系人群组导出', @groupMenuId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:group:export', '#', 'admin', NOW(), '', NULL, '');

-- 插入销售数据管理子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('销售数据', @emailMenuId, 3, 'sales', 'email/sales/index', NULL, 1, 0, 'C', '0', '0', 'email:sales:list', 'money', 'admin', NOW(), '', NULL, '销售数据管理菜单');

-- 获取销售数据管理菜单ID
SET @salesMenuId = LAST_INSERT_ID();

-- 插入销售数据管理按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('销售数据查询', @salesMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:sales:query', '#', 'admin', NOW(), '', NULL, ''),
('销售数据新增', @salesMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:sales:add', '#', 'admin', NOW(), '', NULL, ''),
('销售数据修改', @salesMenuId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:sales:edit', '#', 'admin', NOW(), '', NULL, ''),
('销售数据删除', @salesMenuId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:sales:remove', '#', 'admin', NOW(), '', NULL, ''),
('销售数据导出', @salesMenuId, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:sales:export', '#', 'admin', NOW(), '', NULL, ''),
('销售数据导入', @salesMenuId, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:sales:import', '#', 'admin', NOW(), '', NULL, '');

-- 插入邮件账号管理子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件账号', @emailMenuId, 4, 'account', 'email/account/index', NULL, 1, 0, 'C', '0', '0', 'email:account:list', 'server', 'admin', NOW(), '', NULL, '邮件账号管理菜单');

-- 获取邮件账号管理菜单ID
SET @accountMenuId = LAST_INSERT_ID();

-- 插入邮件账号管理按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件账号查询', @accountMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:account:query', '#', 'admin', NOW(), '', NULL, ''),
('邮件账号新增', @accountMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:account:add', '#', 'admin', NOW(), '', NULL, ''),
('邮件账号修改', @accountMenuId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:account:edit', '#', 'admin', NOW(), '', NULL, ''),
('邮件账号删除', @accountMenuId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:account:remove', '#', 'admin', NOW(), '', NULL, '');

-- 插入邮件发送管理子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件发送', @emailMenuId, 5, 'send', 'email/send/index', NULL, 1, 0, 'C', '0', '0', 'email:send:list', 'send', 'admin', NOW(), '', NULL, '邮件发送管理菜单');

-- 获取邮件发送管理菜单ID
SET @sendMenuId = LAST_INSERT_ID();

-- 插入邮件发送管理按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件发送查询', @sendMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:send:query', '#', 'admin', NOW(), '', NULL, ''),
('邮件发送新增', @sendMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:send:add', '#', 'admin', NOW(), '', NULL, ''),
('邮件发送修改', @sendMenuId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:send:edit', '#', 'admin', NOW(), '', NULL, ''),
('邮件发送删除', @sendMenuId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:send:remove', '#', 'admin', NOW(), '', NULL, '');

-- 插入邮件模板管理子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件模板', @emailMenuId, 6, 'template', 'email/template/index', NULL, 1, 0, 'C', '0', '0', 'email:template:list', 'documentation', 'admin', NOW(), '', NULL, '邮件模板管理菜单');

-- 获取邮件模板管理菜单ID
SET @templateMenuId = LAST_INSERT_ID();

-- 插入邮件模板管理按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件模板查询', @templateMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:template:query', '#', 'admin', NOW(), '', NULL, ''),
('邮件模板新增', @templateMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:template:add', '#', 'admin', NOW(), '', NULL, ''),
('邮件模板修改', @templateMenuId, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:template:edit', '#', 'admin', NOW(), '', NULL, ''),
('邮件模板删除', @templateMenuId, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:template:remove', '#', 'admin', NOW(), '', NULL, '');

-- 插入邮件统计子菜单
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件统计', @emailMenuId, 7, 'statistics', 'email/statistics/index', NULL, 1, 0, 'C', '0', '0', 'email:statistics:list', 'chart', 'admin', NOW(), '', NULL, '邮件统计菜单');

-- 获取邮件统计菜单ID
SET @statisticsMenuId = LAST_INSERT_ID();

-- 插入邮件统计按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
('邮件统计查询', @statisticsMenuId, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:query', '#', 'admin', NOW(), '', NULL, ''),
('邮件统计导出', @statisticsMenuId, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:statistics:export', '#', 'admin', NOW(), '', NULL, '');
