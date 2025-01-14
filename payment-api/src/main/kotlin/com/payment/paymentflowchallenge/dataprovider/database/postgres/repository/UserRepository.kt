package com.payment.paymentflowchallenge.dataprovider.database.postgres.repository

import com.payment.paymentflowchallenge.core.entity.User
import org.springframework.data.r2dbc.repository.Modifying
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono
import java.math.BigDecimal

interface UserRepository: ReactiveCrudRepository<User, Long> {
    @Modifying
    @Query("UPDATE users SET balance = :newBalance WHERE id = :userId")
    fun updateUserBalanceById(@Param("userId") userId: Long, @Param("newBalance") newBalance: BigDecimal): Mono<Void>
}