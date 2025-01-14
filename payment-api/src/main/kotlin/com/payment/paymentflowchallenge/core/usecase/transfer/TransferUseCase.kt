package com.payment.paymentflowchallenge.core.usecase.transfer

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.Transfer
import com.payment.paymentflowchallenge.core.usecase.user.FindUserUseCase
import com.payment.paymentflowchallenge.core.usecase.user.UpdateUserBalanceUseCase
import com.payment.paymentflowchallenge.dataprovider.client.bank.AuthServiceClient
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.TransferRepository
import com.payment.paymentflowchallenge.entrypoint.api.dto.TransferRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class TransferUseCase (
    private val transferRepository: TransferRepository,
    private val authServiceClient: AuthServiceClient,
    private val updateUserBalanceUseCase: UpdateUserBalanceUseCase,
    private val findUserUseCase: FindUserUseCase
) {

    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun transfer(transferRequest: TransferRequest): Mono<Transfer> {
        val payeeMono = findUserUseCase.findUserById(transferRequest.payee)
        val payerMono = findUserUseCase.findUserById(transferRequest.payer)

        return authServiceClient.authenticate().then(
            payerMono.zipWith(payeeMono).flatMap { tuple ->
                val payer = tuple.t1
                val payee = tuple.t2

                val hasEnoughMoney = payer.balance > transferRequest.value
                if (!hasEnoughMoney)
                    return@flatMap Mono.error<Transfer>(IllegalArgumentException("payer doesn't have enough money"))

                if (payer.role == UserRoleEnum.MERCHANT)
                    return@flatMap Mono.error<Transfer>(IllegalArgumentException("merchants can not do transfers"))

                val payerFinalBalance = payer.balance - transferRequest.value
                val payeeFinalBalance = payee.balance + transferRequest.value

                updateUserBalanceUseCase.updateUserBalanceById(payer.id, payerFinalBalance)
                    .then(updateUserBalanceUseCase.updateUserBalanceById(payee.id, payeeFinalBalance))
                    .then(transferRepository.save(transferRequest.toEntity()))
            }
        ).doOnSuccess {
            log.info("transfer realized with success $it")
        }
    }
}