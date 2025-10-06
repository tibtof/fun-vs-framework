package fvf4j.demo.domain;


import fvf4j.demo.domain.CategorizedTransaction.ExpenseCategory;
import fvf4j.demo.domain.Transaction.ClientId;
import fvf4j.demo.domain.Transaction.TransactionId;

import java.util.List;
import java.util.Optional;


public interface CategorizedTransactionPorts {

    @FunctionalInterface
    interface SaveCategorizedTransaction {
        CategorizedTransaction save(CategorizedTransaction categorizedTransaction);
    }

    @FunctionalInterface
    interface FindByTransactionId {
        Optional<CategorizedTransaction> findBy(TransactionId transactionId);
    }

    @FunctionalInterface
    interface FindByClientIdAndExpenseCategory {
        List<CategorizedTransaction> findBy(ClientId clientId, ExpenseCategory expenseCategory);
    }

    @FunctionalInterface
    interface FindBudgetsByCategory {
        List<CategoryBudget> findBudgetsByCategory(ClientId clientId);
    }

    @FunctionalInterface
    interface FindExpenseCategoriesByClient {
        List<ExpenseCategory> findExpenseCategoriesBy(ClientId clientId);
    }

    interface CategorizedTransactionRepository extends SaveCategorizedTransaction,
            FindByTransactionId,
            FindByClientIdAndExpenseCategory,
            FindBudgetsByCategory,
            FindExpenseCategoriesByClient {}
}
