package com.payment.paymentflowchallenge.dataprovider.database.postgres

import com.payment.paymentflowchallenge.core.entity.Transfer
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface TransferenceRepository: ReactiveCrudRepository<Transfer, Long>
