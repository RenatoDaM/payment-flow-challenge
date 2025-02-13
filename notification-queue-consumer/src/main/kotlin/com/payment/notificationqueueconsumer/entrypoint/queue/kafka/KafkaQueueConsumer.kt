package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.fromJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaQueueConsumer(
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @KafkaListener(topics = ["transfer-notification"], groupId = "payment")
    fun listenGroupFoo(message: String) {
        log.info("Received message from topic [transfer-notification]: $message")
        val serializedMessage = message.fromJson(NotificationDTO::class.java)
        sendNotificationUseCase.sendNotification(serializedMessage)
    }
}
