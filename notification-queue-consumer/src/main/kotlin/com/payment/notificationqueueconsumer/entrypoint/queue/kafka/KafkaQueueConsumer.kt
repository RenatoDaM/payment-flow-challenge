package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.payment.notificationqueueconsumer.core.common.fromJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Configuration
class KafkaQueueConsumer(
    private val kafkaConsumer: KafkaConsumer<String, String>,
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    fun <T> repeatUntilSome(block: () -> T?): T = block() ?: repeatUntilSome(block)

    suspend fun consumeMessages() {
        kafkaConsumer.use { consumer ->
            consumer.subscribe(listOf("test"))
            val message = repeatUntilSome {
                consumer.poll(400.milliseconds.toJavaDuration()).map { it.value() }.firstOrNull()
            }
            println("Received message: $message")
            val serializedMessage = message.fromJson(NotificationDTO::class.java)

            sendNotificationUseCase.sendNotification(serializedMessage)
        }
    }
}
