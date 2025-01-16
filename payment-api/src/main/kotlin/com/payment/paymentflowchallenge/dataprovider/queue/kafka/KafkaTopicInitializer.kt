package com.payment.paymentflowchallenge.dataprovider.queue.kafka

import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class KafkaTopicInitializer(
    private val kafkaQueueProducer: KafkaQueueProducer
) {

    @PostConstruct
    fun init() {

        val topics = listOf(
            Pair("topic-1", 3),
            Pair("topic-2", 2)
        )

        topics.forEach { (topicName, partitions) ->
            kafkaQueueProducer.createTopic(topicName, partitions)
        }
    }
}
