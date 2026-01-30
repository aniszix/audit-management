// ===========================================
// Jenkinsfile - Pipeline CI/CD Simplifié
// Backend Spring Boot - Tests + JaCoCo
// Utilise Maven Wrapper (pas de config Jenkins)
// ===========================================

pipeline {
    agent any

    // Variables d'environnement
    environment {
        APP_NAME = 'audit-management'
        DOCKER_IMAGE = 'aniszix/audit-management'
        DOCKER_TAG = "${BUILD_NUMBER}"
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
        stage('📥 Checkout') {
            steps {
                echo '📥 Récupération du code source depuis Git...'
                checkout scm
                sh 'chmod +x mvnw'
            }
        }

        // ============ Stage 2: Vérification Environnement ============
        stage('🔧 Vérification Env') {
            steps {
                echo '🔧 Vérification de l environnement...'
                sh '''
                    echo "Java version:"
                    java -version
                    echo "Maven Wrapper version:"
                    ./mvnw -version
                '''
            }
        }

        // ============ Stage 3: Build Maven ============
        stage('🔨 Build') {
            steps {
                echo '🔨 Compilation du projet Maven...'
                sh './mvnw clean compile -DskipTests -B'
            }
        }

        // ============ Stage 4: Tests Unitaires + JaCoCo ============
        stage('🧪 Tests Unitaires') {
            steps {
                echo '🧪 Exécution des tests unitaires avec couverture JaCoCo...'
                sh './mvnw test -Dspring.profiles.active=test -B'
            }
            post {
                always {
                    // Publier les résultats des tests JUnit
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    
                    // Publier le rapport JaCoCo (nécessite plugin JaCoCo)
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        // ============ Stage 5: Package JAR ============
        stage('📦 Package') {
            steps {
                echo '📦 Création du package JAR...'
                sh './mvnw package -DskipTests -B'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ============ Stage 6: Rapport de Couverture ============
        stage('📊 Rapport Couverture') {
            steps {
                echo '📊 Génération du rapport de couverture détaillé...'
                sh './mvnw jacoco:report -B'
                echo '✅ Rapport JaCoCo généré dans target/site/jacoco/'
            }
            post {
                always {
                    // Archiver le rapport HTML JaCoCo
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: 'JaCoCo Coverage Report'
                    ])
                }
            }
        }
    }

    // Actions post-pipeline
    post {
        always {
            echo '📋 Résumé du Pipeline'
            echo '===================='
        }
        success {
            echo '✅ =========================================='
            echo '✅ Pipeline terminé avec SUCCÈS!'
            echo '✅ Tests passés + Rapport JaCoCo généré!'
            echo '✅ =========================================='
        }
        failure {
            echo '❌ =========================================='
            echo '❌ Pipeline ÉCHOUÉ!'
            echo '❌ Vérifiez les logs pour plus de détails'
            echo '❌ =========================================='
        }
    }
}
