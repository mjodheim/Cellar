# 📚 StockMovement

> `StockMovement` est le **ledger immuable** qui explique pourquoi le stock physique a changé.

---

## 🎯 Pourquoi un ledger ?

```text
Batch.quantityOnHand
└── répond à : "Combien reste-t-il ?"

StockMovement
└── répond à : "Pourquoi cette quantité a-t-elle changé ?"
```

Les deux sont complémentaires : état courant rapide + historique auditable.

---

## 🧾 Types de mouvements

| Type | Signification |
| --- | --- |
| `RECEIPT` | réception |
| `SHIPMENT` | expédition |
| `ADJUSTMENT_IN` | correction positive |
| `ADJUSTMENT_OUT` | correction négative |
| `WASTE` | perte, casse ou périmé |
| `RETURN` | retour en stock |

---

## ➕ Quantités positives

La quantité est toujours strictement positive.

La direction du mouvement est portée par le type, pas par un signe `+` ou `-`.

Cela évite les ambiguïtés.

---

## 🔒 Immutabilité

Un mouvement historique n'est pas réécrit après coup.

S'il faut corriger le stock, on crée un **nouveau mouvement** qui explique la correction.

---

## 🔗 Voir aussi

- [Batch](BATCH.md)
- [Allocation](ALLOCATION.md)
