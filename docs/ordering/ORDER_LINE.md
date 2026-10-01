# 🧮 OrderLine

> `OrderLine` est une ligne **immuable** représentant le snapshot commercial d'un produit dans une commande.

---

## 🧾 Données conservées

| Champ | Rôle |
| --- | --- |
| `productId` | identifiant du produit |
| `productName` | nom au moment de la commande |
| `quantity` | quantité commandée |
| `unitPrice` | prix au moment de la commande |
| `createdAt` | création de la ligne |

---

## 💰 Total

```text
total = unitPrice × quantity
```

Le total est calculé à partir des valeurs figées dans la ligne.

---

## 📸 Pourquoi un snapshot ?

Supposons qu'un produit coûte 12 € lors de la commande et passe ensuite à 14 €.

L'ancienne commande doit continuer à afficher **12 €**.

C'est pour cela que la ligne ne dépend pas dynamiquement du prix actuel du catalogue.

---

## 🔒 Immutabilité

Une `OrderLine` ne change pas après sa création.

Cette simplicité facilite :

- la traçabilité ;
- les calculs historiques ;
- les audits ;
- les futures générations de documents.

---

## 🔗 Voir aussi

- [Order](ORDER.md)
- [Product](../catalog/PRODUCT.md)
