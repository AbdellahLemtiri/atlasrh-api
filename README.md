# AtlasRH - API REST de Gestion des Ressources Humaines

API REST développée avec **Spring Boot** et **Java 17** pour la gestion des collaborateurs, des départements, des contrats et des congés au sein d'AtlasRH.

Ce document correspond au **Livrable 1 : Module Responsable RH (US1 à US3)** et détaille les instructions d'exécution ainsi que l'architecture technique mise en œuvre.

---

## 1. Prérequis

Assurez-vous d'avoir installé sur votre environnement :
* **Java Development Kit (JDK)** : version 17 ou supérieure
* **Apache Maven** : version 3.8+ (ou utiliser le wrapper `./mvnw`)
* **PostgreSQL** : version 14+
* **Git**

---

## 2. Configuration & Lancement

### Étape 1 : Cloner le dépôt Git
```bash
git clone <URL_DU_DEPOT_GIT>
cd atlasrh-api
```

### Étape 2 : Configurer la base de données PostgreSQL
Créez une base de données locale nommée `atlasrh_db` :
```sql
CREATE DATABASE atlasrh_db;
```

Vérifiez ou adaptez les accès dans le fichier `src/main/resources/application.yml` :
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/atlasrh_db
    username: postgres
    password: votre_mot_de_passe
    driver-class-name: org.postgresql.Driver
```

> **Note :** La création des tables et des contraintes relationnelles est exécutée automatiquement au démarrage grâce aux scripts de migration **Liquibase** (`01-create-tables.yaml`).

### Étape 3 : Compiler et exécuter l'application
Via le terminal à la racine du projet :
```bash
mvn clean install
mvn spring-boot:run
```
L'application démarre sur le port par défaut **8080**.

---

## 3. Documentation de l'API (Swagger UI)

Une fois l'application démarrée, l'interface interactive Swagger / OpenAPI est accessible à l'adresse suivante :

👉 **[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)**

Endpoints disponibles pour le Livrable 1 :
* **Départements (`/api/departments`)** : CRUD complet (création, modification, consultation, suppression conditionnelle).
* **Employés (`/api/employees`)** : CRUD, consultation hiérarchique (équipe d'un manager), pagination et recherche multicritères.
* **Contrats (`/api/contracts`)** : Création avec contrôle de contrat actif unique, clôture et historique ordonné par collaborateur.

---

## 4. Architecture en Couches (Layered Architecture)

Le projet respecte une architecture en couches stricte à sens unique afin de garantir le découplage, la testabilité et la sécurité du modèle de données :

```
Client (HTTP/JSON)
       │
       ▼
┌──────────────────────────────────────────────┐
│  Couche Contrôleur (com.atlasrh.controller)  │
│  - Réception des requêtes HTTP               │
│  - Validation des DTOs (@Valid)              │
│  - Documentation Swagger (@Operation)        │
└──────────────────────┬───────────────────────┘
                       │ DTOs (Request / Response)
                       ▼
┌──────────────────────────────────────────────┐
│  Couche Service (com.atlasrh.service)        │
│  - Règles de gestion métier (Business Logic) │
│  - Gestion des transactions (@Transactional) │
│  - Mapping Entité <-> DTO (MapStruct)        │
└──────────────────────┬───────────────────────┘
                       │ Entités JPA
                       ▼
┌──────────────────────────────────────────────┐
│  Couche Repository (com.atlasrh.repository)  │
│  - Accès aux données avec Spring Data JPA    │
│  - Pagination et filtres (Pageable)          │
└──────────────────────┬───────────────────────┘
                       │ SQL
                       ▼
           PostgreSQL Database (Liquibase)
```

### Règle clé de sécurité architecturale :
**Les entités JPA ne sont jamais exposées à l'extérieur de la couche Service.**  
Les échanges avec la couche Web s'effectuent exclusivement via des objets de transfert (**DTO**), convertis de façon performante au moment de la compilation grâce à **MapStruct**.

---

## 5. Organisation des Packages

```text
com.atlasrh
├── config/                 # Configurations Swagger / OpenAPI et Beans globaux
├── controller/             # Contrôleurs REST exposant l'API JSON
├── dto/
│   ├── request/            # Objets reçus en entrée (avec validation Bean Validation)
│   └── response/           # Objets renvoyés en sortie vers le client
├── entity/                 # Entités JPA représentatives du schéma de données
│   └── enums/              # Énumérations métier (ContractType, LeaveStatus)
├── exception/              # Gestionnaire centralisé d'erreurs (GlobalExceptionHandler)
├── mapper/                 # Mappers MapStruct (DTO <-> Entité)
├── repository/             # Interfaces d'accès aux données (Spring Data JPA)
└── service/
    ├── interfaces          # Contrats des services métier
    └── impl/               # Implémentations contenant les règles de gestion
```

---

## 6. Conventions & Règles de Gestion Appliquées

* **Codes HTTP normalisés** :
  * `200 OK` : Opération de lecture ou modification réussie.
  * `201 CREATED` : Ressource créée avec succès.
  * `204 NO CONTENT` : Suppression réussie sans contenu retourné.
  * `400 BAD REQUEST` : Erreur de validation de format ou champs manquants.
  * `404 NOT FOUND` : Ressource demandée inexistante.
  * `409 CONFLICT` : Conflit métier (doublon de nom, matricule ou email déjà assigné, suppression d'un département non vide).
* **Règles métier validées (US1 à US3)** :
  * Un département contenant des collaborateurs ne peut pas être supprimé.
  * Le matricule et l'email d'un collaborateur sont strictement uniques.
  * Un collaborateur ne peut pas être son propre manager.
  * Un collaborateur ne peut disposer que d'un seul contrat actif à la fois.
  * Les contrats de type `CDD` et `STAGE` exigent obligatoirement une date de fin.