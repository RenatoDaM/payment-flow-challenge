package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.User
import java.math.BigDecimal

data class CreateUserRequest (
    private val fullName: String,
    private val documentNumber: String,
    private val email: String,
    private val password: String,
    private val role: UserRoleEnum,
    private val balance: BigDecimal
) {
    fun toEntity(): User {
        return User(
            id = null,
            email = email,
            password = password,
            fullName = fullName,
            documentNumber = documentNumber,
            role = role,
            balance = balance
        )
    }
}