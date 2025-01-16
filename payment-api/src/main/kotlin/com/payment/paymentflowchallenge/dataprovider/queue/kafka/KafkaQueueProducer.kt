package com.payment.paymentflowchallenge.dataprovider.queue.kafka

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.stereotype.Service
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Service
class KafkaQueueProducer(
    private val producer: KafkaProducer<String, String>,
    private val adminClient: Admin
) {

    suspend fun <K, V> KafkaProducer<K, V>.asyncSend(record: ProducerRecord<K, V>) =
        suspendCoroutine<RecordMetadata> { continuation ->
            send(record) { metadata, exception ->
                exception?.let(continuation::resumeWithException)
                    ?: continuation.resume(metadata)
            }
        }

    fun send(record: ProducerRecord<String, String>) {
        producer.send(record) { metadata, exception ->
            if (exception != null) {
                println("Erro ao enviar mensagem: ${exception.message}")
            } else {
                println("Mensagem enviada com sucesso para o tópico ${metadata.topic()} na partição ${metadata.partition()} com offset ${metadata.offset()}")
            }
        }
    }

    fun createTopic(topicName: String, partitions: Int = 1, replicationFactor: Short = 1) {
        try {
            val newTopic = NewTopic(topicName, partitions, replicationFactor)
            adminClient.createTopics(listOf(newTopic)).all().get()
            println("Tópico '$topicName' criado com sucesso.")
        } catch (e: Exception) {
            println("Erro ao criar o tópico: ${e.message}")
        }
    }
}
