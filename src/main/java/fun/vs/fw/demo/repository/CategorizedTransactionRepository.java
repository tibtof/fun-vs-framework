package fun.vs.fw.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing and accessing categorized financial transactions.
 * This interface extends JpaRepository to provide CRUD operations and custom query
 * methods for the CategorizedTransaction entity.
 */
public interface CategorizedTransactionRepository extends JpaRepository<CategorizedTransaction, Long> {

    Optional<CategorizedTransaction> findByTransactionId(String transactionId);

    List<CategorizedTransaction> findByClientIdAndExpenseCategory(String clientId, String expenseCategory);

    @Query("""
            SELECT t.expenseCategory as category, SUM(t.amount) as totalAmount
            FROM CategorizedTransaction t
            WHERE t.clientId = :clientId
            GROUP BY t.expenseCategory
            """)
    List<CategoryBudget> findBudgetsByCategory(String clientId);


    @Query("""
            SELECT DISTINCT t.expenseCategory
            FROM CategorizedTransaction t
            WHERE t.clientId = :clientId
            """)
    List<String> findDistinctExpenseCategoriesByClientId(String clientId);
}
