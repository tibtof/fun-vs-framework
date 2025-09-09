package fun.vs.fw.demo.kafka;


import fun.vs.fw.demo.domain.TransactionCategorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * A listener class responsible for processing transaction messages received from a Kafka topic.
 * It uses the {@link TransactionCategorizer} to categorize transactions and save categorized details.
 */
@Component
public class TransactionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionKafkaListener.class);
    private final TransactionCategorizer categorizationService;

    public TransactionKafkaListener(TransactionCategorizer categorizationService) {
        this.categorizationService = categorizationService;
    }

    /**
     * Listens to messages from the "transactions" Kafka topic and processes them for categorization.
     *
     * @param message The message received from the "transactions" Kafka topic. This contains details
     *                like message ID, account ID, client ID, merchant category value (MCC), and the message amount.
     */
    @KafkaListener(topics = "transactions", groupId = "transactions-group")
    public void listen(TransactionMessage message) {
        log.info("Received message: {}", message);
        categorizationService.categorize(message.toTransaction());
    }
}
