package io.jackson.oliveira.grpc.playground.client;

import java.util.logging.Logger;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ForwardingClientCall;
import io.grpc.Metadata;
import io.grpc.Metadata.Key;
import io.grpc.MethodDescriptor;

public class AuthClientInterceptor implements ClientInterceptor {
	private static final Logger logger = Logger.getLogger(AuthClientInterceptor.class.getName());

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(final MethodDescriptor<ReqT, RespT> methodDescriptor, final CallOptions callOptions, final Channel channel) {
		logger.info("Setting auth Token");
		
		return new ForwardingClientCall.SimpleForwardingClientCall<ReqT, RespT>(channel.newCall(methodDescriptor, callOptions)) {
			
			public void start(final Listener<RespT> responseListener, final Metadata headers) {
				headers.put(Key.of("auth_token", Metadata.ASCII_STRING_MARSHALLER), "some_auth_token");
				super.start(responseListener, headers);
			}
			
		};
	}

}
