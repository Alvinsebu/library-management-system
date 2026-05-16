package com.libmgmt.controller

import com.libmgmt.dto.ApiResponse
import com.libmgmt.model.User
import com.libmgmt.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User endpoints")
@SecurityRequirement(name = "bearer-jwt")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    fun getCurrentUser(authentication: Authentication): ResponseEntity<ApiResponse<User>> {
        @Suppress("UNCHECKED_CAST")
        val userId = (authentication.details as Map<String, String>)["userId"] ?: ""
        val user = userService.getUserById(userId)
        return ResponseEntity.ok(
            ApiResponse(success = true, message = "User profile retrieved", data = user)
        )
    }
}
