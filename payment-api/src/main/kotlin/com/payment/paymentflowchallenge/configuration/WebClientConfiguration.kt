package com.payment.paymentflowchallenge.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebClientConfiguration(
    @Value("\${auth.service.base-url}")
    private val authServiceBaseUrl: String
) {

    @Bean(name = ["authService"])
    fun authServiceClient(): WebClient {
        return WebClient.builder()
            .baseUrl(authServiceBaseUrl)
            .build()
    }

}