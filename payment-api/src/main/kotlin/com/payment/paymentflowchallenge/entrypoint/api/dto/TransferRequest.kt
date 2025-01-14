package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.entity.Transfer
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import java.math.BigDecimal

data class TransferRequest (
    @field:DecimalMin(value = "0.0", inclusive = false)
    @field:Digits(integer=10, fraction=2)
    val value: BigDecimal,
    val payer: Long,
    val payee: Long
) {
    fun toEntity(): Transfer {
        return Transfer(
            id = null,
            value = this.value,
            payer = this.payer,
            payee = this.payee,
            transferDate = null
        )
    }
}