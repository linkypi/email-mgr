# 邮件管理系统数据库迁移指南

## 概述

本目录包含了邮件管理系统的数据库迁移脚本，采用Flyway版本管理方式，每个SQL文件都有独立的版本号，方便系统升级和迁移。

## 目录结构

```
db/
├── V1.0.0__Create_email_personal_table.sql      # 创建个人邮件表
├── V1.0.1__Insert_email_personal_test_data.sql  # 插入测试数据
├── V1.0.2__Create_email_menu.sql                # 创建菜单配置
├── V1.1.0__Add_email_personal_fields.sql        # 添加邮件表字段
├── V1.1.1__Fix_email_personal_status.sql        # 修复状态字段
├── V1.2.0__Add_sender_id_to_send_task.sql       # 发送任务表增强
├── V1.2.1__Update_contact_tags_structure.sql    # 联系人标签结构优化
└── README.md                                     # 本文件
```

## 版本说明

### V1.0.0 - 创建个人邮件表
- **创建时间**: 2024-01-15
- **功能**: 创建个人邮件表的基础结构
- **包含**: 表结构、索引、约束

### V1.0.1 - 插入测试数据
- **创建时间**: 2024-01-15
- **功能**: 插入个人邮件表的测试数据
- **包含**: 收件箱、发件箱、星标邮件、已删除邮件的测试数据

### V1.0.2 - 创建菜单配置
- **创建时间**: 2024-01-15
- **功能**: 创建邮件管理相关的菜单配置
- **包含**: 个人邮件、邮件管理、批量操作、数据统计等菜单

### V1.1.0 - 添加邮件表字段
- **创建时间**: 2024-01-20
- **功能**: 为个人邮件表添加新字段
- **包含**: 回复状态、送达状态、Message-ID字段及索引

### V1.1.1 - 修复状态字段
- **创建时间**: 2024-01-20
- **功能**: 统一邮件状态字段的值
- **包含**: 状态值标准化、布尔字段统一

### V1.2.0 - 发送任务表增强
- **创建时间**: 2024-01-25
- **功能**: 为发送任务表添加发件人ID字段
- **包含**: 发送任务表创建、发件人ID字段、索引

### V1.2.1 - 联系人标签结构优化
- **创建时间**: 2024-01-25
- **功能**: 优化联系人标签结构，支持多标签
- **包含**: 标签表、联系人表、关联表、默认标签数据

## 迁移指南

### 全新安装

如果是全新安装，请按版本号顺序执行所有脚本：

```bash
# 按版本号顺序执行所有SQL文件
mysql -u username -p database_name < db/V1.0.0__Create_email_personal_table.sql
mysql -u username -p database_name < db/V1.0.1__Insert_email_personal_test_data.sql
mysql -u username -p database_name < db/V1.0.2__Create_email_menu.sql
mysql -u username -p database_name < db/V1.1.0__Add_email_personal_fields.sql
mysql -u username -p database_name < db/V1.1.1__Fix_email_personal_status.sql
mysql -u username -p database_name < db/V1.2.0__Add_sender_id_to_send_task.sql
mysql -u username -p database_name < db/V1.2.1__Update_contact_tags_structure.sql
```

### 版本升级

#### 从V1.0.x升级到V1.1.x
```bash
mysql -u username -p database_name < db/V1.1.0__Add_email_personal_fields.sql
mysql -u username -p database_name < db/V1.1.1__Fix_email_personal_status.sql
```

#### 从V1.1.x升级到V1.2.x
```bash
mysql -u username -p database_name < db/V1.2.0__Add_sender_id_to_send_task.sql
mysql -u username -p database_name < db/V1.2.1__Update_contact_tags_structure.sql
```

#### 从V1.0.x直接升级到V1.2.x
```bash
# 先执行V1.1.x的脚本
mysql -u username -p database_name < db/V1.1.0__Add_email_personal_fields.sql
mysql -u username -p database_name < db/V1.1.1__Fix_email_personal_status.sql

# 再执行V1.2.x的脚本
mysql -u username -p database_name < db/V1.2.0__Add_sender_id_to_send_task.sql
mysql -u username -p database_name < db/V1.2.1__Update_contact_tags_structure.sql
```

### 使用Flyway工具

如果使用Flyway工具进行数据库版本管理，只需将db目录配置为Flyway的迁移脚本目录：

```properties
# flyway.conf
flyway.locations=filesystem:db
flyway.baseline-on-migrate=true
flyway.validate-on-migrate=true
```

然后运行：
```bash
flyway migrate
```

## 注意事项

1. **备份数据**: 在执行任何迁移脚本之前，请务必备份数据库。

2. **权限检查**: 确保数据库用户有足够的权限执行DDL和DML操作。

3. **版本兼容性**: 请按照版本顺序执行脚本，不要跳过中间版本。

4. **测试环境**: 建议先在测试环境中执行迁移脚本，确认无误后再在生产环境执行。

5. **回滚计划**: 每个版本都应该有对应的回滚脚本（如有需要）。

## 故障排除

### 常见问题

1. **表已存在错误**: 如果遇到表已存在的错误，可以检查脚本中的`DROP TABLE IF EXISTS`语句。

2. **字段已存在错误**: 如果遇到字段已存在的错误，可以检查脚本中的`ADD COLUMN IF NOT EXISTS`语句。

3. **外键约束错误**: 如果遇到外键约束错误，请检查相关表的数据完整性。

### 联系支持

如果遇到迁移问题，请联系系统管理员或开发团队。

## 版本管理规范

### 命名规范
- 文件名格式：`V{version}__{description}.sql`
- 版本号格式：`主版本.次版本.修订版本`
- 描述使用下划线分隔的英文描述

### 版本号规则
- **主版本号**：重大功能变更或架构调整
- **次版本号**：新功能添加或重要改进
- **修订版本号**：Bug修复或小功能调整

### 文件创建规则
1. 每次数据库结构变更都必须创建新的版本文件
2. 版本号必须递增，不能重复
3. 每个文件只包含一个具体的变更内容
4. 文件内容必须可重复执行（幂等性）

## 更新日志

- **2024-01-25**: 创建V1.2.1版本，优化联系人标签结构
- **2024-01-25**: 创建V1.2.0版本，添加发送任务表发件人ID字段
- **2024-01-20**: 创建V1.1.1版本，修复邮件状态字段
- **2024-01-20**: 创建V1.1.0版本，添加邮件表新字段
- **2024-01-15**: 创建V1.0.2版本，添加菜单配置
- **2024-01-15**: 创建V1.0.1版本，添加测试数据
- **2024-01-15**: 创建V1.0.0版本，基础邮件表结构
