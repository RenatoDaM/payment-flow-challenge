package com.payment.paymentflowchallenge.core.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transfers")
data class Transfer(
    @Id
    private val id: Long,
    private val value: BigDecimal,
    private val payer: Long,
    private val payee: Long,
    private val transferenceDate: LocalDateTime
)