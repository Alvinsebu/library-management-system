package com.libmgmt.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

enum class BookPolicy {
    NORMAL,
    EXPIRY,
    END_OF_DAY
}

@Document(collection = "books")
data class Book(
    @Id
    val id: String? = null,
    val title: String,
    val author: String,
    val available: Boolean = true,
    val borrowedBy: String? = null,
    val borrowedAt: LocalDateTime? = null,
    val expiryAt: LocalDateTime? = null,
    val policy: BookPolicy = BookPolicy.NORMAL,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
