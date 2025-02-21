package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.common.validation.DocumentNumber
import com.payment.paymentflowchallenge.core.entity.User
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class CreateUserRequest (
    @field:NotBlank
    @field:Size(max = 255)
    private val fullName: String,
    @field:NotBlank
    @field:Size(max = 40)
    @field:DocumentNumber
    private val documentNumber: String,
    @field:NotBlank
    @field:Size(max = 255)
    @field:Email
    private val email: String,
    @field:NotBlank
    @field:Size(max = 255, min = 8)
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