package com.rabbitmq.client.osgi.exchange;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import com.rabbitmq.client.osgi.common.CMPropertyAccessor;
import com.rabbitmq.client.osgi.common.ServiceProperties;

public class ConnectionExchangeMSF implements ManagedServiceFactory {
	
	private static final String EXCHANGE_TYPE_DIRECT = "direct";
	private static final Logger LOG = Logger.getLogger(ConnectionExchangeMSF.class);
	
	private final BundleContext context;
	
	private Map<String, ConnectionExchangeTracker> map = new HashMap<String, ConnectionExchangeTracker>();
	
	public ConnectionExchangeMSF(BundleContext context) {
		this.context = context;
	}

	public void deleted(String pid) {
		ConnectionExchangeTracker tracker = null;
		synchronized (map) {
			tracker = map.remove(pid);
		}
		if(tracker != null) tracker.close();
		LOG.debug("STOPPED tracking connection name '" + tracker.getConnectionName() + "' to declare exchange '" + tracker.getExchangeName() + "'.");
	}

	public String getName() {
		return "Exchange Service Factory";
	}

	public void updated(String pid, @SuppressWarnings("unchecked") Dictionary dict)
			throws ConfigurationException {
		// Load properties
		CMPropertyAccessor accessor = new CMPropertyAccessor(dict);
		String connName = accessor.getMandatoryString(ServiceProperties.CONNECTION_NAME);
		String exchangeName = accessor.getMandatoryString(ServiceProperties.EXCHANGE_NAME);
		
		String exchangeType = accessor.getString(ServiceProperties.EXCHANGE_TYPE);
		if(exchangeType == null) {
			exchangeType = EXCHANGE_TYPE_DIRECT;
		}
		
		boolean passive = accessor.getBoolean(ServiceProperties.EXCHANGE_PASSIVE, false);
		boolean durable = accessor.getBoolean(ServiceProperties.EXCHANGE_DURABLE, false);
		boolean autoDelete = accessor.getBoolean(ServiceProperties.EXCHANGE_AUTODELETE, false);
		
		// TODO
		Map<String, Object> args = null;
		
		
		// Create Tracker
		LOG.debug("STARTING to track connection name '" + connName + "' to declare exchange '" + exchangeName + "'.");
		ConnectionExchangeTracker tracker = new ConnectionExchangeTracker(context, connName, exchangeName, exchangeType, passive, durable, autoDelete, args);
		tracker.open();
		
		// Add to map
		ConnectionExchangeTracker old = null;
		synchronized (map) {
			old = map.put(pid, tracker);
		}
		if(old != null) old.close();
	}
	
	
}
