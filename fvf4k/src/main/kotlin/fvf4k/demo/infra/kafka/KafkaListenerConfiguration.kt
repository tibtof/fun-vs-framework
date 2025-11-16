package fvf4k.demo.infra.kafka

import fvf4k.demo.domain.TransactionCategorizerService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka


@Configuration
@EnableKafka
class KafkaListenerConfiguration {

    @Bean
    fun transactionKafkaListener(transactionCategorizerService: TransactionCategorizerService) =
        TransactionKafkaListener(transactionCategorizerService)
}