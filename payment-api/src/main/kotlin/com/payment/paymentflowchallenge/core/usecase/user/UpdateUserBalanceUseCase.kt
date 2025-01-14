package com.payment.paymentflowchallenge.core.usecase.user

import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class UpdateUserBalanceUseCase (
    private val userRepository: UserRepository
) {
    fun updateUserBalanceById(userId: Long, newBalance: BigDecimal): Mono<Void> {
        return userRepository.updateUserBalanceById(userId, newBalance)
    }
}