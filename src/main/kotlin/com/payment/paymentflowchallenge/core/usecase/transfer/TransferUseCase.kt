package com.payment.paymentflowchallenge.core.usecase.transfer

import com.payment.paymentflowchallenge.core.entity.Transfer
import com.payment.paymentflowchallenge.dataprovider.client.bank.AuthServiceClient
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.TransferRepository
import com.payment.paymentflowchallenge.entrypoint.api.dto.TransferRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class TransferUseCase (
    private val transferRepository: TransferRepository,
    private val authServiceClient: AuthServiceClient
) {
    fun transfer(transferRequest: TransferRequest): Mono<Transfer> {
        authServiceClient.authenticate()
        return transferRepository.save(transferRequest.toEntity())
    }
}