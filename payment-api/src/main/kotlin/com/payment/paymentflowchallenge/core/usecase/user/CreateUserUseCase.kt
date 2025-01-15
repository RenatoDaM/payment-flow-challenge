package com.payment.paymentflowchallenge.core.usecase.user

import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import com.payment.paymentflowchallenge.entrypoint.api.dto.CreateUserRequest
import com.payment.paymentflowchallenge.entrypoint.api.dto.UserResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CreateUserUseCase (
    private val userRepository: UserRepository
) {
    fun createUser(createUserRequest: CreateUserRequest): Mono<User> {
        return userRepository.save(createUserRequest.toEntity())
    }
}