package com.payment.paymentflowchallenge.dataprovider.queue.kafka.dto

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import java.math.BigDecimal

@Builder
@AllArgsConstructor
@Data
data class NotificationDTO (
    val email: String,
    val transferValue: BigDecimal,
    val payer: Long
)