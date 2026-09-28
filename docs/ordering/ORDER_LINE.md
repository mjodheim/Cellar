# OrderLine

`OrderLine` représente une ligne immuable d'une commande.

Elle contient :

- l'identifiant du produit ;
- le nom du produit au moment de la commande ;
- la quantité ;
- le prix unitaire au moment de la commande ;
- sa date de création.

Le total de ligne est calculé par `unitPrice × quantity`.

Une ligne ne référence pas directement la classe interne `Product` du module Catalog. Cette séparation évite de coupler les modèles internes de deux modules.
