package com.example.audit.audit_management.service;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.entity.User;
import com.example.audit.audit_management.exception.DuplicateResourceException;
import com.example.audit.audit_management.exception.ResourceNotFoundException;
import com.example.audit.audit_management.mapper.UserMapper;
import com.example.audit.audit_management.repository.UserRepository;
import com.example.audit.audit_management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour UserServiceImpl.
 * 
 * Ces tests utilisent Mockito pour isoler le service de ses dépendances.
 * Ils vérifient la logique métier sans accéder à la base de données.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserService - Tests unitaires")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        // Données de test réutilisables
        user = User.builder()
                .id(1L)
                .username("john.doe")
                .email("john.doe@example.com")
                .role("AUDITOR")
                .createdAt(LocalDateTime.now())
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .email("john.doe@example.com")
                .role("AUDITOR")
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsersTests {

        @Test
        @DisplayName("Doit retourner la liste de tous les utilisateurs")
        void shouldReturnAllUsers() {
            // Given
            List<User> users = Arrays.asList(user);
            when(userRepository.findAll()).thenReturn(users);
            when(userMapper.toDTO(user)).thenReturn(userDTO);

            // When
            List<UserDTO> result = userService.getAllUsers();

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).isEqualTo("john.doe");
            verify(userRepository).findAll();
        }

        @Test
        @DisplayName("Doit retourner une liste vide si aucun utilisateur")
        void shouldReturnEmptyListWhenNoUsers() {
            // Given
            when(userRepository.findAll()).thenReturn(List.of());

            // When
            List<UserDTO> result = userService.getAllUsers();

            // Then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUserById()")
    class GetUserByIdTests {

        @Test
        @DisplayName("Doit retourner l'utilisateur quand il existe")
        void shouldReturnUserWhenFound() {
            // Given
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userMapper.toDTO(user)).thenReturn(userDTO);

            // When
            UserDTO result = userService.getUserById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("john.doe");
        }

        @Test
        @DisplayName("Doit lever ResourceNotFoundException quand utilisateur non trouvé")
        void shouldThrowExceptionWhenNotFound() {
            // Given
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Utilisateur")
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("createUser()")
    class CreateUserTests {

        @Test
        @DisplayName("Doit créer un utilisateur avec succès")
        void shouldCreateUserSuccessfully() {
            // Given
            when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
            when(userMapper.toEntity(userDTO)).thenReturn(user);
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toDTO(user)).thenReturn(userDTO);

            // When
            UserDTO result = userService.createUser(userDTO);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getUsername()).isEqualTo("john.doe");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Doit lever DuplicateResourceException si username existe déjà")
        void shouldThrowExceptionWhenUsernameExists() {
            // Given
            when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));

            // When/Then
            assertThatThrownBy(() -> userService.createUser(userDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("username");
        }

        @Test
        @DisplayName("Doit lever DuplicateResourceException si email existe déjà")
        void shouldThrowExceptionWhenEmailExists() {
            // Given
            when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));

            // When/Then
            assertThatThrownBy(() -> userService.createUser(userDTO))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("email");
        }
    }

    @Nested
    @DisplayName("updateUser()")
    class UpdateUserTests {

        @Test
        @DisplayName("Doit mettre à jour un utilisateur avec succès")
        void shouldUpdateUserSuccessfully() {
            // Given
            UserDTO updateDTO = UserDTO.builder()
                    .username("john.updated")
                    .email("john.updated@example.com")
                    .role("ADMIN")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.findByUsername(updateDTO.getUsername())).thenReturn(Optional.empty());
            when(userRepository.findByEmail(updateDTO.getEmail())).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(userMapper.toDTO(user)).thenReturn(updateDTO);

            // When
            UserDTO result = userService.updateUser(1L, updateDTO);

            // Then
            assertThat(result).isNotNull();
            verify(userMapper).updateEntityFromDTO(any(User.class), eq(updateDTO));
            verify(userRepository).save(user);
        }

        @Test
        @DisplayName("Doit lever ResourceNotFoundException si utilisateur non trouvé")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> userService.updateUser(99L, userDTO))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUserTests {

        @Test
        @DisplayName("Doit supprimer un utilisateur avec succès")
        void shouldDeleteUserSuccessfully() {
            // Given
            when(userRepository.existsById(1L)).thenReturn(true);
            doNothing().when(userRepository).deleteById(1L);

            // When
            userService.deleteUser(1L);

            // Then
            verify(userRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Doit lever ResourceNotFoundException si utilisateur non trouvé")
        void shouldThrowExceptionWhenUserNotFound() {
            // Given
            when(userRepository.existsById(99L)).thenReturn(false);

            // When/Then
            assertThatThrownBy(() -> userService.deleteUser(99L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getUsersByRole()")
    class GetUsersByRoleTests {

        @Test
        @DisplayName("Doit retourner les utilisateurs avec le rôle spécifié")
        void shouldReturnUsersByRole() {
            // Given
            when(userRepository.findByRole("AUDITOR")).thenReturn(List.of(user));
            when(userMapper.toDTO(user)).thenReturn(userDTO);

            // When
            List<UserDTO> result = userService.getUsersByRole("AUDITOR");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getRole()).isEqualTo("AUDITOR");
        }
    }

    @Nested
    @DisplayName("searchUsersByUsername()")
    class SearchUsersByUsernameTests {

        @Test
        @DisplayName("Doit retourner les utilisateurs correspondant à la recherche")
        void shouldReturnMatchingUsers() {
            // Given
            when(userRepository.findByUsernameContainingIgnoreCase("john")).thenReturn(List.of(user));
            when(userMapper.toDTO(user)).thenReturn(userDTO);

            // When
            List<UserDTO> result = userService.searchUsersByUsername("john");

            // Then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUsername()).contains("john");
        }
    }
}
