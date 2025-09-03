@echo off
REM Docker构建脚本 (Windows版本)
REM 用于构建前后端应用的Docker镜像

setlocal enabledelayedexpansion

REM 设置颜色代码
set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "NC=[0m"

REM 打印带颜色的消息
:print_message
echo %GREEN%[INFO]%NC% %~1
goto :eof

:print_warning
echo %YELLOW%[WARNING]%NC% %~1
goto :eof

:print_error
echo %RED%[ERROR]%NC% %~1
goto :eof

REM 检查Docker是否安装
:check_docker
docker --version >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker未安装，请先安装Docker"
    exit /b 1
)

docker-compose --version >nul 2>&1
if errorlevel 1 (
    call :print_error "Docker Compose未安装，请先安装Docker Compose"
    exit /b 1
)
goto :eof

REM 复制配置文件
:copy_config_files
call :print_message "复制配置文件到docker目录..."
copy "..\ruoyi-admin\src\main\resources\application.yml" "application.yml" >nul
copy "..\ruoyi-admin\src\main\resources\application-druid.yml" "application-druid.yml" >nul
if errorlevel 1 (
    call :print_error "配置文件复制失败"
    exit /b 1
)
call :print_message "配置文件复制完成"
goto :eof

REM 构建后端镜像
:build_backend
call :print_message "开始构建后端镜像..."

REM 检查Maven是否安装
mvn --version >nul 2>&1
if errorlevel 1 (
    call :print_error "Maven未安装，请先安装Maven"
    exit /b 1
)

REM 构建Java项目
call :print_message "编译Java项目..."
cd ..
call mvn clean package -DskipTests
if errorlevel 1 (
    call :print_error "Java项目编译失败"
    exit /b 1
)

REM 构建Docker镜像
call :print_message "构建后端Docker镜像..."
docker build -f docker/Dockerfile.backend -t ruoyi-backend:latest .
if errorlevel 1 (
    call :print_error "后端镜像构建失败"
    exit /b 1
)

call :print_message "后端镜像构建成功"
cd docker
goto :eof

REM 构建前端镜像
:build_frontend
call :print_message "开始构建前端镜像..."

REM 检查Node.js是否安装
node --version >nul 2>&1
if errorlevel 1 (
    call :print_warning "Node.js未安装，将使用Docker多阶段构建"
)

REM 构建Docker镜像
call :print_message "构建前端Docker镜像..."
docker build -f Dockerfile.frontend -t ruoyi-frontend:latest ..
if errorlevel 1 (
    call :print_error "前端镜像构建失败"
    exit /b 1
)

call :print_message "前端镜像构建成功"
goto :eof

REM 构建所有镜像
:build_all
call :print_message "开始构建所有镜像..."
call :copy_config_files
call :build_backend
call :build_frontend
call :print_message "所有镜像构建完成！"
goto :eof

REM 清理镜像
:clean_images
call :print_message "清理Docker镜像..."
docker rmi ruoyi-backend:latest ruoyi-frontend:latest 2>nul
call :print_message "镜像清理完成"
goto :eof

REM 显示帮助信息
:show_help
echo 用法: %~nx0 [选项]
echo.
echo 选项:
echo   backend    构建后端镜像
echo   frontend   构建前端镜像
echo   all        构建所有镜像（默认）
echo   clean      清理所有镜像
echo   help       显示此帮助信息
echo.
echo 示例:
echo   %~nx0              # 构建所有镜像
echo   %~nx0 backend      # 只构建后端镜像
echo   %~nx0 frontend     # 只构建前端镜像
echo   %~nx0 clean        # 清理镜像
goto :eof

REM 主函数
:main
call :check_docker

set "command=%~1"
if "%command%"=="" set "command=all"

if "%command%"=="backend" (
    call :copy_config_files
    call :build_backend
) else if "%command%"=="frontend" (
    call :build_frontend
) else if "%command%"=="all" (
    call :build_all
) else if "%command%"=="clean" (
    call :clean_images
) else if "%command%"=="help" (
    call :show_help
) else (
    call :print_error "未知选项: %command%"
    call :show_help
    exit /b 1
)

exit /b 0
