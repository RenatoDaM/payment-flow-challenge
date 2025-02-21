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
    override fun isValid(value: String?, context: ConstraintValidatorContext): Boolean {
        if (value.isNullOrBlank()) return false
        val digits = value.replace("""\D""".toRegex(), "")
        return isValidCPF(digits) || isValidCNPJ(digits)
    }

    private fun isValidCPF(cpf: String): Boolean {
        if (cpf.length != 11 || cpf.all { it == cpf[0] }) return false
        return cpf.calculateCheckDigits(9) == cpf.substring(9).toInt()
    }

    private fun isValidCNPJ(cnpj: String): Boolean {
        if (cnpj.length != 14) return false
        return cnpj.calculateCheckDigits(12) == cnpj.substring(12).toInt()
    }

    private fun String.calculateCheckDigits(length: Int): Int {
        val weights = (2..9).toList().reversed() + (2..9).toList()
        val firstDigit = this.take(length).map { it.toString().toInt() }
            .reversed()
            .mapIndexed { index, num -> num * weights[index] }
            .sum()
            .let { 11 - (it % 11) }
            .let { if (it >= 10) 0 else it }

        val secondDigit = (this.take(length) + firstDigit).map { it.toString().toInt() }
            .reversed()
            .mapIndexed { index, num -> num * weights[index] }
            .sum()
            .let { 11 - (it % 11) }
            .let { if (it >= 10) 0 else it }

        return firstDigit * 10 + secondDigit
    }
}
