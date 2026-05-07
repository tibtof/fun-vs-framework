package fvf4k.demo

import tools.jackson.databind.json.JsonMapper
import fvf4k.demo.infra.jpa.CategorizedTransactionJpaRepository
import fvf4k.demo.infra.kafka.TransactionMessage
import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.spec.style.FreeSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.math.BigDecimal
import java.util.*
import kotlin.time.Duration.Companion.seconds

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
class TransactionCategorizationIntegrationTest(val repository: CategorizedTransactionJpaRepository) : FreeSpec({
    beforeTest {
        // Clear database before each test since containers are shared
        repository.deleteAll()
    }

    fun publishTransactionMessage(message: TransactionMessage) {
        KafkaProducer<String, String>(Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, TestContainers.kafkaContainer.bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.ACKS_CONFIG, "all")
        }).use { producer ->
            val objectMapper = JsonMapper.builder().build()
            val json = objectMapper.writeValueAsString(message)
            val record = ProducerRecord<String, String>("transactions", message.transactionId, json)
            producer.send(record).get()
        }
    }

    "Transaction categorization flow" - {
        "should process transaction message from Kafka and store categorization in database" {
            // Given: A transaction message
            val transactionId = UUID.randomUUID()
            val clientId = UUID.randomUUID()
            val accountId = UUID.randomUUID()

            val transactionMessage = TransactionMessage(
                transactionId = transactionId.toString(),
                clientId = clientId.toString(),
                accountId = accountId.toString(),
                mcc = "5812", // Restaurant
                amount = BigDecimal("42.50"),
                currencyCode = "USD"
            )

            // When: Publishing the message to Kafka
            publishTransactionMessage(transactionMessage)

            // Then: Eventually the message should be processed and stored in database
            eventually(30.seconds) {
                val categorizedTransaction = repository.findByTransactionId(transactionId)
                categorizedTransaction shouldNotBe null
                categorizedTransaction!!.transactionId shouldBe transactionId
                categorizedTransaction.clientId shouldBe clientId
                categorizedTransaction.accountId shouldBe accountId
                categorizedTransaction.amount shouldBe BigDecimal("42.50")
                categorizedTransaction.currencyCode shouldBe "USD"
                categorizedTransaction.mcc shouldBe "5812"
                categorizedTransaction.expenseCategory shouldBe "Restaurants"
            }
        }

        "should handle multiple concurrent transactions" {
            // Given: Multiple transaction messages
            val transactions = (1..5).map {
                val txnId = UUID.randomUUID()
                Triple(
                    txnId,
                    UUID.randomUUID(),
                    TransactionMessage(
                        transactionId = txnId.toString(),
                        clientId = UUID.randomUUID().toString(),
                        accountId = UUID.randomUUID().toString(),
                        mcc = if (it % 2 == 0) "5812" else "5411",
                        amount = BigDecimal.valueOf(100.0 + it),
                        currencyCode = "USD"
                    )
                )
            }

            // When: Publishing multiple messages concurrently
            transactions.forEach { (_, _, msg) ->
                publishTransactionMessage(msg)
            }

            // Then: Eventually all transactions should be processed and stored
            eventually(30.seconds) {
                transactions.forEach { (txnId, _, _) ->
                    val result = repository.findByTransactionId(txnId)
                    result shouldNotBe null
                }

                val allCategorized = repository.findAll()
                allCategorized shouldHaveSize 5
            }
        }

        "should categorize grocery store transactions correctly" {
            // Given: A grocery store transaction
            val transactionId = UUID.randomUUID()
            val transactionMessage = TransactionMessage(
                transactionId = transactionId.toString(),
                clientId = UUID.randomUUID().toString(),
                accountId = UUID.randomUUID().toString(),
                mcc = "5411", // Grocery Store
                amount = BigDecimal("125.75"),
                currencyCode = "USD"
            )

            // When: Publishing the message
            publishTransactionMessage(transactionMessage)

            // Then: Eventually should be categorized as Grocery Stores
            eventually(30.seconds) {
                val categorizedTransaction = repository.findByTransactionId(transactionId)
                categorizedTransaction shouldNotBe null
                categorizedTransaction!!.expenseCategory shouldBe "Grocery Stores"
                categorizedTransaction.mcc shouldBe "5411"
            }
        }

        "should update existing transaction if same transactionId is processed again" {
            // Given: A transaction that was already processed
            val transactionId = UUID.randomUUID()
            val firstMessage = TransactionMessage(
                transactionId = transactionId.toString(),
                clientId = UUID.randomUUID().toString(),
                accountId = UUID.randomUUID().toString(),
                mcc = "5812",
                amount = BigDecimal("50.00"),
                currencyCode = "USD"
            )

            publishTransactionMessage(firstMessage)

            // Wait for first processing to complete
            val firstCategorizedId = eventually(30.seconds) {
                val first = repository.findByTransactionId(transactionId)
                first shouldNotBe null
                first!!.id
            }

            // When: Publishing the same transaction again with different details
            val secondMessage = TransactionMessage(
                transactionId = transactionId.toString(),
                clientId = UUID.randomUUID().toString(),
                accountId = UUID.randomUUID().toString(),
                mcc = "5411", // Different category
                amount = BigDecimal("75.00"),
                currencyCode = "EUR"
            )

            publishTransactionMessage(secondMessage)

            // Then: Eventually should keep the same categorized transaction ID but update the details
            eventually(30.seconds) {
                val second = repository.findByTransactionId(transactionId)
                second shouldNotBe null
                second!!.id shouldBe firstCategorizedId // Same ID
                second.expenseCategory shouldBe "Grocery Stores" // Updated category
                second.amount shouldBe BigDecimal("75.00") // Updated amount
            }
        }
    }
})