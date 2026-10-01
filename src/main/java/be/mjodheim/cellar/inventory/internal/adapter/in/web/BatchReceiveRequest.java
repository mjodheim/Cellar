package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;

/**
 * HTTP payload used to receive a new physical stock batch.
 *
 * @param productId catalogue product identifier
 * @param lotNumber supplier or production lot reference
 * @param quantity initial received quantity
 * @param receivedAt physical reception timestamp
 * @param expiresOn optional expiration date
 */
record BatchReceiveRequest(
        @NotNull @Positive Long productId,
        @NotBlank @Size(max = 100) String lotNumber,
        @Min(1) int quantity,
        @NotNull Instant receivedAt,
        LocalDate expiresOn
) {}
