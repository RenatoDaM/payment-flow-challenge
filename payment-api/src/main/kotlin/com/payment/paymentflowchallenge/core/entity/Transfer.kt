package com.payment.paymentflowchallenge.core.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.time.LocalDateTime

@Table("transfers")
data class Transfer(
    @Id val id: Long?,
    private val value: BigDecimal,
    private val payer: Long,
    private val payee: Long,
    @CreatedDate
    private val transferDate: LocalDateTime?
)