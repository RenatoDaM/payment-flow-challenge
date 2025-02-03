package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.fromJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

import kotlinx.coroutines.reactor.awaitSingleOrNull

@Configuration
class KafkaQueueConsumer(
    private val kafkaConsumer: KafkaConsumer<String, String>,
    private val sendNotificationUseCase: SendNotificationUseCase
) {

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    private fun consumeMessages() {
        GlobalScope.launch(Dispatchers.IO) {
            kafkaConsumer.subscribe(listOf("transfer-notification"))
            kafkaConsumer.use { consumer ->
                while (true) {
                    val messages = consumer.poll(400.milliseconds.toJavaDuration())
                    messages.forEach { record ->
                        launch {
                            val message = record.value()
                            log.info("Received message: $message")
                            val serializedMessage = message.fromJson(NotificationDTO::class.java)

                            try {
                                sendNotificationUseCase.sendNotification(serializedMessage)
                                    .awaitSingleOrNull()

                                log.info("Committing message: $message")
                                kafkaConsumer.commitAsync()
                            } catch (e: Exception) {
                                log.error("Error processing message: ${e.message}", e)
                            }
                        }
                    }
                }
            }
        }
    }
}
