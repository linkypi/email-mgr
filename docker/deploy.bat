@echo off
REM Docker部署脚本 (Windows版本)
REM 用于启动、停止和管理Docker服务

setlocal enabledelayedexpansion

REM 设置颜色代码
set "GREEN=[92m"
set "YELLOW=[93m"
set "RED=[91m"
set "BLUE=[94m"
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

:print_header
echo %BLUE%================================%NC%
echo %BLUE%  %~1%NC%
echo %BLUE%================================%NC%
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

REM 启动服务
:start_services
call :print_header "启动服务"

REM 检查镜像是否存在
docker image inspect ruoyi-backend:latest >nul 2>&1
if errorlevel 1 (
    call :print_warning "后端镜像不存在，请先运行 build.bat 构建镜像"
    exit /b 1
)

docker image inspect ruoyi-frontend:latest >nul 2>&1
if errorlevel 1 (
    call :print_warning "前端镜像不存在，请先运行 build.bat 构建镜像"
    exit /b 1
)

call :print_message "启动所有服务..."
docker-compose up -d
if errorlevel 1 (
    call :print_error "服务启动失败"
    exit /b 1
)

call :print_message "服务启动成功！"
call :print_message "前端访问地址: http://localhost"
call :print_message "后端API地址: http://localhost:28080"
call :print_message "MySQL端口: 3306"
call :print_message "Redis端口: 6379"
goto :eof

REM 停止服务
:stop_services
call :print_header "停止服务"
call :print_message "停止所有服务..."
docker-compose down
if errorlevel 1 (
    call :print_error "停止服务失败"
    exit /b 1
)

call :print_message "服务已停止"
goto :eof

REM 重启服务
:restart_services
call :print_header "重启服务"
call :print_message "重启所有服务..."
docker-compose restart
if errorlevel 1 (
    call :print_error "重启服务失败"
    exit /b 1
)

call :print_message "服务重启成功"
goto :eof

REM 查看服务状态
:status_services
call :print_header "服务状态"
docker-compose ps

echo.
call :print_message "容器日志查看命令:"
echo   docker-compose logs -f [服务名]  # 查看指定服务的日志
echo   docker-compose logs -f           # 查看所有服务的日志
goto :eof

REM 查看服务日志
:show_logs
set "service=%~1"
if "%service%"=="" (
    call :print_message "显示所有服务的日志..."
    docker-compose logs -f
) else (
    call :print_message "显示 %service% 服务的日志..."
    docker-compose logs -f "%service%"
)
goto :eof

REM 进入容器
:enter_container
set "service=%~1"
if "%service%"=="" (
    call :print_error "请指定服务名称"
    echo 可用服务: backend, frontend, mysql, redis
    exit /b 1
)

call :print_message "进入 %service% 容器..."
docker-compose exec "%service%" sh
goto :eof

REM 备份数据
:backup_data
call :print_header "备份数据"

set "backup_dir=.\backups\%date:~0,4%%date:~5,2%%date:~8,2%_%time:~0,2%%time:~3,2%%time:~6,2%"
set "backup_dir=%backup_dir: =0%"
mkdir "%backup_dir%" 2>nul

call :print_message "备份MySQL数据..."
docker-compose exec -T mysql mysqldump -u root -p12345678 email_mgr > "%backup_dir%\mysql_backup.sql"

call :print_message "备份Redis数据..."
docker-compose exec -T redis redis-cli BGSAVE
timeout /t 2 /nobreak >nul
docker cp ruoyi-redis:/data/dump.rdb "%backup_dir%\redis_backup.rdb"

call :print_message "备份上传文件..."
docker cp ruoyi-backend:/home/ruoyi/uploadPath "%backup_dir%\upload_files"

call :print_message "备份完成，备份目录: %backup_dir%"
goto :eof

REM 恢复数据
:restore_data
set "backup_dir=%~1"
if "%backup_dir%"=="" (
    call :print_error "请指定备份目录"
    exit /b 1
)

if not exist "%backup_dir%" (
    call :print_error "备份目录不存在: %backup_dir%"
    exit /b 1
)

call :print_header "恢复数据"
call :print_warning "此操作将覆盖现有数据，请确认！"
set /p "confirm=确认恢复数据？(y/N): "
if /i "%confirm%"=="y" (
    call :print_message "恢复MySQL数据..."
    docker-compose exec -T mysql mysql -u root -p12345678 email_mgr < "%backup_dir%\mysql_backup.sql"
    
    call :print_message "恢复Redis数据..."
    docker cp "%backup_dir%\redis_backup.rdb" ruoyi-redis:/data/dump.rdb
    docker-compose exec redis redis-cli BGREWRITEAOF
    
    call :print_message "恢复上传文件..."
    docker cp "%backup_dir%\upload_files\." ruoyi-backend:/home/ruoyi/uploadPath/
    
    call :print_message "数据恢复完成"
) else (
    call :print_message "取消恢复操作"
)
goto :eof

REM 清理资源
:clean_resources
call :print_header "清理资源"
call :print_warning "此操作将删除所有容器、网络和数据卷，请确认！"
set /p "confirm=确认清理所有资源？(y/N): "
if /i "%confirm%"=="y" (
    call :print_message "停止并删除所有服务..."
    docker-compose down -v --remove-orphans
    
    call :print_message "清理未使用的镜像..."
    docker image prune -f
    
    call :print_message "清理未使用的网络..."
    docker network prune -f
    
    call :print_message "资源清理完成"
) else (
    call :print_message "取消清理操作"
)
goto :eof

REM 显示帮助信息
:show_help
echo 用法: %~nx0 [命令] [选项]
echo.
echo 命令:
echo   start       启动所有服务
echo   stop        停止所有服务
echo   restart     重启所有服务
echo   status      查看服务状态
echo   logs        查看服务日志
echo   exec        进入指定容器
echo   backup      备份数据
echo   restore     恢复数据
echo   clean       清理所有资源
echo   help        显示此帮助信息
echo.
echo 选项:
echo   logs [服务名]    查看指定服务的日志
echo   exec [服务名]    进入指定容器
echo   restore [目录]   从指定目录恢复数据
echo.
echo 示例:
echo   %~nx0 start              # 启动所有服务
echo   %~nx0 logs backend       # 查看后端日志
echo   %~nx0 exec mysql         # 进入MySQL容器
echo   %~nx0 backup             # 备份数据
echo   %~nx0 restore .\backups\20241201_120000  # 恢复数据
goto :eof

REM 主函数
:main
call :check_docker

set "command=%~1"
if "%command%"=="" set "command=help"

if "%command%"=="start" (
    call :start_services
) else if "%command%"=="stop" (
    call :stop_services
) else if "%command%"=="restart" (
    call :restart_services
) else if "%command%"=="status" (
    call :status_services
) else if "%command%"=="logs" (
    call :show_logs "%~2"
) else if "%command%"=="exec" (
    call :enter_container "%~2"
) else if "%command%"=="backup" (
    call :backup_data
) else if "%command%"=="restore" (
    call :restore_data "%~2"
) else if "%command%"=="clean" (
    call :clean_resources
) else if "%command%"=="help" (
    call :show_help
) else (
    call :print_error "未知命令: %command%"
    call :show_help
    exit /b 1
)

exit /b 0
