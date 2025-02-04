package com.payment.notificationqueueconsumer.core.usecase

import com.payment.notificationqueueconsumer.dataprovider.client.notification.NotificationServiceClient
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class SendNotificationUseCase (
    private val notificationServiceClient: NotificationServiceClient
) {
    fun sendNotification(notificationDTO: NotificationDTO): Mono<ResponseEntity<Void>> {
        return notificationServiceClient.notify(notificationDTO)
    }
}