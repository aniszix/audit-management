# ===========================================
# Script de déploiement Kubernetes - PowerShell
# ===========================================

param(
    [switch]$Install,
    [switch]$Deploy,
    [switch]$Status,
    [switch]$Logs,
    [switch]$Delete,
    [switch]$PortForward
)

$Namespace = "audit-app"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  Kubernetes - Audit Management" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

# Vérifier kubectl
try {
    kubectl version --client | Out-Null
} catch {
    Write-Host "❌ kubectl n'est pas installé" -ForegroundColor Red
    exit 1
}

if ($Install) {
    Write-Host "`n[INSTALL] Installation de Minikube..." -ForegroundColor Yellow
    
    # Vérifier si Minikube est installé
    $minikube = Get-Command minikube -ErrorAction SilentlyContinue
    if (-not $minikube) {
        Write-Host "Installation de Minikube via Chocolatey..."
        choco install minikube -y
    }
    
    # Démarrer Minikube
    Write-Host "`nDémarrage de Minikube..."
    minikube start --driver=docker --cpus=2 --memory=4096
    
    # Activer les addons
    Write-Host "`nActivation des addons..."
    minikube addons enable ingress
    minikube addons enable metrics-server
    
    Write-Host "✅ Minikube installé et démarré" -ForegroundColor Green
    minikube status
}

if ($Deploy) {
    Write-Host "`n[DEPLOY] Déploiement sur Kubernetes..." -ForegroundColor Yellow
    
    # Appliquer les manifests dans l'ordre
    $k8sPath = Join-Path $PSScriptRoot "k8s"
    
    Write-Host "1. Création du namespace..."
    kubectl apply -f "$k8sPath\namespace.yaml"
    
    Write-Host "2. Création des secrets..."
    kubectl apply -f "$k8sPath\secret.yaml"
    
    Write-Host "3. Création des ConfigMaps..."
    kubectl apply -f "$k8sPath\configmap.yaml"
    
    Write-Host "4. Déploiement de PostgreSQL..."
    kubectl apply -f "$k8sPath\postgres.yaml"
    
    Write-Host "5. Attente de PostgreSQL (30s)..."
    Start-Sleep -Seconds 30
    
    Write-Host "6. Déploiement du backend..."
    kubectl apply -f "$k8sPath\deployment.yaml"
    
    Write-Host "7. Création des services..."
    kubectl apply -f "$k8sPath\service.yaml"
    
    Write-Host "`n⏳ Attente du déploiement..."
    kubectl rollout status deployment/audit-management -n $Namespace --timeout=120s
    
    Write-Host "✅ Déploiement terminé!" -ForegroundColor Green
}

if ($Status) {
    Write-Host "`n[STATUS] État du déploiement..." -ForegroundColor Yellow
    
    Write-Host "`n📦 Pods:" -ForegroundColor White
    kubectl get pods -n $Namespace -o wide
    
    Write-Host "`n🔌 Services:" -ForegroundColor White
    kubectl get svc -n $Namespace
    
    Write-Host "`n📊 Deployments:" -ForegroundColor White
    kubectl get deployments -n $Namespace
    
    Write-Host "`n💾 PVC:" -ForegroundColor White
    kubectl get pvc -n $Namespace
}

if ($Logs) {
    Write-Host "`n[LOGS] Logs de l'application..." -ForegroundColor Yellow
    kubectl logs -l app=audit-management -n $Namespace -f --tail=100
}

if ($Delete) {
    Write-Host "`n[DELETE] Suppression du déploiement..." -ForegroundColor Yellow
    kubectl delete namespace $Namespace
    Write-Host "✅ Namespace supprimé" -ForegroundColor Green
}

if ($PortForward) {
    Write-Host "`n[PORT-FORWARD] Accès local à l'application..." -ForegroundColor Yellow
    Write-Host "L'API sera accessible sur http://localhost:8081"
    Write-Host "Appuyez sur Ctrl+C pour arrêter"
    kubectl port-forward svc/audit-management-service 8081:8081 -n $Namespace
}

if (-not ($Install -or $Deploy -or $Status -or $Logs -or $Delete -or $PortForward)) {
    Write-Host "`nUsage:" -ForegroundColor White
    Write-Host "  .\k8s-deploy.ps1 -Install      # Installer Minikube"
    Write-Host "  .\k8s-deploy.ps1 -Deploy       # Déployer l'application"
    Write-Host "  .\k8s-deploy.ps1 -Status       # Voir l'état"
    Write-Host "  .\k8s-deploy.ps1 -Logs         # Voir les logs"
    Write-Host "  .\k8s-deploy.ps1 -PortForward  # Accès local"
    Write-Host "  .\k8s-deploy.ps1 -Delete       # Supprimer tout"
}
