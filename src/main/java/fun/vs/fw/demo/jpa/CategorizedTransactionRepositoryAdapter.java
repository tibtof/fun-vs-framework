package fun.vs.fw.demo.jpa;


import fun.vs.fw.demo.domain.CategorizedTransaction;
import fun.vs.fw.demo.domain.CategorizedTransaction.ExpenseCategory;
import fun.vs.fw.demo.domain.CategorizedTransactionRepository;
import fun.vs.fw.demo.domain.CategoryBudget;
import fun.vs.fw.demo.domain.Transaction;
import fun.vs.fw.demo.domain.Transaction.ClientId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategorizedTransactionRepositoryAdapter implements
        CategorizedTransactionRepository.SaveCategorizedTransaction,
        CategorizedTransactionRepository.FindByTransactionId,
        CategorizedTransactionRepository.FindBudgetsByCategory,
        CategorizedTransactionRepository.FindByClientIdAndExpenseCategory,
        CategorizedTransactionRepository.FindExpenseCategoriesByClient {

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
    public Optional<CategorizedTransaction> findBy(Transaction.TransactionId transactionId) {
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
