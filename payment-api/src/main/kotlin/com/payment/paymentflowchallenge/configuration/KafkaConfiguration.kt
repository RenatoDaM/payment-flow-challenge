package com.payment.paymentflowchallenge.configuration

import com.payment.paymentflowchallenge.dataprovider.queue.kafka.dto.NotificationDTO
import org.apache.kafka.clients.admin.NewTopic
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaAdmin
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.support.serializer.JsonSerializer



@Configuration
class KafkaConfiguration {

    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private lateinit var bootstrapAddress: String

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        return DefaultKafkaProducerFactory(kafkaProperties(), StringSerializer(), JsonSerializer())
    }

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        return KafkaAdmin(kafkaProperties())
    }

    private fun kafkaProperties(): Map<String, Any> {
        val notificationDTOClassName = NotificationDTO::class.java.name
        val notificationDTOClassSimpleName = NotificationDTO::class.simpleName
        val notificationTypeMapping = "$notificationDTOClassSimpleName:$notificationDTOClassName"

        return mapOf(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to String::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            ProducerConfig.SECURITY_PROVIDERS_CONFIG to "PLAINTEXT",
            ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG to 180000,
            JsonSerializer.TYPE_MAPPINGS to notificationTypeMapping
        )
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun transferNotificationTopic(): NewTopic {
        return NewTopic("transfer-notification", 1, 1.toShort())
    }


}