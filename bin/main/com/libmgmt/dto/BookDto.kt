package com.libmgmt.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Schema(description = "Payload to create a new book")
data class CreateBookRequest(
    @field:NotBlank(message = "Book title cannot be blank")
    @field:Size(max = 255, message = "Book title cannot exceed 255 characters")
    @Schema(description = "Title of the book", example = "Clean Code", required = true)
    val title: String,

    @field:NotBlank(message = "Author cannot be blank")
    @field:Size(max = 255, message = "Author cannot exceed 255 characters")
    @Schema(description = "Author of the book", example = "Robert C. Martin", required = true)
    val author: String,

    @field:NotBlank(message = "Policy cannot be blank")
    @Schema(description = "Borrowing policy (NORMAL, EXPIRY, END_OF_DAY)", example = "NORMAL")
    val policy: String = "NORMAL"
)

@Schema(description = "Book details returned by the API")
data class BookResponse(
    @Schema(description = "Book id", example = "6253e5c8f1a4b7a0359da6f1")
    val id: String,
    @Schema(description = "Book title", example = "Clean Code")
    val title: String,
    @Schema(description = "Book author", example = "Robert C. Martin")
    val author: String,
    @Schema(description = "Is the book currently available for borrowing", example = "true")
    val available: Boolean,
    @Schema(description = "Id of user who borrowed the book", example = "6a0865896b5a667c973a8d30")
    val borrowedBy: String?,
    @Schema(description = "When the book was borrowed", example = "2026-05-16T12:00:00")
    val borrowedAt: LocalDateTime?,
    @Schema(description = "Expiry timestamp for the borrowed book", example = "2026-05-17T12:00:00")
    val expiryAt: LocalDateTime?,
    @Schema(description = "Borrowing policy for this book", example = "NORMAL")
    val policy: String
)

@Schema(description = "Payload to borrow a book")
data class BorrowBookRequest(
    @field:Positive(message = "Expiry minutes must be a positive number")
    @Schema(description = "Optional expiry window in minutes for EXPIRY policy", example = "1440")
    val expiryMinutes: Long? = null
)

@Schema(description = "Response after borrowing a book")
data class BorrowBookResponse(
    @Schema(description = "Book id", example = "6253e5c8f1a4b7a0359da6f1")
    val id: String,
    @Schema(description = "Book title", example = "Clean Code")
    val title: String,
    @Schema(description = "Id of user who borrowed the book", example = "6a0865896b5a667c973a8d30")
    val borrowedBy: String,
    @Schema(description = "Timestamp when the book was borrowed", example = "2026-05-16T12:00:00")
    val borrowedAt: LocalDateTime,
    @Schema(description = "Optional expiry timestamp for the borrowed book", example = "2026-05-17T12:00:00")
    val expiryAt: LocalDateTime?,
    @Schema(description = "Human readable status message", example = "Book borrowed successfully")
    val message: String
)

@Schema(description = "Response after returning a book")
data class ReturnBookResponse(
    @Schema(description = "Book id", example = "6253e5c8f1a4b7a0359da6f1")
    val id: String,
    @Schema(description = "Book title", example = "Clean Code")
    val title: String,
    @Schema(description = "Human readable status message", example = "Book returned successfully")
    val message: String
)

@Schema(description = "User borrowed books response")
data class UserBooksResponse(
    @Schema(description = "Id of the user", example = "6a0865896b5a667c973a8d30")
    val userId: String,
    @Schema(description = "List of books borrowed by the user")
    val books: List<BookResponse>,
    @Schema(description = "Total number of returned books", example = "2")
    val count: Int
)
