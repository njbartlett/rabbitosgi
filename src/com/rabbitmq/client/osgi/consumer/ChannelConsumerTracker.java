package com.rabbitmq.client.osgi.consumer;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.rabbitmq.client.Channel;

public class ChannelConsumerTracker extends ServiceTracker {
	
	private final String queueName;

	public ChannelConsumerTracker(BundleContext context, String queueName) {
		super(context, Channel.class.getName(), null);
		this.queueName = queueName;
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		Channel channel = (Channel) context.getService(reference);
		String consumerTag = null;
		
		ConsoleOutputConsumer consumer = new ConsoleOutputConsumer(channel);
		try {
			consumerTag = channel.basicConsume(queueName, false, consumer);
		} catch (IOException e) {
			System.err.println("Error subscribing consumer");
			e.printStackTrace();
		}
		
		return new Pair<Channel,String>(channel, consumerTag);
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		@SuppressWarnings("unchecked")
		Pair<Channel,String> pair = (Pair<Channel, String>) service;
		
		try {
			pair.getFst().basicCancel(pair.getSnd());
		} catch (IOException e) {
			System.err.println("Error unsubscribing consumer");
			e.printStackTrace();
		}
		
		context.ungetService(reference);
	}
}
