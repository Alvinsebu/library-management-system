package com.libmgmt.service

import com.libmgmt.dto.SignupRequest
import com.libmgmt.dto.LoginRequest
import com.libmgmt.exception.DuplicateResourceException
import com.libmgmt.exception.UnauthorizedException
import com.libmgmt.model.User
import com.libmgmt.model.UserRole
import com.libmgmt.repository.UserRepository
import com.libmgmt.util.JwtTokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.Optional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AuthServiceTest {

    private lateinit var authService: AuthService
    private val userRepository = mockk<UserRepository>()
    private val passwordEncoder = mockk<PasswordEncoder>()
    private val jwtTokenProvider = mockk<JwtTokenProvider>()

    @BeforeEach
    fun setUp() {
        authService = AuthService(userRepository, passwordEncoder, jwtTokenProvider)
    }

    @Test
    fun `should successfully signup a new user`() {
        // Arrange
        val request = SignupRequest(
            name = "John Doe",
            email = "john@test.com",
            password = "password123"
        )
        val user = User(
            id = "1",
            name = "John Doe",
            email = "john@test.com",
            password = "hashedPassword",
            role = UserRole.USER
        )

        every { userRepository.existsByEmail(request.email) } returns false
        every { passwordEncoder.encode(request.password) } returns "hashedPassword"
        every { userRepository.save(any()) } returns user
        every { jwtTokenProvider.generateToken(user.email, user.id!!) } returns "jwt-token"

        // Act
        val response = authService.signup(request)

        // Assert
        assertEquals(response.email, "john@test.com")
        assertEquals(response.name, "John Doe")
        assertEquals(response.token, "jwt-token")
        assertTrue(response.message.contains("successfully"))
        verify { userRepository.existsByEmail(request.email) }
        verify { passwordEncoder.encode(request.password) }
        verify { userRepository.save(any()) }
    }

    @Test
    fun `should throw DuplicateResourceException if email already exists`() {
        // Arrange
        val request = SignupRequest(
            name = "John Doe",
            email = "existing@test.com",
            password = "password123"
        )

        every { userRepository.existsByEmail(request.email) } returns true

        // Act & Assert
        assertThrows<DuplicateResourceException> {
            authService.signup(request)
        }
    }

    @Test
    fun `should successfully login existing user`() {
        // Arrange
        val request = LoginRequest(
            email = "john@test.com",
            password = "password123"
        )
        val user = User(
            id = "1",
            name = "John Doe",
            email = "john@test.com",
            password = "hashedPassword",
            role = UserRole.USER,
            active = true
        )

        every { userRepository.findByEmail(request.email) } returns Optional.of(user)
        every { passwordEncoder.matches(request.password, user.password) } returns true
        every { jwtTokenProvider.generateToken(user.email, user.id!!) } returns "jwt-token"

        // Act
        val response = authService.login(request)

        // Assert
        assertEquals(response.email, "john@test.com")
        assertNotNull(response.token)
        verify { userRepository.findByEmail(request.email) }
        verify { passwordEncoder.matches(request.password, user.password) }
    }

    @Test
    fun `should throw UnauthorizedException for invalid password`() {
        // Arrange
        val request = LoginRequest(
            email = "john@test.com",
            password = "wrongpassword"
        )
        val user = User(
            id = "1",
            name = "John Doe",
            email = "john@test.com",
            password = "hashedPassword",
            role = UserRole.USER,
            active = true
        )

        every { userRepository.findByEmail(request.email) } returns Optional.of(user)
        every { passwordEncoder.matches(request.password, user.password) } returns false

        // Act & Assert
        assertThrows<UnauthorizedException> {
            authService.login(request)
        }
    }
}
