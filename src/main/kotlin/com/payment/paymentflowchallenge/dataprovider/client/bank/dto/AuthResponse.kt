package com.payment.paymentflowchallenge.dataprovider.client.bank.dto

data class AuthResponse(
    val status: String,
    val data: AuthResponseData
)

data class AuthResponseData(
    val authorization: Boolean
)