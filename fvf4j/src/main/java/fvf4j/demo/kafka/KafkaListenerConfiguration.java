package fvf4j.demo.kafka;


import fvf4j.demo.domain.TransactionCategorizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;

@Configuration
@EnableKafka
public class KafkaListenerConfiguration {

    @Bean
    public TransactionKafkaListener transactionKafkaListener(TransactionCategorizer categorizationService) {
        return new TransactionKafkaListener(categorizationService);
    }
}
