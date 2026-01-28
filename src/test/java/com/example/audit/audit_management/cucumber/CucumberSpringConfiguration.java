package com.example.audit.audit_management.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Configuration Spring pour les tests Cucumber.
 * 
 * Cette classe permet à Cucumber d'utiliser le contexte Spring Boot
 * pour l'injection de dépendances et l'accès à la base de données.
 */
@CucumberContextConfiguration
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
public class CucumberSpringConfiguration {
    // Configuration marker class
}
