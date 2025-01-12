package com.payment.paymentflowchallenge.entrypoint.api.controller

import com.payment.paymentflowchallenge.core.usecase.TransferUseCase
import com.payment.paymentflowchallenge.entrypoint.api.dto.TransferRequest
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TransferController(private val transferUseCase: TransferUseCase) {
  @PostMapping( "/transfer")
  fun transfer(@RequestBody transferRequest: TransferRequest) {
    transferUseCase.transfer(transferRequest)
  }
}


