package com.payment.paymentflowchallenge.core.usecase.user

import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import com.payment.paymentflowchallenge.entrypoint.api.dto.UserResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FindUserUseCase (
    private val userRepository: UserRepository
) {
    fun findUserById(userId: Long): Mono<UserResponse> {
        return userRepository.findById(userId)
            .map { entity -> UserResponse.fromEntity(entity) }
    }
}