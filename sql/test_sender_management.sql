-- 测试发件人管理功能的SQL脚本

-- 1. 创建发件人信息表
CREATE TABLE IF NOT EXISTS `email_sender` (
  `sender_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '发件人ID',
  `sender_name` varchar(100) NOT NULL COMMENT '发件人姓名',
  `company` varchar(200) DEFAULT NULL COMMENT '公司名称',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `position` varchar(100) DEFAULT NULL COMMENT '职位',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(500) DEFAULT NULL COMMENT '地址',
  `description` text COMMENT '发件人描述',
  `level` char(1) DEFAULT '3' COMMENT '等级(1重要 2普通 3一般)',
  `tags` varchar(500) DEFAULT NULL COMMENT '标签(逗号分隔)',
  `total_accounts` int(11) DEFAULT 0 COMMENT '关联邮箱账号总数',
  `active_accounts` int(11) DEFAULT 0 COMMENT '活跃邮箱账号数',
  `total_sent` int(11) DEFAULT 0 COMMENT '总发送邮件数',
  `total_replied` int(11) DEFAULT 0 COMMENT '总回复邮件数',
  `reply_rate` decimal(5,2) DEFAULT 0.00 COMMENT '回复率',
  `status` char(1) DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` char(1) DEFAULT '0' COMMENT '删除标志(0代表存在 2代表删除)',
  PRIMARY KEY (`sender_id`),
  KEY `idx_sender_name` (`sender_name`),
  KEY `idx_company` (`company`),
  KEY `idx_level` (`level`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发件人信息表';

-- 2. 为邮箱账号表添加发件人关联字段
ALTER TABLE `email_account` 
ADD COLUMN IF NOT EXISTS `sender_id` bigint(20) DEFAULT NULL COMMENT '关联发件人ID' AFTER `account_id`,
ADD INDEX IF NOT EXISTS `idx_sender_id` (`sender_id`);

-- 3. 插入测试数据
INSERT INTO `email_sender` VALUES 
(1, '张三', 'ABC科技有限公司', '市场部', '市场经理', '13800138001', '北京市朝阳区', '负责公司市场推广邮件发送', '1', '市场,推广,重要', 0, 0, 0, 0, 0.00, '0', '主要发件人', 'admin', NOW(), 'admin', NOW(), '0'),
(2, '李四', 'ABC科技有限公司', '客服部', '客服主管', '13800138002', '北京市朝阳区', '负责客户服务邮件发送', '2', '客服,服务', 0, 0, 0, 0, 0.00, '0', '客服发件人', 'admin', NOW(), 'admin', NOW(), '0'),
(3, '王五', 'XYZ贸易公司', '销售部', '销售总监', '13800138003', '上海市浦东区', '负责销售邮件发送', '1', '销售,重要', 0, 0, 0, 0, 0.00, '0', '销售发件人', 'admin', NOW(), 'admin', NOW(), '0');

-- 4. 更新现有邮箱账号，关联到发件人
UPDATE `email_account` SET `sender_id` = 1 WHERE `account_id` = 1;
UPDATE `email_account` SET `sender_id` = 1 WHERE `account_id` = 2;
UPDATE `email_account` SET `sender_id` = 2 WHERE `account_id` = 3;

-- 5. 更新发件人统计信息
UPDATE `email_sender` SET 
  `total_accounts` = (SELECT COUNT(*) FROM `email_account` WHERE `sender_id` = 1 AND `deleted` = '0'),
  `active_accounts` = (SELECT COUNT(*) FROM `email_account` WHERE `sender_id` = 1 AND `status` = '0' AND `deleted` = '0')
WHERE `sender_id` = 1;

UPDATE `email_sender` SET 
  `total_accounts` = (SELECT COUNT(*) FROM `email_account` WHERE `sender_id` = 2 AND `deleted` = '0'),
  `active_accounts` = (SELECT COUNT(*) FROM `email_account` WHERE `sender_id` = 2 AND `status` = '0' AND `deleted` = '0')
WHERE `sender_id` = 2;

UPDATE `email_sender` SET 
  `total_accounts` = (SELECT COUNT(*) FROM `email_account` WHERE `sender_id` = 3 AND `deleted` = '0'),
  `active_accounts` = (SELECT COUNT(*) FROM `email_account` WHERE `sender_id` = 3 AND `status` = '0' AND `deleted` = '0')
WHERE `sender_id` = 3;

-- 6. 测试查询语句
-- 查询所有发件人信息
SELECT * FROM `email_sender` WHERE `deleted` = '0' ORDER BY `create_time` DESC;

-- 查询发件人及其关联的邮箱账号
SELECT 
    s.sender_id,
    s.sender_name,
    s.company,
    s.department,
    s.total_accounts,
    s.active_accounts,
    a.account_id,
    a.account_name,
    a.email_address,
    a.status as account_status
FROM `email_sender` s
LEFT JOIN `email_account` a ON s.sender_id = a.sender_id AND a.deleted = '0'
WHERE s.deleted = '0'
ORDER BY s.sender_id, a.create_time DESC;

-- 查询特定发件人的邮箱账号
SELECT 
    a.account_id,
    a.account_name,
    a.email_address,
    a.smtp_host,
    a.smtp_port,
    a.status,
    s.sender_name
FROM `email_account` a
LEFT JOIN `email_sender` s ON a.sender_id = s.sender_id
WHERE a.sender_id = 1 AND a.deleted = '0'
ORDER BY a.create_time DESC;

-- 7. 验证数据完整性
SELECT 
    '发件人总数' as type,
    COUNT(*) as count
FROM `email_sender` 
WHERE `deleted` = '0'

UNION ALL

SELECT 
    '邮箱账号总数' as type,
    COUNT(*) as count
FROM `email_account` 
WHERE `deleted` = '0'

UNION ALL

SELECT 
    '已关联发件人的邮箱账号数' as type,
    COUNT(*) as count
FROM `email_account` 
WHERE `sender_id` IS NOT NULL AND `deleted` = '0'

UNION ALL

SELECT 
    '未关联发件人的邮箱账号数' as type,
    COUNT(*) as count
FROM `email_account` 
WHERE `sender_id` IS NULL AND `deleted` = '0';


