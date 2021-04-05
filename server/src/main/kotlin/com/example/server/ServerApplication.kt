package com.example.server

import grpc.kotlin.api.HelloReply
import grpc.kotlin.api.HelloRequest
import grpc.kotlin.api.SimpleGrpcKt
import io.grpc.Metadata
import io.grpc.ServerCall
import io.grpc.ServerCallHandler
import io.grpc.ServerInterceptor
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import net.devh.boot.grpc.server.service.GrpcService
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class ServerApplication

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}

@GrpcService
class GrpcServerService : SimpleGrpcKt.SimpleCoroutineImplBase() {
    override suspend fun sayHello(request: HelloRequest): HelloReply {
        return HelloReply.newBuilder().setMessage("Hello ==> " + request.name).build()
    }
}


@Configuration(proxyBeanMethods = false)
class GlobalInterceptorConfiguration {
    @GrpcGlobalServerInterceptor
    fun logServerInterceptor(): ServerInterceptor {
        return LogGrpcInterceptor()
    }
}

class LogGrpcInterceptor : ServerInterceptor {
    override fun <ReqT, RespT> interceptCall(
        serverCall: ServerCall<ReqT, RespT>, metadata: Metadata,
        serverCallHandler: ServerCallHandler<ReqT, RespT>
    ): ServerCall.Listener<ReqT> {
        log.info(serverCall.methodDescriptor.fullMethodName)
        return serverCallHandler.startCall(serverCall, metadata)
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogGrpcInterceptor::class.java)
    }
}
