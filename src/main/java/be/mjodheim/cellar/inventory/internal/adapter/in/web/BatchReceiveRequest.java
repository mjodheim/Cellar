package be.mjodheim.cellar.inventory.internal.adapter.in.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;
import java.time.LocalDate;

record BatchReceiveRequest(
        @NotNull @Positive Long productId,
        @NotBlank String lotNumber,
        @Min(1) int quantity,
        @NotNull Instant receivedAt,
        LocalDate expiresOn
) {}
