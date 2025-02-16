package com.payment.paymentflowchallenge.core.exception.handler

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
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
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

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
        val errorDetail = "Error from external service: " + ex.message
        log.error(errorDetail, ex)

        val problemDetail = ProblemDetail.forStatusAndDetail(ex.statusCode, "Error from external service")
        problemDetail.detail = errorDetail
        problemDetail.title = "External Service Error"
        return problemDetail
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid input or argument.")
        problemDetail.detail = "Invalid input provided: ${ex.message}"
        problemDetail.title = "Bad Request Error"
        return problemDetail
    }

    @ExceptionHandler(OptimisticLockingFailureException::class)
    fun handleOptimisticLockingFailureException(ex: OptimisticLockingFailureException): ProblemDetail {
        log.error("Optimistic Locking error", ex)

        val problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "The requested operation could not be completed because the resource was modified by another process."
        )
        problemDetail.title = "Optimistic Locking Conflict"
        problemDetail.detail = """
        Another transaction updated this resource before your request was processed. 
        Please refresh the data and try again.
    """.trimIndent()
        return problemDetail
    }
}