package fvf4j.demo.jpa;


import fvf4j.demo.domain.CategorizedTransaction;
import fvf4j.demo.domain.CategorizedTransaction.ExpenseCategory;
import fvf4j.demo.domain.CategorizedTransactionPorts.*;
import fvf4j.demo.domain.CategoryBudget;
import fvf4j.demo.domain.Transaction.ClientId;
import fvf4j.demo.domain.Transaction.TransactionId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorizedTransactionRepositoryAdapter implements
        SaveCategorizedTransaction,
        FindByTransactionId,
        FindBudgetsByCategory,
        FindByClientIdAndExpenseCategory,
        FindExpenseCategoriesByClient {

    private final CategorizedTransactionJpaRepository jpaRepository;

    public CategorizedTransactionRepositoryAdapter(CategorizedTransactionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CategorizedTransaction save(CategorizedTransaction categorizedTransaction) {
        return jpaRepository
                .save(CategorizedTransactionEntity.valueOf(categorizedTransaction))
                .toDomain();
    }

    @Override
    public List<CategorizedTransaction> findBy(ClientId clientId, ExpenseCategory expenseCategory) {
        return jpaRepository.findByClientIdAndExpenseCategory(clientId.value(), expenseCategory.value())
                .stream()
                .map(CategorizedTransactionEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<CategorizedTransaction> findBy(TransactionId transactionId) {
        return jpaRepository.findByTransactionId(transactionId.value())
                .map(CategorizedTransactionEntity::toDomain);
    }

    @Override
    public List<ExpenseCategory> findExpenseCategoriesBy(ClientId clientId) {
        return jpaRepository.findDistinctExpenseCategoriesByClientId(clientId.value())
                .stream()
                .map(ExpenseCategory::new)
                .toList();
    }

    @Override
    public List<CategoryBudget> findBudgetsByCategory(ClientId clientId) {
        return jpaRepository.findBudgetsByCategory(clientId.value());
    }
}
