package com.payment.paymentflowchallenge.configuration

import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
class KafkaConfiguration {

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(kafkaProperties())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        return KafkaAdmin(kafkaProperties())
    }

    @Bean
    fun transferNotificationTopic(): NewTopic {
        return NewTopic("transfer-notification", 1, 1.toShort())
    }

    private fun kafkaProperties(): Map<String, Any> {
        return mapOf(
            "bootstrap.servers" to "PLAINTEXT://localhost:9092",
            "key.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
            "value.serializer" to "org.apache.kafka.common.serialization.StringSerializer",
            "security.protocol" to "PLAINTEXT",
            "connections.max.idle.ms" to 180000,
            "enable.auto.commit" to false
        )
    }
}