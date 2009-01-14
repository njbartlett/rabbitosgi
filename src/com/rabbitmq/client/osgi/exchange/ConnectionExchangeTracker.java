package com.rabbitmq.client.osgi.exchange;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.osgi.api.Exchange;
import com.rabbitmq.client.osgi.common.Pair;
import com.rabbitmq.client.osgi.common.ServiceProperties;

public class ConnectionExchangeTracker extends ServiceTracker {
	
	private final LogService log;
	
	private final String connName;
	private final String exchangeName;
	private final String type;
	private final boolean passive;
	private final boolean durable;
	private final boolean autoDelete;
	private final Map<String, Object> arguments;
	

	public ConnectionExchangeTracker(BundleContext context, LogService log, String connName, String exchangeName, String type, boolean passive, boolean durable, boolean autoDelete, Map<String, Object> arguments) {
		super(context, createFilter(connName), null);
		this.log = log;
		this.connName = connName;
		this.exchangeName = exchangeName;
		this.type = type;
		this.passive = passive;
		this.durable = durable;
		this.autoDelete = autoDelete;
		this.arguments = arguments;
	}

	private static Filter createFilter(String connName) {
		String filterStr = String.format("(%s=%s)", ServiceProperties.CONNECTION_NAME, connName);
		try {
			return FrameworkUtil.createFilter(filterStr);
		} catch (InvalidSyntaxException e) {
			// shouldn't happen
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public Object addingService(ServiceReference reference) {
		Connection conn = (Connection) context.getService(reference);
		
		Channel channel = null;
		try {
			channel = conn.createChannel();
			
			log.log(LogService.LOG_DEBUG, "ConnectionExchangeTracker: connection '" + connName + "' added, declaring exchange '" + exchangeName + "'");
			
			channel.exchangeDeclare(exchangeName, type, passive, durable, autoDelete, arguments);
			ExchangeImpl exchange = new ExchangeImpl(exchangeName, channel);

			Properties props = new Properties();
			props.put(ServiceProperties.EXCHANGE_NAME, exchangeName);
			props.put(ServiceProperties.EXCHANGE_CONNECTION, connName);
			props.put(ServiceProperties.EXCHANGE_TYPE, type);
			ServiceRegistration reg = context.registerService(Exchange.class.getName(), exchange, props);
			
			Pair<Channel, ServiceRegistration> pair = new Pair<Channel, ServiceRegistration>(channel, reg);
			return pair;
		} catch (IOException e) {
			log.log(LogService.LOG_ERROR, "Error opening channel", e);
			return null;
		} finally {
			try {
				channel.close();
			} catch (IOException e) {
				log.log(LogService.LOG_ERROR, "Error closing channel", e);
			}
		}
	}
	
	@Override
	public void removedService(ServiceReference reference, Object service) {
		@SuppressWarnings("unchecked")
		Pair<Channel, ServiceRegistration> pair = (Pair<Channel, ServiceRegistration>) service;
		
		pair.getSnd().unregister();
		try {
			pair.getFst().close();
		} catch (IOException e) {
			log.log(LogService.LOG_ERROR, "Error closing channel", e);
		}
		
		context.ungetService(reference);
	}

	public String getConnectionName() {
		return connName;
	}

	public String getExchangeName() {
		return exchangeName;
	}
}
