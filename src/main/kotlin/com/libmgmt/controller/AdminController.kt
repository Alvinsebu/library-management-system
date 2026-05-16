package com.libmgmt.controller

import com.libmgmt.dto.ApiResponse
import com.libmgmt.dto.CreateBookRequest
import com.libmgmt.dto.BookResponse
import com.libmgmt.service.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.core.Authentication

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin endpoints")
@SecurityRequirement(name = "bearer-jwt")
class AdminController(
    private val bookService: BookService
) {

    @PostMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Add a new book (Admin only)")
    fun addBook(
        @Valid @RequestBody request: CreateBookRequest,
        authentication: Authentication
    ): ResponseEntity<ApiResponse<BookResponse>> {
        val userId = authentication.details as? Map<*, *>
        val response = bookService.createBook(request, userId?.get("userId") as String)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(success = true, message = "Book added successfully", data = response))
    }

    @GetMapping("/books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all books (Admin only)")
    fun getAllBooks(): ResponseEntity<ApiResponse<List<BookResponse>>> {
        val books = bookService.getAllBooks()
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Books retrieved successfully", data = books)
        )
    }
}
