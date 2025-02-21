package com.payment.notificationqueueconsumer.configuration

import com.payment.notificationqueueconsumer.dataprovider.queue.KafkaManager
import io.github.resilience4j.circuitbreaker.CircuitBreaker.StateTransition
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerOnStateTransitionEvent
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration

@Configuration
class CircuitBreakerKafkaConsumerConfiguration (
    private val circuitBreakerRegistry: CircuitBreakerRegistry,
    private val kafkaManager: KafkaManager
) {
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun configureCircuitBreaker() {
        circuitBreakerRegistry.circuitBreaker("notification-service-A")
            .eventPublisher
            .onStateTransition(this::handleCircuitBreakerEvent)
    }

    private fun handleCircuitBreakerEvent(event: CircuitBreakerOnStateTransitionEvent) {
        when (event.stateTransition) {
            StateTransition.CLOSED_TO_OPEN -> {
                log.warn("Circuit Breaker state changed from CLOSED to OPEN. Stopping Kafka consumer")
                kafkaManager.pause()
            }

            StateTransition.CLOSED_TO_FORCED_OPEN -> {
                log.warn("Circuit Breaker state changed from CLOSED to FORCED_OPEN. Stopping Kafka consumer")
                kafkaManager.pause()
            }

            StateTransition.HALF_OPEN_TO_OPEN -> {
                log.warn("Circuit Breaker state changed from HALF_OPEN to OPEN. Stopping Kafka consumer")
                kafkaManager.pause()
            }

            StateTransition.OPEN_TO_HALF_OPEN -> {
                log.info("Circuit Breaker state changed from OPEN to HALF_OPEN. Re-starting Kafka consumer")
                kafkaManager.resume()
            }

            StateTransition.HALF_OPEN_TO_CLOSED -> {
                log.info("Circuit Breaker service stabilized, changing state from HALF_OPEN to CLOSED. Kafka consumer will continue running")
            }

            StateTransition.FORCED_OPEN_TO_CLOSED -> {
                log.info("Circuit Breaker state changed from OPEN to CLOSED. Re-starting Kafka consumer")
                kafkaManager.resume()
            }

            StateTransition.FORCED_OPEN_TO_HALF_OPEN -> {
                log.info("Circuit Breaker state changed from FORCED_OPEN to HALF_OPEN. Re-starting Kafka consumer")
                kafkaManager.resume()
            }

            else -> throw IllegalStateException("Unknown transition state: " + event.stateTransition)
        }
    }
}