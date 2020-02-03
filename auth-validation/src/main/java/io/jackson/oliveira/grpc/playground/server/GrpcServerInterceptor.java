package io.jackson.oliveira.grpc.playground.server;

import java.util.logging.Logger;

import io.grpc.Metadata.Key;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;

public class GrpcServerInterceptor implements ServerInterceptor {
	private static final Logger logger = Logger.getLogger(GrpcServerInterceptor.class.getName());

	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
		logger.info("calling interceptor");

		String authToken = headers.get(Key.of("auth_token", Metadata.ASCII_STRING_MARSHALLER));

		if (authToken == null || !authToken.equals("some_auth_token")) {
			throw new RuntimeException("Invalid auth key");
		}

		logger.info("Token received from client" + authToken);
		return next.startCall(call, headers);
	}

}
