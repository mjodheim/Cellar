# 🧱 Pratiques professionnelles retenues

> Ce document rassemble les garde-fous actuellement appliqués dans Cellar et les prochains chantiers de robustesse.

---

## 🗃️ Source de vérité et migrations

| Élément | Décision |
| --- | --- |
| Base transactionnelle | PostgreSQL |
| Évolution du schéma | Flyway |
| Hibernate | `ddl-auto: validate` |
| Contraintes critiques | également protégées en SQL |

Le schéma courant est à la migration **V6**.

Une migration déjà appliquée n'est pas modifiée rétroactivement : toute correction passe par une nouvelle version Flyway.

---

## 🧩 Frontières Modulith

Modules principaux :

```text
catalog
inventory
ordering
identity
```

Collaborations publiques actuelles :

```text
ordering → catalog.CatalogProducts
ordering → inventory.InventoryOperations
```

Les classes `internal` restent privées au module.

`ModulithArchitectureTest` vérifie automatiquement les dépendances et les cycles.

---

## 🗑️ Soft delete et désactivation

Le soft delete est utilisé lorsqu'une suppression physique ferait perdre de la traçabilité.

| Objet | Stratégie |
| --- | --- |
| `Batch` | soft delete uniquement si stock et réservations = 0 |
| `Order` | soft delete uniquement en `DRAFT` ou `CANCELLED` |
| `User` | soft delete + désactivation + révocation des refresh tokens |
| `Product` | désactivation métier via `active=false` |

La désactivation d'un produit est volontairement distincte d'une suppression historique.

---

## 📦 Traçabilité du stock

Deux représentations complémentaires :

- `Batch` = **état courant** ;
- `StockMovement` = **historique immuable** des variations physiques.

Une réservation n'est pas un mouvement physique : elle est représentée par `Allocation`.

---

## ⏳ FEFO

Les lots disponibles sont ordonnés selon **First Expired, First Out** :

1. date d'expiration ;
2. date de réception ;
3. identifiant comme critère stable.

Les lots sans date d'expiration passent après les lots qui en possèdent une.

Une ligne de commande peut être répartie sur plusieurs lots.

---

## 💶 Snapshot commercial

Une `OrderLine` conserve :

- le nom du produit ;
- le prix unitaire ;
- la quantité.

Ces valeurs sont figées au moment de la commande.

> Modifier le catalogue aujourd'hui ne réécrit jamais l'histoire commerciale d'hier.

---

## ✅ Validation en couches

| Couche | Responsabilité |
| --- | --- |
| API | forme et contraintes HTTP |
| Application | règles nécessitant repositories ou plusieurs agrégats |
| Domaine | invariants et transitions d'état |
| PostgreSQL | contraintes structurelles critiques |

---

## 🔄 Transactions

Les opérations composées sont transactionnelles.

Exemples :

- réception d'un lot + mouvement `RECEIPT` ;
- confirmation d'une commande + réservations FEFO ;
- annulation + libération des réservations ;
- expédition + consommation des allocations + mouvements `SHIPMENT` ;
- refresh token : révocation de l'ancien + création du nouveau.

---

## 🔐 Sécurité

La sécurité est maintenant implémentée :

- Spring Security stateless ;
- BCrypt ;
- JWT HS256 ;
- refresh tokens opaques ;
- rotation des refresh tokens ;
- rôles `USER` et `ADMIN` ;
- bootstrap optionnel du premier administrateur ;
- secrets uniquement via environnement local.

Voir [SECURITY.md](SECURITY.md).

---

## 📝 Documentation du code

Le projet maintient trois niveaux complémentaires :

1. **README** : comprendre le projet en quelques minutes ;
2. **docs/** : comprendre les décisions métier et architecturales ;
3. **Javadoc** : comprendre les classes et méthodes directement dans l'IDE.

---

## 🛣️ Prochains garde-fous

Les prochaines étapes de robustesse sont :

- tests d'intégration avec PostgreSQL réel / Testcontainers ;
- verrouillage et gestion de concurrence sur le stock ;
- idempotence des commandes critiques ;
- audit de l'utilisateur à l'origine des opérations ;
- gestion globale des erreurs ;
- pagination et recherche ;
- Redis uniquement là où il apporte une vraie valeur ;
- rate limiting ;
- CI/CD ;
- observabilité ;
- stratégie de sauvegarde et restauration.

---

## 📚 Lire ensuite

- [Architecture](ARCHITECTURE.md)
- [Sécurité](SECURITY.md)
