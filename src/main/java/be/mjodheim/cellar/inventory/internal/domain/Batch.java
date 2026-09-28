package be.mjodheim.cellar.inventory.internal.domain;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public final class Batch {

    private final Long id;
    private final Long productId;
    private final String lotNumber;
    private final int receivedQuantity;
    private final Instant receivedAt;
    private final LocalDate expiresOn;
    private final Instant createdAt;

    private int quantityOnHand;
    private int quantityReserved;
    private Instant updatedAt;
    private Instant deletedAt;

    private Batch(
            Long id,
            Long productId,
            String lotNumber,
            int receivedQuantity,
            int quantityOnHand,
            int quantityReserved,
            Instant receivedAt,
            LocalDate expiresOn,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException("Product id must be positive");
        }
        if (lotNumber == null || lotNumber.isBlank()) {
            throw new IllegalArgumentException("Lot number is required");
        }
        if (receivedQuantity <= 0) {
            throw new IllegalArgumentException("Received quantity must be greater than zero");
        }
        if (quantityOnHand < 0) {
            throw new IllegalArgumentException("Quantity on hand cannot be negative");
        }
        if (quantityReserved < 0 || quantityReserved > quantityOnHand) {
            throw new IllegalArgumentException("Reserved quantity must be between zero and quantity on hand");
        }

        this.id = id;
        this.productId = productId;
        this.lotNumber = lotNumber.trim();
        this.receivedQuantity = receivedQuantity;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = quantityReserved;
        this.receivedAt = Objects.requireNonNull(receivedAt, "Received date is required");
        this.expiresOn = expiresOn;
        this.createdAt = Objects.requireNonNull(createdAt, "Creation date is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "Update date is required");
        this.deletedAt = deletedAt;
    }

    public static Batch receive(
            Long productId,
            String lotNumber,
            int quantity,
            Instant receivedAt,
            LocalDate expiresOn,
            Instant now
    ) {
        Objects.requireNonNull(now, "Current date is required");

        if (expiresOn != null && expiresOn.isBefore(receivedAt.atZone(java.time.ZoneOffset.UTC).toLocalDate())) {
            throw new IllegalArgumentException("Expiration date cannot be before reception date");
        }

        return new Batch(
                null,
                productId,
                lotNumber,
                quantity,
                quantity,
                0,
                receivedAt,
                expiresOn,
                now,
                now,
                null
        );
    }

    public static Batch rehydrate(
            Long id,
            Long productId,
            String lotNumber,
            int receivedQuantity,
            int quantityOnHand,
            int quantityReserved,
            Instant receivedAt,
            LocalDate expiresOn,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt
    ) {
        return new Batch(
                id,
                productId,
                lotNumber,
                receivedQuantity,
                quantityOnHand,
                quantityReserved,
                receivedAt,
                expiresOn,
                createdAt,
                updatedAt,
                deletedAt
        );
    }

    public void reserve(int quantity, Instant now) {
        ensureNotDeleted();
        requirePositive(quantity);

        if (quantity > availableQuantity()) {
            throw new IllegalStateException("Not enough available stock to reserve");
        }

        quantityReserved += quantity;
        updatedAt = Objects.requireNonNull(now);
    }

    public void release(int quantity, Instant now) {
        ensureNotDeleted();
        requirePositive(quantity);

        if (quantity > quantityReserved) {
            throw new IllegalStateException("Cannot release more than reserved quantity");
        }

        quantityReserved -= quantity;
        updatedAt = Objects.requireNonNull(now);
    }

    public void shipReserved(int quantity, Instant now) {
        ensureNotDeleted();
        requirePositive(quantity);

        if (quantity > quantityReserved) {
            throw new IllegalStateException("Cannot ship more than reserved quantity");
        }

        quantityReserved -= quantity;
        quantityOnHand -= quantity;
        updatedAt = Objects.requireNonNull(now);
    }

    public void addStock(int quantity, Instant now) {
        ensureNotDeleted();
        requirePositive(quantity);

        quantityOnHand += quantity;
        updatedAt = Objects.requireNonNull(now);
    }

    public void removeAvailableStock(int quantity, Instant now) {
        ensureNotDeleted();
        requirePositive(quantity);

        if (quantity > availableQuantity()) {
            throw new IllegalStateException("Cannot remove more than available quantity");
        }

        quantityOnHand -= quantity;
        updatedAt = Objects.requireNonNull(now);
    }

    public void softDelete(Instant now) {
        if (deletedAt != null) {
            return;
        }
        if (quantityOnHand != 0 || quantityReserved != 0) {
            throw new IllegalStateException("A batch with stock or reservations cannot be deleted");
        }

        deletedAt = Objects.requireNonNull(now);
        updatedAt = now;
    }

    public int availableQuantity() {
        return quantityOnHand - quantityReserved;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }

    private void ensureNotDeleted() {
        if (isDeleted()) {
            throw new IllegalStateException("Deleted batch cannot be modified");
        }
    }

    private static void requirePositive(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    public Long id() { return id; }
    public Long productId() { return productId; }
    public String lotNumber() { return lotNumber; }
    public int receivedQuantity() { return receivedQuantity; }
    public int quantityOnHand() { return quantityOnHand; }
    public int quantityReserved() { return quantityReserved; }
    public Instant receivedAt() { return receivedAt; }
    public LocalDate expiresOn() { return expiresOn; }
    public Instant createdAt() { return createdAt; }
    public Instant updatedAt() { return updatedAt; }
    public Instant deletedAt() { return deletedAt; }
}
