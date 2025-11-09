package fvf4j.demo.jpa;


import fvf4j.demo.domain.CategoryBudget;
import fvf4j.demo.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            SELECT new fvf4j.demo.domain.CategoryBudget(t.expenseCategory, SUM(t.amount))
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            GROUP BY t.expenseCategory
            """)
    List<CategoryBudget> findBudgetsByCategory(@Param("clientId") String clientId);


    @Query("""
            SELECT DISTINCT t.expenseCategory
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            """)
    List<String> findDistinctExpenseCategoriesByClientId(@Param("clientId") String clientId);
}
