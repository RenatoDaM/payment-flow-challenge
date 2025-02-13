package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.toJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.retrytopic.DltStrategy
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono


@Component
class KafkaQueueConsumer(
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    //Using the FAIL_ON_ERROR strategy we can configure the DLT consumer to end the execution without retrying if the DLT processing fails
    @RetryableTopic(
        attempts = "1",
        kafkaTemplate = "kafkaTemplate",
        dltStrategy = DltStrategy.ALWAYS_RETRY_ON_ERROR,
        backoff = Backoff(10000L)
    )
    @KafkaListener(topics = ["transfer-notification"], groupId = "payment")
    fun listenTransferNotification(notificationDTO: NotificationDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String): Mono<ResponseEntity<Void>> {
        val transferId = notificationDTO.transferId
        log.info("Received message from topic $topic: ${notificationDTO.toJson()}")

        return sendNotificationUseCase.sendNotification(notificationDTO)
            .doOnError { error ->
                log.error("Error processing message with transferId $transferId. " +
                        "Retrying...", error)
            }
            .doOnSuccess {
                log.info("Message with transferId $transferId from topic $topic successfully processed.")
            }
    }

    @DltHandler
    fun handleDltTransferNotification(
        notificationDTO: NotificationDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String
    ): Mono<ResponseEntity<Void>> {
        val transferId = notificationDTO.transferId
        log.info("Event on dlt topic={}, payload={}", topic, notificationDTO.toJson())

        return sendNotificationUseCase.sendNotification(notificationDTO)
            .doOnError { error ->
                log.error("Error processing message at dead letter queue with transferId $transferId", error)
            }
            .doOnSuccess {
                log.info("Message with transferId $transferId from topic $topic successfully processed")
            }
    }
}
