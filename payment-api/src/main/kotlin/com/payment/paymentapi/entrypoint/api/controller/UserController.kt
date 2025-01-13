package com.payment.paymentapi.entrypoint.api.controller

import com.payment.paymentapi.core.usecase.user.CreateUserUseCase
import com.payment.paymentapi.entrypoint.api.dto.CreateUserRequest
import com.payment.paymentapi.entrypoint.api.dto.CreateUserResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class UserController(private val createUserUseCase: CreateUserUseCase) {
    @PostMapping( "/users")
    fun transfer(@Valid @RequestBody createUserRequest: CreateUserRequest): Mono<CreateUserResponse> {
        val createUser = createUserUseCase.createUser(createUserRequest)
        return createUser
    }
}