package com.libmgmt.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Standard API response wrapper")
data class ApiResponse<T>(
    @Schema(description = "Indicates whether the request succeeded", example = "true")
    val success: Boolean,
    @Schema(description = "Human readable message", example = "Operation completed successfully")
    val message: String,
    @Schema(description = "Payload returned by the API")
    val data: T? = null,
    @Schema(description = "Error code or exception name when the request fails", example = "ValidationError")
    val error: String? = null,
    @Schema(description = "Response timestamp in milliseconds since epoch", example = "1625079047000")
    val timestamp: Long = System.currentTimeMillis()
)

@Schema(description = "Validation error detail for a specific field")
data class ValidationError(
    @Schema(description = "The request field with invalid input", example = "email")
    val field: String,
    @Schema(description = "Validation error message", example = "Email must be valid")
    val message: String
)

@Schema(description = "Error response returned when an exception occurs")
data class ErrorResponse(
    @Schema(description = "Indicates failure", example = "false")
    val success: Boolean = false,
    @Schema(description = "Human readable error message", example = "Validation failed")
    val message: String,
    @Schema(description = "Optional validation errors included with the response")
    val errors: List<ValidationError>? = null,
    @Schema(description = "Error type or exception class name", example = "ValidationError")
    val error: String? = null,
    @Schema(description = "Response timestamp in milliseconds since epoch", example = "1625079047000")
    val timestamp: Long = System.currentTimeMillis(),
    @Schema(description = "HTTP status code returned by the API", example = "400")
    val status: Int
)
