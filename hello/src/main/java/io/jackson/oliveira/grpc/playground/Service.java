package io.jackson.oliveira.grpc.playground;

import io.grpc.examples.helloworld.HelloRequest;
import io.grpc.examples.helloworld.GreeterGrpc.GreeterImplBase;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.stub.StreamObserver;

public class Service extends GreeterImplBase {

	@Override
	public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
		HelloReply reply =  HelloReply.newBuilder().setMessage("Hello "  + req.getName()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
	
}
