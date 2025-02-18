package com.payment.notificationqueueconsumer.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
    @Value("\${notification.service.base-url}")
    private val notificationServiceBaseUrl: String
) {

    @Bean(name = ["notificationService"])
    fun notificationServiceClient(): WebClient {
        return WebClient.builder()
            .baseUrl(notificationServiceBaseUrl)
            .build()
    }

}