package com.example.audit.audit_management.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI/Swagger pour la documentation de l'API.
 * 
 * Cette configuration personnalise l'affichage de Swagger UI
 * avec les informations du projet.
 */
@Configuration
public class OpenAPIConfig {

    @Value("${server.port:8081}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Audit Management API")
                        .version("1.0.0")
                        .description("""
                                API REST pour la gestion d'audit.
                                
                                Cette API permet de gérer les utilisateurs du système d'audit
                                avec les opérations CRUD complètes.
                                
                                ## Fonctionnalités
                                - Gestion des utilisateurs (CRUD)
                                - Recherche par nom ou rôle
                                - Validation des données
                                
                                ## Authentification
                                Pour le moment, l'API n'utilise pas d'authentification (projet pédagogique).
                                """)
                        .contact(new Contact()
                                .name("Équipe Audit Management")
                                .email("contact@audit-management.com")
                                .url("https://github.com/votre-repo/audit-management"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement")
                ));
    }
}
