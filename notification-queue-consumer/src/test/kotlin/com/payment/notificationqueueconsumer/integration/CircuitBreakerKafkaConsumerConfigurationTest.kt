package com.payment.notificationqueueconsumer.integration

import com.github.tomakehurst.wiremock.client.WireMock.*
import com.payment.notificationqueueconsumer.configuration.KafkaTestContainer.createKafkaTestContainer
import com.payment.notificationqueueconsumer.configuration.WireMockConfiguration.createWireMock
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import com.payment.notificationqueueconsumer.dataprovider.queue.KafkaManager
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.shaded.org.awaitility.Awaitility
import java.math.BigDecimal
import java.util.concurrent.TimeUnit


@SpringBootTest
@DirtiesContext
class CircuitBreakerKafkaConsumerConfigurationTest {

    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry


    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, Any>

    @Autowired
    private lateinit var kafkaManager: KafkaManager

    @BeforeEach
    fun setup() {

    }

    @Test
    fun `should pause Kafka consumer when Circuit Breaker transitions to OPEN`() {
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("notification-service-A")
        circuitBreaker.transitionToClosedState()

        EXTERNAL_SERVICE.stubFor(post("/notify")
            .willReturn(serverError()))

        repeat(10) {
            kafkaTemplate
                .send("transfer-notification", NotificationDTO(it + 1L, "teste@gmail.com", BigDecimal(100), 1))
        }

        Awaitility.await()
            .atMost(8, TimeUnit.SECONDS)
            .untilAsserted {
                assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.state, "Circuit Breaker should be OPEN")
            }

        Awaitility.await()
            .atMost(8, TimeUnit.SECONDS)
            .untilAsserted {
                assertEquals(true, kafkaManager.isPaused(), "Kafka consumer should be PAUSED, because Circuit Breaker is OPEN")
            }
    }

    @Test
    fun `should resume Kafka consumer when Circuit Breaker transitions to HALF_OPEN`() {
        val circuitBreaker = circuitBreakerRegistry.circuitBreaker("notification-service-A")
        circuitBreaker.transitionToOpenState()

        EXTERNAL_SERVICE.stubFor(post("/notify")
            .willReturn(ok()))

        repeat(3) {
            kafkaTemplate
                .send("transfer-notification", NotificationDTO(it + 1L, "teste@gmail.com", BigDecimal(100), 1))
        }

        Awaitility.await()
            .atMost(25, TimeUnit.SECONDS)
            .untilAsserted {
                assertEquals(CircuitBreaker.State.CLOSED, circuitBreaker.state, "Circuit Breaker should be CLOSED")
            }

        Awaitility.await()
            .atMost(25, TimeUnit.SECONDS)
            .untilAsserted {
                assertEquals(false, kafkaManager.isPaused(), "Kafka consumer shouldn't be PAUSED, because Circuit Breaker is CLOSED")
            }
    }

    companion object {

        @Container
        val kafka = createKafkaTestContainer()

        @RegisterExtension
        @JvmStatic
        val EXTERNAL_SERVICE = createWireMock()

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