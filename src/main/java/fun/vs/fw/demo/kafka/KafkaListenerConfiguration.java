package fun.vs.fw.demo.kafka;


import fun.vs.fw.demo.domain.TransactionCategorizer;
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
