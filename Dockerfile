# ===========================================
# Dockerfile multi-stage pour Spring Boot
# ===========================================

# ============ Stage 1: Build ============
# Utilise une image Maven avec JDK 17 pour compiler
FROM maven:3.9-eclipse-temurin-17 AS builder

# Définir le répertoire de travail
WORKDIR /app

# Copier d'abord le pom.xml pour mettre en cache les dépendances
COPY pom.xml .

# Télécharger les dépendances (mise en cache Docker)
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler l'application (sans exécuter les tests - ils sont déjà passés dans la CI)
RUN mvn clean package -DskipTests

# ============ Stage 2: Runtime ============
# Utilise une image JRE légère pour l'exécution
FROM eclipse-temurin:17-jre-alpine

# Métadonnées de l'image
LABEL maintainer="Équipe Audit Management"
LABEL description="Application de gestion d'audit - Spring Boot"
LABEL version="1.0.0"

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/target/*.jar app.jar

# Changer le propriétaire des fichiers
RUN chown -R appuser:appgroup /app

# Utiliser l'utilisateur non-root
USER appuser

# Exposer le port de l'application
EXPOSE 8081

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=prod

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Point d'entrée
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
