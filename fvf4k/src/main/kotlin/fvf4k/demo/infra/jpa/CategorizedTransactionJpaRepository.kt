package fvf4k.demo.infra.jpa

import arrow.core.raise.ExperimentalRaiseAccumulateApi
import arrow.core.raise.Raise
import arrow.core.raise.context.accumulate
import arrow.core.raise.context.accumulating
import arrow.core.raise.context.withError
import fvf4k.demo.domain.failure.CategorizedTransactionCorrupted
import fvf4k.demo.domain.model.CategoryBudget
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.Money
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.*

@Repository
interface CategorizedTransactionJpaRepository : JpaRepository<CategorizedTransactionEntity, UUID> {

    fun findByTransactionId(transactionId: UUID): CategorizedTransactionEntity?

    fun findByClientIdAndExpenseCategory(
        clientId: ClientId,
        expenseCategory: ExpenseCategory
    ): List<CategorizedTransactionEntity>

    @Query(
        """
            SELECT new fvf4k.demo.infra.jpa.CategoryBudgetResult(t.expenseCategory, SUM(t.amount), t.currencyCode)
            FROM CategorizedTransactionEntity t
            WHERE t.clientId = :clientId
            GROUP BY t.expenseCategory
            """
    )
    fun findCategoryBudgetsByClientId(@Param("clientId") clientId: String): List<CategoryBudgetResult>
}

data class CategoryBudgetResult(val category: String?, val amount: BigDecimal?, val currencyCode: String?)

@OptIn(ExperimentalRaiseAccumulateApi::class)
context(_: Raise<CategorizedTransactionCorrupted>)
fun CategoryBudgetResult.toDomain(): CategoryBudget = withError(::CategorizedTransactionCorrupted) {
    accumulate {
        val expenseCategory = accumulating { ExpenseCategory(category) }
        val money = accumulating { Money(amount, currencyCode) }

        CategoryBudget(expenseCategory.value, money.value)
    }
}
