# User

`User` représente un compte humain autorisé à utiliser Cellar.

## Responsabilités

Il porte :

- l'email normalisé et immuable ;
- le nom d'affichage ;
- uniquement le **hash** du mot de passe ;
- le rôle `USER` ou `ADMIN` ;
- l'état enabled/disabled ;
- les dates de création, modification et soft delete.

Le domaine ne reçoit jamais le mot de passe en clair. Le hash est produit dans un adapter de sécurité avant la création ou la modification du modèle métier.

## Désactivation et soft delete

`enabled=false` bloque l'authentification tout en conservant le compte.

`deletedAt` représente une suppression logique. Un compte soft-deleted est automatiquement désactivé et n'est plus retourné par les repositories métier.

Cette distinction permet de conserver la traçabilité future des opérations effectuées par un utilisateur sans maintenir son compte comme actif.
