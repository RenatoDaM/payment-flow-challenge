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
        val cleanedValue = value.replace("[^0-9]".toRegex(), "")

        return isValidCPF(cleanedValue) || isValidCNPJ(cleanedValue)
    }

    private val pesoCPF: IntArray = intArrayOf(11, 10, 9, 8, 7, 6, 5, 4, 3, 2)
    private val pesoCNPJ: IntArray = intArrayOf(6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2)

    fun isValid(cpfCnpj: String): Boolean {
        return (isValidCPF(cpfCnpj) || isValidCNPJ(cpfCnpj))
    }

    private fun calcularDigito(str: String, peso: IntArray): Int {
        var soma = 0
        var indice = str.length - 1
        var digito: Int
        while (indice >= 0) {
            digito = str.substring(indice, indice + 1).toInt()
            soma += digito * peso[peso.size - str.length + indice]
            indice--
        }
        soma = 11 - soma % 11
        return if (soma > 9) 0 else soma
    }

    private fun padLeft(text: String, character: Char): String {
        return String.format("%11s", text).replace(' ', character)
    }

    private fun isValidCPF(cpf: String): Boolean {
        var cpf: String? = cpf
        cpf = cpf!!.trim { it <= ' ' }.replace(".", "").replace("-", "")
        if ((cpf == null) || (cpf.length != 11)) return false

        for (j in 0..9) if (padLeft(j.toString(), Character.forDigit(j, 10)) == cpf) return false

        val digito1 = calcularDigito(cpf.substring(0, 9), pesoCPF)
        val digito2 = calcularDigito(cpf.substring(0, 9) + digito1, pesoCPF)
        return cpf == cpf.substring(0, 9) + digito1.toString() + digito2.toString()
    }

    private fun isValidCNPJ(cnpj: String): Boolean {
        var cnpj: String? = cnpj
        cnpj = cnpj!!.trim { it <= ' ' }.replace(".", "").replace("-", "")
        if ((cnpj == null) || (cnpj.length != 14)) return false

        val digito1 = calcularDigito(cnpj.substring(0, 12), pesoCNPJ)
        val digito2 = calcularDigito(cnpj.substring(0, 12) + digito1, pesoCNPJ)
        return cnpj == cnpj.substring(0, 12) + digito1.toString() + digito2.toString()
    }
}
