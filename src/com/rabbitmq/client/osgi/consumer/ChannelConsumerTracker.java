/*******************************************************************************
 * Copyright (c) 2009 Neil Bartlett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Neil Bartlett - initial API and implementation
 ******************************************************************************/
package com.rabbitmq.client.osgi.consumer;

import java.io.IOException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class ChannelConsumerTracker extends ServiceTracker {
	
	private final String queueName;

	public ChannelConsumerTracker(BundleContext context, String queueName) {
		super(context, Connection.class.getName(), null);
		this.queueName = queueName;
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		Connection conn = (Connection) context.getService(reference);
		String consumerTag = null;
		
		Channel channel = null;
		try {
			channel = conn.createChannel();
			ConsoleOutputConsumer consumer = new ConsoleOutputConsumer(channel);
			channel.queueDeclare(queueName);
			consumerTag = channel.basicConsume(queueName, false, consumer);
			return new Pair<Channel,String>(channel, consumerTag);
		} catch (IOException e) {
			System.err.println("Error subscribing consumer");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		@SuppressWarnings("unchecked")
		Pair<Channel,String> pair = (Pair<Channel, String>) service;
		
		try {
			pair.getFst().basicCancel(pair.getSnd());
			pair.getFst().close();
		} catch (IOException e) {
			System.err.println("Error unsubscribing consumer");
			e.printStackTrace();
		}
		
		context.ungetService(reference);
	}
}

class Pair<A, B> {
	private final A fst;
	private final B snd;

	public Pair(A fst, B snd) {
		this.fst = fst;
		this.snd = snd;
	}

	public A getFst() {
		return fst;
	}

	public B getSnd() {
		return snd;
	}

}
