package com.libmgmt.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class JwtTokenProviderTest {

    private val secret = "0123456789012345678901234567890123456789012345678901234567890123"
    private val expiration = 3600000L
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @BeforeEach
    fun setUp() {
        jwtTokenProvider = JwtTokenProvider(secret, expiration)
    }

    @Test
    fun `should generate valid JWT token`() {
        // Arrange
        val userEmail = "test@example.com"
        val userId = "user-123"

        // Act
        val token = jwtTokenProvider.generateToken(userEmail, userId)

        // Assert
        assertNotNull(token)
        assertTrue(token.isNotBlank())
    }

    @Test
    fun `should extract user email from token`() {
        // Arrange
        val userEmail = "test@example.com"
        val userId = "user-123"
        val token = jwtTokenProvider.generateToken(userEmail, userId)

        // Act
        val extractedEmail = jwtTokenProvider.getUserEmailFromToken(token)

        // Assert
        assertEquals(extractedEmail, userEmail)
    }

    @Test
    fun `should extract user ID from token`() {
        // Arrange
        val userEmail = "test@example.com"
        val userId = "user-123"
        val token = jwtTokenProvider.generateToken(userEmail, userId)

        // Act
        val extractedUserId = jwtTokenProvider.getUserIdFromToken(token)

        // Assert
        assertEquals(extractedUserId, userId)
    }

    @Test
    fun `should validate valid token`() {
        // Arrange
        val userEmail = "test@example.com"
        val userId = "user-123"
        val token = jwtTokenProvider.generateToken(userEmail, userId)

        // Act
        val isValid = jwtTokenProvider.isTokenValid(token)

        // Assert
        assertTrue(isValid)
    }

    @Test
    fun `should extract token from authorization header`() {
        // Arrange
        val token = "valid-jwt-token"
        val authHeader = "Bearer $token"

        // Act
        val extractedToken = jwtTokenProvider.extractTokenFromHeader(authHeader)

        // Assert
        assertEquals(extractedToken, token)
    }

    @Test
    fun `should return null for invalid authorization header`() {
        // Arrange
        val authHeader = "InvalidHeader"

        // Act
        val extractedToken = jwtTokenProvider.extractTokenFromHeader(authHeader)

        // Assert
        assertEquals(extractedToken, null)
    }
}
