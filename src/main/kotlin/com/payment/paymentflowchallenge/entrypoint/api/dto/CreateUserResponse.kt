package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.User
import java.math.BigDecimal

class CreateUserResponse(
    private val id: Long,
    private val fullName: String,
    private val documentNumber: String,
    private val email: String,
    private val role: UserRoleEnum,
    private val balance: BigDecimal
) {
    companion object {
        fun fromEntity(user: User): CreateUserResponse {
            return CreateUserResponse(
                user.id!!,
                user.fullName,
                user.documentNumber,
                user.email,
                user.role,
                user.balance
            )
        }
    }
}
