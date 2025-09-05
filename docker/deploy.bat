@echo off
chcp 65001 >nul
echo ========================================
echo Docker 服务部署脚本
echo ========================================

if "%1"=="start" goto start
if "%1"=="stop" goto stop
if "%1"=="restart" goto restart
if "%1"=="logs" goto logs
if "%1"=="status" goto status
if "%1"=="clean" goto clean

echo 用法: deploy.bat [start^|stop^|restart^|logs^|status^|clean]
echo.
echo 参数说明:
echo   start   - 启动所有服务
echo   stop    - 停止所有服务
echo   restart - 重启所有服务
echo   logs    - 查看服务日志
echo   status  - 查看服务状态
echo   clean   - 清理所有容器和镜像
echo.
pause
exit /b

:start
echo [INFO] 启动所有服务...
docker-compose up -d
if %errorlevel% neq 0 (
    echo [ERROR] 服务启动失败
    pause
    exit /b 1
)
echo [INFO] 所有服务启动成功！
echo.
echo 服务访问地址:
echo   前端: http://localhost
echo   后端: http://localhost:28080
echo   MySQL: localhost:3306
echo   Redis: localhost:6379
echo.
goto end

:stop
echo [INFO] 停止所有服务...
docker-compose down
echo [INFO] 所有服务已停止
goto end

:restart
echo [INFO] 重启所有服务...
docker-compose down
docker-compose up -d
if %errorlevel% neq 0 (
    echo [ERROR] 服务重启失败
    pause
    exit /b 1
)
echo [INFO] 所有服务重启成功！
goto end

:logs
echo [INFO] 查看服务日志...
docker-compose logs -f
goto end

:status
echo [INFO] 查看服务状态...
docker-compose ps
echo.
echo 容器状态:
docker ps -a --filter "name=ruoyi-"
goto end

:clean
echo [WARNING] 即将清理所有容器和镜像，此操作不可恢复！
set /p confirm="确认继续? (y/N): "
if /i "%confirm%" neq "y" (
    echo 操作已取消
    goto end
)
echo [INFO] 清理所有容器和镜像...
docker-compose down -v --rmi all
docker system prune -af
echo [INFO] 清理完成
goto end

:end
echo.
pause


