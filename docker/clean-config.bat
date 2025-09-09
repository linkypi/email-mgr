@echo off
REM 清理配置文件脚本 (Windows版本)
REM 用于清理构建过程中临时复制的配置文件

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

REM 清理配置文件
:clean_config_files
call :print_message "清理临时配置文件..."
if exist "application.yml" (
    del "application.yml"
    call :print_message "已删除 application.yml"
) else (
    call :print_warning "application.yml 不存在"
)

if exist "application-druid.yml" (
    del "application-druid.yml"
    call :print_message "已删除 application-druid.yml"
) else (
    call :print_warning "application-druid.yml 不存在"
)

call :print_message "配置文件清理完成"
goto :eof

REM 显示帮助信息
:show_help
echo 用法: %~nx0
echo.
echo 功能: 清理构建过程中临时复制的配置文件
echo.
echo 注意: 此脚本会删除以下文件（如果存在）:
echo   - application.yml
echo   - application-druid.yml
echo.
echo 这些文件是从项目资源目录复制过来的临时文件
goto :eof

REM 主函数
:main
if "%~1"=="help" (
    call :show_help
) else (
    call :clean_config_files
)

exit /b 0




