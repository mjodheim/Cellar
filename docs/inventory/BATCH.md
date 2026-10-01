# 📦 Batch

> `Batch` représente un **lot physique réel** d'un produit du catalogue.

---

## 🔍 Product vs Batch

```text
Product "Hydromel Classique"
├── LOT-2026-001
├── LOT-2026-002
└── LOT-2026-003
```

| Product | Batch |
| --- | --- |
| décrit ce qui est vendu | décrit ce qui existe physiquement |
| prix / nom / volume | quantité / lot / expiration |
| Catalog | Inventory |

---

## 📊 État suivi

Un lot conserve notamment :

- produit concerné ;
- numéro de lot ;
- quantité reçue ;
- quantité physique ;
- quantité réservée ;
- réception ;
- expiration éventuelle ;
- dates de création, modification et soft delete.

### Quantité disponible

```text
availableQuantity = quantityOnHand - quantityReserved
```

---

## ✅ Règles métier

- quantité reçue > 0 ;
- stock physique ≥ 0 ;
- quantité réservée ≥ 0 ;
- quantité réservée ≤ quantité physique ;
- impossible de réserver plus que le disponible ;
- impossible de libérer plus que le réservé ;
- expédier du réservé diminue réservation **et** stock physique ;
- un lot soft-deleted ne peut plus être modifié ;
- un lot n'est supprimable logiquement que si stock et réservations valent 0.

---

## 🗑️ Soft delete

Le lot n'est pas supprimé physiquement.

`deletedAt` conserve l'historique indispensable aux commandes et mouvements déjà réalisés.

---

## ⏳ FEFO

`expiresOn` participe à l'ordre **First Expired, First Out**.

```text
expire tôt → utilisé en premier
sans expiration → après les lots datés
```

---

## 🔗 Voir aussi

- [StockMovement](STOCK_MOVEMENT.md)
- [Allocation](ALLOCATION.md)
- [Product](../catalog/PRODUCT.md)
