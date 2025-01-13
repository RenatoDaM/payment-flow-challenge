package com.payment.paymentapi.dataprovider.database.postgres.repository

import com.payment.paymentapi.core.entity.Transfer
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TransferRepository: ReactiveCrudRepository<Transfer, Long>
