package com.example.audit.audit_management.cucumber;

import com.example.audit.audit_management.dto.UserDTO;
import com.example.audit.audit_management.entity.User;
import com.example.audit.audit_management.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.fr.Alors;
import io.cucumber.java.fr.Etantdonné;
import io.cucumber.java.fr.Quand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Définitions des étapes Cucumber pour les tests BDD.
 * 
 * Chaque méthode correspond à une étape dans les fichiers .feature
 */
public class UserStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MvcResult lastResult;
    private UserDTO lastCreatedUser;
    private User currentUser;
    private int lastStatusCode;

    @Before
    public void setUp() {
        userRepository.deleteAll();
        lastResult = null;
        lastCreatedUser = null;
        currentUser = null;
    }

    // ============ Etant donné (Given) ============

    @Etantdonné("la base de données est vide")
    public void laBaseDeDonneesEstVide() {
        userRepository.deleteAll();
        assertThat(userRepository.count()).isZero();
    }

    @Etantdonné("un utilisateur existe avec le nom {string}")
    public void unUtilisateurExisteAvecLeNom(String username) {
        User user = User.builder()
                .username(username)
                .email(username.replace(".", "_") + "@example.com")
                .role("USER")
                .build();
        currentUser = userRepository.save(user);
    }

    @Etantdonné("les utilisateurs suivants existent:")
    public void lesUtilisateursSuivantsExistent(DataTable dataTable) {
        List<Map<String, String>> users = dataTable.asMaps();
        for (Map<String, String> userData : users) {
            User user = User.builder()
                    .username(userData.get("username"))
                    .email(userData.get("email"))
                    .role(userData.get("role"))
                    .build();
            userRepository.save(user);
        }
    }

    // ============ Quand (When) ============

    @Quand("je crée un utilisateur avec:")
    public void jeCreerUnUtilisateurAvec(DataTable dataTable) throws Exception {
        Map<String, String> userData = dataTable.asMaps().get(0);
        
        UserDTO userDTO = UserDTO.builder()
                .username(userData.get("username"))
                .email(userData.get("email"))
                .role(userData.get("role"))
                .build();

        lastResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andReturn();

        lastStatusCode = lastResult.getResponse().getStatus();
        
        if (lastStatusCode == 201) {
            lastCreatedUser = objectMapper.readValue(
                    lastResult.getResponse().getContentAsString(),
                    UserDTO.class
            );
        }
    }

    @Quand("je récupère l'utilisateur par son ID")
    public void jeRecupereUtilisateurParSonId() throws Exception {
        lastResult = mockMvc.perform(get("/api/users/" + currentUser.getId()))
                .andReturn();
        lastStatusCode = lastResult.getResponse().getStatus();
        
        if (lastStatusCode == 200) {
            lastCreatedUser = objectMapper.readValue(
                    lastResult.getResponse().getContentAsString(),
                    UserDTO.class
            );
        }
    }

    @Quand("je modifie son rôle en {string}")
    public void jeModifieSonRoleEn(String newRole) throws Exception {
        UserDTO updateDTO = UserDTO.builder()
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .role(newRole)
                .build();

        lastResult = mockMvc.perform(put("/api/users/" + currentUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andReturn();

        lastStatusCode = lastResult.getResponse().getStatus();
        
        if (lastStatusCode == 200) {
            lastCreatedUser = objectMapper.readValue(
                    lastResult.getResponse().getContentAsString(),
                    UserDTO.class
            );
        }
    }

    @Quand("je supprime cet utilisateur")
    public void jeSupprimeCetUtilisateur() throws Exception {
        lastResult = mockMvc.perform(delete("/api/users/" + currentUser.getId()))
                .andReturn();
        lastStatusCode = lastResult.getResponse().getStatus();
    }

    @Quand("je crée un utilisateur avec le même nom {string}")
    public void jeCreerUnUtilisateurAvecLeMemeNom(String username) throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .username(username)
                .email("different@example.com")
                .role("USER")
                .build();

        lastResult = mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andReturn();

        lastStatusCode = lastResult.getResponse().getStatus();
    }

    @Quand("je recherche les utilisateurs avec le rôle {string}")
    public void jeRechercheParRole(String role) throws Exception {
        lastResult = mockMvc.perform(get("/api/users/role/" + role))
                .andReturn();
        lastStatusCode = lastResult.getResponse().getStatus();
    }

    // ============ Alors (Then) ============

    @Alors("l'utilisateur est créé avec succès")
    public void lutilisateurEstCreeAvecSucces() {
        assertThat(lastStatusCode).isEqualTo(201);
    }

    @Alors("l'utilisateur a un ID généré")
    public void lutilisateurAUnIdGenere() {
        assertThat(lastCreatedUser.getId()).isNotNull();
    }

    @Alors("l'utilisateur a une date de création")
    public void lutilisateurAUneDateDeCreation() {
        assertThat(lastCreatedUser.getCreatedAt()).isNotNull();
    }

    @Alors("je reçois les informations de l'utilisateur")
    public void jeRecoisLesInformationsDeLutilisateur() {
        assertThat(lastStatusCode).isEqualTo(200);
        assertThat(lastCreatedUser).isNotNull();
    }

    @Alors("le nom d'utilisateur est {string}")
    public void leNomDutilisateurEst(String expectedUsername) {
        assertThat(lastCreatedUser.getUsername()).isEqualTo(expectedUsername);
    }

    @Alors("l'utilisateur est mis à jour")
    public void lutilisateurEstMisAJour() {
        assertThat(lastStatusCode).isEqualTo(200);
    }

    @Alors("son nouveau rôle est {string}")
    public void sonNouveauRoleEst(String expectedRole) {
        assertThat(lastCreatedUser.getRole()).isEqualTo(expectedRole);
    }

    @Alors("l'utilisateur n'existe plus dans le système")
    public void lutilisateurNexistePlus() throws Exception {
        assertThat(lastStatusCode).isEqualTo(204);
        assertThat(userRepository.existsById(currentUser.getId())).isFalse();
    }

    @Alors("je reçois une erreur de conflit")
    public void jeRecoisUneErreurDeConflit() {
        assertThat(lastStatusCode).isEqualTo(409);
    }

    @Alors("le message indique que le nom existe déjà")
    public void leMessageIndiqueQueLeNomExisteDeja() throws Exception {
        String content = lastResult.getResponse().getContentAsString();
        assertThat(content).contains("existe déjà");
    }

    @Alors("je reçois {int} utilisateurs")
    public void jeRecoisNUtilisateurs(int count) throws Exception {
        String content = lastResult.getResponse().getContentAsString();
        List<?> users = objectMapper.readValue(content, List.class);
        assertThat(users).hasSize(count);
    }
}
