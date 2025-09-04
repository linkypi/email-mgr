# Docker Image Build Script (PowerShell Version)

param(
    [Parameter(Position=0)]
    [ValidateSet("backend", "frontend", "all")]
    [string]$Target = "all",
    
    [Parameter(Position=1)]
    [ValidateSet("build", "dist-only")]
    [string]$FrontendMode = "build"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Docker Image Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Target: $Target" -ForegroundColor Yellow
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "Frontend Mode: $FrontendMode" -ForegroundColor Yellow
}
Write-Host "========================================" -ForegroundColor Cyan

# Check Docker installation
Write-Host "[INFO] Checking Docker environment..." -ForegroundColor Green
try {
    $dockerVersion = docker --version 2>$null
    if ($dockerVersion) {
        Write-Host "[INFO] Docker version: $dockerVersion" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Docker not installed, please install Docker first" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "[ERROR] Docker not installed, please install Docker first" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

try {
    $composeVersion = docker-compose --version 2>$null
    if ($composeVersion) {
        Write-Host "[INFO] Docker Compose version: $composeVersion" -ForegroundColor Green
    } else {
        Write-Host "[ERROR] Docker Compose not installed, please install Docker Compose first" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
} catch {
    Write-Host "[ERROR] Docker Compose not installed, please install Docker Compose first" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "[INFO] Docker environment check passed" -ForegroundColor Green

# 切换到项目根目录
$projectRoot = Join-Path (Get-Location) ".."
Set-Location $projectRoot
Write-Host "[INFO] Working directory: $(Get-Location)" -ForegroundColor Green

# Verify we're in the right directory
if (-not (Test-Path "pom.xml")) {
    Write-Host "[ERROR] pom.xml not found. Current directory: $(Get-Location)" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
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
            Write-Host "[ERROR] Maven not installed, please install Maven first" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
    } catch {
        Write-Host "[ERROR] Maven not installed, please install Maven first" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }

    # Build Java project
    Write-Host "[INFO] Compiling Java project..." -ForegroundColor Green
    try {
        & mvn clean package -DskipTests
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[ERROR] Java project compilation failed" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
        Write-Host "[INFO] Java project compiled successfully" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Java project compilation failed: $($_.Exception.Message)" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
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
            Write-Host "[ERROR] ruoyi-ui\dist directory not found. Please build frontend first or use 'build' mode." -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
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
                Write-Host "[ERROR] Node.js or NPM not installed, please install Node.js first" -ForegroundColor Red
                Read-Host "Press Enter to exit"
                exit 1
            }
        } catch {
            Write-Host "[ERROR] Node.js or NPM not installed, please install Node.js first" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }

        # Verify frontend directory exists
        if (-not (Test-Path "ruoyi-ui")) {
            Write-Host "[ERROR] ruoyi-ui directory not found. Current directory: $(Get-Location)" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }

        # Build frontend project
        Write-Host "[INFO] Building frontend project..." -ForegroundColor Green
        Set-Location ruoyi-ui
        try {
            # Install dependencies
            Write-Host "[INFO] Installing frontend dependencies..." -ForegroundColor Green
            & npm.cmd ci
            if ($LASTEXITCODE -ne 0) {
                Write-Host "[ERROR] Frontend dependencies installation failed" -ForegroundColor Red
                Read-Host "Press Enter to exit"
                exit 1
            }

            # Build frontend
            Write-Host "[INFO] Building frontend application..." -ForegroundColor Green
            & npm.cmd run build:prod
            if ($LASTEXITCODE -ne 0) {
                Write-Host "[ERROR] Frontend build failed" -ForegroundColor Red
                Read-Host "Press Enter to exit"
                exit 1
            }
            Write-Host "[INFO] Frontend project built successfully" -ForegroundColor Green
        } catch {
            Write-Host "[ERROR] Frontend build failed: $($_.Exception.Message)" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
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
            Write-Host "[ERROR] Backend image build failed" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
        Write-Host "[INFO] Backend image built successfully" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Backend image build failed: $($_.Exception.Message)" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# Build frontend Docker image
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "[INFO] Building frontend Docker image..." -ForegroundColor Green
    try {
        & docker build -f docker/Dockerfile.frontend -t ruoyi-frontend:latest .
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[ERROR] Frontend image build failed" -ForegroundColor Red
            Read-Host "Press Enter to exit"
            exit 1
        }
        Write-Host "[INFO] Frontend image built successfully" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Frontend image build failed: $($_.Exception.Message)" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# Return to docker directory
Set-Location docker

# Final message
Write-Host "[INFO] Build completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Built images:" -ForegroundColor Yellow
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "  - ruoyi-backend:latest" -ForegroundColor Green
}
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "  - ruoyi-frontend:latest" -ForegroundColor Green
}
Write-Host ""
Write-Host "Next step: Run .\deploy.bat start to start services" -ForegroundColor Yellow
Read-Host "Press Enter to exit"
