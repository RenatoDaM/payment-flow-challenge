package com.payment.paymentflowchallenge.integration

import com.payment.paymentflowchallenge.core.common.enums.UserRoleEnum
import com.payment.paymentflowchallenge.core.entity.Transfer
import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.core.usecase.transfer.TransferUseCase
import com.payment.paymentflowchallenge.dataprovider.client.auth.AuthServiceClient
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import com.payment.paymentflowchallenge.dataprovider.queue.kafka.KafkaQueueProducer
import org.junit.jupiter.api.*
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.testcontainers.service.connection.ServiceConnection
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.LocalDateTime

@Testcontainers
@SpringBootTest
class TransferOptimisticLockingTest (
    @Autowired private val transferUseCase: TransferUseCase,
    @Autowired private val userRepository: UserRepository
) {

    @MockitoBean
    private lateinit var authServiceClient: AuthServiceClient

    @MockitoBean
    private lateinit var kafkaQueueProducer: KafkaQueueProducer

    private lateinit var payer: User
    private lateinit var payee: User

    @BeforeEach
    fun setup() {
        userRepository.deleteAll().block()

        val usersBalance = BigDecimal(1000.00)

        payer = userRepository.save(
            User(null, "Payer Test", "12345678901",
                "payer@test.com", "password", UserRoleEnum.COMMON, usersBalance, null)
        ).block()!!

        payee = userRepository.save(
            User(null, "Payee Test", "10987654321",
                "payee@test.com", "password", UserRoleEnum.MERCHANT, usersBalance, null
            )
        ).block()!!
    }

    @Test
    fun `should throw OptimisticLockingFailureException when two transactions update the same entity concurrently`() {
        val transactionsValue = BigDecimal(50.00).setScale(2)
        // one transaction should fail due optimistic locking
        val payerExpectedFinalBalance = payer.balance - transactionsValue
        val payeeExpectedFinalBalance = payee.balance + transactionsValue

        whenever(authServiceClient.authenticate()).thenReturn(Mono.empty())

        val transfer1 = Transfer(null, transactionsValue, payer.id!!, payee.id!!, LocalDateTime.now())
        val transfer2 = Transfer(null, transactionsValue, payer.id!!, payee.id!!, LocalDateTime.now())

        val transaction1 = transferUseCase.transfer(transfer1)
        val transaction2 = transferUseCase.transfer(transfer2)

        val concurrentTransfers = Mono.zip(transaction1, transaction2)

        StepVerifier.create(concurrentTransfers)
            .expectError(OptimisticLockingFailureException::class.java)
            .verify()

        StepVerifier.create(userRepository.findById(payer.id!!))
            .assertNext { updatedPayer ->
                assert(updatedPayer.balance == payerExpectedFinalBalance) {
                    "Payer balance mismatch: expected $payerExpectedFinalBalance but was ${updatedPayer.balance}"
                }
            }
            .verifyComplete()

        StepVerifier.create(userRepository.findById(payee.id!!))
            .assertNext { updatedPayee ->
                assert(updatedPayee.balance == payeeExpectedFinalBalance) {
                    "Payee balance mismatch: expected $payeeExpectedFinalBalance but was ${updatedPayee.balance}"
                }
            }
            .verifyComplete()
    }


    companion object {
        @Container
        @JvmStatic
        @ServiceConnection
        val postgres = PostgreSQLContainer("postgres:16.3")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgres.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            postgres.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }
}