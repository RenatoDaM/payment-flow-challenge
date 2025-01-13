package com.payment.paymentapi.entrypoint.api.controller

import com.payment.paymentapi.core.entity.Transfer
import com.payment.paymentapi.core.usecase.transfer.TransferUseCase
import com.payment.paymentapi.entrypoint.api.dto.TransferRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TransferController(private val transferUseCase: TransferUseCase) {
  @PostMapping( "/transfer")
  fun transfer(@RequestBody transferRequest: TransferRequest): Mono<Transfer> {
    return transferUseCase.transfer(transferRequest)
  }
}


