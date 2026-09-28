# Sécurité de Cellar

## Objectif

Cellar utilise une API HTTP stateless protégée par Spring Security.

L'implémentation reprend les concepts étudiés en cours — JWT, BCrypt, rôles, filtres de sécurité — en utilisant les primitives natives actuelles de Spring Security pour éviter une implémentation JWT artisanale.

## Architecture

```text
Client
  |
  | POST /api/auth/login
  v
AuthenticationService
  |-- PasswordHashingPort -> BCrypt
  |-- AccessTokenPort     -> JWT signé
  '-- RefreshTokenCodec   -> token opaque + hash SHA-256

Requête protégée
  |
  | Authorization: Bearer <JWT>
  v
Spring Security OAuth2 Resource Server
  |
  v
SecurityContext
  |
  v
Controller
```

## Access token

L'access token est un JWT signé en HS256.

Il contient notamment :

- `sub` : email ;
- `uid` : identifiant utilisateur ;
- `name` : nom d'affichage ;
- `roles` : rôles de sécurité ;
- `iss`, `iat`, `exp`.

La durée par défaut est de 15 minutes.

## Secret JWT

Aucun secret n'est versionné.

Le secret est lu depuis :

```text
JWT_SECRET
```

Il doit contenir une clé Base64 représentant au minimum 256 bits.

Exemple de génération locale :

```bash
openssl rand -base64 32
```

La valeur réelle va dans `.env`, jamais dans Git.

## Passwords

Les mots de passe sont hashés avec BCrypt avant d'entrer dans le domaine.

Le coût BCrypt est actuellement fixé à 12.

Le mot de passe en clair :

- n'est jamais persisté ;
- n'est jamais journalisé ;
- n'est jamais placé dans un JWT ;
- n'est jamais retourné par l'API.

## Refresh tokens

Les refresh tokens sont opaques, générés avec `SecureRandom`, persistés uniquement sous forme de hash SHA-256 et tournés à chaque refresh.

Le logout révoque le refresh token présenté.

Un access token déjà émis reste valide jusqu'à son expiration courte. C'est un compromis volontaire d'une architecture JWT stateless.

## Autorisations

Politique initiale :

```text
Swagger / OpenAPI              public
health actuator               public
register/login/refresh/logout public

lecture Catalog               authenticated
écriture Catalog              ADMIN
Inventory                     ADMIN
Orders                        authenticated
/api/auth/me                  authenticated
```

Cette politique sera raffinée lorsque les rôles réels de l'équipe Mjödheim seront définis.

## Administrateur initial

Un premier administrateur peut être créé au démarrage uniquement si les trois variables suivantes sont présentes :

```text
BOOTSTRAP_ADMIN_EMAIL
BOOTSTRAP_ADMIN_NAME
BOOTSTRAP_ADMIN_PASSWORD
```

Si l'email existe déjà, rien n'est recréé.

Les credentials de bootstrap ne doivent jamais être ajoutés au dépôt.

## Suppression de compte

La suppression utilisateur est un soft delete.

Elle :

- renseigne `deletedAt` ;
- désactive le compte ;
- révoque les refresh tokens encore actifs.

Les access tokens déjà émis expirent naturellement après leur courte durée de vie.

## Swagger

Swagger déclare le schéma `bearerAuth`.

Après un login :

1. copier `accessToken` ;
2. cliquer sur **Authorize** ;
3. saisir le JWT ;
4. tester les endpoints protégés.
