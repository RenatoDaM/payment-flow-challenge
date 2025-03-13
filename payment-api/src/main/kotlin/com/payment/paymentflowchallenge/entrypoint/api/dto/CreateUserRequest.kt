package com.payment.paymentflowchallenge.entrypoint.api.dto

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.common.validation.DocumentNumber
import com.payment.paymentflowchallenge.core.entity.User
import jakarta.validation.constraints.*
import java.math.BigDecimal

data class CreateUserRequest (
    @field:NotBlank
    @field:Size(max = 255)
     val fullName: String?,
    @field:NotBlank
    @field:Size(max = 40)
    @field:DocumentNumber
     val documentNumber: String?,
    @field:NotBlank
    @field:Size(max = 255)
    @field:Email
     val email: String?,
    @field:NotBlank
    @field:Size(max = 255, min = 8)
    val password: String?,
    @field:NotNull
     val role: UserRoleEnum
) {
    fun toEntity(): User {
        return User(
            id = null,
            email = email!!,
            password = password!!,
            fullName = fullName!!,
            documentNumber = documentNumber!!,
            role = role,
            balance = BigDecimal(0.00),
            version = null
        )
    }
}