package fvf4k.demo.infra.jpa

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailed
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailure
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.CategoryBudget
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import fvf4k.demo.domain.spi.FindBudgetsByCategory
import fvf4k.demo.domain.spi.FindByClientIdAndExpenseCategory
import fvf4k.demo.domain.spi.FindByTransactionId


class CategorizedTransactionReadRepositoryAdapter(
    val jpaRepository: CategorizedTransactionJpaRepository
) : FindByTransactionId,
    FindByClientIdAndExpenseCategory,
    FindBudgetsByCategory {

    context(_: Raise<QueryCategorizedTransactionFailure>)
    override fun invoke(transactionId: TransactionId): CategorizedTransaction? =
        catch({
            jpaRepository.findByTransactionId(transactionId.value)?.toDomain()
        }) { exception ->
            raise(
                QueryCategorizedTransactionFailed(
                    "could not execute query by transaction id. " +
                            "id='$transactionId' cause: ${exception.message}"
                )
            )
        }
}
