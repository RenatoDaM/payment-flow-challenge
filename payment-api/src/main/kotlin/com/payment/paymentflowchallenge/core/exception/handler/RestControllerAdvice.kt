package com.payment.paymentflowchallenge.core.exception.handler

import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler


@RestControllerAdvice
class RestControllerAdvice: ResponseEntityExceptionHandler() {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    protected fun handleNotFound(ex: MethodArgumentNotValidException, request: WebRequest): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "The request was processed, but one or more fields contain invalid values")
        problemDetail.title = "Validation Error"
        val errorDetails: MutableList<Map<String, String?>> = ArrayList()

        for (fieldError in ex.bindingResult.fieldErrors) {
            val pointer = fieldError.field
            val errorMessage = fieldError.defaultMessage
            val errorDetail: MutableMap<String, String?> = HashMap()
            errorDetail["pointer"] = pointer
            errorDetail["error"] = errorMessage
            errorDetails.add(errorDetail)
        }

        problemDetail.setProperty("errors", errorDetails)
        return problemDetail
    }

    @ExceptionHandler(WebClientResponseException::class)
    fun handleWebClientResponseException(ex: WebClientResponseException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(ex.statusCode, "Error from external service")
        problemDetail.detail = "Error from external service: " + ex.message
        problemDetail.title = "External Service Error"
        return problemDetail
    }
}