package com.payment.paymentflowchallenge.core.usecase

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.Transfer
import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.core.usecase.transfer.TransferUseCase
import com.payment.paymentflowchallenge.core.usecase.user.FindUserUseCase
import com.payment.paymentflowchallenge.core.usecase.user.UpdateUserBalanceUseCase
import com.payment.paymentflowchallenge.dataprovider.client.auth.AuthServiceClient
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.TransferRepository
import com.payment.paymentflowchallenge.dataprovider.queue.kafka.KafkaQueueProducer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*
import org.mockito.kotlin.anyVararg
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.LocalDateTime

class TransferUseCaseTest {
    private lateinit var transferUseCase: TransferUseCase
    private lateinit var transferRepository: TransferRepository
    private lateinit var authServiceClient: AuthServiceClient
    private lateinit var updateUserBalanceUseCase: UpdateUserBalanceUseCase
    private lateinit var findUserUseCase: FindUserUseCase
    private lateinit var kafkaQueueProducer: KafkaQueueProducer

    @BeforeEach
    fun setup() {
        transferRepository = mock(TransferRepository::class.java)
        authServiceClient = mock(AuthServiceClient::class.java)
        updateUserBalanceUseCase = mock(UpdateUserBalanceUseCase::class.java)
        findUserUseCase = mock(FindUserUseCase::class.java)
        kafkaQueueProducer = mock(KafkaQueueProducer::class.java)
        transferUseCase = TransferUseCase(transferRepository, authServiceClient, updateUserBalanceUseCase, findUserUseCase, kafkaQueueProducer)
    }

    @Test
    fun `should fail if payer does not have enough balance`() {
        val payer = User(1L, "Payer Test", "12345678901", "payer@test.com", "password", UserRoleEnum.COMMON, BigDecimal(50))
        val payee = User(2L, "Payee Test", "10987654321", "payee@test.com", "password", UserRoleEnum.COMMON, BigDecimal(100))
        val transfer = Transfer(3L, BigDecimal(200), payer.id!!, payee.id!!, LocalDateTime.now())

        `when`(findUserUseCase.findUserById(payer.id!!))
            .thenReturn(Mono.just(payer))

        `when`(findUserUseCase.findUserById(payee.id!!))
            .thenReturn(Mono.just(payee))

        `when`(authServiceClient.authenticate())
            .thenReturn(Mono.empty())

        `when`(transferRepository.save(any()))
            .thenReturn(Mono.just(transfer.copy(id = 3L)))

        `when`(updateUserBalanceUseCase.updateUserBalanceById(payer.id!!, BigDecimal(-150)))
            .thenReturn(Mono.empty())

        `when`(updateUserBalanceUseCase.updateUserBalanceById(payee.id!!, BigDecimal(300)))
            .thenReturn(Mono.empty())

        StepVerifier.create(transferUseCase.transfer(transfer))
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    @Test
    fun `should fail if payer is merchant type`() {
        val payer = User(1L, "Merchant Test", "12345678901", "merchant@test.com", "password", UserRoleEnum.MERCHANT, BigDecimal(500))
        val payee = User(2L, "Payee Test", "10987654321", "payee@test.com", "password", UserRoleEnum.COMMON, BigDecimal(100))
        val transfer = Transfer(3L, BigDecimal(100), payer.id!!, payee.id!!, LocalDateTime.now())

        `when`(findUserUseCase.findUserById(payer.id!!))
            .thenReturn(Mono.just(payer))

        `when`(findUserUseCase.findUserById(payee.id!!))
            .thenReturn(Mono.just(payee))

        `when`(authServiceClient.authenticate())
            .thenReturn(Mono.empty())

        `when`(transferRepository.save(any()))
            .thenReturn(Mono.just(transfer.copy(id = 3L)))

        `when`(updateUserBalanceUseCase.updateUserBalanceById(ArgumentMatchers.eq(payee.id!!), anyVararg(BigDecimal::class)))
            .thenReturn(Mono.empty())

        `when`(updateUserBalanceUseCase.updateUserBalanceById(ArgumentMatchers.eq(payer.id!!), anyVararg(BigDecimal::class)))
            .thenReturn(Mono.empty())

        StepVerifier.create(transferUseCase.transfer(transfer))
            .expectError(IllegalArgumentException::class.java)
            .verify()
    }

    @Test
    fun `should succeed when payer has enough balance and is not a merchant`() {
        val payer = User(1L, "Payer Test", "12345678901", "payer@test.com", "password", UserRoleEnum.COMMON, BigDecimal(500))
        val payee = User(2L, "Payee Test", "10987654321", "payee@test.com", "password", UserRoleEnum.MERCHANT, BigDecimal(100))
        val transfer = Transfer(1L, BigDecimal(200), payer.id!!, payee.id!!, LocalDateTime.now())

        `when`(findUserUseCase.findUserById(payer.id!!))
            .thenReturn(Mono.just(payer))

        `when`(findUserUseCase.findUserById(payee.id!!))
            .thenReturn(Mono.just(payee))

        `when`(authServiceClient.authenticate())
            .thenReturn(Mono.empty())

        `when`(updateUserBalanceUseCase.updateUserBalanceById(1L, BigDecimal(300)))
            .thenReturn(Mono.empty())

        `when`(updateUserBalanceUseCase.updateUserBalanceById(2L, BigDecimal(300)))
            .thenReturn(Mono.empty())

        `when`(transferRepository.save(any()))
            .thenReturn(Mono.just(transfer))

        StepVerifier.create(transferUseCase.transfer(transfer))
            .expectNext(transfer)
            .verifyComplete()
    }
}
