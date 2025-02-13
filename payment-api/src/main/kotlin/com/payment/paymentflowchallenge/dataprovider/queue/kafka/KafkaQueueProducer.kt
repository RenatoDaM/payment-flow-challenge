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
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    fun <T : Any> send(topicName: String, messageValue: T) {

        val future: CompletableFuture<SendResult<String, Any>> =
            kafkaTemplate.send(topicName, messageValue)

        future.whenComplete { result, exception ->
            if (exception != null) {
                log.error("Error sending message: ${messageValue.toJson()}", exception)
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
