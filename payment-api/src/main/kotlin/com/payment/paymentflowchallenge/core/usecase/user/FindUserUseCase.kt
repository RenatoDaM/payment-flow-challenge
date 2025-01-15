package com.payment.paymentflowchallenge.core.usecase.user

import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FindUserUseCase (
    private val userRepository: UserRepository
) {
    fun findUserById(userId: Long): Mono<User> {
        return userRepository.findById(userId)
            .switchIfEmpty(Mono.error(IllegalArgumentException("User not found with ID: $userId")))
    }
}