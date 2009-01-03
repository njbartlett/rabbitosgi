package com.rabbitmq.client.osgi.consumer;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class ConsoleOutputConsumer extends DefaultConsumer {

	public ConsoleOutputConsumer(Channel channel) {
		super(channel);
	}
	
	@Override
	public void handleDelivery(String consumerTag, Envelope envelope,
			BasicProperties properties, byte[] body) throws IOException {
		long deliveryTag = envelope.getDeliveryTag();
		
		String message = new String(body);
		System.out.println("---Message Received---");
		System.out.println(message);
		
		getChannel().basicAck(deliveryTag, false);
	}

}
