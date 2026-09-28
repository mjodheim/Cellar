package be.mjodheim.cellar.ordering.internal.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

record CreateOrderRequest(
        @Size(max = 150) String customerReference,
        @NotEmpty List<@Valid Line> lines
) {
    record Line(
            @Positive Long productId,
            @Positive int quantity
    ) {}
}
