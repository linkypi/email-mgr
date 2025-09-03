# Docker Image Build Script (PowerShell Version)

param(
    [Parameter(Position=0)]
    [ValidateSet("backend", "frontend", "all")]
    [string]$Target = "all"
)

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Docker Image Build Script" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Target: $Target" -ForegroundColor Yellow
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

# Copy configuration files (only needed for backend)
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "[INFO] Copying configuration files to docker directory..." -ForegroundColor Green
    try {
        $sourceDir = Join-Path (Get-Location) "..\ruoyi-admin\src\main\resources"
        $appYml = Join-Path $sourceDir "application.yml"
        $appDruidYml = Join-Path $sourceDir "application-druid.yml"
        
        Write-Host "[INFO] Source directory: $sourceDir" -ForegroundColor Yellow
        Write-Host "[INFO] Looking for: $appYml" -ForegroundColor Yellow
        Write-Host "[INFO] Looking for: $appDruidYml" -ForegroundColor Yellow
        
        if (-not (Test-Path $appYml)) {
            throw "application.yml not found at: $appYml"
        }
        
        if (-not (Test-Path $appDruidYml)) {
            throw "application-druid.yml not found at: $appDruidYml"
        }
        
        Copy-Item $appYml "application.yml" -Force
        Copy-Item $appDruidYml "application-druid.yml" -Force
        Write-Host "[INFO] Configuration files copied successfully" -ForegroundColor Green
    } catch {
        Write-Host "[ERROR] Failed to copy configuration files: $($_.Exception.Message)" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }
}

# Build backend image
if ($Target -eq "backend" -or $Target -eq "all") {
    Write-Host "[INFO] Building backend image..." -ForegroundColor Green
    
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
    $projectRoot = Join-Path (Get-Location) ".."
    Set-Location $projectRoot
    Write-Host "[INFO] Current directory: $(Get-Location)" -ForegroundColor Green

    # Verify we're in the right directory
    if (-not (Test-Path "pom.xml")) {
        Write-Host "[ERROR] pom.xml not found. Current directory: $(Get-Location)" -ForegroundColor Red
        Read-Host "Press Enter to exit"
        exit 1
    }

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

    # Build backend Docker image
    Write-Host "[INFO] Building backend Docker image..." -ForegroundColor Green
    try {
        & docker build -f Dockerfile.backend -t ruoyi-backend:latest .
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
    
    # Return to docker directory
    Set-Location docker
}

# Build frontend image
if ($Target -eq "frontend" -or $Target -eq "all") {
    Write-Host "[INFO] Building frontend image..." -ForegroundColor Green
    
    # Check if we need to go to project root for frontend build
    if ($Target -eq "frontend") {
        $projectRoot = Join-Path (Get-Location) ".."
        Set-Location $projectRoot
        Write-Host "[INFO] Current directory: $(Get-Location)" -ForegroundColor Green
    }
    
    try {
        & docker build -f Dockerfile.frontend -t ruoyi-frontend:latest .
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
    
    # Return to docker directory
    Set-Location docker
}

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
