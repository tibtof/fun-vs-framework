package fun.vs.fw.demo.messages;


import fun.vs.fw.demo.service.TransactionCategorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * A listener class responsible for processing transaction messages received from a Kafka topic.
 * It uses the {@link TransactionCategorizationService} to categorize transactions and save categorized details.
 */
@Component
public class TransactionKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionKafkaListener.class);
    private final TransactionCategorizationService categorizationService;

    public TransactionKafkaListener(TransactionCategorizationService categorizationService) {
        this.categorizationService = categorizationService;
    }

    /**
     * Listens to messages from the "transactions" Kafka topic and processes them for categorization.
     *
     * @param transaction The transaction message received from the "transactions" Kafka topic. This contains details
     *                    like transaction ID, account ID, client ID, merchant category code (MCC), and the transaction amount.
     */
    @KafkaListener(topics = "transactions", groupId = "transaction-group")
    public void listen(TransactionMessage transaction) {
        log.info("Received transaction: {}", transaction);
        categorizationService.categorizeTransaction(transaction);
    }
}
