# Allocation

`Allocation` représente l'affectation d'une quantité d'un lot à une ligne de commande.

Elle relie donc :

```text
OrderLine -> Allocation -> Batch
```

sans faire dépendre directement le domaine Inventory des classes internes du module Ordering.

## États

- `RESERVED` : la quantité est réservée ;
- `RELEASED` : la réservation a été annulée/libérée ;
- `CONSUMED` : la réservation a été consommée par l'expédition.

Seule une allocation `RESERVED` peut passer vers `RELEASED` ou `CONSUMED`.

## Pourquoi une entité séparée ?

Une ligne de commande peut être satisfaite par plusieurs lots. L'allocation rend ce lien explicite et permet de reconstruire exactement quels lots ont été utilisés pour quelle commande.
