package fvf4j.demo;

import fvf4j.demo.controller.CategorizedTransactionResponse;
import fvf4j.demo.domain.CategoryBudget;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Import(TestcontainersConfiguration.class)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql-test-data/categorized-transactions.sql")
class TransactionCategorizerControllerIT {

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.0")
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void wiremockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.url", wireMockContainer::getBaseUrl);
    }

    @Value("${wiremock.server.url}")
    private String wiremockUrl;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldReturnTransactionsByClientAndCategory() {
        ResponseEntity<List<CategorizedTransactionResponse>> response = restTemplate.exchange(
                "/client/client-001/transactions?category=GROCERIES",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody())
                .extracting(CategorizedTransactionResponse::transactionId)
                .containsExactlyInAnyOrder("txn-001", "txn-002");
        assertThat(response.getBody())
                .extracting(CategorizedTransactionResponse::expenseCategory)
                .containsOnly("GROCERIES");
    }

    @Test
    void shouldReturnEmptyListWhenNoTransactionsMatchCategory() {
        ResponseEntity<List<CategorizedTransactionResponse>> response = restTemplate.exchange(
                "/client/client-001/transactions?category=TRAVEL",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturnBudgetsByCategory() {
        ResponseEntity<List<CategoryBudget>> response = restTemplate.exchange(
                "/client/client-001/categories-budget",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(6);

        var groceriesBudget = response.getBody().stream()
                .filter(budget -> "GROCERIES".equals(budget.category()))
                .findFirst()
                .orElseThrow();

        assertThat(groceriesBudget.totalAmount()).isEqualTo(BigDecimal.valueOf(41));
    }

    @Test
    void shouldReturnEmptyBudgetListForClientWithNoTransactions() {
        ResponseEntity<List<CategoryBudget>> response = restTemplate.exchange(
                "/client/client-999/categories-budget",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturnDistinctExpenseCategories() {
        ResponseEntity<List<String>> response = restTemplate.exchange(
                "/client/client-001/categories",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(7);
        assertThat(response.getBody()).containsExactlyInAnyOrder(
                "GROCERIES", "TRANSPORTATION", "RENT", "UTILITIES", "ENTERTAINMENT", "DINING", "HEALTHCARE"
        );
    }

    @Test
    void shouldReturnEmptyCategoriesListForNonExistentClient() {
        ResponseEntity<List<String>> response = restTemplate.exchange(
                "/client/non-existent-client/categories",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturnTransactionsForDifferentClient() {
        ResponseEntity<List<CategorizedTransactionResponse>> response = restTemplate.exchange(
                "/client/client-002/transactions?category=RENT",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().getFirst().transactionId()).isEqualTo("txn-011");
    }
}
