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

    @Test
    fun `should throw OptimisticLockingFailureException when two transactions update the same entity concurrently`() {
        userRepository.save(
            User(null, "Payer Test", "12345678901", "payer@test.com", "password",
                UserRoleEnum.COMMON, BigDecimal(500), null)
        ).block()!!

         userRepository.save(
            User(null, "Payee Test", "10987654321", "payee@test.com", "password",
                UserRoleEnum.MERCHANT, BigDecimal(100), null)
        ).block()!!

        // this test should not be dependent on external API
        whenever(authServiceClient.authenticate())
            .thenReturn(Mono.empty())

        val transfer1 = Transfer(null, BigDecimal(200), 1, 2, LocalDateTime.now())
        val transfer2 = Transfer(null, BigDecimal(300), 1, 2, LocalDateTime.now())



        val transaction1 = transferUseCase.transfer(transfer1)
        val transaction2 = transferUseCase.transfer(transfer2)

        StepVerifier.create(transaction1)
            .expectErrorMatches { it is OptimisticLockingFailureException }
            .verify()

        StepVerifier.create(transaction2)
            .expectErrorMatches { it is OptimisticLockingFailureException }
            .verify()
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