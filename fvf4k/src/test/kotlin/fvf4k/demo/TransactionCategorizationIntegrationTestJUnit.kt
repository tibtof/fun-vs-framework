package fvf4k.demo

import com.fasterxml.jackson.databind.ObjectMapper
import fvf4k.demo.infra.jpa.CategorizedTransactionJpaRepository
import fvf4k.demo.infra.kafka.TransactionMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import io.kotest.assertions.nondeterministic.eventually
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.wiremock.integrations.testcontainers.WireMockContainer
import java.math.BigDecimal
import java.util.*

private val log = KotlinLogging.logger {}

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TransactionCategorizationIntegrationTestJUnit {

    @Autowired
    private lateinit var repository: CategorizedTransactionJpaRepository

    @BeforeEach
    fun setUp() {
        repository.deleteAll()
    }

    companion object {
        @Container
        @JvmStatic
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"))

        @Container
        @JvmStatic
        val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:17-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test")

        @Container
        @JvmStatic
        val wiremockContainer = WireMockContainer(DockerImageName.parse("wiremock/wiremock:3.13.2"))
            .withMappingFromJSON("merchant-directory-restaurant", """
                {
                  "request": {
                    "method": "GET",
                    "urlPath": "/merchant-directory/5812"
                  },
                  "response": {
                    "status": 200,
                    "jsonBody": {
                      "mcc": "5812",
                      "category": "Restaurants"
                    },
                    "headers": {
                      "Content-Type": "application/json"
                    }
                  }
                }
            """.trimIndent())
            .withMappingFromJSON("merchant-directory-grocery", """
                {
                  "request": {
                    "method": "GET",
                    "urlPath": "/merchant-directory/5411"
                  },
                  "response": {
                    "status": 200,
                    "jsonBody": {
                      "mcc": "5411",
                      "category": "Grocery Stores"
                    },
                    "headers": {
                      "Content-Type": "application/json"
                    }
                  }
                }
            """.trimIndent())

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers)
            registry.add("spring.datasource.url", postgresContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresContainer::getUsername)
            registry.add("spring.datasource.password", postgresContainer::getPassword)
            registry.add("merchant.directory.url") { "http://${wiremockContainer.host}:${wiremockContainer.firstMappedPort}" }
        }
    }

    @AfterEach
    fun cleanup() {
        repository.deleteAll()
    }

    @Test
    @Order(1)
    fun `should process transaction message from Kafka and store categorization in database`() = runBlocking {
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
        log.info { "TEST: Published message to Kafka: $transactionMessage" }

        // Then: Eventually the message should be processed and stored in database
        eventually(30.seconds) {
            val categorizedTransaction = repository.findByTransactionId(transactionId)
            val allTransactions = repository.findAll().toList()
            log.info { "TEST: Categorized transaction result: $categorizedTransaction" }
            log.info { "TEST: Database contains ${repository.count()} categorized transactions: $allTransactions" }
            
            Assertions.assertNotNull(categorizedTransaction, "Transaction should be categorized and saved to database")
            Assertions.assertEquals(transactionId, categorizedTransaction!!.transactionId)
            Assertions.assertEquals(clientId, categorizedTransaction.clientId)
            Assertions.assertEquals(accountId, categorizedTransaction.accountId)
            Assertions.assertEquals(BigDecimal("42.50"), categorizedTransaction.amount)
            Assertions.assertEquals("USD", categorizedTransaction.currencyCode)
            Assertions.assertEquals("5812", categorizedTransaction.mcc)
            Assertions.assertEquals("Restaurants", categorizedTransaction.expenseCategory)
        }
    }

    @Test
    @Order(2)
    fun `should handle multiple concurrent transactions`() = runBlocking {
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
                Assertions.assertNotNull(result)
            }
            
            val allCategorized = repository.findAll()
            Assertions.assertEquals(5, allCategorized.size)
        }
    }

    @Test
    @Order(3)
    fun `should categorize grocery store transactions correctly`() = runBlocking {
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
            Assertions.assertNotNull(categorizedTransaction)
            Assertions.assertEquals("Grocery Stores", categorizedTransaction!!.expenseCategory)
            Assertions.assertEquals("5411", categorizedTransaction.mcc)
        }
    }

    // Helper function in functional style
    private fun publishTransactionMessage(message: TransactionMessage) {
        KafkaProducer<String, String>(Properties().apply {
            put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.bootstrapServers)
            put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer::class.java.name)
            put(ProducerConfig.ACKS_CONFIG, "all")
        }).use { producer ->
            val objectMapper = ObjectMapper()
            val json = objectMapper.writeValueAsString(message)
            val record = ProducerRecord<String, String>("transactions", message.transactionId, json)
            producer.send(record).get()
        }
    }
}
