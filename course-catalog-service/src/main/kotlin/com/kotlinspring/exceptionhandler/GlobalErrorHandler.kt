package com.kotlinspring.exceptionhandler

import com.kotlinspring.exception.InstructorNotValidException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@Component
@ControllerAdvice
class GlobalErrorHandler : ResponseEntityExceptionHandler() {

    private val logger = KotlinLogging.logger {}

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatus,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.error(ex) { "MethodArgumentNotValidException observed : ${ex.message}" }
        val errors = ex.bindingResult.allErrors
            .map { error -> error.defaultMessage!! }
            .sorted()

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                errors.joinToString(", ") { it }
            )
    }

    @ExceptionHandler(InstructorNotValidException::class)
    fun handleInputRequestError(ex: InstructorNotValidException, request: WebRequest): ResponseEntity<Any> {
        logger.info { "Exception occurred: ${ex.message} on request: $request" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ex.message
            )
    }

    @ExceptionHandler(java.lang.Exception::class)
    fun handleAllExceptions(ex: java.lang.Exception, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex) { "Exception occurred: ${ex.message} on request: $request" }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ex.message
            )
    }
}