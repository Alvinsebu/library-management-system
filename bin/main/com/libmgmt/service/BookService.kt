package com.libmgmt.service

import com.libmgmt.dto.BookResponse
import com.libmgmt.dto.BorrowBookRequest
import com.libmgmt.dto.BorrowBookResponse
import com.libmgmt.dto.CreateBookRequest
import com.libmgmt.dto.ReturnBookResponse
import com.libmgmt.exception.BookAlreadyBorrowedException
import com.libmgmt.exception.BookNotAvailableException
import com.libmgmt.exception.ForbiddenException
import com.libmgmt.exception.ResourceNotFoundException
import com.libmgmt.model.Book
import com.libmgmt.model.BookPolicy
import com.libmgmt.repository.BookRepository
import com.libmgmt.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BookService(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun createBook(request: CreateBookRequest, userId: String): BookResponse {
        // Ensure the requesting user exists before creating the book
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        logger.info("Creating book title={} by userId={}", request.title, user.id)

        val book = Book(
            title = request.title,
            author = request.author,
            policy = BookPolicy.valueOf(request.policy)
        )

        val savedBook = bookRepository.save(book)
        return toBookResponse(savedBook)
    }

    fun getAllBooks(): List<BookResponse> {
        return bookRepository.findAll().map { toBookResponse(it) }
    }

    fun getAvailableBooks(): List<BookResponse> {
        return bookRepository.findAllAvailableBooks().map { toBookResponse(it) }
    }

    fun getBookById(bookId: String): BookResponse {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book not found with id: $bookId") }
        return toBookResponse(book)
    }

    fun borrowBook(bookId: String, userId: String, request: BorrowBookRequest): BorrowBookResponse {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book not found with id: $bookId") }

        if (!book.available) {
            throw BookNotAvailableException("Book is not available for borrowing")
        }

        userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User not found with id: $userId") }

        val userBorrowedBooks = bookRepository.findAllByBorrowedBy(userId)
        if (userBorrowedBooks.any { it.id == bookId }) {
            throw BookAlreadyBorrowedException("You have already borrowed this book")
        }

        val now = LocalDateTime.now()
        val expiryAt = when (book.policy) {
            BookPolicy.NORMAL -> null
            BookPolicy.EXPIRY -> now.plusMinutes(request.expiryMinutes ?: 1440)
            BookPolicy.END_OF_DAY -> now.withHour(22).withMinute(0).withSecond(0)
        }

        val updatedBook = book.copy(
            available = false,
            borrowedBy = userId,
            borrowedAt = now,
            expiryAt = expiryAt,
            updatedAt = now
        )

        val savedBook = bookRepository.save(updatedBook)
        logger.info("Book borrowed bookId={} userId={}", bookId, userId)

        return BorrowBookResponse(
            id = savedBook.id!!,
            title = savedBook.title,
            borrowedBy = savedBook.borrowedBy!!,
            borrowedAt = savedBook.borrowedAt!!,
            expiryAt = savedBook.expiryAt,
            message = "Book borrowed successfully"
        )
    }

    fun returnBook(bookId: String, userId: String): ReturnBookResponse {
        val book = bookRepository.findById(bookId)
            .orElseThrow { ResourceNotFoundException("Book not found with id: $bookId") }

        if (book.available) {
            throw BookNotAvailableException("Book is not currently borrowed")
        }

        if (book.borrowedBy != userId) {
            throw ForbiddenException("You cannot return a book you didn't borrow")
        }

        val returnedBook = book.copy(
            available = true,
            borrowedBy = null,
            borrowedAt = null,
            expiryAt = null,
            updatedAt = LocalDateTime.now()
        )

        val savedBook = bookRepository.save(returnedBook)
        logger.info("Book returned bookId={} userId={}", bookId, userId)

        return ReturnBookResponse(
            id = savedBook.id!!,
            title = savedBook.title,
            message = "Book returned successfully"
        )
    }

    fun getUserBorrowedBooks(userId: String): List<BookResponse> {
        return bookRepository.findAllByBorrowedBy(userId).map { toBookResponse(it) }
    }

    fun autoReturnExpiredBooks() {
        val now = LocalDateTime.now()
        val expiredBooks = bookRepository.findExpiredBooks(now)

        expiredBooks.forEach { book ->
            val returnedBook = book.copy(
                available = true,
                borrowedBy = null,
                borrowedAt = null,
                expiryAt = null,
                updatedAt = now
            )
            bookRepository.save(returnedBook)
        }
    }

    fun autoReturnEndOfDayBooks() {
        val now = LocalDateTime.now()
        val endOfDayBooks = bookRepository.findEndOfDayBooks()

        endOfDayBooks.forEach { book ->
            if (book.expiryAt != null && now.isAfter(book.expiryAt)) {
                val returnedBook = book.copy(
                    available = true,
                    borrowedBy = null,
                    borrowedAt = null,
                    expiryAt = null,
                    updatedAt = now
                )
                bookRepository.save(returnedBook)
            }
        }
    }

    private fun toBookResponse(book: Book): BookResponse {
        return BookResponse(
            id = book.id!!,
            title = book.title,
            author = book.author,
            available = book.available,
            borrowedBy = book.borrowedBy,
            borrowedAt = book.borrowedAt,
            expiryAt = book.expiryAt,
            policy = book.policy.name
        )
    }
}
