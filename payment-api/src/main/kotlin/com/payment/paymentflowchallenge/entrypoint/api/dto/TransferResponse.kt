package com.payment.paymentflowchallenge.entrypoint.api.dto

import org.springframework.hateoas.server.core.Relation
import java.math.BigDecimal
import java.time.LocalDateTime

@Relation(collectionRelation = "transfers")
data class TransferResponse(
    val id: Long,
    val value: BigDecimal,
    val payer: Long,
    val payee: Long,
    val transferDate: LocalDateTime
)