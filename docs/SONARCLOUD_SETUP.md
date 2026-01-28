# 🔍 Guide de Configuration SonarCloud

## Étape 1 : Créer un compte SonarCloud

1. Aller sur [https://sonarcloud.io](https://sonarcloud.io)
2. Se connecter avec GitHub/GitLab/Bitbucket
3. Autoriser l'accès au dépôt

## Étape 2 : Créer un projet

1. Cliquer sur **"+"** → **"Analyze new project"**
2. Sélectionner votre dépôt `audit-management`
3. Choisir **"With Jenkins"** comme méthode d'analyse

## Étape 3 : Récupérer les informations

Après création, notez :
- **Project Key** : `votre-username_audit-management`
- **Organization** : `votre-username`
- **Token** : Générer dans **My Account** → **Security** → **Generate Token**

## Étape 4 : Configurer le Quality Gate

1. Aller dans **Quality Gates**
2. Créer un nouveau Quality Gate "Audit Management Gate"
3. Ajouter les conditions :

| Métrique | Opérateur | Valeur |
|----------|-----------|--------|
| Coverage | < | 80% |
| Bugs | > | 0 (Blocker/Critical) |
| Vulnerabilities | > | 0 |
| Code Smells | > | 50 (Major) |
| Duplicated Lines | > | 10% |

4. Associer le Quality Gate au projet

## Étape 5 : Configurer Jenkins

### Ajouter les Credentials

1. Jenkins → **Manage Jenkins** → **Credentials**
2. Ajouter un **Secret text** :
   - ID : `sonarcloud-token`
   - Secret : Votre token SonarCloud

### Configurer SonarQube Server

1. Jenkins → **Manage Jenkins** → **Configure System**
2. Section **SonarQube servers** :
   - Name : `SonarCloud`
   - Server URL : `https://sonarcloud.io`
   - Server authentication token : Sélectionner `sonarcloud-token`

### Installer le plugin

1. Jenkins → **Manage Jenkins** → **Manage Plugins**
2. Installer **SonarQube Scanner**

## Étape 6 : Mettre à jour les fichiers

### sonar-project.properties
```properties
sonar.projectKey=VOTRE_PROJECT_KEY
sonar.organization=VOTRE_ORGANIZATION
```

### Jenkinsfile
```groovy
environment {
    SONAR_PROJECT_KEY = 'VOTRE_PROJECT_KEY'
    SONAR_ORGANIZATION = 'VOTRE_ORGANIZATION'
}
```

## Étape 7 : Vérifier l'intégration

1. Lancer un build Jenkins
2. Vérifier le rapport sur SonarCloud
3. Confirmer que le Quality Gate fonctionne

## 📊 Commande manuelle (test local)

```bash
mvn clean verify sonar:sonar \
  -Dsonar.projectKey=VOTRE_PROJECT_KEY \
  -Dsonar.organization=VOTRE_ORGANIZATION \
  -Dsonar.host.url=https://sonarcloud.io \
  -Dsonar.login=VOTRE_TOKEN
```

## ✅ Résultat attendu

- Dashboard SonarCloud avec métriques
- Quality Gate vert si tout est OK
- Pipeline bloqué si Quality Gate échoue
