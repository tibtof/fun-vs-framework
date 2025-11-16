package fvf4k.demo.infra.kafka

import arrow.core.raise.eagerEffect
import fvf4k.demo.domain.TransactionCategorizerService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener

private val log = KotlinLogging.logger {}

class TransactionKafkaListener(val transactionCategorizerService: TransactionCategorizerService) {

    @KafkaListener(topics = ["transactions"], groupId = "fvf4k-transactions-group")
    fun listen(transaction: TransactionMessage) {
        log.info { "Received transaction $transaction" }
        eagerEffect {
            transactionCategorizerService.categorize(transaction.toDomain())
        }
    }
}