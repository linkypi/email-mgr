#!/bin/bash

# Docker构建脚本
# 用于构建前后端应用的Docker镜像

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

# 检查Docker是否安装
check_docker() {
    if ! command -v docker &> /dev/null; then
        print_error "Docker未安装，请先安装Docker"
        exit 1
    fi
    
    if ! command -v docker-compose &> /dev/null; then
        print_error "Docker Compose未安装，请先安装Docker Compose"
        exit 1
    fi
}

# 复制配置文件
copy_config_files() {
    print_message "复制配置文件到docker目录..."
    
    if ! cp ../ruoyi-admin/src/main/resources/application.yml .; then
        print_error "application.yml复制失败"
        exit 1
    fi
    
    if ! cp ../ruoyi-admin/src/main/resources/application-druid.yml .; then
        print_error "application-druid.yml复制失败"
        exit 1
    fi
    
    print_message "配置文件复制完成"
}

# 构建后端镜像
build_backend() {
    print_message "开始构建后端镜像..."
    
    # 检查Maven是否安装
    if ! command -v mvn &> /dev/null; then
        print_error "Maven未安装，请先安装Maven"
        exit 1
    fi
    
    # 构建Java项目
    print_message "编译Java项目..."
    cd ..
    mvn clean package -DskipTests
    
    if [ $? -ne 0 ]; then
        print_error "Java项目编译失败"
        exit 1
    fi
    
    # 构建Docker镜像
    print_message "构建后端Docker镜像..."
    docker build -f docker/Dockerfile.backend -t ruoyi-backend:latest .
    
    if [ $? -eq 0 ]; then
        print_message "后端镜像构建成功"
    else
        print_error "后端镜像构建失败"
        exit 1
    fi
    
    cd docker
}

# 构建前端镜像
build_frontend() {
    print_message "开始构建前端镜像..."
    
    # 检查Node.js是否安装
    if ! command -v node &> /dev/null; then
        print_warning "Node.js未安装，将使用Docker多阶段构建"
    fi
    
    # 构建Docker镜像
    print_message "构建前端Docker镜像..."
    docker build -f Dockerfile.frontend -t ruoyi-frontend:latest ..
    
    if [ $? -eq 0 ]; then
        print_message "前端镜像构建成功"
    else
        print_error "前端镜像构建失败"
        exit 1
    fi
}

# 构建所有镜像
build_all() {
    print_message "开始构建所有镜像..."
    copy_config_files
    build_backend
    build_frontend
    print_message "所有镜像构建完成！"
}

# 清理镜像
clean_images() {
    print_message "清理Docker镜像..."
    docker rmi ruoyi-backend:latest ruoyi-frontend:latest 2>/dev/null || true
    print_message "镜像清理完成"
}

# 显示帮助信息
show_help() {
    echo "用法: $0 [选项]"
    echo ""
    echo "选项:"
    echo "  backend    构建后端镜像"
    echo "  frontend   构建前端镜像"
    echo "  all        构建所有镜像（默认）"
    echo "  clean      清理所有镜像"
    echo "  help       显示此帮助信息"
    echo ""
    echo "示例:"
    echo "  $0              # 构建所有镜像"
    echo "  $0 backend      # 只构建后端镜像"
    echo "  $0 frontend     # 只构建前端镜像"
    echo "  $0 clean        # 清理镜像"
}

# 主函数
main() {
    check_docker
    
    case "${1:-all}" in
        "backend")
            copy_config_files
            build_backend
            ;;
        "frontend")
            build_frontend
            ;;
        "all")
            build_all
            ;;
        "clean")
            clean_images
            ;;
        "help"|"-h"|"--help")
            show_help
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
