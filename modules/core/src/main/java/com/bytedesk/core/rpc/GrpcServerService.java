package com.bytedesk.core.rpc;

import org.springframework.stereotype.Service;

import com.bytedesk.core.proto.HelloReply;
import com.bytedesk.core.proto.HelloRequest;
import com.bytedesk.core.proto.SimpleGrpc.SimpleImplBase;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;

/**
 * gRPC Server Service
 * https://docs.spring.io/spring-grpc/reference/getting-started.html
 * 
 * test: grpcurl -d '{"name":"Hi"}' -plaintext 127.0.0.1:9003 Simple.SayHello
 *
 * @author bytedesk.com
 */
@Slf4j
@Service
class GrpcServerService extends SimpleImplBase {

    @Override
    public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        log.info("Hello " + req.getName());
        if (req.getName().startsWith("error")) {
            throw new IllegalArgumentException("Bad name: " + req.getName());
        }
        if (req.getName().startsWith("internal")) {
            throw new RuntimeException();
        }
        HelloReply reply = HelloReply.newBuilder().setMessage("Hello ==> " + req.getName()).build();
        responseObserver.onNext(reply);
        responseObserver.onCompleted();
    }

    @Override
    public void streamHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
        log.info("Hello " + req.getName());
        int count = 0;
        while (count < 10) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello(" + count + ") ==> " + req.getName()).build();
            responseObserver.onNext(reply);
            count++;
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                responseObserver.onError(e);
                return;
            }
        }
        responseObserver.onCompleted();
    }
}
