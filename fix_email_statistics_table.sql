-- 修复 email_statistics 表结构
-- 解决 Field 'stat_date' doesn't have a default value 错误

-- 1. 检查表结构
DESCRIBE `email_statistics`;

-- 2. 如果表不存在，创建表
CREATE TABLE IF NOT EXISTS `email_statistics` (
  `stat_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` date NOT NULL COMMENT '统计日期',
  `stat_type` char(1) NOT NULL COMMENT '统计类型(1日统计 2月统计 3年统计)',
  `total_sent` int(11) DEFAULT 0 COMMENT '总发送数',
  `total_delivered` int(11) DEFAULT 0 COMMENT '总送达数',
  `total_opened` int(11) DEFAULT 0 COMMENT '总打开数',
  `total_replied` int(11) DEFAULT 0 COMMENT '总回复数',
  `delivery_rate` decimal(5,2) DEFAULT 0.00 COMMENT '送达率',
  `open_rate` decimal(5,2) DEFAULT 0.00 COMMENT '打开率',
  `reply_rate` decimal(5,2) DEFAULT 0.00 COMMENT '回复率',
  `message_id` varchar(255) DEFAULT NULL COMMENT '邮件Message-ID',
  `status` varchar(50) DEFAULT NULL COMMENT '邮件状态',
  `delivered_time` datetime DEFAULT NULL COMMENT '送达时间',
  `opened_time` datetime DEFAULT NULL COMMENT '打开时间',
  `replied_time` datetime DEFAULT NULL COMMENT '回复时间',
  `clicked_time` datetime DEFAULT NULL COMMENT '点击时间',
  `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`stat_id`),
  UNIQUE KEY `uk_date_type` (`stat_date`, `stat_type`),
  KEY `idx_stat_date` (`stat_date`),
  KEY `idx_stat_type` (`stat_type`),
  KEY `idx_message_id` (`message_id`),
  KEY `idx_status` (`status`),
  KEY `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='邮件统计表';

-- 3. 如果表已存在但缺少字段，添加缺失的字段
ALTER TABLE `email_statistics` 
ADD COLUMN IF NOT EXISTS `account_id` bigint(20) DEFAULT NULL COMMENT '邮箱账号ID' AFTER `clicked_time`;

-- 4. 添加索引（如果不存在）
CREATE INDEX IF NOT EXISTS `idx_account_id` ON `email_statistics` (`account_id`);

-- 5. 验证表结构
DESCRIBE `email_statistics`;

-- 6. 显示表结构信息
SHOW CREATE TABLE `email_statistics`;
