package com.payment.paymentapi.core.usecase.transfer

import com.payment.paymentapi.core.entity.Transfer
import com.payment.paymentapi.dataprovider.client.authservice.AuthServiceClient
import com.payment.paymentapi.dataprovider.database.postgres.repository.TransferRepository
import com.payment.paymentapi.entrypoint.api.dto.TransferRequest
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