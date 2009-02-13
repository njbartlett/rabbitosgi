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

import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.Wire;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.osgi.api.Exchange;

/**
 * A Wire Admin consumer that acts as an endpoint for publishing to an AMQP exchange. Delivered values must be of type (byte[]) therefore
 * this service must be published with the wireadmin.consumer.flavors property set to byte arr
 * 
 * @author Neil Bartlett
 */
public class ExchangeWireEndpoint implements Consumer {
	
	private final Exchange exchange;
	private final String routingKey;
	private final boolean mandatory;
	private final boolean immediate;
	private final BasicProperties props;

	public ExchangeWireEndpoint(Exchange exchange, String routingKey, boolean mandatory, boolean immediate, BasicProperties props) {
		this.exchange = exchange;
		this.routingKey = routingKey;
		this.mandatory = mandatory;
		this.immediate = immediate;
		this.props = props;
	}

	public void producersConnected(Wire[] wires) {
		// Not interested.
	}

	public void updated(Wire wire, Object value) {
		byte[] body = (byte[]) value;
		try {
			exchange.basicPublish(routingKey, mandatory, immediate, props, body);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
