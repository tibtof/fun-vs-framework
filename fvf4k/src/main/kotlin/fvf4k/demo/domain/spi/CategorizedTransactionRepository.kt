package fvf4k.demo.domain.spi

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.ApplicationError
import fvf4k.demo.domain.CategoryBudget
import fvf4k.demo.domain.DatabaseQueryError
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.ClientId
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId


fun interface SaveCategorizedTransaction {
    context(_: Raise<ApplicationError>)
    fun insert(transaction: CategorizedTransaction): CategorizedTransaction
}

fun interface UpdateCategorizedTransaction {
    context(_: Raise<ApplicationError>)
    fun update(transaction: CategorizedTransaction): CategorizedTransaction
}

fun interface FindByTransactionId {
    context(_: Raise<DatabaseQueryError>)
    operator fun invoke(transactionId: TransactionId): CategorizedTransaction?
}

fun interface FindByClientIdAndExpenseCategory {
    context(_: Raise<DatabaseQueryError>)
    operator fun invoke(
        clientId: ClientId,
        expenseCategory: ExpenseCategory
    ): List<CategorizedTransaction>
}

fun interface FindBudgetsByCategory {
    context(_: Raise<DatabaseQueryError>)
    operator fun invoke(): List<CategoryBudget>
}

fun interface FindExpenseCategoriesByClientId {
    context(_: Raise<DatabaseQueryError>)
    operator fun invoke(clientId: ClientId): List<CategoryBudget>
}
