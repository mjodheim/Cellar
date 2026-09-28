# Product

Ce document décrit le rôle de `Product` dans le module `catalog`.

## Rôle

`Product` représente un produit du catalogue Mjödheim.

Il décrit **ce que l'on vend**, pas ce qui est physiquement présent en stock.

Le stock réel, les lots, les mouvements et les réservations appartiennent au module `inventory`.

## Données portées par Product

La première version de `Product` contient :

- `id` : identifiant du produit ;
- `name` : nom du produit ;
- `type` : type de produit, représenté par `ProductType` ;
- `description` : description libre ;
- `volumeMl` : contenance en millilitres ;
- `price` : prix du produit ;
- `active` : indique si le produit peut encore être utilisé dans le catalogue ;
- `createdAt` : date de création ;
- `updatedAt` : date de dernière modification.

## Pourquoi Product n'est pas une Entity JPA

`Product` appartient au domaine métier.

Il ne contient donc pas d'annotations techniques comme :

```java
@Entity
@Table
@Column
@Enumerated
```

Ces annotations appartiendront plus tard à `ProductEntity`, dans l'adapter de persistence.

Cela permet au modèle métier de rester indépendant de PostgreSQL, Hibernate et JPA.

## Validation dans Product

`Product` protège lui-même ses invariants.

Exemples :

- le nom ne peut pas être vide ;
- le type est obligatoire ;
- le volume doit être supérieur à zéro ;
- le prix ne peut pas être négatif.

L'objectif est qu'un `Product` valide le reste quel que soit son point d'entrée : API REST, import, test ou autre.

Les validations qui nécessitent de consulter d'autres données n'appartiennent pas directement à `Product`.

Exemple :

> Le nom du produit doit être unique.

Cette règle nécessite de consulter le catalogue existant. Elle sera donc vérifiée par un cas d'usage dans la couche `application`.

## create(...)

`create(...)` sert à créer un **nouveau produit métier**.

Exemple conceptuel :

```java
Product.create(...)
```

Lors d'une création :

- l'identifiant peut ne pas encore exister ;
- le produit est actif par défaut ;
- `createdAt` et `updatedAt` sont initialisés ;
- les invariants métier sont vérifiés.

## rehydrate(...)

`rehydrate(...)` signifie littéralement **reconstruire un objet métier à partir de données déjà existantes**.

Ce n'est pas une nouvelle création.

Exemple :

1. PostgreSQL contient déjà un produit ;
2. l'adapter de persistence lit une `ProductEntity` ;
3. le mapper transforme cette Entity en `Product` ;
4. il appelle `Product.rehydrate(...)`.

Cela permet de reconstruire le produit avec :

- son identifiant existant ;
- son état actif ou inactif ;
- sa date de création d'origine ;
- sa date de dernière modification.

Schéma :

```text
PostgreSQL
    ↓
ProductEntity
    ↓
mapper
    ↓
Product.rehydrate(...)
    ↓
Product
```

La différence essentielle est donc :

```text
create()     = création d'un nouveau produit
rehydrate()  = reconstruction d'un produit déjà existant
```

## changeDetails(...)

`changeDetails(...)` modifie les informations éditables d'un produit.

Cette méthode passe à nouveau par les mêmes règles métier afin qu'une modification ne puisse pas rendre le produit invalide.

Elle met également à jour `updatedAt`.

## deactivate(...)

`deactivate(...)` désactive un produit sans le supprimer physiquement.

L'idée est de conserver l'historique.

Un produit déjà utilisé dans une commande ou associé à des mouvements de stock ne devrait généralement pas disparaître de la base simplement parce qu'il n'est plus vendu.

`active = false` permet donc de le retirer du catalogue actif tout en conservant sa trace.

## Pourquoi createdAt et updatedAt sont dans le domaine

Dans cette première conception, ces dates font partie de l'état métier du produit.

Elles permettent de savoir :

- quand le produit a été créé ;
- quand son état a été modifié pour la dernière fois.

Elles ne sont pas générées implicitement par JPA : le domaine reste maître de son état.

## ProductType

`ProductType` est un enum du domaine.

Exemple :

```java
public enum ProductType {
    MEAD,
    BEER
}
```

Il n'a pas besoin de `@Enumerated`.

L'annotation JPA sera placée plus tard sur le champ correspondant de `ProductEntity`, par exemple avec `EnumType.STRING`.

## Responsabilité de Product

En résumé, `Product` doit :

- représenter un produit valide ;
- protéger ses règles métier internes ;
- contrôler ses propres changements d'état ;
- rester indépendant de la persistence ;
- ne jamais accéder directement à PostgreSQL ;
- ne jamais dépendre d'un Controller ou d'un DTO HTTP.

`Product` est donc le **modèle métier**, tandis que `ProductEntity` sera sa représentation technique pour la base de données.
