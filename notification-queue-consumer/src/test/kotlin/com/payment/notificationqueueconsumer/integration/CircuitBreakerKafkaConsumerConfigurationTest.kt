package com.payment.notificationqueueconsumer.integration

import com.payment.notificationqueueconsumer.configuration.CircuitBreakerKafkaConsumerConfiguration
import com.payment.notificationqueueconsumer.dataprovider.client.notification.NotificationServiceClient
import com.payment.notificationqueueconsumer.dataprovider.queue.KafkaManager
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.kafka.KafkaContainer

@Testcontainers
@SpringBootTest
class CircuitBreakerKafkaConsumerConfigurationTest {

    @Autowired private lateinit var notificationServiceClient: NotificationServiceClient

    @MockitoBean
    private lateinit var kafkaManager: KafkaManager

    @MockitoBean
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry

    private lateinit var circuitBreaker: CircuitBreaker

    @BeforeEach
    fun setup() {
        circuitBreaker = mock(CircuitBreaker::class.java)
        `when`(circuitBreakerRegistry.circuitBreaker("notification-service-A")).thenReturn(circuitBreaker)
        CircuitBreakerKafkaConsumerConfiguration(circuitBreakerRegistry, kafkaManager).configureCircuitBreaker()
    }

    @Test
    fun `should pause Kafka consumer when Circuit Breaker transitions to OPEN`() {
        val event = mock(CircuitBreakerOnStateTransitionEvent::class.java)
        `when`(event.stateTransition).thenReturn(CircuitBreaker.StateTransition.CLOSED_TO_OPEN)
        circuitBreaker.eventPublisher.onStateTransition.invoke(event)

        verify(kafkaManager, times(1)).pause()
    }

    @Test
    fun `should resume Kafka consumer when Circuit Breaker transitions to HALF_OPEN`() {
        val event = mock(CircuitBreakerOnStateTransitionEvent::class.java)
        `when`(event.stateTransition).thenReturn(CircuitBreaker.StateTransition.FORCED_OPEN_TO_HALF_OPEN)
        circuitBreaker.eventPublisher.onStateTransition.invoke(event)

        verify(kafkaManager, times(1)).resume()
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