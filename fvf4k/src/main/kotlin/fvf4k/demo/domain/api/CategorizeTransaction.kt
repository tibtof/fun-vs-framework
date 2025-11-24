package fvf4k.demo.domain.api

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.failure.CategorizeTransactionFailure
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.CategorizedTransactionId
import fvf4k.demo.domain.model.Transaction
import fvf4k.demo.domain.spi.FindByTransactionId
import fvf4k.demo.domain.spi.ResolveExpenseCategory
import fvf4k.demo.domain.spi.SaveCategorizedTransaction

fun interface CategorizeTransaction {
    context(_: Raise<CategorizeTransactionFailure>)
    suspend operator fun invoke(transaction: Transaction): CategorizedTransaction
}

internal class TransactionCategorizerService(
    private val findByTransactionId: FindByTransactionId,
    private val saveTransaction: SaveCategorizedTransaction,
    private val resolveExpenseCategory: ResolveExpenseCategory
) : CategorizeTransaction {
    context(_: Raise<CategorizeTransactionFailure>)
    override suspend fun invoke(transaction: Transaction): CategorizedTransaction {
        val expenseCategory = resolveExpenseCategory(transaction.mcc)
        val existingCategorizedTransaction = findByTransactionId(transaction.id)

        val categorizedTransaction = existingCategorizedTransaction?.let { existing ->
            //keep the same id if the transaction was already categorized
            CategorizedTransaction(existing.id, transaction, expenseCategory)
        } ?: CategorizedTransaction(
            id = CategorizedTransactionId.generate(),
            transaction = transaction,
            expenseCategory = expenseCategory
        )
        return saveTransaction(categorizedTransaction)
    }
}
