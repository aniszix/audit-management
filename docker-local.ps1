# ===========================================
# Script de test Docker local - PowerShell
# ===========================================

param(
    [switch]$Build,
    [switch]$Run,
    [switch]$Stop,
    [switch]$Push,
    [string]$Tag = "latest"
)

$ImageName = "audit-management"
$ContainerName = "audit-management-app"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  Docker - Audit Management" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

if ($Build) {
    Write-Host "`n[BUILD] Construction de l'image Docker..." -ForegroundColor Yellow
    docker build -t ${ImageName}:${Tag} .
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Image construite: ${ImageName}:${Tag}" -ForegroundColor Green
        docker images | Select-String $ImageName
    } else {
        Write-Host "❌ Échec de la construction" -ForegroundColor Red
        exit 1
    }
}

if ($Run) {
    Write-Host "`n[RUN] Démarrage du conteneur..." -ForegroundColor Yellow
    
    # Arrêter si existant
    docker stop $ContainerName 2>$null
    docker rm $ContainerName 2>$null
    
    # Démarrer le conteneur
    docker run -d `
        --name $ContainerName `
        -p 8081:8081 `
        -e SPRING_PROFILES_ACTIVE=prod `
        -e DB_HOST=host.docker.internal `
        -e DB_PORT=5433 `
        -e DB_NAME=auditdb `
        -e DB_USERNAME=postgres `
        -e DB_PASSWORD=postgres `
        ${ImageName}:${Tag}
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ Conteneur démarré: $ContainerName" -ForegroundColor Green
        Write-Host "`n📋 Informations:" -ForegroundColor White
        Write-Host "   URL API: http://localhost:8081/api/users"
        Write-Host "   Swagger: http://localhost:8081/swagger-ui.html"
        Write-Host "   Health:  http://localhost:8081/actuator/health"
        Write-Host "`n📊 Logs:" -ForegroundColor White
        docker logs -f $ContainerName
    } else {
        Write-Host "❌ Échec du démarrage" -ForegroundColor Red
    }
}

if ($Stop) {
    Write-Host "`n[STOP] Arrêt du conteneur..." -ForegroundColor Yellow
    docker stop $ContainerName
    docker rm $ContainerName
    Write-Host "✅ Conteneur arrêté et supprimé" -ForegroundColor Green
}

if ($Push) {
    Write-Host "`n[PUSH] Publication sur Docker Hub..." -ForegroundColor Yellow
    Write-Host "⚠️  Assurez-vous d'être connecté: docker login" -ForegroundColor Yellow
    
    $DockerHubUser = Read-Host "Entrez votre username Docker Hub"
    $FullImageName = "${DockerHubUser}/${ImageName}"
    
    docker tag ${ImageName}:${Tag} ${FullImageName}:${Tag}
    docker tag ${ImageName}:${Tag} ${FullImageName}:latest
    docker push ${FullImageName}:${Tag}
    docker push ${FullImageName}:latest
    
    Write-Host "✅ Image publiée: ${FullImageName}" -ForegroundColor Green
}

if (-not ($Build -or $Run -or $Stop -or $Push)) {
    Write-Host "`nUsage:" -ForegroundColor White
    Write-Host "  .\docker-local.ps1 -Build        # Construire l'image"
    Write-Host "  .\docker-local.ps1 -Run          # Démarrer le conteneur"
    Write-Host "  .\docker-local.ps1 -Stop         # Arrêter le conteneur"
    Write-Host "  .\docker-local.ps1 -Push         # Publier sur Docker Hub"
    Write-Host "  .\docker-local.ps1 -Build -Run   # Build et Run"
}
