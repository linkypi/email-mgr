# Docker 构建和部署说明

## 概述
本项目提供了完整的Docker构建和部署解决方案，支持Windows PowerShell和Linux Bash两种环境。

## 构建脚本

### Windows PowerShell版本 (`build.ps1`)
```powershell
# 构建所有镜像并导出tar文件
.\build.ps1

# 只构建后端镜像并导出tar文件
.\build.ps1 backend

# 只构建前端镜像并导出tar文件
.\build.ps1 frontend

# 使用现有的dist目录构建前端（跳过编译）
.\build.ps1 frontend dist-only
```

### Linux Bash版本 (`build.sh`)
```bash
# 构建所有镜像并导出tar文件
./build.sh

# 只构建后端镜像并导出tar文件
./build.sh backend

# 只构建前端镜像并导出tar文件
./build.sh frontend

# 清理所有镜像
./build.sh clean

# 显示帮助信息
./build.sh help
```

## 新增功能：镜像导出

### 功能说明
构建脚本现在会在Docker镜像构建完成后，自动将镜像导出为tar文件，存储到docker目录中。

### 导出的文件
- `docker/backend.tar` - 后端应用镜像
- `docker/frontend.tar` - 前端应用镜像

### 使用场景
1. **离线部署**：在没有网络的环境中部署应用
2. **镜像备份**：保存特定版本的镜像
3. **跨机器部署**：将镜像传输到其他机器上部署
4. **CI/CD流水线**：在构建服务器上生成镜像文件

### 在其他机器上加载镜像
```bash
# 加载后端镜像
docker load -i docker/backend.tar

# 加载前端镜像
docker load -i docker/frontend.tar

# 验证镜像已加载
docker images | grep ruoyi
```

## 部署脚本

### Windows版本 (`deploy.bat`)
```batch
# 启动所有服务
deploy.bat start

# 停止所有服务
deploy.bat stop

# 重启所有服务
deploy.bat restart

# 查看服务状态
deploy.bat status

# 查看服务日志
deploy.bat logs
```

### Linux版本 (`deploy.sh`)
```bash
# 启动所有服务
./deploy.sh start

# 停止所有服务
./deploy.sh stop

# 重启所有服务
./deploy.sh restart

# 查看服务状态
./deploy.sh status

# 查看服务日志
./deploy.sh logs
```

## 配置文件

### 环境配置
- `docker-compose.yml` - Docker Compose配置文件
- `application.yml` - 应用配置文件
- `application-druid.yml` - 数据库连接池配置
- `nginx.conf` - Nginx反向代理配置

### 环境变量
创建 `.env` 文件来配置环境变量：
```env
# 数据库配置
MYSQL_ROOT_PASSWORD=your_password
MYSQL_DATABASE=email_mgr
MYSQL_USER=ruoyi
MYSQL_PASSWORD=ruoyi123

# Redis配置
REDIS_PASSWORD=your_redis_password

# 应用配置
SPRING_PROFILES_ACTIVE=docker
```

## 目录结构
```
docker/
├── build.ps1              # Windows构建脚本
├── build.sh               # Linux构建脚本
├── deploy.bat             # Windows部署脚本
├── deploy.sh              # Linux部署脚本
├── docker-compose.yml     # Docker Compose配置
├── Dockerfile.backend     # 后端Dockerfile
├── Dockerfile.frontend    # 前端Dockerfile
├── nginx.conf             # Nginx配置
├── application.yml        # 应用配置
├── application-druid.yml  # 数据库配置
├── backend.tar    # 后端镜像文件
├── frontend.tar   # 前端镜像文件
└── README.md              # 说明文档
```

## 注意事项

1. **磁盘空间**：tar文件通常比较大（几百MB到几GB），请确保有足够的磁盘空间
2. **网络传输**：tar文件较大，传输时请注意网络带宽和时间
3. **版本管理**：建议为不同版本创建不同的tar文件名称
4. **安全性**：tar文件包含完整的应用代码，请妥善保管

## 故障排除

### 常见问题

1. **构建失败**
   - 检查Docker和Docker Compose是否正确安装
   - 检查Maven和Node.js环境是否正确配置
   - 查看构建日志中的具体错误信息

2. **镜像导出失败**
   - 检查磁盘空间是否充足
   - 检查docker目录的写入权限
   - 确认Docker镜像已成功构建

3. **部署失败**
   - 检查端口是否被占用
   - 检查配置文件是否正确
   - 查看容器日志排查问题

### 日志查看
```bash
# 查看所有服务日志
docker-compose logs

# 查看特定服务日志
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql
docker-compose logs redis
```