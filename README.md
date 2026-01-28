# Audit Management

Application de gestion d'audit développée avec Spring Boot.

## 🚀 Technologies utilisées

- **Java 17** - Langage de programmation
- **Spring Boot 3.x** - Framework backend
- **Spring Data JPA** - Persistance des données
- **H2 Database** - Base de données en développement
- **PostgreSQL** - Base de données en production
- **Swagger/OpenAPI** - Documentation API
- **JUnit 5 + Mockito** - Tests unitaires
- **Cucumber** - Tests BDD
- **JaCoCo** - Couverture de code
- **Docker** - Conteneurisation
- **Jenkins** - CI/CD

## 📁 Structure du projet

```
src/
├── main/
│   ├── java/
│   │   └── com/example/audit/audit_management/
│   │       ├── config/          # Configuration (OpenAPI, etc.)
│   │       ├── controller/      # Contrôleurs REST
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── entity/          # Entités JPA
│   │       ├── exception/       # Gestion des erreurs
│   │       ├── mapper/          # Mappers Entity <-> DTO
│   │       ├── repository/      # Repositories JPA
│   │       └── service/         # Services métier
│   └── resources/
│       ├── application.yaml           # Config commune
│       ├── application-dev.yaml       # Config développement
│       └── application-prod.yaml      # Config production
└── test/
    ├── java/                    # Tests unitaires et intégration
    └── resources/
        └── features/            # Scénarios Cucumber
```

## 🛠️ Prérequis

- JDK 17+
- Maven 3.9+
- Docker (optionnel)

## ⚡ Démarrage rapide

### Mode développement (H2)

```bash
# Compiler et lancer
mvn spring-boot:run

# Ou avec le profil explicite
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

L'application démarre sur http://localhost:8081

### Accès aux interfaces

| Interface | URL |
|-----------|-----|
| API REST | http://localhost:8081/api/users |
| Swagger UI | http://localhost:8081/swagger-ui.html |
| API Docs | http://localhost:8081/api-docs |
| Console H2 | http://localhost:8081/h2-console |
| Actuator | http://localhost:8081/actuator/health |

### Connexion H2 Console

- JDBC URL: `jdbc:h2:mem:auditdb`
- User: `sa`
- Password: *(vide)*

## 🧪 Tests

```bash
# Tous les tests
mvn test

# Tests avec couverture JaCoCo
mvn verify

# Voir le rapport de couverture
# Ouvrir: target/site/jacoco/index.html
```

## 🐳 Docker

### Build et exécution

```bash
# Construire l'image
docker build -t audit-management:latest .

# Exécuter le conteneur
docker run -p 8081:8081 audit-management:latest

# Avec variables d'environnement
docker run -p 8081:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=host.docker.internal \
  audit-management:latest
```

### Docker Compose (avec PostgreSQL)

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Arrêter
docker-compose down
```

## 📊 Qualité de code

### JaCoCo (Couverture)

```bash
# Générer le rapport
mvn jacoco:report

# Vérifier le seuil (80%)
mvn jacoco:check
```

### SonarCloud

```bash
# Analyse locale
mvn sonar:sonar \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=YOUR_TOKEN
```

## 🔧 Configuration

### Profils Spring

| Profil | Usage | Base de données |
|--------|-------|-----------------|
| `dev` | Développement local | H2 (mémoire) |
| `prod` | Production | PostgreSQL |
| `test` | Tests automatisés | H2 (mémoire) |

### Variables d'environnement (Production)

| Variable | Description | Défaut |
|----------|-------------|--------|
| `DB_HOST` | Hôte PostgreSQL | localhost |
| `DB_PORT` | Port PostgreSQL | 5432 |
| `DB_NAME` | Nom de la base | auditdb |
| `DB_USERNAME` | Utilisateur | audit_user |
| `DB_PASSWORD` | Mot de passe | audit_password |
| `SWAGGER_ENABLED` | Activer Swagger | false |

## 📚 API Endpoints

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/users` | Liste tous les utilisateurs |
| GET | `/api/users/{id}` | Récupère un utilisateur |
| POST | `/api/users` | Crée un utilisateur |
| PUT | `/api/users/{id}` | Met à jour un utilisateur |
| DELETE | `/api/users/{id}` | Supprime un utilisateur |
| GET | `/api/users/search?username=xxx` | Recherche par nom |
| GET | `/api/users/role/{role}` | Filtre par rôle |

## 🔄 CI/CD avec Jenkins

Le `Jenkinsfile` inclut les étapes suivantes :

1. **Checkout** - Récupération du code
2. **Build** - Compilation Maven
3. **Unit Tests** - Tests unitaires
4. **Integration Tests** - Tests d'intégration
5. **Code Coverage** - Rapport JaCoCo
6. **SonarCloud Analysis** - Analyse qualité
7. **Quality Gate** - Validation des seuils
8. **Package** - Création du JAR
9. **Docker Build** - Construction de l'image
10. **Docker Push** - Publication (branche main)
11. **Deploy** - Déploiement (branche develop)

## 📝 Licence

MIT License
