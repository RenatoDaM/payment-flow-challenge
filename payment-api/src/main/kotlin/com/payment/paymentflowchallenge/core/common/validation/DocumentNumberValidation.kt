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

    override fun isValid(value: String, context: ConstraintValidatorContext): Boolean {
        if (!hasOnlyNumbers(value)) return false

        return isValidCPF(value) || isValidCNPJ(value)
    }

    private fun hasOnlyNumbers(value: String) =
        value.matches(Regex("^\\d+$"))


    private val cpfMultipliers: IntArray = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    private val cnpjMultipliers: IntArray = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

    private fun calculateDigit(str: String, weight: IntArray): Int {
        var sum = 0
        var index = str.length - 1
        var digit: Int
        while (index >= 0) {
            digit = str.substring(index, index + 1).toInt()
            sum += digit * weight[weight.size - str.length + index]
            index--
        }
        sum = 11 - sum % 11
        return if (sum > 9) 0 else sum
    }

    private fun padLeft(text: String, character: Char): String {
        return String.format("%11s", text).replace(' ', character)
    }

    private fun isValidCPF(cpf: String): Boolean {
        if (cpf.length != 11) return false
        for (j in 0..9) if (padLeft(j.toString(), Character.forDigit(j, 10)) == cpf) return false

        val digit1 = calculateDigit(cpf.substring(0, 9), cpfMultipliers)
        val digit2 = calculateDigit(cpf.substring(0, 9) + digit1, cpfMultipliers)
        return cpf == cpf.substring(0, 9) + digit1.toString() + digit2.toString()
    }

    private fun isValidCNPJ(cnpj: String): Boolean {
        if (cnpj.length != 14) return false

        val digit1 = calculateDigit(cnpj.substring(0, 12), cnpjMultipliers)
        val digit2 = calculateDigit(cnpj.substring(0, 12) + digit1, cnpjMultipliers)
        return cnpj == cnpj.substring(0, 12) + digit1.toString() + digit2.toString()
    }
}
