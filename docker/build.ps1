# Docker Image Build Script (PowerShell Version)

param(
    [Parameter(Position=0)]
    [ValidateSet("backend", "frontend", "all")]
    [string]$Target = "all",
    
    [Parameter(Position=1)]
    [ValidateSet("build", "dist-only")]
    [string]$FrontendMode = "build"
)

# 记录原始目录
$originalDirectory = Get-Location

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Docker Image Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Target: $Target" -ForegroundColor Yellow
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "Frontend Mode: $FrontendMode" -ForegroundColor Yellow
}
Write-Host "========================================" -ForegroundColor Cyan

# 错误处理函数
function Handle-Error {
    param([string]$ErrorMessage)
    Write-Host "[ERROR] $ErrorMessage" -ForegroundColor Red
    Write-Host "[INFO] Returning to original directory: $originalDirectory" -ForegroundColor Yellow
    Set-Location $originalDirectory
    Read-Host "Press Enter to exit"
    exit 1
}

# Check Docker installation
Write-Host "[INFO] Checking Docker environment..." -ForegroundColor Green
try {
    $dockerVersion = docker --version 2>$null
    if ($dockerVersion) {
        Write-Host "[INFO] Docker version: $dockerVersion" -ForegroundColor Green
    } else {
        Handle-Error "Docker not installed, please install Docker first"
    }
} catch {
    Handle-Error "Docker not installed, please install Docker first"
}

try {
    $composeVersion = docker-compose --version 2>$null
    if ($composeVersion) {
        Write-Host "[INFO] Docker Compose version: $composeVersion" -ForegroundColor Green
    } else {
        Handle-Error "Docker Compose not installed, please install Docker Compose first"
    }
} catch {
    Handle-Error "Docker Compose not installed, please install Docker Compose first"
}

Write-Host "[INFO] Docker environment check passed" -ForegroundColor Green

# 切换到项目根目录
$projectRoot = Join-Path (Get-Location) ".."
Set-Location $projectRoot
Write-Host "[INFO] Working directory: $(Get-Location)" -ForegroundColor Green

# Verify we're in the right directory
if (-not (Test-Path "pom.xml")) {
    Handle-Error "pom.xml not found. Current directory: $(Get-Location)"
}

# Build backend project (if needed)
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "[INFO] Building backend project..." -ForegroundColor Green
    
    # Check Maven installation
    Write-Host "[INFO] Checking Maven environment..." -ForegroundColor Green
    try {
        $mvnVersion = mvn --version 2>$null
        if ($mvnVersion) {
            Write-Host "[INFO] Maven environment check passed" -ForegroundColor Green
        } else {
            Handle-Error "Maven not installed, please install Maven first"
        }
    } catch {
        Handle-Error "Maven not installed, please install Maven first"
    }

    # Build Java project
    Write-Host "[INFO] Compiling Java project..." -ForegroundColor Green
    try {
        & mvn clean package -DskipTests
        if ($LASTEXITCODE -ne 0) {
            Handle-Error "Java project compilation failed"
        }
        Write-Host "[INFO] Java project compiled successfully" -ForegroundColor Green
    } catch {
        Handle-Error "Java project compilation failed: $($_.Exception.Message)"
    }
}

# Build frontend project (if needed)
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "[INFO] Building frontend project..." -ForegroundColor Green
    
    if ($FrontendMode -eq "dist-only") {
        # 直接从 dist 目录构建，跳过编译
        Write-Host "[INFO] Using existing dist directory (dist-only mode)" -ForegroundColor Yellow
        
        # 验证 dist 目录是否存在
        if (-not (Test-Path "ruoyi-ui\dist")) {
            Handle-Error "ruoyi-ui\dist directory not found. Please build frontend first or use 'build' mode."
        }
        
        Write-Host "[INFO] Found existing dist directory, skipping compilation" -ForegroundColor Green
    } else {
        # 先编译再构建
        Write-Host "[INFO] Building frontend from source (build mode)" -ForegroundColor Yellow
        
        # Check Node.js installation
        Write-Host "[INFO] Checking Node.js environment..." -ForegroundColor Green
        try {
            $nodeVersion = node --version 2>$null
            $npmVersion = npm --version 2>$null
            if ($nodeVersion -and $npmVersion) {
                Write-Host "[INFO] Node.js version: $nodeVersion" -ForegroundColor Green
                Write-Host "[INFO] NPM version: $npmVersion" -ForegroundColor Green
            } else {
                Handle-Error "Node.js or NPM not installed, please install Node.js first"
            }
        } catch {
            Handle-Error "Node.js or NPM not installed, please install Node.js first"
        }

        # Verify frontend directory exists
        if (-not (Test-Path "ruoyi-ui")) {
            Handle-Error "ruoyi-ui directory not found. Current directory: $(Get-Location)"
        }

        # Build frontend project
        Write-Host "[INFO] Building frontend project..." -ForegroundColor Green
        Set-Location ruoyi-ui
        try {
            # Install dependencies
            Write-Host "[INFO] Installing frontend dependencies..." -ForegroundColor Green
            & npm.cmd ci
            if ($LASTEXITCODE -ne 0) {
                Handle-Error "Frontend dependencies installation failed"
            }

            # Build frontend
            Write-Host "[INFO] Building frontend application..." -ForegroundColor Green
            & npm.cmd run build:prod
            if ($LASTEXITCODE -ne 0) {
                Handle-Error "Frontend build failed"
            }
            Write-Host "[INFO] Frontend project built successfully" -ForegroundColor Green
        } catch {
            Handle-Error "Frontend build failed: $($_.Exception.Message)"
        } finally {
            # Return to project root
            Set-Location ..
        }
    }
}

# Build Docker images
Write-Host "[INFO] Building Docker images..." -ForegroundColor Green

# Build backend Docker image
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "[INFO] Building backend Docker image..." -ForegroundColor Green
    try {
        & docker build -f docker/Dockerfile.backend -t ruoyi-backend:latest .
        if ($LASTEXITCODE -ne 0) {
            Handle-Error "Backend image build failed"
        }
        Write-Host "[INFO] Backend image built successfully" -ForegroundColor Green
    } catch {
        Handle-Error "Backend image build failed: $($_.Exception.Message)"
    }
}

# Build frontend Docker image
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "[INFO] Building frontend Docker image..." -ForegroundColor Green
    try {
        & docker build -f docker/Dockerfile.frontend -t ruoyi-frontend:latest .
        if ($LASTEXITCODE -ne 0) {
            Handle-Error "Frontend image build failed"
        }
        Write-Host "[INFO] Frontend image built successfully" -ForegroundColor Green
    } catch {
        Handle-Error "Frontend image build failed: $($_.Exception.Message)"
    }
}

# Export Docker images to tar files
Write-Host "[INFO] Exporting Docker images to tar files..." -ForegroundColor Green

# Create docker directory if it doesn't exist
$dockerDir = Join-Path $projectRoot "docker"
if (-not (Test-Path $dockerDir)) {
    New-Item -ItemType Directory -Path $dockerDir -Force | Out-Null
}

# Export backend image
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "[INFO] Exporting backend image to tar file..." -ForegroundColor Green
    try {
        $backendTarPath = Join-Path $dockerDir "backend.tar"
        & docker save -o $backendTarPath ruoyi-backend:latest
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[WARNING] Failed to export backend image" -ForegroundColor Yellow
        } else {
            $backendSize = (Get-Item $backendTarPath).Length / 1MB
            Write-Host "[INFO] Backend image exported successfully: $backendTarPath (Size: $([math]::Round($backendSize, 2)) MB)" -ForegroundColor Green
        }
    } catch {
        Write-Host "[WARNING] Failed to export backend image: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# Export frontend image
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "[INFO] Exporting frontend image to tar file..." -ForegroundColor Green
    try {
        $frontendTarPath = Join-Path $dockerDir "frontend.tar"
        & docker save -o $frontendTarPath ruoyi-frontend:latest
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[WARNING] Failed to export frontend image" -ForegroundColor Yellow
        } else {
            $frontendSize = (Get-Item $frontendTarPath).Length / 1MB
            Write-Host "[INFO] Frontend image exported successfully: $frontendTarPath (Size: $([math]::Round($frontendSize, 2)) MB)" -ForegroundColor Green
        }
    } catch {
        Write-Host "[WARNING] Failed to export frontend image: $($_.Exception.Message)" -ForegroundColor Yellow
    }
}

# Return to original directory
Write-Host "[INFO] Returning to original directory: $originalDirectory" -ForegroundColor Yellow
Set-Location $originalDirectory

# Final message
Write-Host "[INFO] Build and export completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Built images:" -ForegroundColor Yellow
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "  - ruoyi-backend:latest" -ForegroundColor Green
}
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "  - ruoyi-frontend:latest" -ForegroundColor Green
}
Write-Host ""
Write-Host "Exported tar files:" -ForegroundColor Yellow
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "  - docker/backend.tar" -ForegroundColor Green
}
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "  - docker/frontend.tar" -ForegroundColor Green
}
Write-Host ""
Write-Host "Next step: Run .\deploy.bat start to start services" -ForegroundColor Yellow
Write-Host "Or use 'docker load -i docker/backend.tar' and 'docker load -i docker/frontend.tar' to load images on other machines" -ForegroundColor Cyan
Read-Host "Press Enter to exit"


