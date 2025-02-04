package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import com.payment.notificationqueueconsumer.core.common.fromJson
import com.payment.notificationqueueconsumer.core.usecase.SendNotificationUseCase
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.SmartLifecycle
import org.springframework.stereotype.Component
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Component
class KafkaQueueConsumer(
    private val kafkaConsumer: KafkaConsumer<String, String>,
    private val sendNotificationUseCase: SendNotificationUseCase
) : SmartLifecycle {

    @Value("\${notification.service.queue-name}")
    private lateinit var notificationQueueName: String

    private val log: Logger = LoggerFactory.getLogger(this.javaClass)
    private var job: Job? = null
    private var running = false

    override fun start() {
        log.info("Starting Kafka consumer...")
        running = true
        job = CoroutineScope(Dispatchers.IO).launch {
            kafkaConsumer.subscribe(listOf(notificationQueueName))
            kafkaConsumer.use {
                while (isActive) {
                    val messages = kafkaConsumer.poll(400.milliseconds.toJavaDuration())

                    for (record in messages) {
                        val message = record.value()
                        log.info("Received message: $message")
                        val serializedMessage = message.fromJson(NotificationDTO::class.java)

                        try {
                            sendNotificationUseCase.sendNotification(serializedMessage)
                                .awaitSingleOrNull()

                            log.info("Committing message with transferId: ${serializedMessage.transferId}")
                            kafkaConsumer.commitAsync()
                        } catch (e: Exception) {
                            log.error("Error processing message. The message with transactionId ${serializedMessage.transferId} wasn't committed, and will be reprocessed", e)
                        }
                    }
                }
            }
        }
    }

    override fun stop() {
        log.info("Stopping Kafka consumer...")
        running = false
        job?.cancel()
    }

    override fun isRunning(): Boolean = running
}
