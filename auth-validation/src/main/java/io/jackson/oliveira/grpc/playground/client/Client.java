package io.jackson.oliveira.grpc.playground.client;

import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.EchoGrpc;
import io.grpc.examples.helloworld.EchoReply;
import io.grpc.examples.helloworld.EchoRequest;

public class Client {

	private final EchoGrpc.EchoBlockingStub blockingStub;

	public Client(Channel channel) {
		blockingStub = EchoGrpc.newBlockingStub(channel).withInterceptors(new AuthClientInterceptor());
	}	

	public void greet(String somethingToSay) {
		System.out.println("Will try to greet " + somethingToSay + " ...");
		EchoRequest request = EchoRequest.newBuilder().setMessage(somethingToSay).build();
		
		EchoReply response =  blockingStub.saySomething(request);
		
		System.out.println("Reply from the server: " + response.getMessage());
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:7001").usePlaintext().build();
	
		Client client = new Client(channel);
		client.greet("Jack!");
		channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
	}
	
}
