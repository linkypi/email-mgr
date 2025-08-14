# 邮件管理系统快速启动指南

## 1. 环境准备

### 必需环境
- JDK 1.8+
- MySQL 5.7+
- Maven 3.6+
- Node.js 14+ (前端)

### 推荐环境
- JDK 1.8
- MySQL 8.0
- Maven 3.8
- Node.js 16

## 2. 数据库初始化

### 2.1 创建数据库
```sql
CREATE DATABASE email_manager DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 2.2 执行SQL脚本
按顺序执行以下SQL文件：

1. **基础数据**：`sql/ry_20250522.sql` (若依框架基础数据)
2. **邮件管理表**：`sql/email_manager.sql` (邮件管理相关表)
3. **菜单权限**：`sql/email_menu.sql` (邮件管理菜单)

```bash
# 在MySQL中执行
source sql/ry_20250522.sql;
source sql/email_manager.sql;
source sql/email_menu.sql;
```

## 3. 后端配置

### 3.1 修改数据库配置
编辑 `ruoyi-admin/src/main/resources/application-druid.yml`：

```yaml
# 数据源配置
spring:
    datasource:
        type: com.alibaba.druid.pool.DruidDataSource
        driverClassName: com.mysql.cj.jdbc.Driver
        druid:
            # 主库数据源
            master:
                url: jdbc:mysql://localhost:3306/email_manager?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
                username: your_username
                password: your_password
```

### 3.2 启动后端服务
```bash
# 进入项目根目录
cd ruoyi-admin

# 编译项目
mvn clean compile

# 启动服务
mvn spring-boot:run
```

或者使用IDE直接运行 `RuoYiApplication.java`

## 4. 前端配置

### 4.1 安装依赖
```bash
# 进入前端目录
cd ruoyi-ui

# 安装依赖
npm install
```

### 4.2 修改API配置
编辑 `ruoyi-ui/.env.development`：

```bash
# 开发环境配置
ENV = 'development'

# 若依管理系统/开发环境
VUE_APP_BASE_API = '/dev-api'

# 路由懒加载
VUE_CLI_BABEL_TRANSPILE_MODULES = true
```

### 4.3 启动前端服务
```bash
# 启动开发服务器
npm run dev
```

## 5. 系统访问

### 5.1 访问地址
- 前端地址：http://localhost:80
- 后端地址：http://localhost:8080

### 5.2 默认账号
- 用户名：admin
- 密码：admin123

## 6. 功能测试

### 6.1 联系人管理测试
1. 登录系统
2. 进入"邮件管理" -> "联系人管理"
3. 点击"新增"按钮
4. 填写联系人信息并保存
5. 测试搜索、编辑、删除功能

### 6.2 邮件账号管理测试
1. 进入"邮件管理" -> "邮件账号"
2. 添加邮件发送账号
3. 配置SMTP服务器信息

### 6.3 权限测试
1. 进入"系统管理" -> "角色管理"
2. 为不同角色分配邮件管理权限
3. 测试不同角色的访问权限

## 7. 常见问题

### 7.1 编译错误
```bash
# 清理并重新编译
mvn clean install -DskipTests
```

### 7.2 数据库连接错误
- 检查MySQL服务是否启动
- 验证数据库连接信息
- 确认数据库用户权限

### 7.3 前端依赖问题
```bash
# 清理并重新安装依赖
rm -rf node_modules package-lock.json
npm install
```

### 7.4 端口占用问题
```bash
# 查看端口占用
netstat -ano | findstr :8080

# 杀死进程
taskkill /F /PID <进程ID>
```

## 8. 部署建议

### 8.1 开发环境
- 使用本地MySQL
- 前后端分离开发
- 开启热重载

### 8.2 生产环境
- 使用云数据库
- 配置反向代理
- 启用HTTPS
- 设置防火墙规则

## 9. 功能扩展

### 9.1 邮件发送功能
需要实现以下核心功能：
- SMTP邮件发送
- 批量发送队列
- 发送状态跟踪
- 邮件模板管理

### 9.2 统计分析功能
- 发送成功率统计
- 打开率分析
- 回复率统计
- 数据可视化

## 10. 技术支持

如遇到问题，请检查：
1. 日志文件：`logs/sys-*.log`
2. 数据库连接状态
3. 网络连接情况
4. 系统资源使用情况

---

**注意**：首次启动时，系统会自动创建必要的数据库表和初始数据。请确保数据库用户具有足够的权限。

