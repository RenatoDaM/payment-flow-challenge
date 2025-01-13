package com.payment.paymentapi.dataprovider.database.postgres.repository

import com.payment.paymentapi.core.entity.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface UserRepository: ReactiveCrudRepository<User, Long>