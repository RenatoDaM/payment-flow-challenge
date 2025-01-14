package com.payment.paymentflowchallenge.core.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transfers")
data class Transfer(
    @Id val id: Long?,
    val value: BigDecimal,
    val payer: Long,
    val payee: Long,
    @CreatedDate
    val transferDate: LocalDateTime?
)