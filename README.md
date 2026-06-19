# Yoga App — Testing Full-Stack

Application de gestion de sessions de yoga avec tests unitaires, d'intégration et end-to-end.

---

## Prérequis

| Outil | Version minimale |
|-------|-----------------|
| Java | 11+ |
| Node.js | 16+ |
| npm | 8+ |
| MySQL | 8 |
| Maven | 3.6+ |
| Angular CLI | 14 (`npm install -g @angular/cli@14`) |

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

## 3. Utilisation de l'application

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

### Fonctionnalités disponibles

| Rôle | Fonctionnalités |
|------|----------------|
| Admin | Créer, modifier, supprimer des sessions de yoga |
| Admin | Voir la liste des sessions et leur détail |
| Utilisateur | S'inscrire / se désinscrire d'une session |
| Utilisateur | Consulter son profil et supprimer son compte |

---

## 4. Lancer les tests

### Backend — Tests unitaires et d'intégration (JUnit + Mockito)

> Les tests utilisent une base H2 en mémoire — **aucune connexion MySQL requise**.

```bash
cd back
mvn test
```

---

### Frontend — Tests unitaires (Jest)

```bash
cd front
npm test
```

---

### Frontend — Tests End-to-End (Cypress)

Les tests E2E nécessitent que l'application Angular tourne avec l'instrumentation de couverture Istanbul activée.

#### Option 1 — Tout-en-un (recommandé en CI)

Lance automatiquement le serveur instrumenté **et** exécute Cypress en mode headless :

```bash
cd front
npm run e2e:ci
```

> Le backend doit être démarré (`mvn spring-boot:run`) pour que les tests E2E fonctionnent.

#### Option 2 — Mode interactif (développement)

**Terminal 1 — Démarrer l'application avec instrumentation :**
```bash
cd front
npx ng run yoga:serve-coverage
```

Attendre que la compilation affiche `Compiled successfully`.

**Terminal 2 — Ouvrir Cypress :**
```bash
cd front
npx cypress open
```

> ⚠️ Ne pas utiliser `ng serve` classique ni `npm start` pour les tests E2E avec couverture. Ces commandes ne chargent pas le webpack d'instrumentation Istanbul.

---

## 5. Générer les rapports de couverture

### Couverture backend — JaCoCo

La couverture JaCoCo est générée automatiquement lors de `mvn test`.

```bash
cd back
mvn test
```

📂 Rapport HTML disponible dans :
```
back/target/site/jacoco/index.html
```

> **Note :** Les packages `dto`, `models`, `payload` et la classe principale `SpringBootSecurityJwtApplication` sont exclus de la mesure de couverture conformément aux consignes du projet (les DTOs ne contiennent que des getters/setters sans logique métier).

---

### Couverture frontend — Jest (tests unitaires)

```bash
cd front
npx jest --coverage --watchAll=false
```

📂 Rapport HTML disponible dans :
```
front/coverage/jest/lcov-report/index.html
```

---

### Couverture frontend — Cypress (tests E2E)

La couverture E2E se génère en **deux étapes** :

**Étape 1 — Exécuter les tests contre l'application instrumentée :**

```bash
cd front
npm run e2e:ci
```

Cette commande démarre l'application avec le webpack Istanbul (`coverage.webpack.ts`), exécute tous les tests Cypress, et `@cypress/code-coverage` sauvegarde les données de couverture dans `.nyc_output/out.json`.

**Étape 2 — Générer le rapport HTML :**

```bash
cd front
npm run e2e:coverage
```

📂 Rapport HTML disponible dans :
```
front/coverage/lcov-report/index.html
```

#### Comment fonctionne la couverture E2E

```
ng run yoga:serve-coverage
    └── webpack Istanbul instrumente le code source
              └── Cypress exécute les tests
                      └── @cypress/code-coverage récupère window.__coverage__
                              └── sauvegarde dans .nyc_output/out.json
                                      └── npm run e2e:coverage génère le rapport HTML
```

---

## 6. Structure du projet

```
projet-5_Testing/
│
├── back/                                   # Backend Spring Boot (Java)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/                       # Code source
│   │   │   └── resources/
│   │   │       ├── application.properties  # Config MySQL (prod)
│   │   │       └── script.sql             # Script BDD initial
│   │   └── test/
│   │       ├── java/                       # Tests JUnit/Mockito
│   │       └── resources/
│   │           └── application.properties  # Config H2 (tests)
│   └── target/site/jacoco/                 # Rapport JaCoCo (après mvn test)
│
├── front/                                  # Frontend Angular
│   ├── src/app/                            # Code source Angular
│   ├── cypress/
│   │   ├── e2e/                            # Tests End-to-End Cypress
│   │   └── support/                        # Commandes et configuration Cypress
│   ├── coverage/
│   │   ├── jest/lcov-report/               # Rapport Jest (après npm test)
│   │   └── lcov-report/                    # Rapport Cypress E2E (après e2e:ci + e2e:coverage)
│   └── .nyc_output/                        # Données brutes couverture E2E
│
└── README.md
```

---

## 7. Résumé des taux de couverture

### Frontend — Tests unitaires (Jest)

| Indicateur | Résultat | Seuil requis |
|-----------|----------|-------------|
| Statements | **100%** | 80% min |
| Branches | **94.11%** | 80% min |
| Functions | **100%** | 80% min |
| Lines | **100%** | 80% min |

Rapport : `front/coverage/jest/lcov-report/index.html`

### Frontend — Tests E2E (Cypress)

| Indicateur | Résultat | Seuil requis |
|-----------|----------|-------------|
| Statements | **84.08%** | 80% min |
| Branches | **83.83%** | 80% min |
| Functions | **83.68%** | 80% min |
| Lines | **83.78%** | 80% min |

Rapport : `front/coverage/lcov-report/index.html`


Le minimum requis de 30% de tests d'intégration est respecté.

### Backend — JaCoCo (tests unitaires + intégration)

| Indicateur | Résultat | Seuil requis |
|-----------|----------|-------------|
| Instructions | **98%** | 80% min |
| Branches | **96%** | 80% min |
| Lines | **≥ 80%** | 80% min |
| Methods | **≥ 80%** | 80% min |

Rapport : `back/target/site/jacoco/index.html`

> Les packages `dto`, `models`, `payload` et la classe `SpringBootSecurityJwtApplication` sont exclus de la mesure de couverture conformément aux consignes du projet.
