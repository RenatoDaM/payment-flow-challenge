package com.payment.paymentflowchallenge.entrypoint.api.controller

import com.payment.paymentflowchallenge.dataprovider.database.mongo.repository.EventRepository

class TransferController(private val eventRepository: EventRepository) {
/*  @PostMapping( "/transfer")
  fun transfer(@RequestBody transferRequest: String) {
    eventRepository.save
      (Transference(UUID.randomUUID().toString(), transferRequest))
      .flux()
  }*/
}


