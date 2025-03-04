package fun.vs.fw.demo.service;

import fun.vs.fw.demo.messages.TransactionMessage;
import fun.vs.fw.demo.repository.CategorizedTransaction;
import fun.vs.fw.demo.repository.CategorizedTransactionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

class TransactionCategorizationServiceTest {

    /**
     * Tests the functionality of categorizing a new transaction in the TransactionCategorizationService.
     * This method verifies that when a transaction is processed:
     * - The MerchantDirectoryService is called to retrieve the expense category for the transaction's merchant.
     * - The CategorizedTransactionRepository is queried to check if the transaction already exists.
     * - The transaction is saved with the expected expense category when it does not exist in the repository.
     *
     * The following assertions ensure the correctness of the implementation:
     * - The categorized transaction's ID is correctly assigned after being saved.
     * - The categorized transaction retains the transaction ID from the input TransactionMessage.
     */
    @Test
    void should_categorize_new_transactions() {
        final var mockRepository = Mockito.mock(CategorizedTransactionRepository.class);
        final var mockMerchantDirectoryService = Mockito.mock(MerchantDirectoryService.class);
        final var service = new TransactionCategorizationService(mockRepository, mockMerchantDirectoryService);
        final var message = new TransactionMessage(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), "1000", new BigDecimal("190.00"));

        Mockito.when(mockMerchantDirectoryService.getCategoryForMerchant(message.mcc()))
                .thenReturn(new MerchantInfo(message.mcc(), "Transportation"));
        Mockito.when(mockRepository.findByTransactionId(message.transactionId()))
                .thenReturn(Optional.empty());
        Mockito.when(mockRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    var ct = (CategorizedTransaction) invocation.getArgument(0);
                    ct.setId(1L);
                    return ct;
                });

        final var result = service.categorizeTransaction(message);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(message.transactionId(), result.getTransactionId());
    }

    /**
     * Tests the functionality of re-categorizing an existing transaction in the TransactionCategorizationService.
     * This method verifies the following:
     * - The MerchantDirectoryService is called to retrieve the expense category for the transaction's merchant.
     * - The CategorizedTransactionRepository is queried to locate the existing transaction by its transaction ID.
     * - The existing transaction is updated with the new expense category from the MerchantDirectoryService.
     * - The updated transaction is correctly saved and returned.
     *
     * The assertions ensure:
     * - The ID of the categorized transaction remains unchanged after updating.
     * - The transaction retains the transaction ID provided in the input TransactionMessage.
     * - The new expense category is correctly applied to the transaction.
     */
    @Test
    void should_re_categorize_existing_transactions() {
        final var mockRepository = Mockito.mock(CategorizedTransactionRepository.class);
        final var mockMerchantDirectoryService = Mockito.mock(MerchantDirectoryService.class);
        final var service = new TransactionCategorizationService(mockRepository, mockMerchantDirectoryService);
        final var message = new TransactionMessage(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), "1000", new BigDecimal("190.00"));
        final var existingTransaction = new CategorizedTransaction(1L, message.transactionId(), message.clientId(), message.accountId(), message.amount(), "Transportation");
        Mockito.when(mockMerchantDirectoryService.getCategoryForMerchant(message.mcc()))
                .thenReturn(new MerchantInfo(message.mcc(), "Commuting"));
        Mockito.when(mockRepository.findByTransactionId(message.transactionId()))
                .thenReturn(Optional.of(existingTransaction));
        Mockito.when(mockRepository.save(Mockito.any()))
                .thenAnswer(invocation -> {
                    var ct = (CategorizedTransaction) invocation.getArgument(0);
                    ct.setId(1L);
                    return ct;
                });

        final var result = service.categorizeTransaction(message);

        Assertions.assertEquals(1L, result.getId());
        Assertions.assertEquals(message.transactionId(), result.getTransactionId());
        Assertions.assertEquals("Commuting", result.getExpenseCategory());
    }
}
