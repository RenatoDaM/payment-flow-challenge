package com.payment.paymentflowchallenge.core.usecase.transfer

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.Transfer
import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.core.usecase.user.FindUserUseCase
import com.payment.paymentflowchallenge.core.usecase.user.UpdateUserBalanceUseCase
import com.payment.paymentflowchallenge.dataprovider.client.auth.AuthServiceClient
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.TransferRepository
import com.payment.paymentflowchallenge.dataprovider.queue.kafka.KafkaQueueProducer
import com.payment.paymentflowchallenge.dataprovider.queue.kafka.dto.NotificationDTO
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class TransferUseCase (
    private val transferRepository: TransferRepository,
    private val authServiceClient: AuthServiceClient,
    private val updateUserBalanceUseCase: UpdateUserBalanceUseCase,
    private val findUserUseCase: FindUserUseCase,
    private val kafkaQueueProducer: KafkaQueueProducer
) {

    @Value("\${kafka.topics.transfer-notification}")
    private lateinit var topicName: String

    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @Transactional
    fun transfer(transfer: Transfer): Mono<Transfer> {
        val payerMono = findUserUseCase.findUserById(transfer.payer)
        val payeeMono = findUserUseCase.findUserById(transfer.payee)

        return authServiceClient.authenticate().then(
            payerMono.zipWith(payeeMono).flatMap { tuple ->

                val payer = tuple.t1
                val payee = tuple.t2

                validatePayerBalance(payer.balance, transfer.value)
                    .then(validatePayerRole(payer))
                    .then(executeTransfer(payer, transfer, payee))
                    .doOnSuccess {
                        log.info("Transfer successful: from payer ${transfer.payer} to payee ${transfer.payee} with value ${transfer.value}")
                        kafkaQueueProducer.send(topicName, NotificationDTO(transferId = it.id!!, email = payee.email, transferValue = transfer.value, payer = transfer.payee))
                    }
            }
        )
    }

    private fun validatePayerBalance(payerBalance: BigDecimal, transferValue: BigDecimal): Mono<Void> =
        if (payerBalance > transferValue) Mono.empty()
        else Mono.error(IllegalArgumentException("Payer doesn't have enough money"))

    private fun validatePayerRole(payer: User): Mono<Void> {
        return if (payer.role == UserRoleEnum.MERCHANT) {
            Mono.error(IllegalArgumentException("Merchants cannot do transfers"))
        } else {
            Mono.empty()
        }
    }

    private fun executeTransfer(payer: User, transfer: Transfer, payee: User): Mono<Transfer> {
        val payerFinalBalance = payer.balance - transfer.value
        val payeeFinalBalance = payee.balance + transfer.value

        return Mono.zip(
            updateUserBalanceUseCase.updateUserBalanceById(payer.id!!, payerFinalBalance),
            updateUserBalanceUseCase.updateUserBalanceById(payee.id!!, payeeFinalBalance)
        ).then(transferRepository.save(transfer))
    }
}
