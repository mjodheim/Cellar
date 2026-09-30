package be.mjodheim.cellar;

import be.mjodheim.cellar.catalog.ProductNotFoundException;
import be.mjodheim.cellar.inventory.InsufficientStockException;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/** HTTP error mapping shared by adapters, including cross-module failures. */
@RestControllerAdvice
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<ProblemDetail> missingProduct(ProductNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Product not found", exception.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    ResponseEntity<ProblemDetail> insufficientStock(InsufficientStockException exception) {
        return problem(HttpStatus.CONFLICT, "Insufficient stock", exception.getMessage());
    }

    @ExceptionHandler(ConcurrencyFailureException.class)
    ResponseEntity<ProblemDetail> concurrentChange(ConcurrencyFailureException exception) {
        return problem(HttpStatus.CONFLICT, "Concurrent change",
                "The data changed during this operation. Reload it and retry the request.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> conflictingData(DataIntegrityViolationException exception) {
        return problem(HttpStatus.CONFLICT, "Data conflict",
                "The request conflicts with existing data or a database constraint.");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ProblemDetail> invalidRequest(IllegalArgumentException exception) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", exception.getMessage());
    }

    private static ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        return ResponseEntity.status(status).body(problem);
    }
}
