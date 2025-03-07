package fun.vs.fw.demo.domain;


import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;

import java.math.BigDecimal;
import java.util.Objects;


record Transaction1(
        String transactionId,
        String clientId,
        String accountId,
        BigDecimal amount,
        String mcc
) {}

public record Transaction(
        TransactionId transactionId,
        ClientId clientId,
        AccountId accountId,
        Amount amount,
        MerchantCategoryCode mcc
) {

    public Transaction {
        Objects.requireNonNull(transactionId, "Transaction ID cannot be null");
        Objects.requireNonNull(clientId, "Client ID cannot be null");
        Objects.requireNonNull(accountId, "Account ID cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(mcc, "Merchant category code cannot be null");
    }

    public CategorizedTransaction toCategorizedTransaction(ExpenseCategory expenseCategory) {
        return new CategorizedTransaction(
                null,
                transactionId(),
                clientId(),
                accountId(),
                amount(),
                expenseCategory);
    }

    /**
     * Represents a unique identifier for a transaction.
     * This identifier is used to distinguish transactions within the system.
     * It is immutable and encapsulates a single string value.
     * <p/>
     * Inline classes, also known as value classes, provide a way to create immutable and memory-efficient data structures.
     * This will improve performance and reduce memory overhead by avoiding object allocation and enabling better
     * optimizations by the JVM. When <a href="https://openjdk.org/projects/valhalla/">Project Valhalla</a> is released, consider refactoring this class to take advantage
     * of these benefits.
     */
    public record TransactionId(String value) {
        public TransactionId {
            Objects.requireNonNull(value, "Transaction ID cannot be null");
            if (value.isBlank()) {
                throw new IllegalArgumentException("Transaction ID cannot be null or blank");
            }
        }
    }

    public record ClientId(String value) {
        public ClientId {
            Objects.requireNonNull(value, "Client ID cannot be null");
            if (value.isBlank()) {
                throw new IllegalArgumentException("Client ID cannot be null or blank");
            }
        }
    }

    public record AccountId(String value) {
        public AccountId {
            Objects.requireNonNull(value, "Account ID cannot be null");
            if (value.isBlank()) {
                throw new IllegalArgumentException("Account ID cannot be null or blank");
            }
        }
    }

    public record Amount(BigDecimal value) {
        public Amount {
            Objects.requireNonNull(value, "Amount cannot be null");
        }
    }

    public record MerchantCategoryCode(String value) {
        public MerchantCategoryCode {
            Objects.requireNonNull(value, "Merchant category code cannot be null");
            if (!value.matches("\\d{4}")) {
                throw new IllegalArgumentException("Merchant category code must be exactly 4 digits");
            }
        }
    }
}
