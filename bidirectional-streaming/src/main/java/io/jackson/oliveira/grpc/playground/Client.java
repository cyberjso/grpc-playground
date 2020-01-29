package io.jackson.oliveira.grpc.playground;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.EchoGrpc;
import io.grpc.examples.helloworld.EchoGrpc.EchoStub;
import io.grpc.examples.helloworld.EchoReply;
import io.grpc.examples.helloworld.EchoRequest;
import io.grpc.stub.StreamObserver;

public class Client {
	private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
	private final EchoStub client;

	public Client(ManagedChannel channel) {
		client = EchoGrpc.newStub(channel);
	}

	public static void main(String[] args) throws InterruptedException {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:7001").usePlaintext().build();

		Client client = new Client(channel);
	
		client.saySomethingAssyncronouslyToServer("Jack!");
		channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);		
	}

	private void saySomethingAssyncronouslyToServer(String message) {
		StreamObserver<EchoReply> responseObserver = new StreamObserver<EchoReply>() {

			@Override
			public void onNext(EchoReply value) {
				logger.info("Receiving a reply: " +  value.getMessage());
			}

			@Override
			public void onError(Throwable t) {
				logger.log(Level.SEVERE, "Error when processing the stream", t);
			}

			@Override
			public void onCompleted() {
				logger.info("Client received the entire stream");
			}
		};
		
		StreamObserver<EchoRequest> requestObserver = client.saySomething(responseObserver);
		for (int i = 0; i < 100000; i++) {
			requestObserver.onNext(EchoRequest.newBuilder().setMessage("Message batch: " + i  +  " - " + message).build());
			try {
				Thread.currentThread().sleep(2000);
			} catch (Exception e) {
				throw new RuntimeException("Error when trying to make current thread to sleep", e);
			}
		}
		
		requestObserver.onCompleted();
	}
	
}

