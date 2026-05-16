package com.libmgmt.repository

import com.libmgmt.model.Book
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface BookRepository : MongoRepository<Book, String> {
    fun findAllByAvailable(available: Boolean): List<Book>
    fun findAllByBorrowedBy(borrowedBy: String): List<Book>
    @Query("{ 'available': true }")
    fun findAllAvailableBooks(): List<Book>
    @Query("{ 'expiryAt': { \$lt: ?0 }, 'available': false }")
    fun findExpiredBooks(currentTime: Any): List<Book>
    @Query("{ 'policy': 'END_OF_DAY', 'available': false }")
    fun findEndOfDayBooks(): List<Book>
}
