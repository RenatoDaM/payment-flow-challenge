package com.payment.paymentflowchallenge.core.usecase.user

import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import com.payment.paymentflowchallenge.entrypoint.api.dto.CreateUserRequest
import com.payment.paymentflowchallenge.entrypoint.exception.ResourceAlreadyExistsException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
class CreateUserUseCase (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun createUser(createUserRequest: CreateUserRequest): Mono<User> {
        val encodedPassword = passwordEncoder.encode(createUserRequest.password)
        val userEntity = createUserRequest.toEntity().copy(password = encodedPassword)

        return userRepository.findByEmailOrDocumentNumber(userEntity.email, userEntity.documentNumber)
            .flatMap<User> { Mono.error(ResourceAlreadyExistsException("User already exists")) }
            .switchIfEmpty(Mono.defer { userRepository.save(userEntity) })
    }

}