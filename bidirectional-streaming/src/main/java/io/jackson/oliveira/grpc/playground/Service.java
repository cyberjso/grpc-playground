package io.jackson.oliveira.grpc.playground;

import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.examples.helloworld.EchoGrpc.EchoImplBase;
import io.grpc.examples.helloworld.EchoReply;
import io.grpc.examples.helloworld.EchoRequest;
import io.grpc.stub.StreamObserver;

public class Service extends EchoImplBase {
	private static final Logger logger = Logger.getLogger(Service.class.getName());

	@Override
	public StreamObserver<EchoRequest> saySomething(StreamObserver<EchoReply> responseObserver) {

		return new StreamObserver<EchoRequest>() {

			@Override
			public void onNext(EchoRequest value) {
				logger.info("Received message on the server: " + value);
				
				// We can see the server on the client logs It stoping reading messages because the server stop sending them.
				for (int i = 0; i < 1000; i++) {
					if (i == 500 || i == 999 ) {
						try {
							Thread.currentThread().sleep(10000);
						} catch (InterruptedException e) {
							logger.log(Level.SEVERE, "Error when trying to stop current thread", e);
						}
					}
					responseObserver.onNext(EchoReply.newBuilder().setMessage("Replying to  message: " + value.getMessage() + ". Reply: " + i).build());
				}
			}

			@Override
			public void onError(Throwable t) {
				logger.log(Level.SEVERE, "Error when processing the stream", t);
			}

			@Override
			public void onCompleted() {
				responseObserver.onCompleted();
			}
		};

	}
}
