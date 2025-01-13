package com.payment.paymentapi.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration {

    @Bean(name = ["authService"])
    fun authServiceClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://util.devi.tools/api/v2")
            .build()
    }

    @Bean(name = ["notificationService"])
    fun notificationServiceClient(): WebClient {
        return WebClient.builder()
            .baseUrl("https://util.devi.tools/api/v1")
            .build()
    }

}