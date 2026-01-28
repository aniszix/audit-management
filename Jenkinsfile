// ===========================================
// Jenkinsfile - Pipeline CI/CD Complet
// Backend Spring Boot → Kubernetes
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
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_IMAGE = "${DOCKER_REGISTRY}/YOUR_DOCKERHUB_USERNAME/audit-management"
        DOCKER_TAG = "${BUILD_NUMBER}"
        SONAR_HOST = 'https://sonarcloud.io'
        SONAR_PROJECT_KEY = 'audit-management'
        SONAR_ORGANIZATION = 'your-organization'
        // Credentials (à configurer dans Jenkins)
        SONAR_TOKEN = credentials('sonarcloud-token')
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        KUBECONFIG_CREDENTIALS = credentials('kubeconfig')
    }

    // Options du pipeline
    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 45, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    // Déclencheur automatique
    triggers {
        pollSCM('H/5 * * * *')  // Vérifie toutes les 5 minutes
    }

    stages {
        // ============ Stage 1: Checkout ============
        stage('📥 Checkout') {
            steps {
                echo '📥 Récupération du code source depuis Git...'
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    env.GIT_BRANCH_NAME = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                }
                echo "Branch: ${env.GIT_BRANCH_NAME}, Commit: ${env.GIT_COMMIT_SHORT}"
            }
        }

        // ============ Stage 2: Build Maven ============
        stage('🔨 Build') {
            steps {
                echo '🔨 Compilation du projet Maven...'
                sh 'mvn clean compile -DskipTests -B'
            }
        }

        // ============ Stage 3: Tests Unitaires + JaCoCo ============
        stage('🧪 Tests Unitaires') {
            steps {
                echo '🧪 Exécution des tests unitaires avec couverture JaCoCo...'
                sh 'mvn test -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        // ============ Stage 4: Tests d'Intégration ============
        stage('🔗 Tests Intégration') {
            steps {
                echo '🔗 Exécution des tests d\'intégration...'
                sh 'mvn verify -DskipUnitTests -B'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: '**/target/failsafe-reports/*.xml'
                }
            }
        }

        // ============ Stage 5: Analyse SonarCloud ============
        stage('🔍 SonarCloud') {
            steps {
                echo '🔍 Analyse de la qualité du code avec SonarCloud...'
                withSonarQubeEnv('SonarCloud') {
                    sh """
                        mvn sonar:sonar \
                            -Dsonar.projectKey=${SONAR_PROJECT_KEY} \
                            -Dsonar.organization=${SONAR_ORGANIZATION} \
                            -Dsonar.host.url=${SONAR_HOST} \
                            -Dsonar.login=${SONAR_TOKEN} \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                    """
                }
            }
        }

        // ============ Stage 6: Quality Gate ============
        stage('✅ Quality Gate') {
            steps {
                echo '✅ Vérification du Quality Gate SonarCloud...'
                timeout(time: 10, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // ============ Stage 7: Package JAR ============
        stage('📦 Package') {
            steps {
                echo '📦 Création du package JAR...'
                sh 'mvn package -DskipTests -B'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ============ Stage 8: Docker Build ============
        stage('🐳 Docker Build') {
            steps {
                echo '🐳 Construction de l\'image Docker...'
                script {
                    docker.build("${DOCKER_IMAGE}:${DOCKER_TAG}")
                    docker.build("${DOCKER_IMAGE}:latest")
                }
            }
        }

        // ============ Stage 9: Docker Push ============
        stage('🚀 Docker Push') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo '🚀 Publication de l\'image sur Docker Hub...'
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'dockerhub-credentials') {
                        docker.image("${DOCKER_IMAGE}:${DOCKER_TAG}").push()
                        docker.image("${DOCKER_IMAGE}:latest").push()
                    }
                }
            }
        }

        // ============ Stage 10: Deploy to Kubernetes ============
        stage('☸️ Deploy Kubernetes') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo '☸️ Déploiement sur Kubernetes...'
                script {
                    // Mise à jour de l'image dans le deployment
                    sh """
                        sed -i 's|image:.*|image: ${DOCKER_IMAGE}:${DOCKER_TAG}|g' k8s/deployment.yaml
                    """
                    
                    // Appliquer les manifests Kubernetes
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh '''
                            kubectl apply -f k8s/namespace.yaml
                            kubectl apply -f k8s/configmap.yaml
                            kubectl apply -f k8s/secret.yaml
                            kubectl apply -f k8s/deployment.yaml
                            kubectl apply -f k8s/service.yaml
                            kubectl rollout status deployment/audit-management -n audit-app --timeout=120s
                        '''
                    }
                }
            }
        }

        // ============ Stage 11: Smoke Test ============
        stage('🔥 Smoke Test') {
            when {
                anyOf {
                    branch 'main'
                    branch 'develop'
                }
            }
            steps {
                echo '🔥 Test de fumée post-déploiement...'
                script {
                    // Attendre que l'application soit prête
                    sleep(30)
                    
                    // Test basique de santé
                    withCredentials([file(credentialsId: 'kubeconfig', variable: 'KUBECONFIG')]) {
                        sh '''
                            NODEPORT=$(kubectl get svc audit-management-service -n audit-app -o jsonpath='{.spec.ports[0].nodePort}')
                            NODE_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}')
                            curl -f http://${NODE_IP}:${NODEPORT}/actuator/health || exit 1
                        '''
                    }
                }
            }
        }
    }

    // Actions post-pipeline
    post {
        always {
            echo '🧹 Nettoyage de l\'espace de travail...'
            cleanWs()
        }
        success {
            echo '✅ =========================================='
            echo '✅ Pipeline terminé avec SUCCÈS!'
            echo '✅ =========================================='
        }
        failure {
            echo '❌ =========================================='
            echo '❌ Pipeline ÉCHOUÉ!'
            echo '❌ =========================================='
        }
    }
}
