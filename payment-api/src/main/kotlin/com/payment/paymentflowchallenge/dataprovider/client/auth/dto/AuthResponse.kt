package com.payment.paymentflowchallenge.dataprovider.client.auth.dto

data class AuthResponse(
    private val status: String,
    private val data: AuthResponseData
)

data class AuthResponseData(
    private val authorization: Boolean
)