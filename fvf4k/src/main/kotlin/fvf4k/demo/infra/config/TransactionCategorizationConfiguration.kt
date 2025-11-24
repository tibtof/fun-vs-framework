package fvf4k.demo.infra.config

import arrow.core.raise.context.Raise
import arrow.core.raise.context.bind
import fvf4k.demo.domain.api.CategorizeTransaction
import fvf4k.demo.domain.api.TransactionCategorizerService
import fvf4k.demo.domain.failure.CategorizeTransactionFailure
import fvf4k.demo.domain.failure.CategorizedTransactionUpdateError
import fvf4k.demo.domain.model.CategorizedTransaction
import fvf4k.demo.domain.model.Transaction
import fvf4k.demo.domain.spi.FindByTransactionId
import fvf4k.demo.domain.spi.ResolveExpenseCategory
import fvf4k.demo.domain.spi.SaveCategorizedTransaction
import io.github.oshai.kotlinlogging.KotlinLogging.logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.transaction.PlatformTransactionManager

val log = logger {}

@Configuration
class TransactionCategorizationConfiguration {

    @Bean
    fun transactionCategorizerService(
        findByTransactionId: FindByTransactionId,
        saveTransaction: SaveCategorizedTransaction,
        resolveExpenseCategory: ResolveExpenseCategory,
        transactionManager: PlatformTransactionManager
    ): CategorizeTransaction = object : CategorizeTransaction {
        val delegate = TransactionCategorizerService(findByTransactionId, saveTransaction, resolveExpenseCategory)

        context(_: Raise<CategorizeTransactionFailure>)
        override suspend fun invoke(transaction: Transaction): CategorizedTransaction =
            transactionManager.inTransaction(::mapDatabaseException) {
                delegate(transaction)
            }.bind()

        private fun mapDatabaseException(exception: Throwable): CategorizeTransactionFailure =
            when (exception) {
                is DataIntegrityViolationException -> {
                    val message = exception.message?.lowercase() ?: "unknown database error"
                    when {
                        message.contains("uk_transaction_id") ||
                                message.contains("duplicate") && message.contains("transaction") ->
                            CategorizedTransactionUpdateError("Categorized transaction already exists for this transaction: $message")

                        else -> CategorizedTransactionUpdateError(
                            message = "Database constraint violation: $message"
                        )
                    }
                }

                else -> CategorizedTransactionUpdateError(message = exception.message ?: "Unknown database error")
            }.also { log.error(exception) { it.message } }
    }
}