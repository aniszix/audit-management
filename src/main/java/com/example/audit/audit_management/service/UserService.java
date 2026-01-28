package com.example.audit.audit_management.service;

import com.example.audit.audit_management.dto.UserDTO;

import java.util.List;

/**
 * Interface de service pour la gestion des utilisateurs.
 * 
 * Définit le contrat pour les opérations métier sur les utilisateurs.
 * L'utilisation d'une interface permet:
 * - Une meilleure testabilité (injection de mocks)
 * - Un couplage faible entre les couches
 * - Une évolution plus facile du code
 */
public interface UserService {

    /**
     * Récupère tous les utilisateurs.
     *
     * @return La liste de tous les utilisateurs
     */
    List<UserDTO> getAllUsers();

    /**
     * Récupère un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur
     * @return L'utilisateur correspondant
     * @throws com.example.audit.audit_management.exception.ResourceNotFoundException si non trouvé
     */
    UserDTO getUserById(Long id);

    /**
     * Crée un nouvel utilisateur.
     *
     * @param userDTO Les données de l'utilisateur à créer
     * @return L'utilisateur créé avec son ID généré
     * @throws com.example.audit.audit_management.exception.DuplicateResourceException si username ou email existe
     */
    UserDTO createUser(UserDTO userDTO);

    /**
     * Met à jour un utilisateur existant.
     *
     * @param id L'identifiant de l'utilisateur à modifier
     * @param userDTO Les nouvelles données
     * @return L'utilisateur mis à jour
     * @throws com.example.audit.audit_management.exception.ResourceNotFoundException si non trouvé
     */
    UserDTO updateUser(Long id, UserDTO userDTO);

    /**
     * Supprime un utilisateur.
     *
     * @param id L'identifiant de l'utilisateur à supprimer
     * @throws com.example.audit.audit_management.exception.ResourceNotFoundException si non trouvé
     */
    void deleteUser(Long id);

    /**
     * Recherche les utilisateurs par rôle.
     *
     * @param role Le rôle à rechercher
     * @return La liste des utilisateurs ayant ce rôle
     */
    List<UserDTO> getUsersByRole(String role);

    /**
     * Recherche les utilisateurs par nom (recherche partielle).
     *
     * @param username La chaîne à rechercher dans le nom
     * @return La liste des utilisateurs correspondants
     */
    List<UserDTO> searchUsersByUsername(String username);
}
