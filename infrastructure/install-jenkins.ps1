# ===========================================
# Script d'installation Jenkins - PowerShell
# ===========================================

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  Installation Jenkins CI/CD" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# Vérifier Docker
Write-Host "`n[1/4] Vérification de Docker..." -ForegroundColor Yellow
try {
    docker --version
    Write-Host "✅ Docker est installé" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker n'est pas installé. Veuillez l'installer d'abord." -ForegroundColor Red
    exit 1
}

# Démarrer Jenkins
Write-Host "`n[2/4] Démarrage de Jenkins avec Docker Compose..." -ForegroundColor Yellow
Set-Location $PSScriptRoot
docker-compose -f docker-compose.jenkins.yml up -d

# Attendre que Jenkins démarre
Write-Host "`n[3/4] Attente du démarrage de Jenkins (60 secondes)..." -ForegroundColor Yellow
Start-Sleep -Seconds 60

# Récupérer le mot de passe initial
Write-Host "`n[4/4] Récupération du mot de passe administrateur..." -ForegroundColor Yellow
$password = docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword 2>$null

if ($password) {
    Write-Host "`n=============================================" -ForegroundColor Green
    Write-Host "  Jenkins est prêt!" -ForegroundColor Green
    Write-Host "=============================================" -ForegroundColor Green
    Write-Host "`nURL: http://localhost:8080" -ForegroundColor Cyan
    Write-Host "Mot de passe initial: $password" -ForegroundColor Yellow
    Write-Host "`n📋 Étapes suivantes:" -ForegroundColor White
    Write-Host "1. Ouvrir http://localhost:8080"
    Write-Host "2. Entrer le mot de passe ci-dessus"
    Write-Host "3. Installer les plugins suggérés"
    Write-Host "4. Créer un utilisateur admin"
    Write-Host "5. Installer les plugins additionnels:"
    Write-Host "   - Docker Pipeline"
    Write-Host "   - SonarQube Scanner"
    Write-Host "   - JaCoCo"
    Write-Host "   - Kubernetes"
} else {
    Write-Host "⏳ Jenkins démarre encore... Réessayez dans quelques minutes." -ForegroundColor Yellow
    Write-Host "Commande: docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword"
}
