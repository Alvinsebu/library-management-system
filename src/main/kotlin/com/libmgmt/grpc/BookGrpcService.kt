package com.libmgmt.grpc

import com.libmgmt.service.BookService
import com.libmgmt.grpc.BookServiceGrpc
import io.grpc.stub.StreamObserver
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class BookGrpcService(
    private val bookService: BookService
) : BookServiceGrpc.BookServiceImplBase() {

    private val dateFormatter = DateTimeFormatter.ISO_DATE_TIME

    override fun getBookById(
        request: GetBookByIdRequest,
        responseObserver: StreamObserver<GetBookByIdResponse>
    ) {
        try {
            val bookResponse = bookService.getBookById(request.id)
            val grpcBook = Book.newBuilder()
                .setId(bookResponse.id)
                .setTitle(bookResponse.title)
                .setAuthor(bookResponse.author)
                .setAvailable(bookResponse.available)
                .setBorrowedBy(bookResponse.borrowedBy ?: "")
                .setBorrowedAt(bookResponse.borrowedAt?.format(dateFormatter) ?: "")
                .setExpiryAt(bookResponse.expiryAt?.format(dateFormatter) ?: "")
                .setPolicy(bookResponse.policy)
                .build()

            val response = GetBookByIdResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Book retrieved successfully")
                .setBook(grpcBook)
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = GetBookByIdResponse.newBuilder()
                .setSuccess(false)
                .setMessage(e.message ?: "Error retrieving book")
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }

    override fun listBooks(
        request: ListBooksRequest,
        responseObserver: StreamObserver<ListBooksResponse>
    ) {
        try {
            val books = if (request.availableOnly) {
                bookService.getAvailableBooks()
            } else {
                bookService.getAllBooks()
            }

            val grpcBooks = books.map { bookResponse ->
                Book.newBuilder()
                    .setId(bookResponse.id)
                    .setTitle(bookResponse.title)
                    .setAuthor(bookResponse.author)
                    .setAvailable(bookResponse.available)
                    .setBorrowedBy(bookResponse.borrowedBy ?: "")
                    .setBorrowedAt(bookResponse.borrowedAt?.format(dateFormatter) ?: "")
                    .setExpiryAt(bookResponse.expiryAt?.format(dateFormatter) ?: "")
                    .setPolicy(bookResponse.policy)
                    .build()
            }

            val response = ListBooksResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Books retrieved successfully")
                .addAllBooks(grpcBooks)
                .setCount(grpcBooks.size)
                .build()

            responseObserver.onNext(response)
            responseObserver.onCompleted()
        } catch (e: Exception) {
            val response = ListBooksResponse.newBuilder()
                .setSuccess(false)
                .setMessage(e.message ?: "Error retrieving books")
                .build()
            responseObserver.onNext(response)
            responseObserver.onCompleted()
        }
    }
}
