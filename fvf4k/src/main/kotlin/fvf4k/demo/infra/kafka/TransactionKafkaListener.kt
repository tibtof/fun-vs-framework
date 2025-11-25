package fvf4k.demo.infra.kafka

import arrow.core.raise.Raise
import arrow.core.raise.either
import fvf4k.demo.domain.api.CategorizeTransaction
import fvf4k.demo.domain.failure.ValidationFailed
import fvf4k.demo.domain.model.ExpenseCategory
import fvf4k.demo.domain.model.TransactionId
import fvf4k.demo.infra.web.CategorizedTransactionResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import java.util.*

private val log = KotlinLogging.logger {}

class TransactionKafkaListener(val categorizeTransaction: CategorizeTransaction) {

    @KafkaListener(topics = ["transactions"], groupId = "fvf4k-transactions-group")
    fun listen(transaction: TransactionMessage) {
        log.info { "Received transaction $transaction" }
        either {
            val domainTransactionResult = either {
                transaction.toDomain()
            }
            
            domainTransactionResult.fold(
                ifLeft = { validationError ->
                    log.error { "Validation error: $validationError" }
                    return@listen
                },
                ifRight = { domainTransaction ->
                    categorizeTransaction(domainTransaction)
                }
            )
        }.onLeft { error ->
            log.error { "Error during categorization: $error" }
        }
    }
}