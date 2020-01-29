package io.jackson.oliveira.grpc.playground;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {
	private static final Logger logger = Logger.getLogger(GrpcServer.class.getName());
	private Server server;

	public static void main(String[] args) throws IOException {
		GrpcServer grpcServer = new GrpcServer();
		grpcServer.start();
		grpcServer.blockUntilShutdown();
	}

	private void blockUntilShutdown() {
		try {
			server.awaitTermination();
		} catch (InterruptedException e) {
			throw new RuntimeException("Error when trying to await termination", e);
		} 
	}

	private void start() throws IOException {
		int port = 7001;

		server = ServerBuilder.forPort(port).addService(new Service()).build().start();

		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				logger.info("*** Stopping the server ****");
				GrpcServer.this.stop();
			}
		});
	}

	private void stop() {
		if (server != null) {
			try {
				server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
			} catch (Exception e) {
				throw new RuntimeException("Error when waitting server to shutdown", e);
			}
			
		}
	}

}
