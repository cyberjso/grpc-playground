package io.jackson.oliveira.grpc.playground;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.examples.helloworld.EchoGrpc;
import io.grpc.examples.helloworld.EchoGrpc.EchoStub;
import io.grpc.examples.helloworld.EchoReply;
import io.grpc.examples.helloworld.EchoRequest;
import io.grpc.stub.ClientCallStreamObserver;
import io.grpc.stub.ClientResponseObserver;

public class Client {
	private static final Logger logger = Logger.getLogger(Client.class.getName());
	private final EchoStub stub;
	private final CountDownLatch done;
	private final ManagedChannel channel;

	public Client(ManagedChannel channel) {
		this.stub = EchoGrpc.newStub(channel);
	    this.done = new CountDownLatch(1);
	    this.channel = channel;
	}

	public static void main(String[] args) throws InterruptedException {
		ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:7001").usePlaintext().build();

		Client client = new Client(channel);
		client.saySomethingAssyncronouslyToServer();
	}

	private void saySomethingAssyncronouslyToServer() throws InterruptedException {
		
		ClientResponseObserver<EchoRequest, EchoReply> clientObserver = new ClientResponseObserver<EchoRequest, EchoReply>() {
			ClientCallStreamObserver<EchoRequest> requestStream;

			@Override
			public void onNext(EchoReply value) {
				logger.info("<-- " + value.getMessage());
				
				requestStream.request(1);

			}

			@Override
			public void onError(Throwable t) {
	            logger.log(Level.SEVERE, "Error when receivinng message from the server", t);
	            
	            done.countDown();

			}

			@Override
			public void onCompleted() {
	            logger.info("All Done");
	            
	            done.countDown();
			}

			@Override
			public void beforeStart(ClientCallStreamObserver<EchoRequest> requestStream) {
				this.requestStream = requestStream;

				requestStream.disableAutoInboundFlowControl();
				requestStream.setOnReadyHandler(new Runnable() {

					Iterator<String> iterator = messages().iterator();

					@Override
					public void run() {
						while (requestStream.isReady()) {
							if (iterator.hasNext()) {
								String message = iterator.next();
								logger.info("Sending message: " + message);
								requestStream.onNext(EchoRequest.newBuilder().setMessage(message).build());
							} else {
								requestStream.onCompleted();
							}
						}

					}
				});

			}
		};
		
		stub.saySomething(clientObserver);
		done.await();
		channel.shutdown();
		channel.awaitTermination(1, TimeUnit.SECONDS);
	}

	private List<String> messages() {
		return Arrays.asList("Sophia", "Jackson", "Emma", "Aiden", "Olivia", "Lucas", "Ava", "Liam", "Mia", "Noah",
				"Isabella", "Ethan", "Riley", "Mason", "Aria", "Caden", "Zoe", "Oliver", "Charlotte", "Elijah", "Lily",
				"Grayson", "Layla", "Jacob", "Amelia", "Michael", "Emily", "Benjamin", "Madelyn", "Carter", "Aubrey",
				"James", "Adalyn", "Jayden", "Madison", "Logan", "Chloe", "Alexander", "Harper", "Caleb", "Abigail",
				"Ryan", "Aaliyah", "Luke", "Avery", "Daniel", "Evelyn", "Jack", "Kaylee", "William", "Ella", "Owen",
				"Ellie", "Gabriel", "Scarlett", "Matthew", "Arianna", "Connor", "Hailey", "Jayce", "Nora", "Isaac",
				"Addison", "Sebastian", "Brooklyn", "Henry", "Hannah", "Muhammad", "Mila", "Cameron", "Leah", "Wyatt",
				"Elizabeth", "Dylan", "Sarah", "Nathan", "Eliana", "Nicholas", "Mackenzie", "Julian", "Peyton", "Eli",
				"Maria", "Levi", "Grace", "Isaiah", "Adeline", "Landon", "Elena", "David", "Anna", "Christian",
				"Victoria", "Andrew", "Camilla", "Brayden", "Lillian", "John", "Natalie", "Lincoln");
	}

}
