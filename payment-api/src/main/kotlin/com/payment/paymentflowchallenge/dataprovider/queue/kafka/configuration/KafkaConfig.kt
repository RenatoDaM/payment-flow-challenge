package com.payment.paymentflowchallenge.dataprovider.queue.kafka.configuration

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "kafka")
data class KafkaConfig(
    val topics: List<TopicConfig>
)

data class TopicConfig(
    val name: String,
    val partitions: Int,
    val replicationFactor: Short = 1
)