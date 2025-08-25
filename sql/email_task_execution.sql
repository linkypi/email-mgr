-- 邮件任务执行表
-- 用于记录每个邮件发送任务的执行情况

CREATE TABLE `email_task_execution` (
  `execution_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '执行ID',
  `task_id` bigint(20) NOT NULL COMMENT '任务ID',
  `execution_status` char(1) NOT NULL DEFAULT '0' COMMENT '执行状态(0未开始 1执行中 2已完成 3执行失败 4执行中断)',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `execution_user` varchar(64) DEFAULT NULL COMMENT '执行人',
  `execution_ip` varchar(128) DEFAULT NULL COMMENT '执行IP',
  `total_count` int(11) DEFAULT 0 COMMENT '总发送数量',
  `sent_count` int(11) DEFAULT 0 COMMENT '已发送数量',
  `success_count` int(11) DEFAULT 0 COMMENT '成功数量',
  `failed_count` int(11) DEFAULT 0 COMMENT '失败数量',
  `error_message` text DEFAULT NULL COMMENT '错误信息',
  `execution_log` text DEFAULT NULL COMMENT '执行日志',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`execution_id`),
  KEY `idx_task_id` (`task_id`),
  KEY `idx_execution_status` (`execution_status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_execution_user` (`execution_user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件任务执行表';

-- 添加邮件任务执行相关菜单权限
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(2200, '任务管理', 2000, 7, 'task', 'email/task/index', NULL, 1, 0, 'C', '0', '0', 'email:task:list', 'list', 'admin', NOW(), 'admin', NOW(), '邮件任务管理菜单'),
(2201, '任务管理查询', 2200, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:query', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2202, '任务管理新增', 2200, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:add', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2203, '任务管理修改', 2200, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:edit', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2204, '任务管理删除', 2200, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:remove', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2205, '任务重新执行', 2200, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:restart', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2206, '任务停止', 2200, 6, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:stop', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2207, '任务复制', 2200, 7, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:copy', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2208, '执行记录', 2200, 8, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:task:execution', '#', 'admin', NOW(), 'admin', NOW(), '');

-- 添加IMAP监听管理菜单
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES 
(2300, 'IMAP监听', 2000, 8, 'imap', 'email/imap/index', NULL, 1, 0, 'C', '0', '0', 'email:imap:list', 'monitor', 'admin', NOW(), 'admin', NOW(), 'IMAP监听管理菜单'),
(2301, 'IMAP监听查询', 2300, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:imap:query', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2302, 'IMAP监听启动', 2300, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:imap:start', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2303, 'IMAP监听停止', 2300, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:imap:stop', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2304, 'IMAP监听重启', 2300, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:imap:restart', '#', 'admin', NOW(), 'admin', NOW(), ''),
(2305, 'IMAP手动同步', 2300, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'email:imap:sync', '#', 'admin', NOW(), 'admin', NOW(), '');

-- 更新菜单顺序
UPDATE `sys_menu` SET `order_num` = 7 WHERE `menu_id` = 2200; -- 任务管理
UPDATE `sys_menu` SET `order_num` = 8 WHERE `menu_id` = 2300; -- IMAP监听
UPDATE `sys_menu` SET `order_num` = 9 WHERE `menu_id` = 2100; -- 邮件统计

