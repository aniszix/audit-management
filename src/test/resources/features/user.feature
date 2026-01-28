# language: fr
# Fichier de feature Cucumber pour les tests BDD
# Les scénarios sont écrits en langage naturel (Gherkin)

Fonctionnalité: Gestion des utilisateurs
  En tant qu'administrateur du système d'audit
  Je veux pouvoir gérer les utilisateurs
  Afin de contrôler l'accès au système

  Contexte:
    Etant donné la base de données est vide

  Scénario: Créer un nouvel utilisateur
    Quand je crée un utilisateur avec:
      | username | email              | role    |
      | jean.dupont | jean@example.com | AUDITOR |
    Alors l'utilisateur est créé avec succès
    Et l'utilisateur a un ID généré
    Et l'utilisateur a une date de création

  Scénario: Récupérer un utilisateur existant
    Etant donné un utilisateur existe avec le nom "marie.martin"
    Quand je récupère l'utilisateur par son ID
    Alors je reçois les informations de l'utilisateur
    Et le nom d'utilisateur est "marie.martin"

  Scénario: Mettre à jour un utilisateur
    Etant donné un utilisateur existe avec le nom "pierre.durand"
    Quand je modifie son rôle en "ADMIN"
    Alors l'utilisateur est mis à jour
    Et son nouveau rôle est "ADMIN"

  Scénario: Supprimer un utilisateur
    Etant donné un utilisateur existe avec le nom "temp.user"
    Quand je supprime cet utilisateur
    Alors l'utilisateur n'existe plus dans le système

  Scénario: Empêcher les doublons de nom d'utilisateur
    Etant donné un utilisateur existe avec le nom "unique.user"
    Quand je crée un utilisateur avec le même nom "unique.user"
    Alors je reçois une erreur de conflit
    Et le message indique que le nom existe déjà

  Scénario: Rechercher des utilisateurs par rôle
    Etant donné les utilisateurs suivants existent:
      | username    | email           | role    |
      | auditor1    | a1@example.com  | AUDITOR |
      | auditor2    | a2@example.com  | AUDITOR |
      | admin1      | ad@example.com  | ADMIN   |
    Quand je recherche les utilisateurs avec le rôle "AUDITOR"
    Alors je reçois 2 utilisateurs
