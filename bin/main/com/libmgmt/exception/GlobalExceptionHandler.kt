package com.libmgmt.exception

import com.libmgmt.dto.ErrorResponse
import com.libmgmt.dto.ValidationError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(LibraryException::class)
    fun handleLibraryException(
        ex: LibraryException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Handled business exception: ${ex.message}", ex)
        val errorResponse = ErrorResponse(
            success = false,
            message = ex.message ?: "An error occurred",
            error = ex.javaClass.simpleName,
            status = ex.statusCode
        )
        return ResponseEntity(errorResponse, HttpStatus.valueOf(ex.statusCode))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: org.springframework.http.HttpHeaders,
        status: org.springframework.http.HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        logger.warn("Validation failure: ${ex.bindingResult.allErrors}")
        val errors = ex.bindingResult.fieldErrors.map { error ->
            ValidationError(
                field = error.field,
                message = error.defaultMessage ?: "Validation failed"
            )
        }

        val errorResponse = ErrorResponse(
            success = false,
            message = "Validation failed",
            errors = errors,
            error = "ValidationError",
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(
        ex: AccessDeniedException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Access denied: ${ex.message}")
        val errorResponse = ErrorResponse(
            success = false,
            message = "Access denied",
            error = ex.javaClass.simpleName,
            status = HttpStatus.FORBIDDEN.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.FORBIDDEN)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatch(
        ex: MethodArgumentTypeMismatchException,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.warn("Type mismatch for parameter ${ex.name}")
        val errorResponse = ErrorResponse(
            success = false,
            message = "Invalid argument: ${ex.name} should be of type ${ex.requiredType?.simpleName}",
            error = ex.javaClass.simpleName,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleGlobalException(
        ex: Exception,
        request: WebRequest
    ): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        val errorResponse = ErrorResponse(
            success = false,
            message = ex.message ?: "An unexpected error occurred",
            error = ex.javaClass.simpleName,
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
