package com.libmgmt.util

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

/**
 * Generates and validates JWT tokens for user sessions.
 *
 * This provider is intentionally lightweight and preserves the existing JWT contract.
 */
@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,

    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long
) {

    private val signingKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    /**
     * Create a reusable JWT parser instance configured with the signing key.
     */
    private fun jwtParser(): JwtParser = Jwts.parser()
        .setSigningKey(signingKey)
        .build()

    /**
     * Create a signed JWT token for the provided user email and ID.
     */
    fun generateToken(userEmail: String, userId: String): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtExpiration)

        return Jwts.builder()
            .setSubject(userEmail)
            .claim("userId", userId)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS512)
            .compact()
    }

    fun getUserEmailFromToken(token: String): String {
        return jwtParser()
            .parseClaimsJws(token)
            .body
            .subject
    }

    fun getUserIdFromToken(token: String): String {
        return jwtParser()
            .parseClaimsJws(token)
            .body
            .get("userId", String::class.java)
    }

    /**
     * Validate the token signature and expiration.
     */
    fun isTokenValid(token: String): Boolean {
        return try {
            jwtParser().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Extract the bearer token value from the Authorization header.
     */
    fun extractTokenFromHeader(authHeader: String?): String? {
        return authHeader
            ?.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)
    }
}