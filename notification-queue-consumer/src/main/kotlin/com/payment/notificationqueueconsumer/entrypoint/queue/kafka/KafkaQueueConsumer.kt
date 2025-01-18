package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.fromJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
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

/*    private tailrec fun <T> repeatUntilSome(block: () -> T?): T = block() ?: repeatUntilSome(block)*/

    @PostConstruct
    private fun consumeMessages() {
        GlobalScope.launch(Dispatchers.IO) {
            kafkaConsumer.subscribe(listOf("transfer-notification"))
            kafkaConsumer.use { consumer ->
                while (true) {
                    val messages = consumer.poll(400.milliseconds.toJavaDuration())
                    messages.forEach {
                        val message = it.value()
                        println("Received message: $message")
                        val serializedMessage = message.fromJson(NotificationDTO::class.java)
                        sendNotificationUseCase.sendNotification(serializedMessage)
                        // kafkaConsumer.commitAsync() pode ser usado para confirmação assíncrona
                    }
                }
            }
        }
    }
}
