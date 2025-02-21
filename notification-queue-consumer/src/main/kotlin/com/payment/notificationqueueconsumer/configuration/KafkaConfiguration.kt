package com.payment.notificationqueueconsumer.configuration

import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.*
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class KafkaConfiguration(
    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private val bootstrapAddress: String
) {

    @Bean
    fun producerFactory(): ProducerFactory<String, Any> {
        return DefaultKafkaProducerFactory(kafkaProperties(), StringSerializer(), JsonSerializer())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory())
    }

    @Bean
    fun consumerFactory(): ConsumerFactory<String, Any> {
        return DefaultKafkaConsumerFactory(kafkaProperties(), StringDeserializer(), JsonDeserializer())
    }

    private fun kafkaProperties(): Map<String, Any> {
        val notificationDTOTypeMapping = "${NotificationDTO::class.simpleName}:${NotificationDTO::class.java.name}"
        val notificationDTOPackageName = NotificationDTO::class.java.`package`

        return mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to String::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to "payment",
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to bootstrapAddress,
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG to String::class.java,
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG to JsonSerializer::class.java,
            ProducerConfig.SECURITY_PROVIDERS_CONFIG to "PLAINTEXT",
            ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG to 180000,
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            JsonDeserializer.TYPE_MAPPINGS to notificationDTOTypeMapping,
            JsonDeserializer.TRUSTED_PACKAGES to notificationDTOPackageName
            )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, NotificationDTO> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, NotificationDTO>()
        factory.consumerFactory = consumerFactory()
        factory.containerProperties.ackMode = ContainerProperties.AckMode.RECORD
        return factory
    }
}