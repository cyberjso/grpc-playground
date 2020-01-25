package io.jackson.oliveira.grpc.playground;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
	private Server server;
	
	public static void main(String[] args) {
		GrpcServer grpcServer = new GrpcServer();
		grpcServer.start();
		grpcServer.blockUntilShutdown();
	}
	
	public void start() {
		int port = 7001;
		
		try {
			server = ServerBuilder
				.forPort(port)
				.addService(new Service())
				.build()
				.start();
		} catch (IOException e) {
			throw new RuntimeException("Error when starting the server", e);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		
			@Override
			public void run() {
				System.out.println("Hello server!!");
				GrpcServer.this.stop();
			}
		});
			
	}

	private void stop() {
		try {
			server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException("Error when shutting the server down", e);
		}
		
	}

	private void blockUntilShutdown() {
		try {
			server.awaitTermination();
		} catch (InterruptedException e) {
			throw new RuntimeException("Error when trying to await termination", e);
		} 
	}
	
}
