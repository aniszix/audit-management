# 🔍 Audit Management - Pipeline CI/CD Complet

## 📋 Vue d'ensemble

Application de gestion d'audit avec une chaîne CI/CD professionnelle complète.

### Stack Technique

| Composant | Technologie | Version |
|-----------|-------------|---------|
| Backend | Spring Boot | 3.5.10 |
| Language | Java | 17 |
| Build | Maven | 3.9.x |
| Base de données | PostgreSQL | 15 |
| Tests | JUnit 5 + Cucumber | - |
| Couverture | JaCoCo | 0.8.11 |
| CI/CD | Jenkins | LTS |
| Qualité | SonarCloud | - |
| Container | Docker | - |
| Orchestration | Kubernetes | - |
| Monitoring | Prometheus + Grafana | - |

---

## 🚀 Démarrage Rapide

### Prérequis

- Java 17+
- Maven 3.9+
- Docker Desktop
- kubectl
- Minikube (optionnel)

### Lancer en local

```bash
# Cloner le projet
git clone <url-du-repo>
cd audit-management

# Lancer avec Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Ou avec Docker
docker-compose up -d
```

### Accès

| Service | URL |
|---------|-----|
| API | http://localhost:8081/api/users |
| Swagger | http://localhost:8081/swagger-ui.html |
| Health | http://localhost:8081/actuator/health |
| Prometheus Metrics | http://localhost:8081/actuator/prometheus |

---

## 🏗️ Architecture CI/CD

```
┌──────────────────────────────────────────────────────────────────┐
│                        PIPELINE CI/CD                            │
├──────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌─────────┐    ┌─────────┐    ┌───────────┐    ┌────────────┐  │
│  │   Git   │───▶│ Jenkins │───▶│ SonarCloud│───▶│   Docker   │  │
│  │ (Push)  │    │ (Build) │    │ (Quality) │    │  (Image)   │  │
│  └─────────┘    └─────────┘    └───────────┘    └────────────┘  │
│                                                        │         │
│                                                        ▼         │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                     KUBERNETES                               ││
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐    ││
│  │  │ Backend  │  │PostgreSQL│  │Prometheus│  │ Grafana  │    ││
│  │  │  :8081   │  │  :5432   │  │  :9090   │  │  :3000   │    ││
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘    ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                  │
└──────────────────────────────────────────────────────────────────┘
```

---

## 📁 Structure du Projet

```
audit-management/
├── src/
│   ├── main/
│   │   ├── java/com/example/audit/
│   │   │   ├── controller/     # REST Controllers
│   │   │   ├── service/        # Business Logic
│   │   │   ├── repository/     # Data Access
│   │   │   ├── entity/         # JPA Entities
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── mapper/         # Object Mappers
│   │   │   ├── exception/      # Exception Handling
│   │   │   └── config/         # Configuration
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── application-dev.yaml
│   │       └── application-prod.yaml
│   └── test/                   # Tests
├── k8s/                        # Kubernetes Manifests
│   ├── namespace.yaml
│   ├── configmap.yaml
│   ├── secret.yaml
│   ├── deployment.yaml
│   ├── service.yaml
│   ├── postgres.yaml
│   └── monitoring/
│       ├── prometheus.yaml
│       └── grafana.yaml
├── infrastructure/             # Infrastructure CI
│   ├── docker-compose.jenkins.yml
│   └── install-jenkins.ps1
├── docs/                       # Documentation
│   └── SONARCLOUD_SETUP.md
├── Dockerfile                  # Image Docker
├── Jenkinsfile                 # Pipeline CI/CD
├── docker-compose.yml          # Dev local
├── pom.xml                     # Maven
└── README.md
```

---

## 🔧 Configuration Étape par Étape

### Étape 1 : Git

```powershell
# Initialiser le dépôt
git init
git checkout -b main
git checkout -b develop

# Premier commit
git add .
git commit -m "Initial commit"

# Pousser vers GitHub/GitLab
git remote add origin <url>
git push -u origin main
git push -u origin develop
```

### Étape 2 : Jenkins

```powershell
# Démarrer Jenkins avec Docker
cd infrastructure
.\install-jenkins.ps1

# Ou manuellement
docker-compose -f docker-compose.jenkins.yml up -d

# Récupérer le mot de passe admin
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

**Configuration Jenkins :**
1. Accéder à http://localhost:8080
2. Installer les plugins suggérés + Docker Pipeline, SonarQube Scanner, JaCoCo
3. Configurer les credentials (SonarCloud, Docker Hub, Kubeconfig)
4. Créer un Pipeline pointant vers le Jenkinsfile

### Étape 3 : SonarCloud

Voir [docs/SONARCLOUD_SETUP.md](docs/SONARCLOUD_SETUP.md)

### Étape 4 : Docker

```powershell
# Build local
.\docker-local.ps1 -Build

# Run local
.\docker-local.ps1 -Run

# Push sur Docker Hub
.\docker-local.ps1 -Push
```

### Étape 5 : Kubernetes

```powershell
# Installer Minikube
.\k8s-deploy.ps1 -Install

# Déployer l'application
.\k8s-deploy.ps1 -Deploy

# Voir l'état
.\k8s-deploy.ps1 -Status

# Accès local
.\k8s-deploy.ps1 -PortForward
```

### Étape 6 : Monitoring

```powershell
# Déployer Prometheus & Grafana
.\monitoring-deploy.ps1 -Deploy

# Ouvrir les interfaces
.\monitoring-deploy.ps1 -Open
```

**Accès Grafana :**
- URL : http://localhost:30030
- Login : admin / admin123

---

## 📊 Endpoints de l'API

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | /api/users | Liste tous les utilisateurs |
| GET | /api/users/{id} | Récupère un utilisateur |
| POST | /api/users | Crée un utilisateur |
| PUT | /api/users/{id} | Met à jour un utilisateur |
| DELETE | /api/users/{id} | Supprime un utilisateur |

---

## 🧪 Tests

```bash
# Tests unitaires
./mvnw test

# Tests d'intégration
./mvnw verify

# Rapport de couverture
./mvnw jacoco:report
# Voir target/site/jacoco/index.html
```

---

## 📈 Monitoring

### Métriques disponibles

| Métrique | Description |
|----------|-------------|
| `up` | Statut de l'application |
| `process_cpu_usage` | Utilisation CPU |
| `jvm_memory_used_bytes` | Mémoire JVM utilisée |
| `http_server_requests_seconds_count` | Nombre de requêtes HTTP |
| `http_server_requests_seconds_sum` | Temps total des requêtes |

### Dashboard Grafana

Le dashboard pré-configuré affiche :
- ✅ Statut de l'application
- 📊 Utilisation CPU
- 💾 Mémoire JVM
- 🔄 Taux de requêtes HTTP
- ⏱️ Temps de réponse moyen

---

## 🔐 Sécurité

- Utilisateur non-root dans Docker
- Secrets Kubernetes encodés en base64
- Quality Gate SonarCloud obligatoire
- Probes de santé Kubernetes

---

## 📝 Variables à personnaliser

| Fichier | Variable | Description |
|---------|----------|-------------|
| Jenkinsfile | `YOUR_DOCKERHUB_USERNAME` | Username Docker Hub |
| Jenkinsfile | `SONAR_PROJECT_KEY` | Clé projet SonarCloud |
| Jenkinsfile | `SONAR_ORGANIZATION` | Organisation SonarCloud |
| k8s/deployment.yaml | `image:` | Image Docker complète |
| sonar-project.properties | `sonar.projectKey` | Clé projet |
| sonar-project.properties | `sonar.organization` | Organisation |

---

## 🎯 Résultat Final

✅ **Git** : Branches main/develop configurées  
✅ **Jenkins** : Pipeline déclarative complète  
✅ **SonarCloud** : Analyse qualité + Quality Gate  
✅ **Docker** : Image multi-stage optimisée  
✅ **Kubernetes** : Déploiement avec rolling updates  
✅ **Monitoring** : Prometheus + Grafana avec dashboard  

---

## 📞 Support

Pour toute question, ouvrir une issue sur le dépôt GitHub.

---

**Auteur** : Équipe Audit Management  
**Version** : 1.0.0  
**Date** : Janvier 2026
