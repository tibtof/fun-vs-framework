package fvf4k.demo.infra.config

import fvf4k.demo.domain.api.CategorizeTransaction
import fvf4k.demo.domain.api.TransactionCategorizerService
import fvf4k.demo.domain.spi.FindByTransactionId
import fvf4k.demo.domain.spi.ResolveExpenseCategory
import fvf4k.demo.domain.spi.SaveCategorizedTransaction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class TransactionCategorizationConfiguration {

    @Bean
    fun transactionCategorizerService(
        findByTransactionId: FindByTransactionId,
        saveTransaction: SaveCategorizedTransaction,
        resolveExpenseCategory: ResolveExpenseCategory,
    ): CategorizeTransaction =
        TransactionCategorizerService(findByTransactionId, saveTransaction, resolveExpenseCategory)
}