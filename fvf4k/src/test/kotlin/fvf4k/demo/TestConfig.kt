package fvf4k.demo

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.extensions.spring.SpringExtension
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.utility.DockerImageName
import org.wiremock.integrations.testcontainers.WireMockContainer

object TestContainers {
    val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.8.0"))
    val postgresContainer = PostgreSQLContainer(DockerImageName.parse("postgres:18"))
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test")
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
}

class ProjectConfig : AbstractProjectConfig() {
    override val specExecutionOrder = SpecExecutionOrder.Annotated

    override val extensions: List<Extension> = listOf(SpringExtension())

    override suspend fun beforeProject() {
        // Start containers before any tests
        TestContainers.kafkaContainer.start()
        TestContainers.postgresContainer.start()
        TestContainers.wiremockContainer.start()

        // Set system properties that Spring will pick up
        System.setProperty("spring.kafka.bootstrap-servers", TestContainers.kafkaContainer.bootstrapServers)
        System.setProperty("spring.datasource.url", TestContainers.postgresContainer.jdbcUrl)
        System.setProperty("spring.datasource.username", TestContainers.postgresContainer.username)
        System.setProperty("spring.datasource.password", TestContainers.postgresContainer.password)
        System.setProperty("merchant.directory.url", "http://${TestContainers.wiremockContainer.host}:${TestContainers.wiremockContainer.firstMappedPort}")
    }

    override suspend fun afterProject() {
        TestContainers.kafkaContainer.stop()
        TestContainers.postgresContainer.stop()
        TestContainers.wiremockContainer.stop()
    }
}
