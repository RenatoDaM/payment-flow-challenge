package com.payment.paymentflowchallenge.dataprovider.database.postgres.repository

import com.payment.paymentflowchallenge.core.entity.Transfer
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TransferRepository: ReactiveCrudRepository<Transfer, Long>
