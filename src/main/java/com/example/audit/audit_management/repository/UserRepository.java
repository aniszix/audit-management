package com.example.audit.audit_management.repository;

import com.example.audit.audit_management.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository Spring Data JPA pour l'entité User.
 * 
 * Spring Data génère automatiquement l'implémentation
 * des méthodes CRUD de base héritées de JpaRepository.
 * 
 * Les méthodes personnalisées suivent la convention de nommage
 * Spring Data pour générer les requêtes automatiquement.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son nom d'utilisateur.
     *
     * @param username Le nom d'utilisateur à rechercher
     * @return Un Optional contenant l'utilisateur ou vide
     */
    Optional<User> findByUsername(String username);

    /**
     * Recherche un utilisateur par son email.
     *
     * @param email L'email à rechercher
     * @return Un Optional contenant l'utilisateur ou vide
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un nom d'utilisateur existe déjà.
     *
     * @param username Le nom d'utilisateur à vérifier
     * @return true si le nom d'utilisateur existe
     */
    boolean existsByUsername(String username);

    /**
     * Vérifie si un email existe déjà.
     *
     * @param email L'email à vérifier
     * @return true si l'email existe
     */
    boolean existsByEmail(String email);

    /**
     * Recherche tous les utilisateurs ayant un rôle spécifique.
     *
     * @param role Le rôle à rechercher
     * @return La liste des utilisateurs avec ce rôle
     */
    List<User> findByRole(String role);

    /**
     * Recherche les utilisateurs dont le nom contient une chaîne (insensible à la casse).
     *
     * @param username La chaîne à rechercher
     * @return La liste des utilisateurs correspondants
     */
    List<User> findByUsernameContainingIgnoreCase(String username);
}
