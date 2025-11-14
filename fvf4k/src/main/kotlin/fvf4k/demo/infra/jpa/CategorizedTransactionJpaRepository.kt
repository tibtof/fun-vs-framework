package fvf4k.demo.infra.jpa

import fvf4k.demo.domain.CategoryBudget
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*


interface CategorizedTransactionJpaRepository : JpaRepository<CategorizedTransactionEntity, UUID> {

    fun findByTransactionId(transactionId: TransactionId): CategorizedTransactionEntity?

    fun findByClientIdAndExpenseCategory(
        clientId: ClientId,
        expenseCategory: ExpenseCategory
    ): List<CategorizedTransactionEntity>

    @Query(
        """
            SELECT new fvf4k.demo.domain.CategoryBudget(t.expenseCategory, SUM(t.amount))
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            GROUP BY t.expenseCategory
            """
    )
    fun findBudgetsByCategory(@Param("clientId") clientId: ClientId): List<CategoryBudget>

    @Query(
        """
            SELECT DISTINCT t.expenseCategory
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            """
    )
    fun findDistinctExpenseCategoriesByClientId(@Param("clientId") clientId: String): List<ExpenseCategory>
}