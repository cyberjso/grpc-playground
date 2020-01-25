package io.jackson.oliveira.grpc.playground;

import java.util.concurrent.TimeUnit;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.GreeterGrpc;
import io.grpc.examples.helloworld.HelloReply;
import io.grpc.examples.helloworld.HelloRequest;

public class Client {

	private final GreeterGrpc.GreeterBlockingStub blockingStub;

	public Client(Channel channel) {
		blockingStub = GreeterGrpc.newBlockingStub(channel);
	}	

	public void greet(String somethingToSay) {
		System.out.println("Will try to greet " + somethingToSay + " ...");
		HelloRequest request = HelloRequest.newBuilder().setName(somethingToSay).build();
		
		HelloReply response =  blockingStub.sayHello(request);
		
		System.out.println("Reply from the server: " + response.getMessage());
	}
	
	
	public static void main(String[] args) throws InterruptedException {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:7001").usePlaintext().build();
	
		Client client = new Client(channel);
		client.greet("Jack!");
		channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
	}
	
}
