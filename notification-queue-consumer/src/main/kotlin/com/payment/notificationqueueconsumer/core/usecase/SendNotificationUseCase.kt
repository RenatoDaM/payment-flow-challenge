package com.payment.notificationqueueconsumer.core.usecase

import com.payment.notificationqueueconsumer.dataprovider.client.notification.NotificationServiceClient
import com.payment.notificationqueueconsumer.dataprovider.client.notification.dto.NotificationDTO
import org.springframework.stereotype.Service

@Service
class SendNotificationUseCase (
    private val notificationServiceClient: NotificationServiceClient
) {
    suspend fun sendNotification(notificationDTO: NotificationDTO) {
        notificationServiceClient.notify(notificationDTO)
    }
}