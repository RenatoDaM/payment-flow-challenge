package com.payment.paymentflowchallenge.core.common.validation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.messaging.handler.annotation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [DocumentNumberValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class DocumentNumber(
    val message: String = "Invalid document number (CPF or CNPJ)",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class DocumentNumberValidator : ConstraintValidator<DocumentNumber, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        if (value.isNullOrBlank()) return false
        val cleanedValue = value.replace("[^0-9]".toRegex(), "")

        return isCpf(cleanedValue) || isCnpj(cleanedValue)
    }

    private fun isCpf(cpf: String): Boolean {
        if (cpf.length != 11) return false

        val digits = cpf.map { it.toString().toInt() }

        val sum1 = digits.take(9).withIndex().sumOf { (i, num) -> num * (10 - i) }
        val sum2 = digits.take(9).withIndex().sumOf { (i, num) -> num * (11 - i) }

        val firstCheck = (sum1 % 11 < 2) == (digits[9] == 0)
        val secondCheck = (sum2 % 11 < 2) == (digits[10] == 0)

        return firstCheck && secondCheck
    }

    private fun isCnpj(cnpj: String): Boolean {
        if (cnpj.length != 14) return false

        val digits = cnpj.map { it.toString().toInt() }

        val sum1 = digits.take(12).withIndex().sumOf { (i, num) -> num * (5 - i % 8) }
        val sum2 = digits.take(12).withIndex().sumOf { (i, num) -> num * (6 - i % 8) }

        val firstCheck = (sum1 % 11 < 2) == (digits[12] == 0)
        val secondCheck = (sum2 % 11 < 2) == (digits[13] == 0)

        return firstCheck && secondCheck
    }
}
