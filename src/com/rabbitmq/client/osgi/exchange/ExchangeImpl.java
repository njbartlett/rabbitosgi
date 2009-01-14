package com.rabbitmq.client.osgi.exchange;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.osgi.api.Exchange;

public class ExchangeImpl implements Exchange {
	
	private final String name;
	private final Channel channel;

	public ExchangeImpl(String name, Channel channel) {
		this.name = name;
		this.channel = channel;
	}

	public void basicPublish(String routingKey, BasicProperties props,
			byte[] body) throws IOException {
		channel.basicPublish(name, routingKey, props, body);
	}

	public void basicPublish(String routingKey, boolean mandatory,
			boolean immediate, BasicProperties props, byte[] body)
			throws IOException {
		channel.basicPublish(name, routingKey, mandatory, immediate, props, body);
	}

}
