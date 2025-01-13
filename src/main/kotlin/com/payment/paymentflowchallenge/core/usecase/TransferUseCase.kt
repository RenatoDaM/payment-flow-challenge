package com.payment.paymentflowchallenge.core.usecase

import com.payment.paymentflowchallenge.dataprovider.client.bank.AuthServiceClient
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.TransferRepository
import com.payment.paymentflowchallenge.entrypoint.api.dto.TransferRequest
import org.springframework.stereotype.Service

@Service
class TransferUseCase (
    private val transferRepository: TransferRepository,
    private val authServiceClient: AuthServiceClient
) {
    fun transfer(transferRequest: TransferRequest) {
        authServiceClient.authenticate()
        transferRepository.save(transferRequest.toEntity())
    }
}