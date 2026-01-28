package com.example.audit.audit_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) pour l'entité User.
 * 
 * Utilisé pour transférer les données entre les couches
 * et éviter d'exposer directement l'entité JPA.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Représentation d'un utilisateur")
public class UserDTO {

    @Schema(description = "Identifiant unique de l'utilisateur", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Schema(description = "Nom d'utilisateur unique", example = "john.doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Schema(description = "Adresse email de l'utilisateur", example = "john.doe@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Le rôle est obligatoire")
    @Size(max = 30, message = "Le rôle ne doit pas dépasser 30 caractères")
    @Schema(description = "Rôle de l'utilisateur", example = "AUDITOR", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;

    @Schema(description = "Date de création", example = "2024-01-15T10:30:00", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
