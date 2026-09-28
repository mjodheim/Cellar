package be.mjodheim.cellar.catalog.internal.adapter.in.web;

import be.mjodheim.cellar.catalog.internal.application.CreateProductService;
import be.mjodheim.cellar.catalog.internal.application.ProductAlreadyExistsException;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/catalog/products")
class ProductController {

    private final CreateProductService createProductService;

    ProductController(CreateProductService createProductService) {
        this.createProductService = createProductService;
    }

    @PostMapping
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

    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handleProductAlreadyExists(ProductAlreadyExistsException exception) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problem.setTitle("Product already exists");
        problem.setDetail(exception.getMessage());
        return problem;
    }
}
