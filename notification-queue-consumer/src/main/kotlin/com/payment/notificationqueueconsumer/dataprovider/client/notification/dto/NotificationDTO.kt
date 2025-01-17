package com.payment.notificationqueueconsumer.dataprovider.client.notification.dto

import java.math.BigDecimal

data class NotificationDTO(
    val email: String,
    val transferValue: BigDecimal,
    val payer: Long
)