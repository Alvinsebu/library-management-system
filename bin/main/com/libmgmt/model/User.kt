package com.libmgmt.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class UserRole {
    USER,
    ADMIN
}

@Document(collection = "users")
data class User(
    @Id
    val id: String? = null,
    val name: String,
    val email: String,
    val password: String,
    val role: UserRole = UserRole.USER,
    val active: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
