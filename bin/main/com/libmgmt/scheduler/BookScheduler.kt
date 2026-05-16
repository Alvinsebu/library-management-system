package com.libmgmt.scheduler

import com.libmgmt.service.BookService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class BookScheduler(
    private val bookService: BookService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Auto-return expired books every 5 minutes
     */
    @Scheduled(fixedDelay = 300000) // 5 minutes
    fun autoReturnExpiredBooks() {
        try {
            logger.info("Starting auto-return of expired books")
            bookService.autoReturnExpiredBooks()
            logger.info("Auto-return of expired books completed")
        } catch (e: Exception) {
            logger.error("Error during auto-return of expired books", e)
        }
    }

    /**
     * Auto-return end-of-day books at 10 PM every day
     */
    @Scheduled(cron = "0 0 22 * * *") // 10 PM daily
    fun autoReturnEndOfDayBooks() {
        try {
            logger.info("Starting auto-return of end-of-day books")
            bookService.autoReturnEndOfDayBooks()
            logger.info("Auto-return of end-of-day books completed")
        } catch (e: Exception) {
            logger.error("Error during auto-return of end-of-day books", e)
        }
    }
}
