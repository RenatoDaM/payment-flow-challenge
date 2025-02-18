package com.payment.notificationqueueconsumer.configuration

import com.payment.notificationqueueconsumer.integration.CircuitBreakerKafkaConsumerConfigurationTest.Companion.kafka
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.Network
import org.testcontainers.kafka.KafkaContainer

object KafkaTestContainer {
    private val network: Network = Network.newNetwork()

    fun createKafkaTestContainer(): KafkaContainer {
        return KafkaContainer("apache/kafka:3.7.2")
            .withNetwork(network)
    }

}
