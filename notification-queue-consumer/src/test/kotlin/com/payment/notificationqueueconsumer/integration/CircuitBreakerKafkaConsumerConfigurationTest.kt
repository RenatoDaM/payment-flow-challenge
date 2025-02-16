package com.payment.notificationqueueconsumer.integration

import com.payment.notificationqueueconsumer.configuration.CircuitBreakerKafkaConsumerConfiguration
import com.payment.notificationqueueconsumer.dataprovider.client.notification.NotificationServiceClient
import com.payment.notificationqueueconsumer.dataprovider.queue.KafkaManager
import com.payment.notificationqueueconsumer.entrypoint.queue.kafka.KafkaQueueConsumer
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.kafka.KafkaContainer

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
class CircuitBreakerKafkaConsumerConfigurationTest {

    @Autowired
    private lateinit var notificationServiceClient: NotificationServiceClient

    @Autowired
    private lateinit var kafkaQueueConsumer: KafkaQueueConsumer

    @MockitoBean
    private lateinit var kafkaManager: KafkaManager

    @MockitoBean
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry

    @Autowired
    private lateinit var circuitBreaker: CircuitBreaker

    @BeforeEach
    fun setup() {
    }

    @Test
    fun `should pause Kafka consumer when Circuit Breaker transitions to OPEN`() {

        circuitBreaker.transitionToOpenState()
        verify(circuitBreaker).transitionToOpenState()
    }

    @Test
    fun `should resume Kafka consumer when Circuit Breaker transitions to HALF_OPEN`() {

    }

    companion object {
        @Container
        @JvmStatic
        @ServiceConnection
        val kafka = KafkaContainer("apache/kafka:3.7.2")

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