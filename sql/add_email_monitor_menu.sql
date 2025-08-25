-- 添加邮件监控菜单
-- 执行时间：2024年

-- 1. 添加邮件监控主菜单
INSERT INTO sys_menu VALUES (3000, '邮件监控', 0, 3, 'email-monitor', NULL, '', 'email-monitor', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', NOW(), '', NULL, '邮件监控管理');

-- 2. 添加任务管理子菜单
INSERT INTO sys_menu VALUES (3001, '任务管理', 3000, 1, 'task', 'email/task/index', '', 'task', 1, 0, 'C', '0', '0', 'email:task:list', 'list', 'admin', NOW(), '', NULL, '邮件任务管理');
INSERT INTO sys_menu VALUES (3002, '任务管理查询', 3001, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3003, '任务管理新增', 3001, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3004, '任务管理修改', 3001, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3005, '任务管理删除', 3001, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3006, '任务重新执行', 3001, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:restart', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3007, '任务停止', 3001, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:stop', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3008, '任务复制', 3001, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:copy', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3009, '执行记录', 3001, 8, '', '', '', '', 1, 0, 'F', '0', '0', 'email:task:execution', '#', 'admin', NOW(), '', NULL, '');

-- 3. 添加邮件追踪子菜单
INSERT INTO sys_menu VALUES (3010, '邮件追踪', 3000, 2, 'track-record', 'email/trackRecord/index', '', 'track-record', 1, 0, 'C', '0', '0', 'email:track:list', 'track', 'admin', NOW(), '', NULL, '邮件追踪记录管理');
INSERT INTO sys_menu VALUES (3011, '邮件追踪查询', 3010, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3012, '邮件追踪新增', 3010, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3013, '邮件追踪修改', 3010, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3014, '邮件追踪删除', 3010, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3015, '邮件追踪导出', 3010, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:track:export', '#', 'admin', NOW(), '', NULL, '');

-- 4. 添加邮件服务监控子菜单
INSERT INTO sys_menu VALUES (3020, '邮件服务监控', 3000, 3, 'monitor', 'email/monitor/index', '', 'monitor', 1, 0, 'C', '0', '0', 'email:monitor:list', 'monitor', 'admin', NOW(), '', NULL, '邮件服务监控管理');
INSERT INTO sys_menu VALUES (3021, '邮件服务监控查询', 3020, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:query', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3022, '邮件服务监控新增', 3020, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:add', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3023, '邮件服务监控修改', 3020, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:edit', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3024, '邮件服务监控删除', 3020, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:remove', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3025, '邮件服务监控导出', 3020, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'email:monitor:export', '#', 'admin', NOW(), '', NULL, '');

-- 5. 验证添加结果
-- 查询邮件监控菜单下的所有子菜单
SELECT menu_id, menu_name, order_num, path 
FROM sys_menu 
WHERE parent_id = 3000 
ORDER BY order_num;

