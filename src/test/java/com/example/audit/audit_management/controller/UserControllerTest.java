package com.example.audit.audit_management.controller;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.exception.GlobalExceptionHandler;
import com.example.audit.audit_management.exception.ResourceNotFoundException;
import com.example.audit.audit_management.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests du contrôleur REST avec MockMvc.
 * 
 * @WebMvcTest charge uniquement le contexte web nécessaire pour tester le contrôleur.
 * Ces tests vérifient:
 * - Les mappings des endpoints
 * - La sérialisation/désérialisation JSON
 * - La validation des données
 * - Les codes de statut HTTP
 */
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("UserController - Tests MockMvc")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .id(1L)
                .username("john.doe")
                .email("john.doe@example.com")
                .role("AUDITOR")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/users")
    class GetAllUsersTests {

        @Test
        @DisplayName("Doit retourner 200 avec la liste des utilisateurs")
        void shouldReturnAllUsers() throws Exception {
            // Given
            List<UserDTO> users = Arrays.asList(userDTO);
            when(userService.getAllUsers()).thenReturn(users);

            // When/Then
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].username", is("john.doe")));
        }

        @Test
        @DisplayName("Doit retourner 200 avec liste vide si aucun utilisateur")
        void shouldReturnEmptyList() throws Exception {
            // Given
            when(userService.getAllUsers()).thenReturn(List.of());

            // When/Then
            mockMvc.perform(get("/api/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserByIdTests {

        @Test
        @DisplayName("Doit retourner 200 avec l'utilisateur trouvé")
        void shouldReturnUser() throws Exception {
            // Given
            when(userService.getUserById(1L)).thenReturn(userDTO);

            // When/Then
            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.username", is("john.doe")));
        }

        @Test
        @DisplayName("Doit retourner 404 si utilisateur non trouvé")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            when(userService.getUserById(99L))
                    .thenThrow(new ResourceNotFoundException("Utilisateur", "id", 99L));

            // When/Then
            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)));
        }
    }

    @Nested
    @DisplayName("POST /api/users")
    class CreateUserTests {

        @Test
        @DisplayName("Doit retourner 201 avec l'utilisateur créé")
        void shouldCreateUser() throws Exception {
            // Given
            UserDTO inputDTO = UserDTO.builder()
                    .username("john.doe")
                    .email("john.doe@example.com")
                    .role("AUDITOR")
                    .build();

            when(userService.createUser(any(UserDTO.class))).thenReturn(userDTO);

            // When/Then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDTO)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.username", is("john.doe")));
        }

        @Test
        @DisplayName("Doit retourner 400 si données invalides (username vide)")
        void shouldReturn400WhenUsernameEmpty() throws Exception {
            // Given
            UserDTO invalidDTO = UserDTO.builder()
                    .username("")  // Invalide
                    .email("john.doe@example.com")
                    .role("AUDITOR")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.username").exists());
        }

        @Test
        @DisplayName("Doit retourner 400 si email invalide")
        void shouldReturn400WhenEmailInvalid() throws Exception {
            // Given
            UserDTO invalidDTO = UserDTO.builder()
                    .username("john.doe")
                    .email("invalid-email")  // Invalide
                    .role("AUDITOR")
                    .build();

            // When/Then
            mockMvc.perform(post("/api/users")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.validationErrors.email").exists());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateUserTests {

        @Test
        @DisplayName("Doit retourner 200 avec l'utilisateur mis à jour")
        void shouldUpdateUser() throws Exception {
            // Given
            UserDTO updateDTO = UserDTO.builder()
                    .username("john.updated")
                    .email("john.updated@example.com")
                    .role("ADMIN")
                    .build();

            UserDTO updatedDTO = UserDTO.builder()
                    .id(1L)
                    .username("john.updated")
                    .email("john.updated@example.com")
                    .role("ADMIN")
                    .createdAt(LocalDateTime.now())
                    .build();

            when(userService.updateUser(eq(1L), any(UserDTO.class))).thenReturn(updatedDTO);

            // When/Then
            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username", is("john.updated")));
        }
    }

    @Nested
    @DisplayName("DELETE /api/users/{id}")
    class DeleteUserTests {

        @Test
        @DisplayName("Doit retourner 204 après suppression")
        void shouldDeleteUser() throws Exception {
            // Given
            doNothing().when(userService).deleteUser(1L);

            // When/Then
            mockMvc.perform(delete("/api/users/1"))
                    .andExpect(status().isNoContent());

            verify(userService).deleteUser(1L);
        }

        @Test
        @DisplayName("Doit retourner 404 si utilisateur non trouvé")
        void shouldReturn404WhenNotFound() throws Exception {
            // Given
            doThrow(new ResourceNotFoundException("Utilisateur", "id", 99L))
                    .when(userService).deleteUser(99L);

            // When/Then
            mockMvc.perform(delete("/api/users/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("GET /api/users/search")
    class SearchUsersTests {

        @Test
        @DisplayName("Doit retourner les utilisateurs correspondants")
        void shouldReturnMatchingUsers() throws Exception {
            // Given
            when(userService.searchUsersByUsername("john")).thenReturn(List.of(userDTO));

            // When/Then
            mockMvc.perform(get("/api/users/search")
                            .param("username", "john"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)));
        }
    }

    @Nested
    @DisplayName("GET /api/users/role/{role}")
    class GetUsersByRoleTests {

        @Test
        @DisplayName("Doit retourner les utilisateurs avec le rôle spécifié")
        void shouldReturnUsersByRole() throws Exception {
            // Given
            when(userService.getUsersByRole("AUDITOR")).thenReturn(List.of(userDTO));

            // When/Then
            mockMvc.perform(get("/api/users/role/AUDITOR"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].role", is("AUDITOR")));
        }
    }
}
