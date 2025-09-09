package fun.vs.fw.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;

@Import(TestcontainersConfiguration.class)
@Testcontainers
@SpringBootTest
class TransactionCategorizerApplicationIT {

    @Container
    static WireMockContainer wireMockContainer = new WireMockContainer("wiremock/wiremock:3.12.0")
            .withExposedPorts(8080);

    @DynamicPropertySource
    static void wiremockProperties(DynamicPropertyRegistry registry) {
        registry.add("wiremock.server.url", wireMockContainer::getBaseUrl);
    }

    @Value("${wiremock.server.url}")
    private String wiremockUrl;

    @Test
    void contextLoads() {
        System.out.println("Wiremock url is: " + wiremockUrl);
    }
}
