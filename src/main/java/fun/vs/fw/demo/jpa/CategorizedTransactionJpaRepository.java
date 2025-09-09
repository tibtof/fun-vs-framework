package fun.vs.fw.demo.jpa;


import fun.vs.fw.demo.domain.CategoryBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing and accessing categorized financial transactions.
 * This interface extends JpaRepository to provide CRUD operations and custom query
 * methods for the CategorizedTransaction entity.
 */
@Repository
public interface CategorizedTransactionJpaRepository extends JpaRepository<CategorizedTransactionEntity, Long> {

    Optional<CategorizedTransactionEntity> findByTransactionId(String transactionId);

    List<CategorizedTransactionEntity> findByClientIdAndExpenseCategory(String clientId, String expenseCategory);

    @Query("""
            SELECT t.expenseCategory as category, SUM(t.amount) as totalAmount
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            GROUP BY t.expenseCategory
            """)
    List<CategoryBudget> findBudgetsByCategory(String clientId);


    @Query("""
            SELECT DISTINCT t.expenseCategory
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            """)
    List<String> findDistinctExpenseCategoriesByClientId(String clientId);
}
