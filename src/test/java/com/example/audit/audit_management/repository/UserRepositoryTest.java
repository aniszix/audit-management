package com.example.audit.audit_management.repository;

import com.example.audit.audit_management.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du repository avec @DataJpaTest.
 * 
 * @DataJpaTest configure automatiquement:
 * - Une base H2 en mémoire
 * - La gestion des transactions (rollback automatique)
 * - Le scan des entités JPA
 */
@DataJpaTest
@DisplayName("UserRepository - Tests d'intégration JPA")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        // Créer des utilisateurs de test
        user1 = User.builder()
                .username("john.doe")
                .email("john.doe@example.com")
                .role("AUDITOR")
                .build();

        user2 = User.builder()
                .username("jane.smith")
                .email("jane.smith@example.com")
                .role("ADMIN")
                .build();

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();
    }

    @Test
    @DisplayName("findByUsername - Doit retourner l'utilisateur")
    void findByUsername_ShouldReturnUser() {
        // When
        Optional<User> found = userRepository.findByUsername("john.doe");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
    }

    @Test
    @DisplayName("findByUsername - Doit retourner vide si non trouvé")
    void findByUsername_ShouldReturnEmpty() {
        // When
        Optional<User> found = userRepository.findByUsername("unknown");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("findByEmail - Doit retourner l'utilisateur")
    void findByEmail_ShouldReturnUser() {
        // When
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john.doe");
    }

    @Test
    @DisplayName("existsByUsername - Doit retourner true si existe")
    void existsByUsername_ShouldReturnTrue() {
        // When
        boolean exists = userRepository.existsByUsername("john.doe");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByUsername - Doit retourner false si n'existe pas")
    void existsByUsername_ShouldReturnFalse() {
        // When
        boolean exists = userRepository.existsByUsername("unknown");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("existsByEmail - Doit retourner true si existe")
    void existsByEmail_ShouldReturnTrue() {
        // When
        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("findByRole - Doit retourner les utilisateurs avec ce rôle")
    void findByRole_ShouldReturnUsers() {
        // When
        List<User> auditors = userRepository.findByRole("AUDITOR");
        List<User> admins = userRepository.findByRole("ADMIN");

        // Then
        assertThat(auditors).hasSize(1);
        assertThat(admins).hasSize(1);
        assertThat(auditors.get(0).getUsername()).isEqualTo("john.doe");
        assertThat(admins.get(0).getUsername()).isEqualTo("jane.smith");
    }

    @Test
    @DisplayName("findByUsernameContainingIgnoreCase - Doit retourner les correspondances")
    void findByUsernameContaining_ShouldReturnMatches() {
        // When
        List<User> results = userRepository.findByUsernameContainingIgnoreCase("JOHN");

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUsername()).isEqualTo("john.doe");
    }

    @Test
    @DisplayName("save - Doit définir createdAt automatiquement")
    void save_ShouldSetCreatedAt() {
        // Given
        User newUser = User.builder()
                .username("new.user")
                .email("new.user@example.com")
                .role("USER")
                .build();

        // When
        User savedUser = userRepository.save(newUser);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
    }
}
