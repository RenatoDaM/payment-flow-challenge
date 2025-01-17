package com.payment.paymentflowchallenge.dataprovider.queue.kafka

import com.payment.paymentflowchallenge.core.common.util.toJson
import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.apache.kafka.common.errors.TopicExistsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Service
class KafkaQueueProducer(
    private val producer: KafkaProducer<String, String>,
    private val adminClient: Admin
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
        // todo better treatment, cannot go null or something like that to kafka, like was happening
        producer.send(ProducerRecord(topicName, jsonMessage)) { metadata, exception ->
            if (exception != null) {
                log.info("Error sending message: ${exception.message}")
            } else {
                log.info("Message sent successfully to topic ${metadata.topic()} on partition ${metadata.partition()} with offset ${metadata.offset()}")
            }
        }
    }

    fun createTopic(topicName: String, partitions: Int = 1, replicationFactor: Short = 1) {
        try {
            val newTopic = NewTopic(topicName, partitions, replicationFactor)
            adminClient.createTopics(listOf(newTopic)).all().get()
            log.info("Topic '$topicName' created successfully.")
        } catch (e: ExecutionException) {
            if (e.cause is TopicExistsException) {
                log.warn("Topic '$topicName' already exists. If this occurs during startup, it can be safely ignored.")
            }
        }
    }
}
