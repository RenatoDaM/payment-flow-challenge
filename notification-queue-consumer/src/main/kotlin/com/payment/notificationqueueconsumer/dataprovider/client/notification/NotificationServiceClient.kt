package com.payment.notificationqueueconsumer.dataprovider.client.notification

import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
class NotificationServiceClient(
    @Qualifier("notificationService") private val authServiceClient: WebClient,
) {

    @CircuitBreaker(name = "notification-service-A")
    fun notify(notificationDTO: NotificationDTO): Mono<ResponseEntity<Void>> {
        return authServiceClient.post()
            .uri("/notify")
            .bodyValue(notificationDTO)
            .retrieve()
            .onStatus({ t -> t.isError }, { it.createException() })
            .toBodilessEntity()
    }
}
