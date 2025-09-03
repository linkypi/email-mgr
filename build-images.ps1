# 邮件管理系统 Docker 镜像构建脚本 (预编译版本)
# 用于将已经编译好的后端 JAR 包和前端文件打包成 Docker 镜像

param(
    [Parameter(Position=0)]
    [string]$Target = "all"
)

# 颜色定义
$Green = "Green"
$Yellow = "Yellow"
$Red = "Red"
$Cyan = "Cyan"
$White = "White"

# 项目配置
$ProjectName = "email-manager"
$BackendImageName = "${ProjectName}-backend"
$FrontendImageName = "${ProjectName}-frontend"
$BackendTag = "latest"
$FrontendTag = "latest"

# 显示帮助信息
function Show-Help {
    Write-Host "Email Manager Docker Image Builder (Pre-compiled Version)" -ForegroundColor $Cyan
    Write-Host ""
    Write-Host "Usage:" -ForegroundColor $Yellow
    Write-Host "  .\build-images.ps1 [target]" -ForegroundColor $White
    Write-Host ""
    Write-Host "Targets:" -ForegroundColor $Yellow
    Write-Host "  backend   - Build backend image from pre-compiled JAR" -ForegroundColor $White
    Write-Host "  frontend  - Build frontend image from pre-compiled dist" -ForegroundColor $White
    Write-Host "  all       - Build both backend and frontend images" -ForegroundColor $White
    Write-Host "  help      - Show this help message" -ForegroundColor $White
    Write-Host ""
    Write-Host "Prerequisites:" -ForegroundColor $Yellow
    Write-Host "  - Backend JAR file: ruoyi-admin/target/ruoyi-admin.jar" -ForegroundColor $White
    Write-Host "  - Frontend dist folder: ./ruoyi-ui/dist/" -ForegroundColor $White
    Write-Host "  - Docker must be running" -ForegroundColor $White
}

# 检查 Docker 环境
function Test-DockerEnvironment {
    Write-Host "[STEP] Checking Docker environment..." -ForegroundColor $Yellow
    
    try {
        $dockerVersion = docker --version 2>$null
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[INFO] Docker environment check passed" -ForegroundColor $Green
            return $true
        } else {
            Write-Host "[ERROR] Docker is not available" -ForegroundColor $Red
            return $false
        }
    } catch {
        Write-Host "[ERROR] Docker is not available" -ForegroundColor $Red
        return $false
    }
}

# 检查预编译文件
function Test-PrecompiledFiles {
    param([string]$Type)
    
    if ($Type -eq "backend") {
        $jarPath = "ruoyi-admin/target/ruoyi-admin.jar"
        if (Test-Path $jarPath) {
            Write-Host "[INFO] Backend JAR file found: $jarPath" -ForegroundColor $Green
            return $true
        } else {
            Write-Host "[ERROR] Backend JAR file not found: $jarPath" -ForegroundColor $Red
            Write-Host "[INFO] Please compile the backend project first: mvn clean package -DskipTests" -ForegroundColor $Yellow
            return $false
        }
    } elseif ($Type -eq "frontend") {
        $distPath = "./ruoyi-ui/dist"
        if (Test-Path $distPath) {
            Write-Host "[INFO] Frontend dist folder found: $distPath" -ForegroundColor $Green
            return $true
        } else {
            Write-Host "[ERROR] Frontend dist folder not found: $distPath" -ForegroundColor $Red
            Write-Host "[INFO] Please compile the frontend project first: npm run build:prod" -ForegroundColor $Yellow
            return $false
        }
    }
    return $false
}

# 构建后端镜像
function Build-BackendImage {
    Write-Host "[STEP] Building backend image..." -ForegroundColor $Yellow
    
    if (-not (Test-PrecompiledFiles "backend")) {
        return $false
    }
    
    # 创建临时的后端 Dockerfile
    $backendDockerfile = @"
# 后端Dockerfile - 使用预编译的JAR包
FROM openjdk:8-jre-alpine

# 设置时区
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && \
    echo "Asia/Shanghai" > /etc/timezone && \
    apk del tzdata

# 创建应用用户
RUN addgroup -g 1000 appgroup && \
    adduser -D -s /bin/sh -u 1000 -G appgroup appuser

# 设置工作目录
WORKDIR /app

# 复制预编译的JAR文件
COPY ruoyi-admin/target/ruoyi-admin.jar app.jar

# 创建日志目录并设置正确的权限
RUN mkdir -p /app/logs && \
    mkdir -p /home/ruoyi/logs && \
    chown -R appuser:appgroup /app && \
    chown -R appuser:appgroup /home/ruoyi && \
    chmod -R 777 /home/ruoyi/logs && \
    chmod -R 777 /app/logs

# 切换到应用用户
USER appuser

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]
"@
    
    # 保存临时 Dockerfile
    $backendDockerfile | Out-File -FilePath "Dockerfile.backend.temp" -Encoding UTF8
    
    try {
        # 构建镜像
        $buildCmd = "docker build -f Dockerfile.backend.temp -t ${BackendImageName}:${BackendTag} ."
        Write-Host "[INFO] Executing: $buildCmd" -ForegroundColor $Cyan
        
        Invoke-Expression $buildCmd
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[SUCCESS] Backend image built successfully: ${BackendImageName}:${BackendTag}" -ForegroundColor $Green
            return $true
        } else {
            Write-Host "[ERROR] Backend image build failed" -ForegroundColor $Red
            return $false
        }
    } finally {
        # 清理临时文件
        if (Test-Path "Dockerfile.backend.temp") {
            Remove-Item "Dockerfile.backend.temp" -Force
        }
    }
}

# 构建前端镜像
function Build-FrontendImage {
    Write-Host "[STEP] Building frontend image..." -ForegroundColor $Yellow
    
    if (-not (Test-PrecompiledFiles "frontend")) {
        return $false
    }
    
    # 创建临时的前端 Dockerfile
    $frontendDockerfile = @"
# 前端Dockerfile - 使用预编译的dist文件
FROM nginx:stable-alpine

# 复制nginx配置
COPY nginx-frontend.conf /etc/nginx/nginx.conf

# 复制预编译的前端文件
COPY ./ruoyi-ui/dist/ /usr/share/nginx/html/

# 创建nginx用户并设置权限
RUN addgroup -g 1000 nginxgroup && \
    adduser -D -s /bin/sh -u 1000 -G nginxgroup nginxuser && \
    chown -R nginxuser:nginxgroup /usr/share/nginx/html && \
    chown -R nginxuser:nginxgroup /var/cache/nginx && \
    chown -R nginxuser:nginxgroup /var/log/nginx && \
    chown -R nginxuser:nginxgroup /etc/nginx/conf.d && \
    chown -R nginxuser:nginxgroup /var/run && \
    chmod -R 755 /var/run && \
    chmod -R 755 /var/log/nginx && \
    chmod -R 755 /var/cache/nginx

# 切换到nginx用户
USER nginxuser

# 暴露端口
EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:80 || exit 1

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
"@
    
    # 保存临时 Dockerfile
    $frontendDockerfile | Out-File -FilePath "Dockerfile.frontend.temp" -Encoding UTF8
    
    try {
        # 构建镜像
        $buildCmd = "docker build -f Dockerfile.frontend.temp -t ${FrontendImageName}:${FrontendTag} ."
        Write-Host "[INFO] Executing: $buildCmd" -ForegroundColor $Cyan
        
        Invoke-Expression $buildCmd
        if ($LASTEXITCODE -eq 0) {
            Write-Host "[SUCCESS] Frontend image built successfully: ${FrontendImageName}:${FrontendTag}" -ForegroundColor $Green
            return $true
        } else {
            Write-Host "[ERROR] Frontend image build failed" -ForegroundColor $Red
            return $false
        }
    } finally {
        # 清理临时文件
        if (Test-Path "Dockerfile.frontend.temp") {
            Remove-Item "Dockerfile.frontend.temp" -Force
        }
    }
}

# 主函数
function Main {
    Write-Host "Email Manager Docker Image Builder (Pre-compiled Version)" -ForegroundColor $Cyan
    Write-Host ""
    
    # 检查参数
    if ($Target -eq "help") {
        Show-Help
        return
    }
    
    # 检查 Docker 环境
    if (-not (Test-DockerEnvironment)) {
        Write-Host "[ERROR] Please start Docker Desktop first" -ForegroundColor $Red
        exit 1
    }
    
    # 根据目标构建镜像
    $success = $true
    
    switch ($Target.ToLower()) {
        "backend" {
            $success = Build-BackendImage
        }
        "frontend" {
            $success = Build-FrontendImage
        }
        "all" {
            Write-Host "[STEP] Building all images..." -ForegroundColor $Yellow
            $backendSuccess = Build-BackendImage
            $frontendSuccess = Build-FrontendImage
            $success = $backendSuccess -and $frontendSuccess
        }
        default {
            Write-Host "[ERROR] Unknown target: $Target" -ForegroundColor $Red
            Show-Help
            exit 1
        }
    }
    
    # 显示结果
    if ($success) {
        Write-Host ""
        Write-Host "[SUCCESS] All operations completed successfully!" -ForegroundColor $Green
        Write-Host ""
        Write-Host "Built images:" -ForegroundColor $Cyan
        if ($Target -eq "backend" -or $Target -eq "all") {
            Write-Host "  - ${BackendImageName}:${BackendTag}" -ForegroundColor $White
        }
        if ($Target -eq "frontend" -or $Target -eq "all") {
            Write-Host "  - ${FrontendImageName}:${FrontendTag}" -ForegroundColor $White
        }
        Write-Host ""
        Write-Host "Next steps:" -ForegroundColor $Cyan
        Write-Host "  - Use '.\deploy.ps1 start' to start the services" -ForegroundColor $White
        Write-Host "  - Use '.\deploy.ps1 status' to check service status" -ForegroundColor $White
    } else {
        Write-Host ""
        Write-Host "[ERROR] Some operations failed. Please check the error messages above." -ForegroundColor $Red
        exit 1
    }
}

# 执行主函数
Main




