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
            .onStateTransition { event: CircuitBreakerOnStateTransitionEvent ->
                when (event.stateTransition) {
                    StateTransition.CLOSED_TO_OPEN,
                    StateTransition.CLOSED_TO_FORCED_OPEN,
                    StateTransition.HALF_OPEN_TO_OPEN -> {
                        log.warn("Circuit Breaker state changed to OPEN. Stopping Kafka consumer")
                        kafkaManager.pause()
                    }
                    StateTransition.OPEN_TO_HALF_OPEN  ->  {
                        log.info("Circuit Breaker state changed to HALF_OPEN. Re-starting Kafka consumer")
                        kafkaManager.resume()
                    }
                    StateTransition.HALF_OPEN_TO_CLOSED,
                    StateTransition.FORCED_OPEN_TO_CLOSED,
                    StateTransition.FORCED_OPEN_TO_HALF_OPEN -> {}
                    else -> throw IllegalStateException("Unknown transition state: " + event.stateTransition)
                }
            }
    }
}