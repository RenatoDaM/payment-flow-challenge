package com.payment.paymentflowchallenge.dataprovider.queue.kafka

import com.payment.paymentflowchallenge.configuration.TopicInitializerProperties
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class KafkaTopicInitializer(
    private val kafkaQueueProducer: KafkaQueueProducer,
    private val kafkaConfig: TopicInitializerProperties
) {
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun init() {
        val requiredTopics = kafkaConfig.topics

        val topicNames = requiredTopics.joinToString(", ") { it.name }
        log.info("Creating the required topics if they don't exist: $topicNames")

        requiredTopics.forEach { topicConfig ->
            kafkaQueueProducer.createTopic(topicConfig.name, topicConfig.partitions, topicConfig.replicationFactor)
        }
    }
}
