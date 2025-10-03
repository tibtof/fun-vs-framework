package fun.vs.fw.demo.domain;

import fun.vs.fw.demo.domain.CategorizedTransaction.CategorizedTransactionId;
import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;
import fun.vs.fw.demo.domain.Transaction.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

class TransactionCategorizerTest {

    /**
     * Tests the functionality of categorizing a new transaction in the TransactionCategorizationService.
     * This method verifies that when a transaction is processed:
     * - The MerchantDirectoryService is called to retrieve the expense category for the transaction's merchant.
     * - The CategorizedTransactionRepository is queried to check if the transaction already exists.
     * - The transaction is saved with the expected expense category when it does not exist in the repository.
     * <p>
     * The following assertions ensure the correctness of the implementation:
     * - The categorized transaction's ID is correctly assigned after being saved.
     * - The categorized transaction retains the transaction ID from the input TransactionMessage.
     */
    @DisplayName("Should categorize new transactions correctly")
    @Test
    void should_categorized_new_transactions() {
        final var transaction = createSampleTransaction();
        final var categorizedTransactionId = new CategorizedTransactionId(1L);
        final var transactionCategorizer = TransactionCategorizer.create(
                (categorizedTransaction) -> categorizedTransaction.withId(categorizedTransactionId),
                (transactionId) -> Optional.empty(),
                (mcc) -> new ExpenseCategory("Transportation")
        );

        final var result = transactionCategorizer.categorize(transaction);

        Assertions.assertEquals(categorizedTransactionId, result.id());
        Assertions.assertEquals(transaction.transactionId(), result.transactionId());
        Assertions.assertEquals("Transportation", result.expenseCategory().value());
    }

    /**
     * Tests the functionality of re-categorizing an existing transaction in the TransactionCategorizationService.
     * This method verifies the following:
     * - The MerchantDirectoryService is called to retrieve the expense category for the transaction's merchant.
     * - The CategorizedTransactionRepository is queried to locate the existing transaction by its transaction ID.
     * - The existing transaction is updated with the new expense category from the MerchantDirectoryService.
     * - The updated transaction is correctly saved and returned.
     * <p>
     * The assertions ensure:
     * - The ID of the categorized transaction remains unchanged after updating.
     * - The transaction retains the transaction ID provided in the input TransactionMessage.
     * - The new expense category is correctly applied to the transaction.
     */
    @DisplayName("Should re-categorize existing transactions correctly")
    @Test
    void should_re_toCategorizedTransaction_existing_transactions() {
        final var transaction = createSampleTransaction();
        final var existingTransaction = transaction
                .toCategorizedTransaction(new ExpenseCategory("Transportation"))
                .withId(new CategorizedTransactionId(1L));
        final var service = TransactionCategorizer.create(
                (categorizedTransaction) ->
                        categorizedTransaction.withId(new CategorizedTransactionId(1L)),
                (transactionId) -> Optional.of(existingTransaction),
                (mcc) -> new ExpenseCategory("Commuting")
        );

        final var result = service.categorize(transaction);

        Assertions.assertEquals(1L, result.id().value());
        Assertions.assertEquals(transaction.transactionId(), result.transactionId());
        Assertions.assertEquals("Commuting", result.expenseCategory().value());
    }

    private Transaction createSampleTransaction() {
        return new Transaction(
                new TransactionId(UUID.randomUUID().toString()),
                new ClientId(UUID.randomUUID().toString()),
                new AccountId(UUID.randomUUID().toString()),
                new Amount(new BigDecimal("190.00")),
                new MerchantCategoryCode("1000")
        );
    }
}
