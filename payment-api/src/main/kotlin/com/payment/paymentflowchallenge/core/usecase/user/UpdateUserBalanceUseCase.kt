package com.payment.paymentflowchallenge.core.usecase.user

import com.payment.paymentflowchallenge.core.entity.User
import com.payment.paymentflowchallenge.dataprovider.database.postgres.repository.UserRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class UpdateUserBalanceUseCase (
    private val userRepository: UserRepository
) {
    fun updateUserBalance(user: User, newBalance: BigDecimal): Mono<User> {
        val updatedUser = user.copy(balance = newBalance)
        return userRepository.save(updatedUser)
    }
}