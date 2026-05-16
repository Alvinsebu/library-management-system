package com.libmgmt.exception

import org.springframework.http.HttpStatus

open class LibraryException(
    message: String,
    open val statusCode: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()
) : RuntimeException(message)

class ResourceNotFoundException(message: String) : LibraryException(message, HttpStatus.NOT_FOUND.value())

class DuplicateResourceException(message: String) : LibraryException(message, HttpStatus.CONFLICT.value())

class BookNotAvailableException(message: String) : LibraryException(message, HttpStatus.BAD_REQUEST.value())

class BookAlreadyBorrowedException(message: String) : LibraryException(message, HttpStatus.BAD_REQUEST.value())

class ForbiddenException(message: String) : LibraryException(message, HttpStatus.FORBIDDEN.value())

class UnauthorizedException(message: String) : LibraryException(message, HttpStatus.UNAUTHORIZED.value())

class ValidationException(message: String) : LibraryException(message, HttpStatus.BAD_REQUEST.value())

class BookNotFoundException(message: String) : LibraryException(message, HttpStatus.NOT_FOUND.value())

class BookAlreadyExistsException(message: String) : LibraryException(message, HttpStatus.CONFLICT.value())

class UserNotFoundException(message: String) : LibraryException(message, HttpStatus.NOT_FOUND.value())

class InvalidOperationException(message: String) : LibraryException(message, HttpStatus.BAD_REQUEST.value())

class TokenExpiredException(message: String) : LibraryException(message, HttpStatus.UNAUTHORIZED.value())
