// ===========================================
// Jenkinsfile - Pipeline CI/CD Simplifié
// Backend Spring Boot - Tests + JaCoCo
// Utilise Maven Wrapper (pas de plugins requis)
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
        stage('Checkout') {
            steps {
                echo '=== Recuperation du code source depuis Git ==='
                checkout scm
                sh 'chmod +x mvnw'
            }
        }

        // ============ Stage 2: Vérification Environnement ============
        stage('Verification Env') {
            steps {
                echo '=== Verification de l environnement ==='
                sh '''
                    echo "Java version:"
                    java -version
                    echo "Maven Wrapper version:"
                    ./mvnw -version
                '''
            }
        }

        // ============ Stage 3: Build Maven ============
        stage('Build') {
            steps {
                echo '=== Compilation du projet Maven ==='
                sh './mvnw clean compile -DskipTests -B'
            }
        }

        // ============ Stage 4: Tests Unitaires + JaCoCo ============
        stage('Tests Unitaires') {
            steps {
                echo '=== Execution des tests unitaires avec couverture JaCoCo ==='
                sh './mvnw test -Dspring.profiles.active=test -B'
            }
            post {
                always {
                    // Publier les résultats des tests JUnit
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                }
            }
        }

        // ============ Stage 5: Package JAR ============
        stage('Package') {
            steps {
                echo '=== Creation du package JAR ==='
                sh './mvnw package -DskipTests -B'
            }
            post {
                success {
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        // ============ Stage 6: Rapport de Couverture ============
        stage('Rapport Couverture') {
            steps {
                echo '=== Generation du rapport de couverture detaille ==='
                sh './mvnw jacoco:report -B'
                echo '=== Rapport JaCoCo genere dans target/site/jacoco/ ==='
                
                // Afficher le résumé de couverture
                sh '''
                    echo "=== Resume de la couverture de code ==="
                    if [ -f target/site/jacoco/index.html ]; then
                        echo "Rapport JaCoCo HTML genere avec succes!"
                        ls -la target/site/jacoco/
                    else
                        echo "Attention: Rapport JaCoCo non trouve"
                    fi
                '''
            }
            post {
                success {
                    // Archiver le rapport JaCoCo comme artefact
                    archiveArtifacts artifacts: 'target/site/jacoco/**/*', allowEmptyArchive: true
                }
            }
        }
    }

    // Actions post-pipeline
    post {
        always {
            echo '=========================================='
            echo 'Resume du Pipeline'
            echo '=========================================='
        }
        success {
            echo '=========================================='
            echo 'Pipeline termine avec SUCCES!'
            echo 'Tests passes + Rapport JaCoCo genere!'
            echo '=========================================='
        }
        failure {
            echo '=========================================='
            echo 'Pipeline ECHOUE!'
            echo 'Verifiez les logs pour plus de details'
            echo '=========================================='
        }
    }
}
