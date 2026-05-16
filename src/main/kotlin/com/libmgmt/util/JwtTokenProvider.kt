package com.libmgmt.util

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret}")
    private val jwtSecret: String,

    @Value("\${jwt.expiration}")
    private val jwtExpiration: Long
) {

    private val signingKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())

    private fun jwtParser(): JwtParser = Jwts.parser()
        .setSigningKey(signingKey)
        .build()

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

    fun isTokenValid(token: String): Boolean {
        return try {
            jwtParser().parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun extractTokenFromHeader(authHeader: String?): String? {
        return authHeader
            ?.takeIf { it.startsWith("Bearer ") }
            ?.substring(7)
    }
}