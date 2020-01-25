package io.jackson.oliveira.grpc.playground;

import io.grpc.examples.helloworld.EchoGrpc.EchoImplBase;
import io.grpc.examples.helloworld.EchoReply;
import io.grpc.examples.helloworld.EchoRequest;
import io.grpc.stub.StreamObserver;

public class Service extends EchoImplBase {

	@Override
	public void saySomething(EchoRequest req, StreamObserver<EchoReply> responseObserver) {
		EchoReply reply =  EchoReply.newBuilder().setMessage("Hello "  + req.getMessage()).build();
		responseObserver.onNext(reply);
		responseObserver.onCompleted();
	}
	
}
