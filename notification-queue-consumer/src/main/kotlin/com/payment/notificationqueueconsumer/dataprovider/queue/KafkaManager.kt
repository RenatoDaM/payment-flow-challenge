package com.payment.notificationqueueconsumer.dataprovider.queue

import org.springframework.kafka.config.KafkaListenerEndpointRegistry
import org.springframework.kafka.listener.MessageListenerContainer
import org.springframework.stereotype.Component

@Component
class KafkaManager(private val registry: KafkaListenerEndpointRegistry) {
    fun pause() {
        registry.listenerContainers.forEach(MessageListenerContainer::pause)
    }

    fun resume() {
        registry.listenerContainers.forEach(MessageListenerContainer::resume)
    }

    fun isPaused(): Boolean {
        return registry.listenerContainers.all { it.isPauseRequested }
    }
}