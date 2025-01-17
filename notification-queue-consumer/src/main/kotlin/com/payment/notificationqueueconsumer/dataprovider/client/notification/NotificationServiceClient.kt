package com.payment.notificationqueueconsumer.dataprovider.client.notification

import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import reactor.core.publisher.Mono

@Service
class NotificationServiceClient (
    @Qualifier("notificationService") private val authServiceClient: WebClient
) {

    fun notify(notificationDTO: NotificationDTO): Mono<ResponseEntity<Void>> {
        return authServiceClient.post()
            .uri("/notify")
            .bodyValue(notificationDTO)
            .retrieve()
            .toBodilessEntity()
    }

}