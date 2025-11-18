package fvf4k.demo.infra.kafka

import arrow.core.raise.eagerEffect
import arrow.core.raise.either
import fvf4k.demo.domain.TransactionCategorizerService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener

private val log = KotlinLogging.logger {}

class TransactionKafkaListener(val transactionCategorizerService: TransactionCategorizerService) {

    @KafkaListener(topics = ["transactions"], groupId = "fvf4k-transactions-group")
    fun listen(transaction: TransactionMessage) {
        log.info { "Received transaction $transaction" }
        either {
            transactionCategorizerService.categorize(transaction.toDomain())
        }.onLeft { error ->
            log.error { "Error during categorization: $error" }
        }
    }
}