package com.libmgmt.controller

import com.libmgmt.dto.ApiResponse
import com.libmgmt.dto.AuthResponse
import com.libmgmt.dto.LoginRequest
import com.libmgmt.dto.SignupRequest
import com.libmgmt.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Exposes authentication endpoints for signup and login.
 *
 * All responses are wrapped in a shared [ApiResponse] envelope.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
class AuthController(
    private val authService: AuthService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping("/signup")
    @Operation(summary = "Register a new user", description = "Create a new user account and return an authentication token")
    @SwaggerApiResponse(responseCode = "201", description = "User registered successfully")
    fun signup(@Valid @RequestBody request: SignupRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        logger.info("Signup requested for email={}", request.email)
        val response = authService.signup(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(success = true, message = "User registered successfully", data = response))
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Validate credentials and return a JWT token")
    @SwaggerApiResponse(responseCode = "200", description = "Login successful")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        logger.info("Login requested for email={}", request.email)
        val response = authService.login(request)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "Login successful", data = response)
        )
    }
}
