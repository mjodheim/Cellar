# Modules métier de Cellar

Le découpage suivant constitue l'hypothèse initiale. Il pourra évoluer lorsque les besoins métier seront mieux connus.

## Catalog

**Responsabilité :** décrire ce que Mjödheim propose.

Concepts probables :

- Product ;
- ProductType ;
- informations commerciales ;
- activation/désactivation du catalogue.

Catalog ne porte pas le stock physique.

## Inventory

**Responsabilité :** représenter ce qui existe physiquement et comment les quantités évoluent.

Concepts probables :

- Batch ;
- StockMovement ;
- Allocation ;
- quantité disponible ;
- réservation ;
- FEFO ;
- ajustements de stock.

Inventory ne décide pas du cycle de vie complet d'une commande.

## Ordering

**Responsabilité :** représenter ce que le client commande et son évolution.

Concepts probables :

- Order ;
- OrderLine ;
- statuts ;
- confirmation ;
- préparation ;
- expédition ;
- annulation.

Ordering collaborera avec Catalog et Inventory via leurs API publiques.

## Identity

**Responsabilité :** savoir qui utilise l'application et ce qu'il est autorisé à faire.

Concepts probables :

- User ;
- Role ;
- authentification ;
- autorisations ;
- identité utilisée pour l'audit.

Les détails JWT, BCrypt ou Spring Security seront des mécanismes techniques internes, pas le domaine lui-même.

## Modules futurs possibles

Ils ne sont **pas créés aujourd'hui**.

Selon l'évolution réelle du produit, on pourra éventuellement identifier des capacités comme :

- reporting ;
- notification ;
- document management.

On ne les ajoutera que lorsqu'ils auront une responsabilité métier autonome et des frontières claires.
