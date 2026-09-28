# Batch

`Batch` représente un **lot physique** d'un produit du catalogue.

## Pourquoi Batch est séparé de Product ?

`Product` décrit ce que Mjödheim vend. `Batch` décrit ce qui existe réellement dans la cave.

Un même produit peut avoir plusieurs lots, reçus à des dates différentes et avec des dates d'expiration différentes.

```text
Product "Hydromel Classique"
├── LOT-2026-001
├── LOT-2026-002
└── LOT-2026-003
```

## État suivi

Un lot conserve notamment :

- l'identifiant du produit ;
- le numéro de lot ;
- la quantité reçue ;
- la quantité physiquement présente ;
- la quantité réservée ;
- la date de réception ;
- la date d'expiration éventuelle ;
- les dates techniques de création, modification et soft delete.

La quantité disponible est calculée :

```text
available = quantityOnHand - quantityReserved
```

## Règles métier

- une quantité reçue doit être strictement positive ;
- la quantité physique ne peut jamais être négative ;
- la quantité réservée ne peut jamais dépasser la quantité physique ;
- une réservation ne peut pas dépasser le stock disponible ;
- une libération ne peut pas dépasser la quantité réservée ;
- une expédition réservée diminue à la fois la réservation et le stock physique ;
- un lot supprimé logiquement ne peut plus être modifié ;
- un lot ne peut être soft-deleted que lorsque son stock et ses réservations sont à zéro.

## Soft delete

Le lot n'est pas supprimé physiquement de la base.

`deletedAt` permet de conserver la traçabilité historique. C'est particulièrement important pour les commandes et mouvements de stock déjà réalisés.

## FEFO

La date `expiresOn` permettra au service d'allocation de choisir les lots selon **FEFO** (First Expired, First Out) : le lot qui expire le plus tôt est utilisé en priorité.
