package be.mjodheim.cellar;

import be.mjodheim.cellar.catalog.internal.application.CreateProductService;
import be.mjodheim.cellar.catalog.internal.domain.Product;
import be.mjodheim.cellar.catalog.internal.domain.ProductType;
import be.mjodheim.cellar.identity.internal.application.AuthenticationResult;
import be.mjodheim.cellar.identity.internal.application.AuthenticationService;
import be.mjodheim.cellar.identity.internal.application.InvalidRefreshTokenException;
import be.mjodheim.cellar.inventory.InsufficientStockException;
import be.mjodheim.cellar.inventory.internal.application.ReceiveBatchService;
import be.mjodheim.cellar.ordering.internal.application.CreateOrderLineCommand;
import be.mjodheim.cellar.ordering.internal.application.CreateOrderService;
import be.mjodheim.cellar.ordering.internal.application.OrderLifecycleService;
import be.mjodheim.cellar.ordering.internal.domain.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Runs the real security chain, Flyway, JPA and transaction boundaries on PostgreSQL. */
@Timeout(40)
class CellarRegressionIntegrationTest extends PostgresIntegrationSupport {

    private static final String PASSWORD = "very-secure-test-password";

    @Autowired JdbcTemplate jdbc;
    @Autowired MockMvc mvc;
    @Autowired AuthenticationService authentication;
    @Autowired CreateProductService products;
    @Autowired ReceiveBatchService batches;
    @Autowired CreateOrderService orders;
    @Autowired OrderLifecycleService lifecycle;
    @Autowired JwtDecoder jwtDecoder;
    @Autowired PlatformTransactionManager transactions;

    @BeforeEach
    void clearDisposableDatabase() {
        jdbc.execute("""
                TRUNCATE TABLE allocation, stock_movement, order_line, customer_order,
                    refresh_token, app_user, batch, product RESTART IDENTITY CASCADE
                """);
    }

    @Test
    void anonymousRequestsCannotReadOrders() throws Exception {
        mvc.perform(get("/api/orders")).andExpect(status().isUnauthorized());
    }

    @Test
    void publicRegistrationDoesNotGrantOrderManagement() throws Exception {
        String bearer = bearer(register("user@example.com"));
        mvc.perform(get("/api/orders").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/orders/1").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/orders").header(HttpHeaders.AUTHORIZATION, bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":1,\"quantity\":1}]}"))
                .andExpect(status().isForbidden());
        for (String action : List.of("confirm", "prepare", "ship", "cancel")) {
            mvc.perform(post("/api/orders/1/" + action).header(HttpHeaders.AUTHORIZATION, bearer))
                    .andExpect(status().isForbidden());
        }
        mvc.perform(delete("/api/orders/1").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void administratorsCanReadOrdersWithARealSignedToken() throws Exception {
        mvc.perform(get("/api/orders").header(HttpHeaders.AUTHORIZATION, adminBearer()))
                .andExpect(status().isOk()).andExpect(content().json("[]"));
    }

    @Test
    void jwtSubjectIsTheImmutableUserId() {
        AuthenticationResult user = register("user@example.com");
        assertEquals(user.userId().toString(), jwtDecoder.decode(user.accessToken()).getSubject());
        assertEquals(user.email(), jwtDecoder.decode(user.accessToken()).getClaimAsString("email"));
    }

    @Test
    void anOldTokenCannotReadOrDeleteAReplacementAccount() throws Exception {
        AuthenticationResult oldAccount = register("user@example.com");
        mvc.perform(delete("/api/auth/me").header(HttpHeaders.AUTHORIZATION, bearer(oldAccount)))
                .andExpect(status().isNoContent());
        AuthenticationResult replacement = register("user@example.com");
        assertNotEquals(oldAccount.userId(), replacement.userId());
        mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, bearer(oldAccount)))
                .andExpect(status().isUnauthorized());
        mvc.perform(delete("/api/auth/me").header(HttpHeaders.AUTHORIZATION, bearer(oldAccount)))
                .andExpect(status().isUnauthorized());
        mvc.perform(get("/api/auth/me").header(HttpHeaders.AUTHORIZATION, bearer(replacement)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.id").value(replacement.userId().intValue()));
    }

    @Test
    void disablingAnAccountImmediatelyInvalidatesItsAccessToken() throws Exception {
        AuthenticationResult user = register("user@example.com");
        jdbc.update("UPDATE app_user SET enabled = false WHERE id = ?", user.userId());
        mvc.perform(get("/api/catalog/products").header(HttpHeaders.AUTHORIZATION, bearer(user)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void onlyOneConcurrentRefreshCanConsumeTheSameToken() throws Exception {
        AuthenticationResult user = register("user@example.com");
        Callable<Boolean> refresh = () -> {
            try {
                authentication.refresh(user.refreshToken());
                return true;
            } catch (InvalidRefreshTokenException expected) {
                return false;
            }
        };
        List<Boolean> successes = concurrentlyWhileLocked("app_user", user.userId(), refresh, refresh);
        assertEquals(1L, successes.stream().filter(Boolean::booleanValue).count());
        assertEquals(1L, count("SELECT COUNT(*) FROM refresh_token WHERE revoked_at IS NULL"));
        assertThrows(InvalidRefreshTokenException.class, () -> authentication.refresh(user.refreshToken()));
    }

    @Test
    void concurrentOrdersCannotReserveMoreThanTheAvailableStock() throws Exception {
        Product product = product("Hydromel");
        var batch = batches.receive(product.id(), "LOT", 10, Instant.now(), null);
        Order first = order(product.id(), 6);
        Order second = order(product.id(), 6);
        List<Boolean> successes = concurrentlyWhileLocked("batch", batch.id(),
                () -> confirm(first.id()), () -> confirm(second.id()));
        assertEquals(1L, successes.stream().filter(Boolean::booleanValue).count());
        assertEquals(6L, count("SELECT SUM(quantity) FROM allocation WHERE status = 'RESERVED'"));
        assertEquals(6L, count("SELECT quantity_reserved FROM batch WHERE id = " + batch.id()));
        assertEquals(1L, count("SELECT COUNT(*) FROM customer_order WHERE status = 'CONFIRMED'"));
        assertEquals(1L, count("SELECT COUNT(*) FROM customer_order WHERE status = 'DRAFT'"));
    }

    @Test
    void simultaneousConfirmationOfOneOrderDoesNotAllocateTwice() throws Exception {
        Product product = product("Hydromel");
        batches.receive(product.id(), "LOT", 10, Instant.now(), null);
        Order order = order(product.id(), 2);
        List<Boolean> successes = concurrentlyWhileLocked("customer_order", order.id(),
                () -> transition(() -> lifecycle.confirm(order.id())),
                () -> transition(() -> lifecycle.confirm(order.id())));
        assertEquals(1L, successes.stream().filter(Boolean::booleanValue).count());
        assertEquals(2L, count("SELECT SUM(quantity) FROM allocation WHERE status = 'RESERVED'"));
        assertEquals(2L, count("SELECT quantity_reserved FROM batch"));
    }

    @Test
    void simultaneousShipmentDoesNotConsumeStockOrWriteItsLedgerTwice() throws Exception {
        Product product = product("Hydromel");
        batches.receive(product.id(), "LOT", 10, Instant.now(), null);
        Order order = order(product.id(), 2);
        lifecycle.confirm(order.id());
        lifecycle.startPreparation(order.id());
        List<Boolean> successes = concurrentlyWhileLocked("customer_order", order.id(),
                () -> transition(() -> lifecycle.ship(order.id())),
                () -> transition(() -> lifecycle.ship(order.id())));
        assertEquals(1L, successes.stream().filter(Boolean::booleanValue).count());
        assertEquals(8L, count("SELECT quantity_on_hand FROM batch"));
        assertEquals(0L, count("SELECT quantity_reserved FROM batch"));
        assertEquals(1L, count("SELECT COUNT(*) FROM stock_movement WHERE type = 'SHIPMENT'"));
    }

    @Test
    void lockedBatchesStillAllocateInFefoOrder() {
        Product product = product("Hydromel");
        Instant received = Instant.now();
        LocalDate day = received.atZone(ZoneOffset.UTC).toLocalDate();
        var later = batches.receive(product.id(), "LATER", 2, received, day.plusDays(10));
        var earlier = batches.receive(product.id(), "EARLIER", 2, received, day.plusDays(2));
        lifecycle.confirm(order(product.id(), 3).id());
        assertEquals(2L, count("SELECT quantity_reserved FROM batch WHERE id = " + earlier.id()));
        assertEquals(1L, count("SELECT quantity_reserved FROM batch WHERE id = " + later.id()));
    }

    @Test
    void databaseEnforcesCaseInsensitiveProductNameUniqueness() {
        Product product = product("Hydromel");
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("""
                INSERT INTO product(name, type, description, volume_ml, price, active, created_at, updated_at)
                SELECT UPPER(name), type, description, volume_ml, price, active, created_at, updated_at
                FROM product WHERE id = ?
                """, product.id()));
        assertEquals(1L, count("SELECT COUNT(*) FROM product"));
    }

    @Test
    void orderErrorsHaveTheirExpectedHttpStatuses() throws Exception {
        String bearer = adminBearer();
        mvc.perform(post("/api/orders").header(HttpHeaders.AUTHORIZATION, bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"lines\":[{\"productId\":999,\"quantity\":1}]}"))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.title").value("Product not found"));
        Product product = product("Hydromel");
        Order order = order(product.id(), 1);
        mvc.perform(post("/api/orders/" + order.id() + "/confirm").header(HttpHeaders.AUTHORIZATION, bearer))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.title").value("Insufficient stock"));
        assertEquals(1L, count("SELECT COUNT(*) FROM customer_order WHERE status = 'DRAFT'"));
        assertEquals(0L, count("SELECT COUNT(*) FROM allocation"));
    }

    @Test
    void nullOrderLinesAndMissingProductIdsAreRejectedBeforeMapping() throws Exception {
        String bearer = adminBearer();
        for (String body : List.of("{\"lines\":[null]}", "{\"lines\":[{\"quantity\":1}]}")) {
            mvc.perform(post("/api/orders").header(HttpHeaders.AUTHORIZATION, bearer)
                            .contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    void receivingAnUnknownProductReturns404() throws Exception {
        mvc.perform(post("/api/inventory/batches").header(HttpHeaders.AUTHORIZATION, adminBearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\":999,\"lotNumber\":\"LOT\",\"quantity\":1,\"receivedAt\":\"2026-09-28T12:00:00Z\"}"))
                .andExpect(status().isNotFound());
        assertEquals(0L, count("SELECT COUNT(*) FROM batch"));
    }

    @Test
    void pricesWithMoreThanTwoDecimalPlacesAreRejected() throws Exception {
        mvc.perform(post("/api/catalog/products").header(HttpHeaders.AUTHORIZATION, adminBearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Hydromel\",\"type\":\"MEAD\",\"volumeMl\":750,\"price\":1.999}"))
                .andExpect(status().isBadRequest());
    }

    private AuthenticationResult register(String email) {
        return authentication.register(email, "User", PASSWORD);
    }

    private String adminBearer() {
        AuthenticationResult user = register("admin@example.com");
        jdbc.update("UPDATE app_user SET role = 'ADMIN' WHERE id = ?", user.userId());
        return bearer(authentication.login(user.email(), PASSWORD));
    }

    private static String bearer(AuthenticationResult user) {
        return "Bearer " + user.accessToken();
    }

    private Product product(String name) {
        return products.create(name, ProductType.MEAD, null, 750, new BigDecimal("14.90"));
    }

    private Order order(Long productId, int quantity) {
        return orders.create("CLIENT", List.of(new CreateOrderLineCommand(productId, quantity)));
    }

    private boolean confirm(Long id) {
        try {
            lifecycle.confirm(id);
            return true;
        } catch (InsufficientStockException expected) {
            return false;
        }
    }

    private static boolean transition(Callable<Order> transition) throws Exception {
        try {
            transition.call();
            return true;
        } catch (IllegalStateException expected) {
            return false;
        }
    }

    private long count(String sql) {
        Long result = jdbc.queryForObject(sql, Long.class);
        assertNotNull(result);
        return result;
    }

    /** Queue two real transactions behind one row lock before releasing them. */
    private <T> List<T> concurrentlyWhileLocked(String table, Long id,
                                              Callable<T> first, Callable<T> second) throws Exception {
        assertTrue(List.of("app_user", "batch", "customer_order").contains(table));
        var executor = Executors.newFixedThreadPool(2);
        List<Future<T>> results = new ArrayList<>();
        try {
            new TransactionTemplate(transactions).executeWithoutResult(status -> {
                jdbc.queryForObject("SELECT id FROM " + table + " WHERE id = ? FOR UPDATE", Long.class, id);
                CountDownLatch ready = new CountDownLatch(2);
                CountDownLatch start = new CountDownLatch(1);
                for (Callable<T> task : List.of(first, second)) {
                    results.add(executor.submit(() -> {
                        ready.countDown();
                        assertTrue(start.await(5, TimeUnit.SECONDS));
                        return task.call();
                    }));
                }
                try {
                    assertTrue(ready.await(5, TimeUnit.SECONDS));
                    start.countDown();
                    awaitDatabaseWaiters();
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    throw new AssertionError(exception);
                }
            });
            return List.of(results.get(0).get(15, TimeUnit.SECONDS), results.get(1).get(15, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
            assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS));
        }
    }

    private void awaitDatabaseWaiters() throws InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
        while (System.nanoTime() < deadline) {
            jdbc.execute("SELECT pg_stat_clear_snapshot()");
            if (count("""
                    SELECT COUNT(*) FROM pg_stat_activity
                    WHERE datname = current_database() AND wait_event_type = 'Lock'
                    """) >= 2) {
                return;
            }
            Thread.sleep(25);
        }
        fail("Both transactions must wait for the database lock before the test releases it");
    }
}
