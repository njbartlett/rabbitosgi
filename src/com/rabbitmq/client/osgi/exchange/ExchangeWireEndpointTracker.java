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

import java.util.Properties;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.wireadmin.Consumer;
import org.osgi.service.wireadmin.WireConstants;
import org.osgi.util.tracker.ServiceTracker;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.osgi.api.Exchange;
import com.rabbitmq.client.osgi.common.ServiceProperties;

public class ExchangeWireEndpointTracker extends ServiceTracker {

	private static final Class<? extends byte[]> CLASS_BYTE_ARRAY = (new byte[] {}).getClass();
	private static final Logger LOG = Logger.getLogger(ExchangeWireEndpointTracker.class);

	private final String endpointPid;

	private final String exchangeName;
	
	private final String routingKey;
	private final boolean mandatory;
	private final boolean immediate;
	private final BasicProperties props;



	public ExchangeWireEndpointTracker(BundleContext context, String exchangeName,
			String connection, String routingKey, String endpointPid, boolean mandatory, boolean immediate, BasicProperties props) {
		super(context, createFilter(exchangeName, connection), null);
		this.exchangeName = exchangeName;
		this.routingKey = routingKey;
		this.endpointPid = endpointPid;
		this.mandatory = mandatory;
		this.immediate = immediate;
		this.props = props;
	}

	private static Filter createFilter(String exchange, String connection) {
		String filterStr;

		if (connection != null) {
			filterStr = String.format("(&(%s=%s)(%s=%s))",
					ServiceProperties.EXCHANGE_NAME, exchange,
					ServiceProperties.CONNECTION_NAME, connection);
		} else {
			filterStr = String.format("(%s=%s)",
					ServiceProperties.EXCHANGE_NAME, exchange);
		}

		try {
			return FrameworkUtil.createFilter(filterStr);
		} catch (InvalidSyntaxException e) {
			// shouldn't happen
			throw new RuntimeException(e);
		}
	}

	@Override
	public Object addingService(ServiceReference reference) {
		Exchange exchange = (Exchange) context.getService(reference);

		ExchangeWireEndpoint endpoint = new ExchangeWireEndpoint(exchange,
				routingKey, mandatory, immediate, props);

		Properties svcProps = new Properties();
		svcProps.put(Constants.SERVICE_PID, endpointPid);
		svcProps.put(WireConstants.WIREADMIN_CONSUMER_FLAVORS,
				new Class<?>[] { CLASS_BYTE_ARRAY });

		LOG.debug("Exchange '" + exchangeName + "' ADDED, registering Wire Admin endpoint with service PID '" + endpointPid + "'.");
		return context.registerService(Consumer.class.getName(), endpoint,
				svcProps);
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		LOG.debug("Exchange '" + exchangeName + "' REMOVED, unregistering Wire Admin endpoint with service PID '" + endpointPid + "'.");
		ServiceRegistration registration = (ServiceRegistration) service;
		registration.unregister();
		context.ungetService(reference);
	}

	public String getExchangeName() {
		return exchangeName;
	}

}
