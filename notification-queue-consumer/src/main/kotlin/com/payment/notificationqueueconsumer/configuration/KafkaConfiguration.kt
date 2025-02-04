package com.payment.notificationqueueconsumer.configuration

import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.io.InputStream
import java.util.*

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaConsumer(): KafkaConsumer<String, String> {
        val kafkaProps = loadProperties()
        return KafkaConsumer(kafkaProps)
    }

    private fun loadProperties(): Properties {
        return try {
            val props = Properties()
            val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("consumer.properties")
                ?: throw RuntimeException("producer.properties file not found in classpath")

            inputStream.use { reader ->
                props.load(reader)
            }

            return props
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Failed to load Kafka properties or create producer", e)
        }
    }
}