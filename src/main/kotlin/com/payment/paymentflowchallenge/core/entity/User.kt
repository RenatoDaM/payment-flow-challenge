package com.payment.paymentflowchallenge.core.entity

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("users")
data class User(
    @Id
    private val id: Long,
    private val fullName: String,
    private val documentNumber: String,
    private val email: String,
    private val password: String,
    private val role: UserRoleEnum
)