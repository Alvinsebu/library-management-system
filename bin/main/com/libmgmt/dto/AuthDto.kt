package com.libmgmt.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "Payload for registering a new user")
data class SignupRequest(
    @field:NotBlank(message = "Name cannot be blank")
    @field:Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Schema(description = "Full name of the user", example = "Test User", required = true)
    val name: String,

    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email must be valid")
    @Schema(description = "Email address used for login", example = "testuser@example.com", required = true)
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    @field:Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    @Schema(description = "Password for the new account", example = "Password123!", required = true)
    val password: String
)

@Schema(description = "Payload for authenticating a user")
data class LoginRequest(
    @field:NotBlank(message = "Email cannot be blank")
    @field:Email(message = "Email must be valid")
    @Schema(description = "Email address used for login", example = "testuser@example.com", required = true)
    val email: String,

    @field:NotBlank(message = "Password cannot be blank")
    @Schema(description = "Password for the account", example = "Password123!", required = true)
    val password: String
)

@Schema(description = "Authentication response payload")
data class AuthResponse(
    @Schema(description = "Created or authenticated user id", example = "6a0865896b5a667c973a8d30")
    val id: String,
    @Schema(description = "Authenticated user name", example = "Test User")
    val name: String,
    @Schema(description = "Authenticated user email", example = "testuser@example.com")
    val email: String,
    @Schema(description = "Assigned user role", example = "USER")
    val role: String,
    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzUxMiJ9...", required = true)
    val token: String,
    @Schema(description = "Human readable status message", example = "User registered successfully")
    val message: String
)
