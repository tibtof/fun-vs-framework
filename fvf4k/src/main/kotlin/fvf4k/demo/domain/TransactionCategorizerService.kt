package fvf4k.demo.domain

import arrow.core.raise.context.Raise
import fvf4k.demo.domain.api.TransactionCategorizer
import fvf4k.demo.domain.failure.Failure
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.CategorizedTransactionId
import fvf4k.demo.domain.model.Transaction
import fvf4k.demo.domain.spi.FindByTransactionId
import fvf4k.demo.domain.spi.MerchantDirectory
import fvf4k.demo.domain.spi.SaveCategorizedTransaction
import fvf4k.demo.infra.jpa.CategorizedTransactionWriteRepositoryAdapter
import fvf4k.demo.infra.merchantdirectory.MerchantDirectoryConfiguration


class TransactionCategorizerService(
    private val findByTransactionId: FindByTransactionId,
    private val saveTransaction: SaveCategorizedTransaction,
    private val merchantDirectory: MerchantDirectory
) : TransactionCategorizer {
    context(_: Raise<Failure>)
    override fun categorize(transaction: Transaction): CategorizedTransaction {
        val expenseCategory = merchantDirectory.getFor(transaction.mcc)
        val existingCategorizedTransaction = findByTransactionId(transaction.id)

        val categorizedTransaction = existingCategorizedTransaction?.let { existing ->
            //keep the same id if the transaction was already categorized
            CategorizedTransaction(existing.id, transaction, expenseCategory)
        } ?: CategorizedTransaction(
            id = CategorizedTransactionId.generate(),
            transaction = transaction,
            expenseCategory = expenseCategory
        )
        println(MerchantDirectoryConfiguration())
        return saveTransaction.insert(categorizedTransaction)
    }
}

fun main() {

}