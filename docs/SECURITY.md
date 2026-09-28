# 🔐 Sécurité de Cellar

> Cellar utilise une API HTTP **stateless** protégée par Spring Security, avec access tokens JWT courts et refresh tokens opaques.

---

## 🧭 Vue d'ensemble

```mermaid
flowchart TD
    CLIENT[Client]
    AUTH[/api/auth/login]
    SERVICE[AuthenticationService]
    BCRYPT[BCrypt]
    JWT[JWT access token]
    REFRESH[Refresh token opaque]
    API[API protégée]

    CLIENT --> AUTH
    AUTH --> SERVICE
    SERVICE --> BCRYPT
    SERVICE --> JWT
    SERVICE --> REFRESH
    CLIENT -->|Authorization: Bearer JWT| API
```

---

## 🎟️ Access token

L'access token est un JWT signé en **HS256**.

Claims principaux :

| Claim | Contenu |
| --- | --- |
| `sub` | email |
| `uid` | identifiant utilisateur |
| `name` | nom d'affichage |
| `roles` | rôles de sécurité |
| `iss` | issuer |
| `iat` | date d'émission |
| `exp` | expiration |

Durée par défaut : **15 minutes**.

---

## 🔑 Secret JWT

Le secret vient exclusivement de :

```text
JWT_SECRET
```

Il doit être une clé Base64 représentant au moins **256 bits**.

```bash
openssl rand -base64 32
```

> La valeur réelle reste dans `.env` et n'est jamais versionnée.

---

## 🔒 Mots de passe

Les mots de passe sont hashés avec **BCrypt**, coût 12.

Un mot de passe en clair :

- n'est jamais persisté ;
- n'est jamais journalisé ;
- n'est jamais placé dans un JWT ;
- n'est jamais renvoyé par l'API.

---

## ♻️ Refresh tokens

Les refresh tokens sont :

- opaques ;
- générés avec `SecureRandom` ;
- persistés uniquement sous forme de hash SHA-256 ;
- tournés à chaque refresh ;
- révocables.

```text
raw refresh token
      ↓ SHA-256
token_hash stocké en base
```

### Rotation

À chaque `POST /api/auth/refresh` :

1. le token reçu est haché ;
2. son hash est recherché ;
3. il doit être actif et non expiré ;
4. l'ancien token est révoqué ;
5. un nouveau refresh token est généré ;
6. un nouvel access token est émis.

---

## 🛡️ Autorisations

| Ressource | Accès |
| --- | --- |
| Swagger / OpenAPI | Public |
| Health actuator | Public |
| Register / Login / Refresh / Logout | Public |
| Lecture Catalog | Authentifié |
| Écriture Catalog | ADMIN |
| Inventory | ADMIN |
| Orders | Authentifié |
| `/api/auth/me` | Authentifié |

---

## 👑 Administrateur initial

Le premier administrateur peut être créé automatiquement au démarrage avec :

```env
BOOTSTRAP_ADMIN_EMAIL=
BOOTSTRAP_ADMIN_NAME=
BOOTSTRAP_ADMIN_PASSWORD=
```

Le compte est créé uniquement si :

- les trois valeurs sont présentes ;
- l'email n'existe pas déjà.

> Ces valeurs sont des secrets locaux et ne doivent jamais être commitées.

---

## 🗑️ Suppression de compte

La suppression utilisateur est logique.

Elle :

- renseigne `deletedAt` ;
- désactive le compte ;
- révoque les refresh tokens encore actifs.

Les access tokens déjà émis restent valides jusqu'à leur expiration courte.

---

## 🧪 Tester avec Swagger

1. ouvrir `http://localhost:8080/swagger-ui/index.html` ;
2. appeler `/api/auth/login` ;
3. copier `accessToken` ;
4. cliquer sur **Authorize** ;
5. coller le JWT ;
6. tester les endpoints protégés.

---

## 📚 Lire ensuite

- [User](identity/USER.md)
- [RefreshToken](identity/REFRESH_TOKEN.md)
