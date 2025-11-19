package fvf4k.demo.domain.spi

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.model.CategoryBudget
import fvf4k.demo.domain.failure.QueryCategorizedTransactionFailure
import fvf4k.demo.domain.failure.CategorizeTransactionFailure
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId


fun interface SaveCategorizedTransaction {
    context(_: Raise<CategorizeTransactionFailure>)
    operator fun invoke(transaction: CategorizedTransaction): CategorizedTransaction
}

fun interface FindByTransactionId {
    context(_: Raise<QueryCategorizedTransactionFailure>)
    operator fun invoke(transactionId: TransactionId): CategorizedTransaction?
}

fun interface FindByClientIdAndExpenseCategory {
    context(_: Raise<QueryCategorizedTransactionFailure>)
    operator fun invoke(
        clientId: ClientId,
        expenseCategory: ExpenseCategory
    ): List<CategorizedTransaction>
}

fun interface FindBudgetsByCategory {
    context(_: Raise<QueryCategorizedTransactionFailure>)
    operator fun invoke(clientId: ClientId): List<CategoryBudget>
}

