-- 更新email_send_task表结构
-- 修改recipient_type字段长度，添加send_mode字段

-- 1. 修改recipient_type字段类型和长度
ALTER TABLE `email_send_task` 
MODIFY COLUMN `recipient_type` varchar(20) DEFAULT 'all' COMMENT '收件人类型(all全部 group群组 tag标签 manual手动)';

-- 2. 添加send_mode字段
ALTER TABLE `email_send_task` 
ADD COLUMN `send_mode` varchar(20) DEFAULT 'immediate' COMMENT '发送模式(immediate立即发送 scheduled定时发送)' AFTER `account_ids`;

-- 3. 更新现有数据的recipient_type值（如果需要）
-- 将旧的数字格式转换为新的字符串格式
UPDATE `email_send_task` SET `recipient_type` = 'all' WHERE `recipient_type` = '1';
UPDATE `email_send_task` SET `recipient_type` = 'group' WHERE `recipient_type` = '2';
UPDATE `email_send_task` SET `recipient_type` = 'tag' WHERE `recipient_type` = '3';
UPDATE `email_send_task` SET `recipient_type` = 'manual' WHERE `recipient_type` = '4';

