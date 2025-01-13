package com.payment.paymentapi.core.entity

import com.payment.paymentapi.core.common.enums.UserRoleEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal

@Table("users")
data class User(
    @Id val id: Long?,
    val fullName: String,
    val documentNumber: String,
    val email: String,
    private val password: String,
    val role: UserRoleEnum,
    val balance: BigDecimal
)