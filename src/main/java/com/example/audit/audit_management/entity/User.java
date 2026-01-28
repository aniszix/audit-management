package com.example.audit.audit_management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entité JPA représentant un utilisateur dans le système d'audit.
 * 
 * Cette classe utilise les annotations JPA pour la persistance
 * et Lombok pour réduire le code boilerplate.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nom d'utilisateur unique.
     * Doit contenir entre 3 et 50 caractères.
     */
    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Adresse email de l'utilisateur.
     * Doit être un format email valide et unique.
     */
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Rôle de l'utilisateur dans le système.
     * Exemples: ADMIN, AUDITOR, USER
     */
    @NotBlank(message = "Le rôle est obligatoire")
    @Size(max = 30, message = "Le rôle ne doit pas dépasser 30 caractères")
    @Column(nullable = false, length = 30)
    private String role;

    /**
     * Date de création de l'utilisateur.
     * Automatiquement définie lors de la création.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Callback JPA exécuté avant la persistance.
     * Définit automatiquement la date de création.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
