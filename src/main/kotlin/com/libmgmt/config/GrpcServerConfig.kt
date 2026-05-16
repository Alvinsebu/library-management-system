package com.libmgmt.config

import io.grpc.Server
import io.grpc.ServerBuilder
import com.libmgmt.grpc.BookGrpcService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.slf4j.LoggerFactory

@Configuration
class GrpcServerConfig(
    @Value("\${grpc.server.port:9091}")
    private val grpcPort: Int,
    private val bookGrpcService: BookGrpcService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun grpcServer(): Server {
        val server = ServerBuilder.forPort(grpcPort)
            .addService(bookGrpcService)
            .maxInboundMessageSize(4 * 1024 * 1024) // 4MB
            .build()
            .start()

        logger.info("gRPC Server started on port $grpcPort")

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.info("Shutting down gRPC Server")
            try {
                server.shutdown()
                if (!server.awaitTermination(30, java.util.concurrent.TimeUnit.SECONDS)) {
                    logger.warn("Server didn't terminate gracefully")
                    server.shutdownNow()
                }
            } catch (e: InterruptedException) {
                logger.error("Error during server shutdown", e)
                server.shutdownNow()
            }
        })

        return server
    }
}
