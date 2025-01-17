package com.payment.notificationqueueconsumer.entrypoint.queue.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.springframework.context.annotation.Configuration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Configuration
class KafkaQueueConsumer(
    private val kafkaConsumer: KafkaConsumer<String, String>
) {

    fun <T> repeatUntilSome(block: () -> T?): T = block() ?: repeatUntilSome(block)

    fun consumeMessages() {
        kafkaConsumer.use { consumer ->
            consumer.subscribe(listOf("test"))
            val message = repeatUntilSome {
                consumer.poll(400.milliseconds.toJavaDuration()).map { it.value() }.firstOrNull()
            }
            println("Received message: $message")
        }
    }
}
