package com.payment.paymentflowchallenge.dataprovider.client.auth

import com.payment.paymentflowchallenge.dataprovider.client.auth.dto.AuthResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class AuthServiceClient (
    @Qualifier("authService") private val authServiceClient: WebClient
) {
    fun authenticate(): Mono<AuthResponse> {
        return authServiceClient.get()
            .uri("/authorize")
            .retrieve()
            .bodyToMono(AuthResponse::class.java)
    }
}