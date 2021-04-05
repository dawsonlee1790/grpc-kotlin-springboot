package com.example.client

import grpc.kotlin.api.HelloReply
import grpc.kotlin.api.HelloRequest
import grpc.kotlin.api.SimpleGrpc
import grpc.kotlin.api.SimpleGrpcKt
import io.grpc.*
import net.devh.boot.grpc.client.inject.GrpcClient
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class ClientApplication

fun main(args: Array<String>) {
    runApplication<ClientApplication>(*args)
}


@RestController
class GrpcClientController(
    private val grpcClientService: GrpcClientService
) {

    @RequestMapping("/")
    suspend fun printMessage(@RequestParam(defaultValue = "Michael") name: String?): String {
        return grpcClientService.sendMessage(name)
    }
}


@Service
class GrpcClientService {

    @GrpcClient("local-grpc-server")
    private lateinit var simpleStub: SimpleGrpcKt.SimpleCoroutineStub

    suspend fun sendMessage(name: String?): String {
        val response: HelloReply = simpleStub.sayHello(HelloRequest.newBuilder().setName(name).build())
        return response.message
    }
}


@Order(Ordered.LOWEST_PRECEDENCE)
@Configuration(proxyBeanMethods = false)
class GlobalClientInterceptorConfiguration {
    @GrpcGlobalClientInterceptor
    fun logClientInterceptor(): ClientInterceptor {
        return LogGrpcInterceptor()
    }
}

class LogGrpcInterceptor : ClientInterceptor {
    override fun <ReqT, RespT> interceptCall(
        method: MethodDescriptor<ReqT, RespT>,
        callOptions: CallOptions, next: Channel
    ): ClientCall<ReqT, RespT> {
        log.info(method.fullMethodName)
        return next.newCall(method, callOptions)
    }

    companion object {
        private val log = LoggerFactory.getLogger(LogGrpcInterceptor::class.java)
    }
}