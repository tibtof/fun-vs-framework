package fun.vs.fw.demo.kafka;


import fun.vs.fw.demo.domain.Transaction;
import fun.vs.fw.demo.domain.Transaction.*;

import java.math.BigDecimal;

/**
 * Represents a transaction message containing details about a specific transaction.
 * <p>
 * Instances of this record are typically used to encapsulate transaction data for
 * processing, categorization, or transmission across systems, such as receiving messages
 * from messaging platforms like Kafka.
 * <p>
 * Fields include:
 * - transactionId: A unique identifier for the transaction.
 * - accountId: The ID of the account associated with the transaction.
 * - clientId: The ID of the client performing the transaction.
 * - mcc: The Merchant Category Code representing the type of business where the transaction occurred.
 * - amount: The monetary value of the transaction.
 */
public record TransactionMessage(
        String transactionId,
        String accountId,
        String clientId,
        String mcc,
        BigDecimal amount) {

    public Transaction toTransaction() {
        return new Transaction(
                new TransactionId(transactionId),
                new ClientId(clientId),
                new AccountId(accountId),
                new Amount(amount),
                new MerchantCategoryCode(mcc)
        );
    }
}
