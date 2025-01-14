package com.payment.paymentflowchallenge.entrypoint.api.controller

import com.payment.paymentflowchallenge.core.usecase.user.CreateUserUseCase
import com.payment.paymentflowchallenge.entrypoint.api.dto.CreateUserRequest
import com.payment.paymentflowchallenge.entrypoint.api.dto.UserResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController(private val createUserUseCase: CreateUserUseCase) {
    @PostMapping( "/users")
    fun transfer(@Valid @RequestBody createUserRequest: CreateUserRequest): Mono<UserResponse> {
        return createUserUseCase.createUser(createUserRequest)
    }
}