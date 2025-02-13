package com.payment.paymentflowchallenge.dataprovider.queue.kafka

import com.payment.paymentflowchallenge.core.common.util.toJson
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture

@Service
class KafkaQueueProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun send(topicName: String, messageValue: Any) {
        val jsonMessage = messageValue.toJson()

        val future: CompletableFuture<SendResult<String, String>> =
            kafkaTemplate.send(topicName, jsonMessage)

        future.whenComplete { result, exception ->
            if (exception != null) {
                log.error("Error sending message: $jsonMessage", exception)
            } else {
                log.info(
                    "Message sent successfully to topic ${result.recordMetadata.topic()} " +
                        "on partition ${result.recordMetadata.partition()} " +
                        "with offset ${result.recordMetadata.offset()}"
                )
            }
        }
    }
}
