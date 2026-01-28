package com.example.audit.audit_management.controller;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des utilisateurs.
 * 
 * Expose les endpoints CRUD pour manipuler les utilisateurs.
 * Chaque endpoint est documenté avec les annotations OpenAPI/Swagger.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Utilisateurs", description = "API de gestion des utilisateurs")
public class UserController {

    private final UserService userService;

    /**
     * GET /api/users - Récupère tous les utilisateurs
     */
    @GetMapping
    @Operation(
            summary = "Récupérer tous les utilisateurs",
            description = "Retourne la liste complète des utilisateurs enregistrés dans le système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        log.info("GET /api/users - Récupération de tous les utilisateurs");
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/{id} - Récupère un utilisateur par son ID
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Récupérer un utilisateur par ID",
            description = "Retourne un utilisateur spécifique basé sur son identifiant unique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "ID de l'utilisateur", required = true, example = "1")
            @PathVariable Long id) {
        log.info("GET /api/users/{} - Récupération de l'utilisateur", id);
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * POST /api/users - Crée un nouvel utilisateur
     */
    @PostMapping
    @Operation(
            summary = "Créer un nouvel utilisateur",
            description = "Crée un nouvel utilisateur avec les données fournies"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Conflit - Username ou email déjà existant"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<UserDTO> createUser(
            @Parameter(description = "Données de l'utilisateur à créer", required = true)
            @Valid @RequestBody UserDTO userDTO) {
        log.info("POST /api/users - Création d'un nouvel utilisateur: {}", userDTO.getUsername());
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * PUT /api/users/{id} - Met à jour un utilisateur existant
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Mettre à jour un utilisateur",
            description = "Met à jour les informations d'un utilisateur existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "409", description = "Conflit - Username ou email déjà existant"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "ID de l'utilisateur à modifier", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Nouvelles données de l'utilisateur", required = true)
            @Valid @RequestBody UserDTO userDTO) {
        log.info("PUT /api/users/{} - Mise à jour de l'utilisateur", id);
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * DELETE /api/users/{id} - Supprime un utilisateur
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "Supprime définitivement un utilisateur du système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID de l'utilisateur à supprimer", required = true, example = "1")
            @PathVariable Long id) {
        log.info("DELETE /api/users/{} - Suppression de l'utilisateur", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/users/search?username=xxx - Recherche par nom
     */
    @GetMapping("/search")
    @Operation(
            summary = "Rechercher des utilisateurs par nom",
            description = "Recherche les utilisateurs dont le nom contient la chaîne fournie (insensible à la casse)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Résultats de la recherche"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UserDTO>> searchUsers(
            @Parameter(description = "Terme de recherche pour le nom d'utilisateur", example = "john")
            @RequestParam String username) {
        log.info("GET /api/users/search?username={} - Recherche d'utilisateurs", username);
        List<UserDTO> users = userService.searchUsersByUsername(username);
        return ResponseEntity.ok(users);
    }

    /**
     * GET /api/users/role/{role} - Filtre par rôle
     */
    @GetMapping("/role/{role}")
    @Operation(
            summary = "Récupérer les utilisateurs par rôle",
            description = "Retourne tous les utilisateurs ayant le rôle spécifié"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des utilisateurs filtrés par rôle"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<UserDTO>> getUsersByRole(
            @Parameter(description = "Rôle à rechercher", example = "AUDITOR")
            @PathVariable String role) {
        log.info("GET /api/users/role/{} - Récupération des utilisateurs par rôle", role);
        List<UserDTO> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }
}
