package com.payment.paymentapi.dataprovider.client.authservice.dto

data class AuthResponse(
    val status: String,
    val data: AuthResponseData
)

data class AuthResponseData(
    val authorization: Boolean
)