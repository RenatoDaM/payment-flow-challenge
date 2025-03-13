package com.payment.paymentflowchallenge.entrypoint.api.controller

import com.payment.paymentflowchallenge.core.usecase.user.CreateUserUseCase
import com.payment.paymentflowchallenge.entrypoint.api.dto.CreateUserRequest
import com.payment.paymentflowchallenge.entrypoint.api.dto.TransferRequest
import com.payment.paymentflowchallenge.entrypoint.api.dto.UserResponse
import jakarta.validation.Valid
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn
import org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import java.math.BigDecimal

@RestController
@RequestMapping("/v1/users")
class UserController(private val createUserUseCase: CreateUserUseCase) {

    @PostMapping
    fun createUser(@Valid @RequestBody createUserRequest: CreateUserRequest): Mono<EntityModel<UserResponse>> {
        return createUserUseCase.createUser(createUserRequest)
            .flatMap { user ->
                val userResponse = UserResponse.fromEntity(user)

                linkTo(methodOn(TransferController::class.java).transfer(dummyTransferRequest()))
                    .withRel("transfers")
                    .toMono()
                    .map { transferLink -> EntityModel.of(userResponse, transferLink) }
            }
    }

    private fun dummyTransferRequest() =
        TransferRequest(BigDecimal.ZERO, 0 , 0)
}