package com.libmgmt.controller

import com.libmgmt.dto.ApiResponse
import com.libmgmt.dto.BookResponse
import com.libmgmt.dto.BorrowBookRequest
import com.libmgmt.dto.BorrowBookResponse
import com.libmgmt.dto.ReturnBookResponse
import com.libmgmt.dto.UserBooksResponse
import com.libmgmt.exception.UnauthorizedException
import com.libmgmt.service.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Rest endpoints for book listing, borrowing, returning, and user book queries.
 *
 * This controller is protected by JWT bearer authentication.
 */
@RestController
@RequestMapping("/books")
@Tag(name = "Books", description = "Book management endpoints")
@SecurityRequirement(name = "bearer-jwt")
class BookController(
    private val bookService: BookService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping
    @Operation(summary = "Get all available books", description = "Retrieve all books currently available for borrowing")
    @SwaggerApiResponse(responseCode = "200", description = "Available books retrieved")
    fun getAvailableBooks(): ResponseEntity<ApiResponse<List<BookResponse>>> {
        val books = bookService.getAvailableBooks()
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Available books retrieved", data = books)
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a single book by its identifier")
    @SwaggerApiResponse(responseCode = "200", description = "Book retrieved")
    fun getBookById(@PathVariable id: String): ResponseEntity<ApiResponse<BookResponse>> {
        val book = bookService.getBookById(id)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Book retrieved", data = book)
        )
    }

    @PostMapping("/{id}/borrow")
    @Operation(summary = "Borrow a book", description = "Borrow a book for the authenticated user")
    @SwaggerApiResponse(responseCode = "200", description = "Book borrowed successfully")
    fun borrowBook(
        @PathVariable id: String,
        @Valid @RequestBody request: BorrowBookRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<BorrowBookResponse>> {
        val userId = extractUserId(authentication)
        logger.info("Borrow book request bookId={} userId={}", id, userId)
        val response = bookService.borrowBook(id, userId, request)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Book borrowed successfully", data = response)
        )
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Return a borrowed book", description = "Return a previously borrowed book for the authenticated user")
    @SwaggerApiResponse(responseCode = "200", description = "Book returned successfully")
    fun returnBook(
        @PathVariable id: String,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<ReturnBookResponse>> {
        val userId = extractUserId(authentication)
        logger.info("Return book request bookId={} userId={}", id, userId)
        val response = bookService.returnBook(id, userId)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Book returned successfully", data = response)
        )
    }

    @GetMapping("/user/borrowed")
    @Operation(summary = "Get user's borrowed books", description = "Retrieve all books borrowed by the authenticated user")
    @SwaggerApiResponse(responseCode = "200", description = "Borrowed books retrieved")
    fun getUserBorrowedBooks(authentication: Authentication): ResponseEntity<ApiResponse<UserBooksResponse>> {
        val userId = extractUserId(authentication)
        val books = bookService.getUserBorrowedBooks(userId)
        val response = UserBooksResponse(userId = userId, books = books, count = books.size)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Borrowed books retrieved", data = response)
        )
    }

    private fun extractUserId(authentication: Authentication): String {
        val details = authentication.details as? Map<*, *>
        return details?.get("userId") as? String
            ?: throw UnauthorizedException("Unable to resolve authenticated user")
    }
}
