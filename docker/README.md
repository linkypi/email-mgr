# Docker 部署指南

本项目提供了完整的Docker化部署方案，包括前端、后端、数据库和缓存服务。

## 目录结构

```
docker/
├── Dockerfile.backend          # 后端Java应用Dockerfile
├── Dockerfile.frontend         # 前端Vue.js应用Dockerfile
├── nginx.conf                  # 前端Nginx配置
├── nginx-proxy.conf            # 生产环境反向代理配置
├── docker-compose.yml          # Docker Compose部署配置
├── application-docker.yml      # Docker环境Spring Boot配置
├── build.sh                    # 镜像构建脚本
├── deploy.sh                   # 服务部署管理脚本
├── .dockerignore               # Docker构建忽略文件
└── README.md                   # 本文档
```

## 环境要求

- Docker 20.10+
- Docker Compose 2.0+
- Maven 3.6+ (用于构建后端)
- Node.js 16+ (可选，用于本地构建前端)

## 快速开始

### 1. 构建镜像

```bash
# 进入docker目录
cd docker

# 构建所有镜像
./build.sh

# 或者分别构建
./build.sh backend    # 只构建后端
./build.sh frontend   # 只构建前端
```

### 2. 启动服务

```bash
# 启动所有服务
./deploy.sh start

# 查看服务状态
./deploy.sh status

# 查看服务日志
./deploy.sh logs
```

### 3. 访问应用

- 前端应用: http://localhost
- 后端API: http://localhost:28080
- MySQL: localhost:3306
- Redis: localhost:6379

## 服务说明

### 后端服务 (ruoyi-backend)
- 基于OpenJDK 8 Alpine镜像
- 端口: 28080
- 健康检查: HTTP GET /
- 数据卷: 上传文件存储

### 前端服务 (ruoyi-frontend)
- 基于Nginx Alpine镜像
- 端口: 80
- 支持Vue Router history模式
- 静态资源缓存优化
- API代理到后端

### MySQL数据库 (ruoyi-mysql)
- 版本: MySQL 8.0
- 端口: 3306
- 数据库: email_mgr
- 用户: ruoyi/ruoyi123
- 数据持久化

### Redis缓存 (ruoyi-redis)
- 版本: Redis 7 Alpine
- 端口: 6379
- AOF持久化
- 数据持久化

## 常用命令

### 服务管理
```bash
./deploy.sh start      # 启动服务
./deploy.sh stop       # 停止服务
./deploy.sh restart    # 重启服务
./deploy.sh status     # 查看状态
```

### 日志查看
```bash
./deploy.sh logs              # 查看所有日志
./deploy.sh logs backend      # 查看后端日志
./deploy.sh logs frontend     # 查看前端日志
./deploy.sh logs mysql        # 查看MySQL日志
./deploy.sh logs redis        # 查看Redis日志
```

### 容器操作
```bash
./deploy.sh exec backend      # 进入后端容器
./deploy.sh exec mysql        # 进入MySQL容器
./deploy.sh exec redis        # 进入Redis容器
```

### 数据管理
```bash
./deploy.sh backup            # 备份数据
./deploy.sh restore [目录]    # 恢复数据
```

### 资源清理
```bash
./deploy.sh clean             # 清理所有资源
./build.sh clean              # 清理镜像
```

## 生产环境部署

### 使用反向代理
```bash
# 启动生产环境配置（包含Nginx反向代理）
docker-compose --profile production up -d
```

### 环境变量配置
可以通过环境变量覆盖默认配置：

```bash
export MYSQL_ROOT_PASSWORD=your_password
export MYSQL_DATABASE=your_database
export MYSQL_USER=your_user
export MYSQL_PASSWORD=your_password
docker-compose up -d
```

### 数据持久化
所有数据都通过Docker卷进行持久化：
- `mysql_data`: MySQL数据
- `redis_data`: Redis数据
- `upload_data`: 上传文件

## 故障排除

### 常见问题

1. **端口冲突**
   - 检查端口是否被占用: `netstat -tulpn | grep :端口号`
   - 修改docker-compose.yml中的端口映射

2. **数据库连接失败**
   - 检查MySQL容器状态: `docker-compose ps mysql`
   - 查看MySQL日志: `docker-compose logs mysql`

3. **前端无法访问后端API**
   - 检查nginx.conf中的代理配置
   - 确认后端服务正常运行

4. **内存不足**
   - 调整JVM参数: 修改Dockerfile.backend中的JAVA_OPTS
   - 增加Docker内存限制

### 日志分析
```bash
# 查看实时日志
docker-compose logs -f

# 查看特定时间段的日志
docker-compose logs --since="2024-01-01T00:00:00" --until="2024-01-01T23:59:59"

# 查看错误日志
docker-compose logs | grep ERROR
```

## 性能优化

### 后端优化
- 调整JVM内存参数
- 配置连接池参数
- 启用Gzip压缩

### 前端优化
- 静态资源缓存
- Gzip压缩
- CDN加速（可选）

### 数据库优化
- 调整MySQL配置
- 配置合适的连接池大小
- 定期维护和优化

## 安全建议

1. **修改默认密码**
   - MySQL root密码
   - 应用数据库用户密码
   - Druid监控密码

2. **网络安全**
   - 限制容器间通信
   - 配置防火墙规则
   - 使用私有网络

3. **数据安全**
   - 定期备份数据
   - 加密敏感信息
   - 监控访问日志

## 更新部署

### 更新应用
```bash
# 1. 停止服务
./deploy.sh stop

# 2. 重新构建镜像
./build.sh

# 3. 启动服务
./deploy.sh start
```

### 更新配置
修改配置文件后需要重新构建镜像或重启服务。

## 技术支持

如遇到问题，请检查：
1. Docker和Docker Compose版本
2. 系统资源使用情况
3. 网络配置
4. 日志信息

更多信息请参考项目主文档。
