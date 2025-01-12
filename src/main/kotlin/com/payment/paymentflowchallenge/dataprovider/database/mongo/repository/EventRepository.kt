package com.payment.paymentflowchallenge.dataprovider.database.mongo.repository

import com.payment.paymentflowchallenge.core.entity.Transfer
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface EventRepository : ReactiveMongoRepository<Transfer, String>