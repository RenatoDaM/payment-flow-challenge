package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.fromJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Configuration
class KafkaQueueConsumer(
    private val kafkaConsumer: KafkaConsumer<String, String>,
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    private tailrec fun <T> repeatUntilSome(block: () -> T?): T = block() ?: repeatUntilSome(block)

    suspend fun consumeMessages() {
        kafkaConsumer.use { consumer ->
            consumer.subscribe(listOf("transfer-notification"))
            val message = repeatUntilSome {
                consumer.poll(400.milliseconds.toJavaDuration()).map { it.value() }.firstOrNull()
            }
            println("Received message: $message")
            val serializedMessage = message.fromJson(NotificationDTO::class.java)

            sendNotificationUseCase.sendNotification(serializedMessage)
        }
    }

    @PostConstruct
    fun startConsuming() {
        CoroutineScope(Dispatchers.IO).launch {
            consumeMessages()
        }
    }
}
