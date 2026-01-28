package com.example.audit.audit_management.mapper;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper pour convertir entre l'entité User et son DTO.
 * 
 * Cette approche manuelle est simple et pédagogique.
 * Pour des projets plus complexes, considérez MapStruct.
 */
@Component
public class UserMapper {

    /**
     * Convertit une entité User vers un DTO.
     *
     * @param user L'entité à convertir
     * @return Le DTO correspondant
     */
    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }

    /**
     * Convertit un DTO vers une entité User.
     *
     * @param dto Le DTO à convertir
     * @return L'entité correspondante
     */
    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .role(dto.getRole())
                .createdAt(dto.getCreatedAt())
                .build();
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO.
     *
     * @param existingUser L'entité à mettre à jour
     * @param dto Les nouvelles données
     */
    public void updateEntityFromDTO(User existingUser, UserDTO dto) {
        if (dto.getUsername() != null) {
            existingUser.setUsername(dto.getUsername());
        }
        if (dto.getEmail() != null) {
            existingUser.setEmail(dto.getEmail());
        }
        if (dto.getRole() != null) {
            existingUser.setRole(dto.getRole());
        }
        // Note: id et createdAt ne sont jamais modifiés
    }
}
