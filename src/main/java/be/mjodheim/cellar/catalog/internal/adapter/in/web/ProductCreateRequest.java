package be.mjodheim.cellar.catalog.internal.adapter.in.web;

import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * HTTP payload used to create a catalogue product.
 *
 * <p>Bean Validation protects the shape of the request. Business invariants are
 * still enforced by the domain model.</p>
 *
 * @param name product name
 * @param type product family
 * @param description optional commercial description
 * @param volumeMl container volume in millilitres
 * @param price unit price, zero or positive
 */
record ProductCreateRequest(

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        ProductType type,

        @Size(max = 2000)
        String description,

        @Min(1)
        int volumeMl,

        @NotNull
        @DecimalMin(value = "0.00")
        BigDecimal price
) {
}
