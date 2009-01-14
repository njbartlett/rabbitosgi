package com.rabbitmq.client.osgi.exchange;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.log.LogService;

import com.rabbitmq.client.osgi.common.CMUtils;
import com.rabbitmq.client.osgi.common.ServiceProperties;

public class ExchangeServiceFactory implements ManagedServiceFactory {
	
	private static final String EXCHANGE_TYPE_DIRECT = "direct";
	private final BundleContext context;
	private final LogService log;
	
	private Map<String, ConnectionExchangeTracker> map = new HashMap<String, ConnectionExchangeTracker>();
	
	public ExchangeServiceFactory(BundleContext context, LogService log) {
		this.context = context;
		this.log = log;
	}

	public void deleted(String pid) {
		ConnectionExchangeTracker tracker = null;
		synchronized (map) {
			tracker = map.remove(pid);
		}
		if(tracker != null) tracker.close();
		log.log(LogService.LOG_INFO, "STOPPED tracking connection name '" + tracker.getConnectionName() + "' to declare exchange '" + tracker.getExchangeName() + "'.");
	}

	public String getName() {
		return "Exchange Service Factory";
	}

	public void updated(String pid, @SuppressWarnings("unchecked") Dictionary dict)
			throws ConfigurationException {
		// Load properties
		String connName = CMUtils.getMandatoryString(ServiceProperties.CONNECTION_NAME, dict);
		String exchangeName = CMUtils.getMandatoryString(ServiceProperties.EXCHANGE_NAME, dict);
		
		String exchangeType = CMUtils.getString(ServiceProperties.EXCHANGE_TYPE, dict);
		if(exchangeType == null) {
			exchangeType = EXCHANGE_TYPE_DIRECT;
		}
		
		Boolean passiveObj = CMUtils.getBoolean(ServiceProperties.EXCHANGE_PASSIVE, dict);
		boolean passive = passiveObj != null ? passiveObj.booleanValue() : false;
		
		Boolean durableObj = CMUtils.getBoolean(ServiceProperties.EXCHANGE_DURABLE, dict);
		boolean durable = durableObj != null ? durableObj.booleanValue() : false;
		
		Boolean autoDeleteObj = CMUtils.getBoolean(ServiceProperties.EXCHANGE_AUTODELETE, dict);
		boolean autoDelete = autoDeleteObj != null ? autoDeleteObj.booleanValue() : false;
		
		// TODO
		Map<String, Object> args = null;
		
		
		// Create Tracker
		log.log(LogService.LOG_INFO, "STARTING to track connection name '" + connName + "' to declare exchange '" + exchangeName + "'.");
		ConnectionExchangeTracker tracker = new ConnectionExchangeTracker(context, log, connName, exchangeName, exchangeType, passive, durable, autoDelete, args);
		tracker.open();
		
		// Add to map
		ConnectionExchangeTracker old = null;
		synchronized (map) {
			old = map.put(pid, tracker);
		}
		if(old != null) old.close();
	}
	
	
}
