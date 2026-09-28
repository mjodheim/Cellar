package be.mjodheim.cellar.ordering.internal.adapter.in.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * HTTP payload used to create a draft order.
 *
 * @param customerReference optional external customer reference
 * @param lines requested order lines
 */
record CreateOrderRequest(
        @Size(max = 150) String customerReference,
        @NotEmpty List<@Valid Line> lines
) {

    /**
     * One requested order line.
     *
     * @param productId catalogue product identifier
     * @param quantity requested quantity
     */
    record Line(
            @Positive Long productId,
            @Positive int quantity
    ) {}
}
