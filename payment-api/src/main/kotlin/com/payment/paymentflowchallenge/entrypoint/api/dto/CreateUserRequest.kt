package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.User
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Digits
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateUserRequest (
    @field:NotBlank
    private val fullName: String,
    @field:NotBlank
    private val documentNumber: String,
    @field:NotBlank
    @field:Email
    private val email: String,
    @field:NotBlank
    private val password: String,
    @field:NotNull
    private val role: UserRoleEnum,
    @field:DecimalMin(value = "0.0", inclusive = false)
    @field:Digits(integer=10, fraction=2)
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
            balance = balance,
            version = null
        )
    }
}