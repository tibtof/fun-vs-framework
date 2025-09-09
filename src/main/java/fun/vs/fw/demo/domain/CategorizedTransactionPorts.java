package fun.vs.fw.demo.domain;


import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;
import fun.vs.fw.demo.domain.Transaction.ClientId;
import fun.vs.fw.demo.domain.Transaction.TransactionId;

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
