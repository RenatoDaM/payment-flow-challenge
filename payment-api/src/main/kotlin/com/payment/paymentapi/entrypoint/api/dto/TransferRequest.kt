package com.payment.paymentapi.entrypoint.api.dto

import com.payment.paymentapi.core.entity.Transfer
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import java.math.BigDecimal

data class TransferRequest (
    @field:DecimalMin(value = "0.0", inclusive = false)
    @field:Digits(integer=10, fraction=2)
    private val value: BigDecimal,
    private val payer: Long,
    private val payee: Long
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