package com.libmgmt.service

import com.libmgmt.dto.AuthResponse
import com.libmgmt.dto.LoginRequest
import com.libmgmt.dto.SignupRequest
import com.libmgmt.exception.DuplicateResourceException
import com.libmgmt.exception.UnauthorizedException
import com.libmgmt.model.User
import com.libmgmt.model.UserRole
import com.libmgmt.repository.UserRepository
import com.libmgmt.util.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun signup(request: SignupRequest): AuthResponse {
        logger.info("Creating new user for email={}", request.email)

        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException("User with email ${request.email} already exists")
        }

        val user = User(
            name = request.name,
            email = request.email,
            password = passwordEncoder.encode(request.password),
            role = UserRole.USER
        )

        val savedUser = userRepository.save(user)
        val token = jwtTokenProvider.generateToken(savedUser.email, savedUser.id!!)

        return AuthResponse(
            id = savedUser.id,
            name = savedUser.name,
            email = savedUser.email,
            role = savedUser.role.name,
            token = token,
            message = "User registered successfully"
        )
    }

    fun login(request: LoginRequest): AuthResponse {
        logger.info("Authenticating user for email={}", request.email)

        val user = userRepository.findByEmail(request.email)
            .orElseThrow { UnauthorizedException("Invalid email or password") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            logger.warn("Invalid login attempt for email={}", request.email)
            throw UnauthorizedException("Invalid email or password")
        }

        if (!user.active) {
            throw UnauthorizedException("User account is inactive")
        }

        val token = jwtTokenProvider.generateToken(user.email, user.id!!)

        return AuthResponse(
            id = user.id,
            name = user.name,
            email = user.email,
            role = user.role.name,
            token = token,
            message = "Login successful"
        )
    }

    fun getUserById(userId: String): User {
        return userRepository.findById(userId)
            .orElseThrow { UnauthorizedException("User not found") }
    }

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { UnauthorizedException("User not found") }
    }
}
