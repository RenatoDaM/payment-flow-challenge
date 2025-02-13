package com.payment.notificationqueueconsumer.configuration

import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer


@Configuration
class KafkaConfiguration {

    @Bean
    fun consumerFactory(): ConsumerFactory<String, NotificationDTO> {
        val payloadJsonDeserializer: JsonDeserializer<NotificationDTO> = JsonDeserializer<NotificationDTO>()
        payloadJsonDeserializer.addTrustedPackages()

        return DefaultKafkaConsumerFactory(kafkaProperties(), StringDeserializer(), JsonDeserializer())
    }

    private fun kafkaProperties(): Map<String, Any> {
        val notificationDTOClassName = NotificationDTO::class.java.name
        val notificationDTOClassSimpleName = NotificationDTO::class.simpleName
        val notificationTypeMapping = "$notificationDTOClassSimpleName:$notificationDTOClassName"

        return mapOf(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "PLAINTEXT://localhost:9092",
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonDeserializer::class.java,
            ConsumerConfig.GROUP_ID_CONFIG to "payment",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
            JsonDeserializer.TYPE_MAPPINGS to notificationTypeMapping,
            JsonDeserializer.TRUSTED_PACKAGES to "com.payment.notificationqueueconsumer.dataprovider.client.notification.dto"
            )
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, NotificationDTO> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, NotificationDTO>()
        factory.consumerFactory = consumerFactory()
        return factory
    }
}