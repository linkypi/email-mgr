@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

REM 邮件管理系统启动脚本
REM 用于停止旧容器、删除旧镜像、加载新镜像并启动服务

echo ==========================================
echo 邮件管理系统启动脚本
echo ==========================================

REM 切换到docker目录
cd /d "%~dp0"
echo 当前目录: %CD%

echo.
echo 1. 停止现有容器...
REM 停止现有容器（如果存在）
docker stop email-manager-backend 2>nul || echo email-manager-backend 容器不存在或已停止
docker stop email-manager-frontend 2>nul || echo email-manager-frontend 容器不存在或已停止

echo.
echo 2. 删除现有容器...
REM 删除现有容器（如果存在）
docker rm email-manager-frontend 2>nul || echo email-manager-frontend 容器不存在
docker rm email-manager-backend 2>nul || echo email-manager-backend 容器不存在

echo.
echo 3. 删除旧镜像...
REM 删除旧镜像（如果存在）
docker rmi email-manager-backend 2>nul || echo email-manager-backend 镜像不存在
docker rmi ruoyi-backend 2>nul || echo ruoyi-backend 镜像不存在
docker rmi ruoyi-frontend 2>nul || echo ruoyi-frontend 镜像不存在

echo.
echo 4. 加载新镜像...
REM 切换到上级目录加载镜像
cd ..
echo 加载后端镜像...
docker load -i backend.tar
echo 加载前端镜像...
docker load -i frontend.tar

echo.
echo 5. 启动服务...
REM 切换回docker目录启动服务
cd docker
echo 启动后端服务...
docker-compose -f docker-compose.backend.yml up -d
echo 启动前端服务...
docker-compose -f docker-compose.frontend.yml up -d

echo.
echo 6. 检查服务状态...
echo 当前运行的容器:
docker ps

echo.
echo ==========================================
echo 邮件管理系统启动完成！
echo ==========================================
echo 服务访问地址:
echo 前端: http://your-server-ip:80
echo 后端: http://your-server-ip:8080
echo ==========================================

pause




