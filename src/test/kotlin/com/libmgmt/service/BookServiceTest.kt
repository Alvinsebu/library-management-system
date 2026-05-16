package com.libmgmt.service

import com.libmgmt.dto.CreateBookRequest
import com.libmgmt.dto.BorrowBookRequest
import com.libmgmt.exception.BookNotAvailableException
import com.libmgmt.exception.ResourceNotFoundException
import com.libmgmt.model.Book
import com.libmgmt.model.BookPolicy
import com.libmgmt.model.User
import com.libmgmt.model.UserRole
import com.libmgmt.repository.BookRepository
import com.libmgmt.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BookServiceTest {

    private lateinit var bookService: BookService
    private val bookRepository = mockk<BookRepository>()
    private val userRepository = mockk<UserRepository>()

    @BeforeEach
    fun setUp() {
        bookService = BookService(bookRepository, userRepository)
    }

    @Test
    fun `should successfully create a new book`() {
        // Arrange
        val request = CreateBookRequest(
            title = "Test Book",
            author = "Test Author",
            policy = "NORMAL"
        )
        val userId = "admin-1"
        val user = User(id = userId, name = "Admin", email = "admin@test.com", password = "hash", role = UserRole.ADMIN)
        val book = Book(
            id = "1",
            title = request.title,
            author = request.author,
            policy = BookPolicy.NORMAL
        )

        every { userRepository.findById(userId) } returns Optional.of(user)
        every { bookRepository.save(any()) } returns book

        // Act
        val response = bookService.createBook(request, userId)

        // Assert
        assertEquals(response.title, "Test Book")
        assertEquals(response.author, "Test Author")
        assertTrue(response.available)
        verify { userRepository.findById(userId) }
        verify { bookRepository.save(any()) }
    }

    @Test
    fun `should get all available books`() {
        // Arrange
        val books = listOf(
            Book(id = "1", title = "Book 1", author = "Author 1"),
            Book(id = "2", title = "Book 2", author = "Author 2")
        )

        every { bookRepository.findAllAvailableBooks() } returns books

        // Act
        val result = bookService.getAvailableBooks()

        // Assert
        assertEquals(result.size, 2)
        assertEquals(result[0].title, "Book 1")
        verify { bookRepository.findAllAvailableBooks() }
    }

    @Test
    fun `should successfully borrow an available book`() {
        // Arrange
        val bookId = "1"
        val userId = "user-1"
        val book = Book(
            id = bookId,
            title = "Test Book",
            author = "Test Author",
            available = true
        )
        val user = User(id = userId, name = "John", email = "john@test.com", password = "hash", role = UserRole.USER)
        val request = BorrowBookRequest(expiryMinutes = 1440L)

        every { bookRepository.findById(bookId) } returns Optional.of(book)
        every { userRepository.findById(userId) } returns Optional.of(user)
        every { bookRepository.findAllByBorrowedBy(userId) } returns emptyList()
        every { bookRepository.save(any()) } returns book.copy(
            available = false,
            borrowedBy = userId,
            borrowedAt = LocalDateTime.now()
        )

        // Act
        val response = bookService.borrowBook(bookId, userId, request)

        // Assert
        assertEquals(response.id, bookId)
        assertEquals(response.borrowedBy, userId)
        verify { bookRepository.findById(bookId) }
        verify { bookRepository.save(any()) }
    }

    @Test
    fun `should throw exception when borrowing unavailable book`() {
        // Arrange
        val bookId = "1"
        val userId = "user-1"
        val book = Book(
            id = bookId,
            title = "Test Book",
            author = "Test Author",
            available = false
        )
        val request = BorrowBookRequest(expiryMinutes = 1440L)

        every { bookRepository.findById(bookId) } returns Optional.of(book)

        // Act & Assert
        assertThrows<BookNotAvailableException> {
            bookService.borrowBook(bookId, userId, request)
        }
    }

    @Test
    fun `should successfully return a borrowed book`() {
        // Arrange
        val bookId = "1"
        val userId = "user-1"
        val book = Book(
            id = bookId,
            title = "Test Book",
            author = "Test Author",
            available = false,
            borrowedBy = userId,
            borrowedAt = LocalDateTime.now()
        )

        every { bookRepository.findById(bookId) } returns Optional.of(book)
        every { bookRepository.save(any()) } returns book.copy(
            available = true,
            borrowedBy = null,
            borrowedAt = null
        )

        // Act
        val response = bookService.returnBook(bookId, userId)

        // Assert
        assertEquals(response.id, bookId)
        assertTrue(response.message.contains("successfully"))
        verify { bookRepository.findById(bookId) }
        verify { bookRepository.save(any()) }
    }
}
