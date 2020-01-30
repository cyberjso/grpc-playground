package io.jackson.oliveira.grpc.playground;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.examples.helloworld.EchoGrpc.EchoImplBase;
import io.grpc.examples.helloworld.EchoReply;
import io.grpc.examples.helloworld.EchoRequest;
import io.grpc.stub.ServerCallStreamObserver;
import io.grpc.stub.StreamObserver;

public class Service extends EchoImplBase {
	private static final Logger logger = Logger.getLogger(Service.class.getName());

	@Override
	public StreamObserver<EchoRequest> saySomething(StreamObserver<EchoReply> responseObserver) {
		ServerCallStreamObserver<EchoReply> serverCallStreamObserver   =  (ServerCallStreamObserver<EchoReply>) responseObserver;
		
		final AtomicBoolean wasReady =  new AtomicBoolean(false);
		
		serverCallStreamObserver.setOnReadyHandler(new Runnable() {
			
			@Override
			public void run() {	
				if (serverCallStreamObserver.isReady() && wasReady.compareAndSet(false, true)) {
					logger.info("SERVER READY!");
					
					serverCallStreamObserver.request(1);
					
				}
			}
		});
		
		return new StreamObserver<EchoRequest>() {
			
			@Override
			public void onNext(EchoRequest request) {
				String message  =  request.getMessage();
				logger.info("Received request ");			
				
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					logger.log(Level.SEVERE, "Error when trying to make the server sleep", e);
				}
				
				EchoReply reply  = EchoReply.newBuilder().setMessage("Replied to: " + message).build();
				
				responseObserver.onNext(reply);
				
				if (serverCallStreamObserver.isReady()) {
					serverCallStreamObserver.request(1);
				} else {
					wasReady.set(false);
				}
			}
			
			@Override
			public void onError(Throwable t) { 
				logger.log(Level.SEVERE, "Error when streaming to the cklient", t);
				
				responseObserver.onCompleted();
			}
			
			@Override
			public void onCompleted() {
				logger.info("Server completed");
				
				responseObserver.onCompleted();
			}
			
		};
		
	}
}
