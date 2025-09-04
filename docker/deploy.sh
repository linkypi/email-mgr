#!/bin/bash

# Docker部署脚本
# 用于启动、停止和管理Docker服务

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
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

print_header() {
    echo -e "${BLUE}================================${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}================================${NC}"
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

# 启动服务
start_services() {
    print_header "启动服务"
    
    # 检查镜像是否存在
    if ! docker image inspect ruoyi-backend:latest &> /dev/null; then
        print_warning "后端镜像不存在，请先运行 ./build.sh 构建镜像"
        return 1
    fi
    
    if ! docker image inspect ruoyi-frontend:latest &> /dev/null; then
        print_warning "前端镜像不存在，请先运行 ./build.sh 构建镜像"
        return 1
    fi
    
    print_message "启动所有服务..."
    docker-compose up -d
    
    if [ $? -eq 0 ]; then
        print_message "服务启动成功！"
        print_message "前端访问地址: http://localhost"
        print_message "后端API地址: http://localhost:28080"
        print_message "MySQL端口: 3306"
        print_message "Redis端口: 6379"
    else
        print_error "服务启动失败"
        return 1
    fi
}

# 停止服务
stop_services() {
    print_header "停止服务"
    print_message "停止所有服务..."
    docker-compose down
    
    if [ $? -eq 0 ]; then
        print_message "服务已停止"
    else
        print_error "停止服务失败"
        return 1
    fi
}

# 重启服务
restart_services() {
    print_header "重启服务"
    print_message "重启所有服务..."
    docker-compose restart
    
    if [ $? -eq 0 ]; then
        print_message "服务重启成功"
    else
        print_error "重启服务失败"
        return 1
    fi
}

# 查看服务状态
status_services() {
    print_header "服务状态"
    docker-compose ps
    
    echo ""
    print_message "容器日志查看命令:"
    echo "  docker-compose logs -f [服务名]  # 查看指定服务的日志"
    echo "  docker-compose logs -f           # 查看所有服务的日志"
}

# 查看服务日志
show_logs() {
    local service=${1:-""}
    
    if [ -z "$service" ]; then
        print_message "显示所有服务的日志..."
        docker-compose logs -f
    else
        print_message "显示 $service 服务的日志..."
        docker-compose logs -f "$service"
    fi
}

# 进入容器
enter_container() {
    local service=${1:-""}
    
    if [ -z "$service" ]; then
        print_error "请指定服务名称"
        echo "可用服务: backend, frontend, mysql, redis"
        return 1
    fi
    
    print_message "进入 $service 容器..."
    docker-compose exec "$service" sh
}

# 备份数据
backup_data() {
    print_header "备份数据"
    
    local backup_dir="./backups/$(date +%Y%m%d_%H%M%S)"
    mkdir -p "$backup_dir"
    
    print_message "备份MySQL数据..."
    docker-compose exec -T mysql mysqldump -u root -p12345678 email_mgr > "$backup_dir/mysql_backup.sql"
    
    print_message "备份Redis数据..."
    docker-compose exec -T redis redis-cli BGSAVE
    sleep 2
    docker cp ruoyi-redis:/data/dump.rdb "$backup_dir/redis_backup.rdb"
    
    print_message "备份上传文件..."
    docker cp ruoyi-backend:/home/ruoyi/uploadPath "$backup_dir/upload_files"
    
    print_message "备份完成，备份目录: $backup_dir"
}

# 恢复数据
restore_data() {
    local backup_dir=${1:-""}
    
    if [ -z "$backup_dir" ]; then
        print_error "请指定备份目录"
        return 1
    fi
    
    if [ ! -d "$backup_dir" ]; then
        print_error "备份目录不存在: $backup_dir"
        return 1
    fi
    
    print_header "恢复数据"
    print_warning "此操作将覆盖现有数据，请确认！"
    read -p "确认恢复数据？(y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_message "恢复MySQL数据..."
        docker-compose exec -T mysql mysql -u root -p12345678 email_mgr < "$backup_dir/mysql_backup.sql"
        
        print_message "恢复Redis数据..."
        docker cp "$backup_dir/redis_backup.rdb" ruoyi-redis:/data/dump.rdb
        docker-compose exec redis redis-cli BGREWRITEAOF
        
        print_message "恢复上传文件..."
        docker cp "$backup_dir/upload_files/." ruoyi-backend:/home/ruoyi/uploadPath/
        
        print_message "数据恢复完成"
    else
        print_message "取消恢复操作"
    fi
}

# 清理资源
clean_resources() {
    print_header "清理资源"
    print_warning "此操作将删除所有容器、网络和数据卷，请确认！"
    read -p "确认清理所有资源？(y/N): " -n 1 -r
    echo
    
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        print_message "停止并删除所有服务..."
        docker-compose down -v --remove-orphans
        
        print_message "清理未使用的镜像..."
        docker image prune -f
        
        print_message "清理未使用的网络..."
        docker network prune -f
        
        print_message "资源清理完成"
    else
        print_message "取消清理操作"
    fi
}

# 显示帮助信息
show_help() {
    echo "用法: $0 [命令] [选项]"
    echo ""
    echo "命令:"
    echo "  start       启动所有服务"
    echo "  stop        停止所有服务"
    echo "  restart     重启所有服务"
    echo "  status      查看服务状态"
    echo "  logs        查看服务日志"
    echo "  exec        进入指定容器"
    echo "  backup      备份数据"
    echo "  restore     恢复数据"
    echo "  clean       清理所有资源"
    echo "  help        显示此帮助信息"
    echo ""
    echo "选项:"
    echo "  logs [服务名]    查看指定服务的日志"
    echo "  exec [服务名]    进入指定容器"
    echo "  restore [目录]   从指定目录恢复数据"
    echo ""
    echo "示例:"
    echo "  $0 start              # 启动所有服务"
    echo "  $0 logs backend       # 查看后端日志"
    echo "  $0 exec mysql         # 进入MySQL容器"
    echo "  $0 backup             # 备份数据"
    echo "  $0 restore ./backups/20241201_120000  # 恢复数据"
}

# 主函数
main() {
    check_docker
    
    case "${1:-help}" in
        "start")
            start_services
            ;;
        "stop")
            stop_services
            ;;
        "restart")
            restart_services
            ;;
        "status")
            status_services
            ;;
        "logs")
            show_logs "$2"
            ;;
        "exec")
            enter_container "$2"
            ;;
        "backup")
            backup_data
            ;;
        "restore")
            restore_data "$2"
            ;;
        "clean")
            clean_resources
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        *)
            print_error "未知命令: $1"
            show_help
            exit 1
            ;;
    esac
}

# 执行主函数
main "$@"

