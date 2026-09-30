package be.mjodheim.cellar.catalog.internal.adapter.in.web;

import be.mjodheim.cellar.catalog.ProductNotFoundException;
import be.mjodheim.cellar.catalog.internal.application.CreateProductService;
import be.mjodheim.cellar.catalog.internal.application.ProductAlreadyExistsException;
import be.mjodheim.cellar.catalog.internal.application.ProductQueryService;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

/**
 * REST adapter exposing catalogue product use cases.
 */
@RestController
@RequestMapping("/api/catalog/products")
@Tag(name = "Products", description = "Gestion du catalogue produit")
class ProductController {

    private final CreateProductService createProductService;
    private final ProductQueryService productQueryService;

    ProductController(
            CreateProductService createProductService,
            ProductQueryService productQueryService
    ) {
        this.createProductService = createProductService;
        this.productQueryService = productQueryService;
    }

    /**
     * Creates a new catalogue product.
     *
     * @param request validated creation payload
     * @return HTTP 201 response containing the created product
     */
    @PostMapping
    @Operation(summary = "Créer un produit")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produit créé",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requête invalide",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Un produit portant ce nom existe déjà",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        Product product = createProductService.create(
                request.name(),
                request.type(),
                request.description(),
                request.volumeMl(),
                request.price()
        );

        return ResponseEntity
                .created(URI.create("/api/catalog/products/" + product.id()))
                .body(ProductResponse.from(product));
    }

    /**
     * Lists all catalogue products.
     *
     * @return product response list
     */
    @GetMapping
    @Operation(summary = "Lister les produits")
    @ApiResponse(responseCode = "200", description = "Liste des produits")
    List<ProductResponse> findAll() {
        return productQueryService.findAll().stream()
                .map(ProductResponse::from)
                .toList();
    }

    /**
     * Retrieves one product by identifier.
     *
     * @param id product identifier
     * @return matching product response
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un produit par son identifiant")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produit trouvé",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produit introuvable",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))
            )
    })
    ProductResponse findById(@PathVariable Long id) {
        return ProductResponse.from(productQueryService.findById(id));
    }

    /**
     * Maps duplicate-product errors to HTTP 409.
     *
     * @param exception application exception
     * @return RFC 9457-style problem details
     */
    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handleProductAlreadyExists(ProductAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Product already exists");
        problem.setDetail(exception.getMessage());
        return problem;
    }

    /**
     * Maps missing-product errors to HTTP 404.
     *
     * @param exception application exception
     * @return RFC 9457-style problem details
     */
    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleProductNotFound(ProductNotFoundException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Product not found");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}
