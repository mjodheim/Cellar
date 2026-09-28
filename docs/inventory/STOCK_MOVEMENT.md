# StockMovement

`StockMovement` est l'écriture de **ledger** qui explique pourquoi le stock physique a changé.

Il est volontairement immuable : un mouvement historique ne doit pas être réécrit après coup.

## Types

- `RECEIPT` : réception de stock ;
- `SHIPMENT` : expédition ;
- `ADJUSTMENT_IN` : correction positive ;
- `ADJUSTMENT_OUT` : correction négative ;
- `WASTE` : perte/casse/périmé ;
- `RETURN` : retour en stock.

Chaque mouvement référence un lot, une quantité positive, une date d'occurrence et éventuellement une référence externe et une note.

La direction du mouvement est portée par son type, ce qui évite les quantités signées ambiguës.

## Pourquoi un ledger ?

Le champ de quantité courante sur `Batch` répond rapidement à « combien reste-t-il ? ».

Le ledger `StockMovement` répond à « pourquoi cette quantité est-elle devenue ce qu'elle est ? ».

En contexte professionnel, les deux sont utiles : état courant rapide + historique auditable.
