package com.payment.paymentflowchallenge.dataprovider.queue.kafka

import com.payment.paymentflowchallenge.core.common.util.toJson
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Service
class KafkaQueueProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    suspend fun <K, V> KafkaProducer<K, V>.asyncSend(record: ProducerRecord<K, V>) =
        suspendCoroutine<RecordMetadata> { continuation ->
            send(record) { metadata, exception ->
                exception?.let(continuation::resumeWithException)
                    ?: continuation.resume(metadata)
            }
        }

    fun send(topicName: String, messageValue: Any) {
        val jsonMessage = messageValue.toJson()

        kafkaTemplate.send(topicName, jsonMessage)
        producer.send(ProducerRecord(topicName, jsonMessage)) { metadata, exception ->
            if (exception != null) {
                log.info("Error sending message: ${exception.message}")
            } else {
                log.info("Message sent successfully to topic ${metadata.topic()} on partition ${metadata.partition()} with offset ${metadata.offset()}")
            }
        }
    }
}
