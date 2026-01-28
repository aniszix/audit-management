// ===========================================
// Jenkinsfile - Pipeline CI/CD pour Audit Management
// ===========================================

pipeline {
    agent any

    // Outils requis (configurés dans Jenkins)
    tools {
        maven 'Maven-3.9'
        jdk 'JDK-17'
    }

    // Variables d'environnement
    environment {
        APP_NAME = 'audit-management'
        DOCKER_IMAGE = "audit-management"
        DOCKER_TAG = "${BUILD_NUMBER}"
        SONAR_HOST = 'https://sonarcloud.io'
        // Credentials (à configurer dans Jenkins)
        SONAR_TOKEN = credentials('sonarcloud-token')
        DOCKER_REGISTRY = credentials('docker-registry')
    }

    // Options du pipeline
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        // ============ Stage 1: Checkout ============
        stage('Checkout') {
            steps {
                echo '📥 Récupération du code source...'
                checkout scm
            }
        }

        // ============ Stage 2: Build ============
        stage('Build') {
            steps {
                echo '🔨 Compilation du projet...'
                sh 'mvn clean compile -DskipTests'
            }
        }

        // ============ Stage 3: Tests Unitaires ============
        stage('Unit Tests') {
            steps {
                echo '🧪 Exécution des tests unitaires...'
                sh 'mvn test'
            }
            post {
                always {
                    // Publication des résultats de tests
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ============ Stage 4: Tests d'Intégration ============
        stage('Integration Tests') {
            steps {
                echo '🔗 Exécution des tests d\'intégration...'
                sh 'mvn verify -DskipUnitTests'
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        // ============ Stage 5: Couverture de Code ============
        stage('Code Coverage') {
            steps {
                echo '📊 Génération du rapport de couverture...'
                sh 'mvn jacoco:report'
            }
            post {
                success {
                    // Publication du rapport JaCoCo
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        // ============ Stage 6: Analyse SonarCloud ============
        stage('SonarCloud Analysis') {
            steps {
                echo '🔍 Analyse de la qualité du code avec SonarCloud...'
                withSonarQubeEnv('SonarCloud') {
                    sh '''
                        mvn sonar:sonar \
                            -Dsonar.projectKey=audit-management \
                            -Dsonar.organization=your-organization \
                            -Dsonar.host.url=${SONAR_HOST} \
                            -Dsonar.login=${SONAR_TOKEN}
                    '''
                }
            }
        }

        // ============ Stage 7: Quality Gate ============
        stage('Quality Gate') {
            steps {
                echo '✅ Vérification du Quality Gate...'
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ============ Stage 8: Package ============
        stage('Package') {
            steps {
                echo '📦 Création du package JAR...'
                sh 'mvn package -DskipTests'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ============ Stage 9: Docker Build ============
        stage('Docker Build') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        // ============ Stage 10: Docker Push (optionnel) ============
        stage('Docker Push') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 Publication de l\'image Docker...'
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-registry') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        // ============ Stage 11: Deploy (optionnel) ============
        stage('Deploy to Staging') {
            when {
                branch 'develop'
            }
            steps {
                echo '🌐 Déploiement en staging...'
                // Exemple de déploiement avec docker-compose
                sh '''
                    docker-compose -f docker-compose.yml down || true
                    docker-compose -f docker-compose.yml up -d
                '''
            }
        }
    }

    // Actions post-pipeline
    post {
        always {
            echo '🧹 Nettoyage...'
            cleanWs()
        }
        success {
            echo '✅ Pipeline terminé avec succès!'
            // Notification Slack/Teams (à configurer)
            // slackSend channel: '#builds', color: 'good', message: "Build ${BUILD_NUMBER} réussi!"
        }
        failure {
            echo '❌ Pipeline échoué!'
            // slackSend channel: '#builds', color: 'danger', message: "Build ${BUILD_NUMBER} échoué!"
        }
    }
}
