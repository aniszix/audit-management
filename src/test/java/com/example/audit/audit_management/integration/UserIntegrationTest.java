package com.example.audit.audit_management.integration;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.entity.User;
import com.example.audit.audit_management.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration complets.
 * 
 * @SpringBootTest charge le contexte Spring complet.
 * Ces tests vérifient le fonctionnement de bout en bout
 * de l'application avec une vraie base de données H2.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
@Transactional
@DisplayName("Tests d'intégration - API Users")
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Nettoyer la base avant chaque test
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Scénario complet CRUD")
    void fullCrudScenario() throws Exception {
        // 1. CREATE - Créer un utilisateur
        UserDTO newUser = UserDTO.builder()
                .username("integration.test")
                .email("integration@test.com")
                .role("AUDITOR")
                .build();

        String createResponse = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username", is("integration.test")))
                .andExpect(jsonPath("$.createdAt").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO createdUser = objectMapper.readValue(createResponse, UserDTO.class);
        Long userId = createdUser.getId();

        // 2. READ - Récupérer l'utilisateur créé
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.username", is("integration.test")));

        // 3. READ ALL - Vérifier qu'il apparaît dans la liste
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        // 4. UPDATE - Modifier l'utilisateur
        UserDTO updateDTO = UserDTO.builder()
                .username("integration.updated")
                .email("integration.updated@test.com")
                .role("ADMIN")
                .build();

        mockMvc.perform(put("/api/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is("integration.updated")))
                .andExpect(jsonPath("$.role", is("ADMIN")));

        // 5. DELETE - Supprimer l'utilisateur
        mockMvc.perform(delete("/api/users/" + userId))
                .andExpect(status().isNoContent());

        // 6. Vérifier la suppression
        mockMvc.perform(get("/api/users/" + userId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Doit empêcher la création de doublons (username)")
    void shouldPreventDuplicateUsername() throws Exception {
        // Given - Créer un premier utilisateur
        User existingUser = User.builder()
                .username("existing.user")
                .email("existing@test.com")
                .role("USER")
                .build();
        userRepository.save(existingUser);

        // When - Tenter de créer avec le même username
        UserDTO duplicateUser = UserDTO.builder()
                .username("existing.user")  // Même username
                .email("different@test.com")
                .role("AUDITOR")
                .build();

        // Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Doit empêcher la création de doublons (email)")
    void shouldPreventDuplicateEmail() throws Exception {
        // Given
        User existingUser = User.builder()
                .username("user.one")
                .email("duplicate@test.com")
                .role("USER")
                .build();
        userRepository.save(existingUser);

        // When
        UserDTO duplicateUser = UserDTO.builder()
                .username("user.two")
                .email("duplicate@test.com")  // Même email
                .role("AUDITOR")
                .build();

        // Then
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateUser)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Recherche par rôle")
    void shouldSearchByRole() throws Exception {
        // Given
        User auditor1 = User.builder().username("auditor1").email("a1@test.com").role("AUDITOR").build();
        User auditor2 = User.builder().username("auditor2").email("a2@test.com").role("AUDITOR").build();
        User admin = User.builder().username("admin1").email("admin@test.com").role("ADMIN").build();
        userRepository.save(auditor1);
        userRepository.save(auditor2);
        userRepository.save(admin);

        // When/Then
        mockMvc.perform(get("/api/users/role/AUDITOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        mockMvc.perform(get("/api/users/role/ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @DisplayName("Recherche par nom partiel")
    void shouldSearchByUsername() throws Exception {
        // Given
        User user1 = User.builder().username("john.doe").email("john@test.com").role("USER").build();
        User user2 = User.builder().username("john.smith").email("smith@test.com").role("USER").build();
        User user3 = User.builder().username("jane.doe").email("jane@test.com").role("USER").build();
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);

        // When/Then - Recherche "john"
        mockMvc.perform(get("/api/users/search").param("username", "john"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        // Recherche "doe"
        mockMvc.perform(get("/api/users/search").param("username", "doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Validation des données - Username trop court")
    void shouldValidateUsernameTooShort() throws Exception {
        UserDTO invalidUser = UserDTO.builder()
                .username("ab")  // Moins de 3 caractères
                .email("valid@test.com")
                .role("USER")
                .build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.username").exists());
    }
}
