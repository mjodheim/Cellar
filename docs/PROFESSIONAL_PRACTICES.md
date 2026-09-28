# Pratiques professionnelles retenues

Ce document résume les garde-fous appliqués avant l'ajout de la sécurité.

## Source de vérité et migrations

- PostgreSQL est la source de vérité transactionnelle.
- Hibernate est configuré en `ddl-auto: validate`.
- Flyway est seul responsable de l'évolution du schéma.
- Les contraintes importantes sont répétées en base lorsque c'est pertinent.

## Frontières Modulith

Les modules principaux sont :

```text
catalog
inventory
ordering
identity
```

Les collaborations entre modules passent par une API publique à la racine du module :

```text
ordering -> catalog.CatalogProducts
ordering -> inventory.InventoryOperations
```

Les classes `internal` d'un module ne doivent pas être utilisées depuis un autre module.

Un test `ModulithArchitectureTest` vérifie automatiquement les frontières et les cycles.

## Soft delete

Le soft delete est utilisé lorsqu'une suppression physique ferait perdre de la traçabilité.

Il est déjà appliqué à :

- `Batch` : suppression uniquement si stock physique et réservations sont à zéro ;
- `Order` : suppression uniquement lorsqu'elle est brouillon ou annulée.

`Product` possède actuellement une désactivation métier (`active=false`). La désactivation et la suppression logique sont volontairement distinguées : un produit qui n'est plus vendu doit rester référencé par l'historique des commandes et du stock.

## Traçabilité des stocks

Le stock utilise deux représentations complémentaires :

- `Batch` contient l'état courant pour répondre rapidement aux lectures ;
- `StockMovement` est un ledger immuable qui explique les variations physiques.

Les réservations ne sont pas des mouvements physiques : elles sont représentées par `Allocation`.

## FEFO

Les lots disponibles sont ordonnés par date d'expiration, puis date de réception.

Les lots sans date d'expiration passent après les lots ayant une date.

L'allocation peut répartir une ligne de commande sur plusieurs lots.

## Snapshot commercial

Une `OrderLine` conserve le nom et le prix du produit au moment de la commande.

Le catalogue peut donc changer sans réécrire l'histoire commerciale.

## Validation en couches

- l'API valide la forme des entrées ;
- l'application valide les règles nécessitant des repositories ou plusieurs agrégats ;
- le domaine protège ses invariants ;
- PostgreSQL protège les contraintes structurelles critiques.

## Transactions

Les opérations composées sont transactionnelles.

Exemples :

- réception d'un lot + écriture du mouvement de réception ;
- confirmation d'une commande + réservations FEFO ;
- annulation + libération des réservations ;
- expédition + consommation des allocations + mouvements de sortie.

## À traiter après validation métier

La sécurité sera ajoutée uniquement après validation du modèle actuel.

Les étapes professionnelles prévues ensuite comprennent notamment :

- authentification et autorisations ;
- audit de l'utilisateur à l'origine des opérations ;
- gestion globale et normalisée des erreurs ;
- pagination et recherche ;
- idempotence des commandes critiques ;
- verrouillage/concurrence sur le stock ;
- cache Redis uniquement là où il apporte une vraie valeur ;
- rate limiting ;
- tests d'intégration avec PostgreSQL réel/Testcontainers ;
- observabilité ;
- CI/CD et sauvegardes.
