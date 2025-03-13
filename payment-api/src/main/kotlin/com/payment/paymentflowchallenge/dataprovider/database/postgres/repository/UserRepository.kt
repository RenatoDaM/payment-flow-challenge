package com.payment.paymentflowchallenge.dataprovider.database.postgres.repository

import com.payment.paymentflowchallenge.core.entity.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Mono

interface UserRepository: ReactiveCrudRepository<User, Long> {
    fun findByEmailOrDocumentNumber(email: String, documentNumber: String): Mono<User>
}