package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.toJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component


@Component
class KafkaQueueConsumer(
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @RetryableTopic(attempts = "1", kafkaTemplate = "kafkaTemplate")
    @KafkaListener(topics = ["transfer-notification"], groupId = "payment")
    fun listenTransferNotification(notificationDTO: NotificationDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String) {
        log.info("Received message from topic [transfer-notification]: ${notificationDTO.toJson()}")
        sendNotificationUseCase.sendNotification(notificationDTO)
            .doOnError { error ->
                log.error("Error processing message with transactionId ${notificationDTO.transferId}. " +
                        "Retrying or sending to dead letter queue", error)
            }
    }

    @DltHandler
    fun handleDltPayment(
        transferNotification: NotificationDTO, @Header(KafkaHeaders.RECEIVED_TOPIC) topic: String
    ) {
        log.info("Event on dlt topic={}, payload={}", topic, transferNotification.toJson())
    }
}
