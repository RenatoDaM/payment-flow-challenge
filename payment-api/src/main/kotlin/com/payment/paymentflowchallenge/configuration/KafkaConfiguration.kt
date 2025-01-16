package com.payment.paymentflowchallenge.configuration

import org.apache.kafka.clients.admin.Admin
import org.apache.kafka.clients.producer.KafkaProducer
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException
import java.io.InputStream
import java.util.*

@Configuration
class KafkaConfiguration {

    @Bean
    fun kafkaProducer(): KafkaProducer<String, String> {
        val kafkaProps = loadProperties()
        return KafkaProducer(kafkaProps)
    }

    @Bean
    fun adminClient(): Admin {
        val kafkaProps = loadProperties()
        return Admin.create(kafkaProps)
    }

    private fun loadProperties(): Properties {
        return try {
            val props = Properties()
            val inputStream: InputStream = this::class.java.classLoader.getResourceAsStream("producer.properties")
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

@ConfigurationProperties(prefix = "kafka")
data class TopicInitializerProperties(
    val topics: List<TopicSetupSpecification>
)

data class TopicSetupSpecification(
    val name: String,
    val partitions: Int,
    val replicationFactor: Short = 1
)