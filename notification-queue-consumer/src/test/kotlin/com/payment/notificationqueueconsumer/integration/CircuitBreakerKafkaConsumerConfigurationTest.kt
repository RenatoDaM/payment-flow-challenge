package com.payment.notificationqueueconsumer.integration

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.payment.notificationqueueconsumer.configuration.KafkaConfiguration
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.NotificationServiceClient
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import com.payment.notificationqueueconsumer.entrypoint.queue.kafka.KafkaQueueConsumer
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.web.reactive.function.client.WebClient
import org.testcontainers.containers.Network
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.kafka.KafkaContainer
import java.math.BigDecimal
import java.time.Duration


@SpringBootTest
@DirtiesContext
@TestPropertySource(
    properties = [
        "spring.kafka.consumer.auto-offset-reset=earliest"
    ]
)
class CircuitBreakerKafkaConsumerConfigurationTest {

    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry


    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>


    @BeforeEach
    fun setup() {

    }

    @Test
    fun `should pause Kafka consumer when Circuit Breaker transitions to OPEN`() {
        EXTERNAL_SERVICE.stubFor(post("/notify")
            .willReturn(serverError()))

        repeat(10) {
            kafkaTemplate
                .send("transfer-notification", NotificationDTO(it + 1L, "teste@gmail.com", BigDecimal(100), 1))
        }

        assertTimeoutPreemptively(Duration.ofSeconds(10)) {
            while (circuitBreakerRegistry.circuitBreaker("notification-service-A").state != CircuitBreaker.State.OPEN) {
                Thread.sleep(500)
            }
        }
    }

/*    @Test
    fun `should resume Kafka consumer when Circuit Breaker transitions to HALF_OPEN`() {

    }*/

    companion object {
        private val network: Network = Network.newNetwork()

        @Container
        val kafka: KafkaContainer = KafkaContainer("apache/kafka:3.7.2").withNetwork(network)

        @RegisterExtension
        @JvmStatic
        val EXTERNAL_SERVICE: WireMockExtension = WireMockExtension.newInstance()
            .options(WireMockConfiguration.wireMockConfig().port(9090))
            .build()

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            kafka.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            kafka.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerKafkaContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers)
        }
    }
}