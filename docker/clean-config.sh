#!/bin/bash

# 清理配置文件脚本 (Linux版本)
# 用于清理构建过程中临时复制的配置文件

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 打印带颜色的消息
print_message() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# 清理配置文件
clean_config_files() {
    print_message "清理临时配置文件..."
    
    if [ -f "application.yml" ]; then
        rm "application.yml"
        print_message "已删除 application.yml"
    else
        print_warning "application.yml 不存在"
    fi
    
    if [ -f "application-druid.yml" ]; then
        rm "application-druid.yml"
        print_message "已删除 application-druid.yml"
    else
        print_warning "application-druid.yml 不存在"
    fi
    
    print_message "配置文件清理完成"
}

# 显示帮助信息
show_help() {
    echo "用法: $0"
    echo ""
    echo "功能: 清理构建过程中临时复制的配置文件"
    echo ""
    echo "注意: 此脚本会删除以下文件（如果存在）:"
    echo "  - application.yml"
    echo "  - application-druid.yml"
    echo ""
    echo "这些文件是从项目资源目录复制过来的临时文件"
}

# 主函数
main() {
    case "${1:-clean}" in
        "help"|"-h"|"--help")
            show_help
            ;;
        "clean")
            clean_config_files
            ;;
        *)
            print_error "未知选项: $1"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"





























