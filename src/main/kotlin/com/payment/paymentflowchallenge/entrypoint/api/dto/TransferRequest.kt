package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.entity.Transfer
import java.math.BigDecimal

data class TransferRequest (
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