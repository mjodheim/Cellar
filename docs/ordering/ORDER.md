# Order

`Order` est l'agrégat qui porte le cycle de vie d'une commande.

## États

```text
DRAFT -> CONFIRMED -> PREPARING -> SHIPPED
   \         \            \
    +--------> CANCELLED <---+
```

Une commande expédiée ne peut plus être annulée.

## Règles métier

- une commande est créée en `DRAFT` ;
- les lignes ne peuvent être ajoutées qu'en brouillon ;
- une commande vide ne peut pas être confirmée ;
- la préparation ne démarre qu'après confirmation ;
- l'expédition n'est possible que pendant la préparation ;
- le soft delete est limité aux commandes brouillon ou annulées.

## Snapshot produit

Chaque `OrderLine` conserve le nom et le prix unitaire au moment de la commande.

C'est volontaire : si le catalogue change plus tard, une ancienne facture/commande doit conserver la réalité commerciale de l'époque.

## Collaboration avec les autres modules

Ordering ne lit pas les classes internes de Catalog ou Inventory.

```text
Ordering -> CatalogProducts (API publique Catalog)
Ordering -> InventoryOperations (API publique Inventory)
```

Cela respecte les frontières du Modulith.
