package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.User
import java.math.BigDecimal

class CreateUserResponse(
    val id: Long,
    val fullName: String,
    val documentNumber: String,
    val email: String,
    val role: UserRoleEnum,
    val balance: BigDecimal
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
