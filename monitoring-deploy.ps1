# ===========================================
# Script de déploiement Monitoring - PowerShell
# ===========================================

param(
    [switch]$Deploy,
    [switch]$Status,
    [switch]$Delete,
    [switch]$Open
)

$Namespace = "audit-app"

Write-Host "=============================================" -ForegroundColor Cyan
Write-Host "  Monitoring - Prometheus & Grafana" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

if ($Deploy) {
    Write-Host "`n[DEPLOY] Déploiement du stack de monitoring..." -ForegroundColor Yellow
    
    $monitoringPath = Join-Path $PSScriptRoot "k8s\monitoring"
    
    Write-Host "1. Déploiement de Prometheus..."
    kubectl apply -f "$monitoringPath\prometheus.yaml"
    
    Write-Host "2. Déploiement de Grafana..."
    kubectl apply -f "$monitoringPath\grafana.yaml"
    
    Write-Host "`n⏳ Attente du déploiement..."
    kubectl rollout status deployment/prometheus -n $Namespace --timeout=120s
    kubectl rollout status deployment/grafana -n $Namespace --timeout=120s
    
    Write-Host "`n✅ Monitoring déployé!" -ForegroundColor Green
    Write-Host "`n📊 Accès:" -ForegroundColor White
    Write-Host "   Prometheus: http://localhost:30090"
    Write-Host "   Grafana:    http://localhost:30030"
    Write-Host "   Login:      admin / admin123"
}

if ($Status) {
    Write-Host "`n[STATUS] État du monitoring..." -ForegroundColor Yellow
    
    Write-Host "`n📊 Prometheus:" -ForegroundColor White
    kubectl get pods -n $Namespace -l app=prometheus
    
    Write-Host "`n📈 Grafana:" -ForegroundColor White
    kubectl get pods -n $Namespace -l app=grafana
    
    Write-Host "`n🔌 Services:" -ForegroundColor White
    kubectl get svc -n $Namespace | Select-String "prometheus|grafana"
}

if ($Delete) {
    Write-Host "`n[DELETE] Suppression du monitoring..." -ForegroundColor Yellow
    
    $monitoringPath = Join-Path $PSScriptRoot "k8s\monitoring"
    
    kubectl delete -f "$monitoringPath\grafana.yaml"
    kubectl delete -f "$monitoringPath\prometheus.yaml"
    
    Write-Host "✅ Monitoring supprimé" -ForegroundColor Green
}

if ($Open) {
    Write-Host "`n[OPEN] Ouverture des interfaces..." -ForegroundColor Yellow
    
    # Ouvrir dans le navigateur
    Start-Process "http://localhost:30090"  # Prometheus
    Start-Process "http://localhost:30030"  # Grafana
}

if (-not ($Deploy -or $Status -or $Delete -or $Open)) {
    Write-Host "`nUsage:" -ForegroundColor White
    Write-Host "  .\monitoring-deploy.ps1 -Deploy   # Déployer Prometheus & Grafana"
    Write-Host "  .\monitoring-deploy.ps1 -Status   # Voir l'état"
    Write-Host "  .\monitoring-deploy.ps1 -Open     # Ouvrir dans le navigateur"
    Write-Host "  .\monitoring-deploy.ps1 -Delete   # Supprimer le monitoring"
}
