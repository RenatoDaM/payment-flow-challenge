package com.payment.paymentflowchallenge.dataprovider.database.postgres

import com.payment.paymentflowchallenge.core.entity.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRepository: ReactiveCrudRepository<User, Long>