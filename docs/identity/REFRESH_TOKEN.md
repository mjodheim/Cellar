# RefreshToken

Cellar utilise deux types de jetons :

```text
Access token JWT
    durée courte
    envoyé dans Authorization: Bearer ...

Refresh token opaque
    durée longue
    utilisé uniquement pour obtenir une nouvelle paire de jetons
```

Le refresh token est aléatoire et opaque : il ne contient aucune donnée métier.

## Stockage

Le token brut n'est jamais stocké dans PostgreSQL.

Cellar stocke uniquement :

```text
SHA-256(rawRefreshToken)
```

Ainsi une fuite de la table des refresh tokens ne permet pas de réutiliser directement les valeurs stockées.

## Rotation

À chaque appel à `/api/auth/refresh` :

1. le refresh token présenté est haché ;
2. le hash est retrouvé en base ;
3. le token doit être actif et non expiré ;
4. l'ancien token est révoqué ;
5. un nouveau refresh token est généré ;
6. un nouvel access token JWT est émis.

La rotation limite la durée d'utilité d'un refresh token compromis.
