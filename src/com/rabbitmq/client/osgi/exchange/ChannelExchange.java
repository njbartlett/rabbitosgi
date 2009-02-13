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
package com.rabbitmq.client.osgi.exchange;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.osgi.api.Exchange;

public class ChannelExchange implements Exchange {
	
	private final String name;
	private final Channel channel;

	public ChannelExchange(String name, Channel channel) {
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
