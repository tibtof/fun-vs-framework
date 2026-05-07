package fvf4k.demo.infra.jpa

import arrow.core.raise.catch
import arrow.core.raise.context.Raise
import arrow.core.raise.context.raise
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailed
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailure
import fvf4k.demo.domain.model.*
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

    context(_: Raise<QueryCategorizedTransactionFailure>)
    override fun invoke(
        clientId: ClientId,
        expenseCategory: ExpenseCategory
    ): List<CategorizedTransaction> = catch({
        jpaRepository.findByClientIdAndExpenseCategory(clientId.value, expenseCategory.value)
            .map { it.toDomain() }
    }) { exception ->
        raise(
            QueryCategorizedTransactionFailed(
                "could not execute query by client id '$clientId' " +
                        "and expense category $expenseCategory because ${exception.message}"
            )
        )
    }

    context(_: Raise<QueryCategorizedTransactionFailure>)
    override fun invoke(clientId: ClientId): List<CategoryBudget> = catch({
        jpaRepository.findCategoryBudgetsByClientId(clientId.value)
            .map { it.toDomain() }
    }) { exception ->
        raise(
            QueryCategorizedTransactionFailed(
                "could not execute query by client id '$clientId' " +
                        "because ${exception.message}"
            )
        )
    }
}
