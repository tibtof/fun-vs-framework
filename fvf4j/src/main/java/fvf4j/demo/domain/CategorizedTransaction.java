package fvf4j.demo.domain;


import fvf4j.demo.domain.Transaction.AccountId;
import fvf4j.demo.domain.Transaction.Amount;
import fvf4j.demo.domain.Transaction.ClientId;
import fvf4j.demo.domain.Transaction.TransactionId;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

public record CategorizedTransaction(
        CategorizedTransactionId id,
        TransactionId transactionId,
        ClientId clientId,
        AccountId accountId,
        Amount amount,
        ExpenseCategory expenseCategory) {

    public CategorizedTransaction {
        requireNonNull(transactionId, "Transaction ID cannot be null");
        requireNonNull(clientId, "Client ID cannot be null");
        requireNonNull(accountId, "Account ID cannot be null");
        requireNonNull(amount, "Transaction Amount cannot be null");
        requireNonNull(expenseCategory, "Expense Category cannot be null");
    }

    public CategorizedTransaction withId(CategorizedTransactionId id) {
        return new CategorizedTransaction(id, transactionId, clientId, accountId, amount, expenseCategory);
    }

    public record CategorizedTransactionId(Long value) {
        public CategorizedTransactionId {
            Objects.requireNonNull(value, "Transaction ID cannot be null");
        }
    }

    public record ExpenseCategory(String value) {
        public ExpenseCategory {
            requireNonNull(value, "Expense category cannot be null");
            if (value.isBlank()) {
                throw new IllegalArgumentException("Expense category cannot be null or blank");
            }
        }
    }
}

