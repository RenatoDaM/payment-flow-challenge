package com.payment.paymentflowchallenge.entrypoint.exception.handler

import com.payment.paymentflowchallenge.entrypoint.exception.ResourceAlreadyExistsException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.reactive.function.client.WebClientResponseException


@RestControllerAdvice
class RestControllerAdvice {
    private final val log: Logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleNotFound(ex: MethodArgumentNotValidException, request: WebRequest): ProblemDetail {
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

    @ExceptionHandler(ResourceAlreadyExistsException::class)
    fun handleResourceAlreadyExistsException(ex: ResourceAlreadyExistsException): ProblemDetail {
        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Resource already exists")
        problemDetail.detail = ex.localizedMessage
        problemDetail.title = "Conflict Error"
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

    @ExceptionHandler(Exception::class)
    fun handleGeneralException(ex: Exception): ProblemDetail {
        log.error("Unexpected error occurred", ex)

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred.")
        problemDetail.title = "Internal Server Error"
        problemDetail.detail = "No further details available"
        return problemDetail
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleGeneralException(ex: HttpMessageNotReadableException): ProblemDetail {
        log.error("HTTP Message Not Readable", ex)

        val problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "HTTP Message Not Readable")
        problemDetail.title = "HTTP Message Not Readable"
        problemDetail.detail = ex.message
        return problemDetail
    }

}