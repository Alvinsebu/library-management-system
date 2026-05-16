package com.libmgmt.grpc

import com.libmgmt.grpc.BookServiceGrpc.getServiceDescriptor
import io.grpc.CallOptions
import io.grpc.CallOptions.DEFAULT
import io.grpc.Channel
import io.grpc.Metadata
import io.grpc.MethodDescriptor
import io.grpc.ServerServiceDefinition
import io.grpc.ServerServiceDefinition.builder
import io.grpc.ServiceDescriptor
import io.grpc.Status
import io.grpc.Status.UNIMPLEMENTED
import io.grpc.StatusException
import io.grpc.kotlin.AbstractCoroutineServerImpl
import io.grpc.kotlin.AbstractCoroutineStub
import io.grpc.kotlin.ClientCalls
import io.grpc.kotlin.ClientCalls.unaryRpc
import io.grpc.kotlin.ServerCalls
import io.grpc.kotlin.ServerCalls.unaryServerMethodDefinition
import io.grpc.kotlin.StubFor
import kotlin.String
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.jvm.JvmOverloads
import kotlin.jvm.JvmStatic

/**
 * Holder for Kotlin coroutine-based client and server APIs for com.libmgmt.grpc.BookService.
 */
public object BookServiceGrpcKt {
  public const val SERVICE_NAME: String = BookServiceGrpc.SERVICE_NAME

  @JvmStatic
  public val serviceDescriptor: ServiceDescriptor
    get() = BookServiceGrpc.getServiceDescriptor()

  public val getBookByIdMethod: MethodDescriptor<GetBookByIdRequest, GetBookByIdResponse>
    @JvmStatic
    get() = BookServiceGrpc.getGetBookByIdMethod()

  public val listBooksMethod: MethodDescriptor<ListBooksRequest, ListBooksResponse>
    @JvmStatic
    get() = BookServiceGrpc.getListBooksMethod()

  /**
   * A stub for issuing RPCs to a(n) com.libmgmt.grpc.BookService service as suspending coroutines.
   */
  @StubFor(BookServiceGrpc::class)
  public class BookServiceCoroutineStub @JvmOverloads constructor(
    channel: Channel,
    callOptions: CallOptions = DEFAULT,
  ) : AbstractCoroutineStub<BookServiceCoroutineStub>(channel, callOptions) {
    public override fun build(channel: Channel, callOptions: CallOptions): BookServiceCoroutineStub
        = BookServiceCoroutineStub(channel, callOptions)

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun getBookById(request: GetBookByIdRequest, headers: Metadata = Metadata()):
        GetBookByIdResponse = unaryRpc(
      channel,
      BookServiceGrpc.getGetBookByIdMethod(),
      request,
      callOptions,
      headers
    )

    /**
     * Executes this RPC and returns the response message, suspending until the RPC completes
     * with [`Status.OK`][Status].  If the RPC completes with another status, a corresponding
     * [StatusException] is thrown.  If this coroutine is cancelled, the RPC is also cancelled
     * with the corresponding exception as a cause.
     *
     * @param request The request message to send to the server.
     *
     * @param headers Metadata to attach to the request.  Most users will not need this.
     *
     * @return The single response from the server.
     */
    public suspend fun listBooks(request: ListBooksRequest, headers: Metadata = Metadata()):
        ListBooksResponse = unaryRpc(
      channel,
      BookServiceGrpc.getListBooksMethod(),
      request,
      callOptions,
      headers
    )
  }

  /**
   * Skeletal implementation of the com.libmgmt.grpc.BookService service based on Kotlin coroutines.
   */
  public abstract class BookServiceCoroutineImplBase(
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
  ) : AbstractCoroutineServerImpl(coroutineContext) {
    /**
     * Returns the response to an RPC for com.libmgmt.grpc.BookService.GetBookById.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun getBookById(request: GetBookByIdRequest): GetBookByIdResponse = throw
        StatusException(UNIMPLEMENTED.withDescription("Method com.libmgmt.grpc.BookService.GetBookById is unimplemented"))

    /**
     * Returns the response to an RPC for com.libmgmt.grpc.BookService.ListBooks.
     *
     * If this method fails with a [StatusException], the RPC will fail with the corresponding
     * [Status].  If this method fails with a [java.util.concurrent.CancellationException], the RPC
     * will fail
     * with status `Status.CANCELLED`.  If this method fails for any other reason, the RPC will
     * fail with `Status.UNKNOWN` with the exception as a cause.
     *
     * @param request The request from the client.
     */
    public open suspend fun listBooks(request: ListBooksRequest): ListBooksResponse = throw
        StatusException(UNIMPLEMENTED.withDescription("Method com.libmgmt.grpc.BookService.ListBooks is unimplemented"))

    public final override fun bindService(): ServerServiceDefinition =
        builder(getServiceDescriptor())
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = BookServiceGrpc.getGetBookByIdMethod(),
      implementation = ::getBookById
    ))
      .addMethod(unaryServerMethodDefinition(
      context = this.context,
      descriptor = BookServiceGrpc.getListBooksMethod(),
      implementation = ::listBooks
    )).build()
  }
}
