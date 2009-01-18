package com.rabbitmq.client.osgi.common;

public final class ServiceProperties {
	
	public static final String CONNECTION_NAME = "connection.name";
	public static final String CONNECTION_HOST = "connection.host";
	
	public static final String EXCHANGE_NAME = "exchange.name";
	public static final String EXCHANGE_CONNECTION = "exchange.connection";
	public static final String EXCHANGE_TYPE = "exchange.type";
	public static final String EXCHANGE_PASSIVE = "exchange.passive";
	public static final String EXCHANGE_DURABLE = "exchange.durable";
	public static final String EXCHANGE_AUTODELETE = "exchange.autoDelete";
	
	public static final String PUBLISH_ROUTING_KEY = "publish.routingKey";
	public static final String PUBLISH_MANDATORY = "publish.mandatory";
	public static final String PUBLISH_IMMEDIATE = "publish.immediate";
	
	public static final String ENDPOINT_SERVICE_PID = "endpoint.service.pid";
	
	
	private ServiceProperties() {
		// Prevent instantiation
	}
}
