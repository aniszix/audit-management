package com.example.audit.audit_management.service.impl;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.entity.User;
import com.example.audit.audit_management.exception.DuplicateResourceException;
import com.example.audit.audit_management.exception.ResourceNotFoundException;
import com.example.audit.audit_management.mapper.UserMapper;
import com.example.audit.audit_management.repository.UserRepository;
import com.example.audit.audit_management.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion des utilisateurs.
 * 
 * Cette classe contient toute la logique métier liée aux utilisateurs.
 * Elle utilise:
 * - @Transactional pour la gestion des transactions
 * - @Slf4j pour le logging
 * - Le pattern d'injection par constructeur (via @RequiredArgsConstructor)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        log.debug("Récupération de tous les utilisateurs");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        log.debug("Récupération de l'utilisateur avec l'id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));
        return userMapper.toDTO(user);
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        log.debug("Création d'un nouvel utilisateur: {}", userDTO.getUsername());

        // Validation des contraintes d'unicité
        validateUniqueConstraints(userDTO, null);

        User user = userMapper.toEntity(userDTO);
        User savedUser = userRepository.save(user);
        
        log.info("Utilisateur créé avec succès: id={}", savedUser.getId());
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        log.debug("Mise à jour de l'utilisateur avec l'id: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        // Validation des contraintes d'unicité (en excluant l'utilisateur actuel)
        validateUniqueConstraints(userDTO, id);

        // Mise à jour des champs
        userMapper.updateEntityFromDTO(existingUser, userDTO);
        User updatedUser = userRepository.save(existingUser);

        log.info("Utilisateur mis à jour avec succès: id={}", id);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.debug("Suppression de l'utilisateur avec l'id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur", "id", id);
        }

        userRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès: id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUsersByRole(String role) {
        log.debug("Recherche des utilisateurs avec le rôle: {}", role);
        return userRepository.findByRole(role)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsersByUsername(String username) {
        log.debug("Recherche des utilisateurs contenant: {}", username);
        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Valide les contraintes d'unicité (username et email).
     *
     * @param userDTO Les données à valider
     * @param excludeId L'ID à exclure de la vérification (pour les mises à jour)
     */
    private void validateUniqueConstraints(UserDTO userDTO, Long excludeId) {
        // Vérification du username
        userRepository.findByUsername(userDTO.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(excludeId)) {
                        throw new DuplicateResourceException("Utilisateur", "username", userDTO.getUsername());
                    }
                });

        // Vérification de l'email
        userRepository.findByEmail(userDTO.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(excludeId)) {
                        throw new DuplicateResourceException("Utilisateur", "email", userDTO.getEmail());
                    }
                });
    }
}
