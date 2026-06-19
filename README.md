# Yoga App — Testing Full-Stack

Application de gestion de sessions de yoga avec tests unitaires, d'intégration et end-to-end.

## Prérequis

- **Java 11** (ou 8+)
- **Node.js 16+** et **npm**
- **MySQL 8** sur le port 3306
- **Angular CLI 14** (`npm install -g @angular/cli@14`)
- **Maven 3.6+**

---

## 1. Installation de la base de données

### Créer la base de données MySQL

```sql
CREATE DATABASE yoga;
```

### Initialiser le schéma et les données

```bash
mysql -u root -p yoga < back/src/main/resources/script.sql
```

Ce script crée les tables (`TEACHERS`, `SESSIONS`, `USERS`, `PARTICIPATE`) et insère :
- 2 professeurs (Margot DELAHAYE, Hélène THIERCELIN)
- 1 compte administrateur : `yoga@studio.com` / `test!1234`

### Configurer la connexion

Dans `back/src/main/resources/application.properties`, ajuster si nécessaire :

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/yoga
spring.datasource.username=root
spring.datasource.password=VotreMotDePasse
```

---

## 2. Installation de l'application

### Backend (Spring Boot)

```bash
cd back
mvn clean install -DskipTests
```

### Frontend (Angular)

```bash
cd front
npm install
```

---

## 3. Lancer l'application

### Démarrer le backend (API REST — port 8080)

```bash
cd back
mvn spring-boot:run
```

L'API est disponible sur `http://localhost:8080`

### Démarrer le frontend (Angular — port 4200)

```bash
cd front
npm start
```

L'application est disponible sur `http://localhost:4200`

### Connexion administrateur

- **Email :** `yoga@studio.com`
- **Mot de passe :** `test!1234`

---

## 4. Lancer les tests

### Tests unitaires et d'intégration — Backend (JUnit + Mockito)

```bash
cd back
mvn test
```

Les tests utilisent une base H2 en mémoire — **aucune connexion MySQL requise**.

#### Rapport de couverture JaCoCo

Après `mvn test`, le rapport est généré dans :

```
back/target/site/jacoco/index.html
```

Ouvrir ce fichier dans un navigateur pour visualiser la couverture.

---

### Tests unitaires et d'intégration — Frontend (Jest)

```bash
cd front
npm test
```

#### Rapport de couverture Jest (lcov)

```bash
cd front
npx jest --coverage --watchAll=false
```

Le rapport HTML est généré dans :

```
front/coverage/jest/lcov-report/index.html
```

---

### Tests End-to-End (Cypress)

> L'application doit être démarrée (backend + frontend) avant de lancer les tests E2E.

#### Mode interactif (interface graphique)

```bash
cd front
npx cypress open
```

#### Mode headless (ligne de commande)

```bash
cd front
npx cypress run
```

---

## 5. Générer les rapports de couverture

### Couverture backend (JaCoCo)

```bash
cd back
mvn test
# Rapport disponible dans : back/target/site/jacoco/index.html
```

### Couverture frontend (Jest)

```bash
cd front
npx jest --coverage --watchAll=false
# Rapport disponible dans : front/coverage/jest/lcov-report/index.html
```

### Couverture E2E (Cypress)

Les tests E2E se trouvent dans `front/cypress/e2e/`. Lancer Cypress en mode headless et consulter la console pour les résultats.

---

## 6. Structure du projet

```
projet-5_Testing/
├── back/                          # Backend Spring Boot (Java)
│   ├── src/
│   │   ├── main/java/             # Code source
│   │   └── test/java/             # Tests JUnit (unitaires + intégration)
│   └── target/site/jacoco/        # Rapport de couverture JaCoCo
│
├── front/                         # Frontend Angular
│   ├── src/
│   │   └── app/                   # Code source Angular
│   ├── cypress/e2e/               # Tests End-to-End Cypress
│   └── coverage/jest/             # Rapport de couverture Jest
│
└── README.md
```

## 7. Résumé des taux de couverture

| Couche | Framework | Couverture |
|--------|-----------|------------|
| Frontend | Jest | ≥ 94% (branches), 100% (statements, functions, lines) |
| Backend | JaCoCo | ≥ 80% |
| E2E | Cypress | Fonctionnalités principales couvertes |
