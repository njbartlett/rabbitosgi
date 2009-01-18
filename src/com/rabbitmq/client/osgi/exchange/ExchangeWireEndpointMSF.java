package com.rabbitmq.client.osgi.exchange;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.osgi.common.CMPropertyAccessor;
import com.rabbitmq.client.osgi.common.ServiceProperties;

public class ExchangeWireEndpointMSF implements ManagedServiceFactory {
	
	private static Logger LOG = Logger.getLogger(ExchangeWireEndpointMSF.class);
	
	private final BundleContext context;
	
	private Map<String, ExchangeWireEndpointTracker> map = new HashMap<String, ExchangeWireEndpointTracker>();
	
	public ExchangeWireEndpointMSF(BundleContext context) {
		this.context = context;
	}

	public void deleted(String pid) {
		ExchangeWireEndpointTracker tracker = null;
		synchronized (map) {
			tracker = map.remove(pid);
		}
		if(tracker != null) tracker.close();
		LOG.info("STOPPED tracking exchange name '" + tracker.getExchangeName() + "' for endpoint declaration.");
	}

	public String getName() {
		return "Exchange Service Factory";
	}

	public void updated(String pid, @SuppressWarnings("unchecked") Dictionary dict)
			throws ConfigurationException {
		// Load properties
		CMPropertyAccessor accessor = new CMPropertyAccessor(dict);
		
		String connection = accessor.getString(ServiceProperties.CONNECTION_NAME);
		String exchange = accessor.getMandatoryString(ServiceProperties.EXCHANGE_NAME);
		String routingKey = accessor.getMandatoryString(ServiceProperties.PUBLISH_ROUTING_KEY);
		String endpointPid = accessor.getMandatoryString(ServiceProperties.ENDPOINT_SERVICE_PID);
		boolean mandatory = accessor.getBoolean(ServiceProperties.PUBLISH_MANDATORY, false);
		boolean immediate = accessor.getBoolean(ServiceProperties.PUBLISH_IMMEDIATE, false);
		
		// TODO
		AMQP.BasicProperties props = null;
		
		// Create Tracker
		LOG.debug("STARTING to track exchange name '" + exchange + "' for endpoint declaration.");
		ExchangeWireEndpointTracker tracker = new ExchangeWireEndpointTracker(context, exchange, connection, routingKey, endpointPid, mandatory, immediate, props);
		tracker.open();
		
		// Add to map
		ExchangeWireEndpointTracker old = null;
		synchronized (map) {
			old = map.put(pid, tracker);
		}
		if(old != null) old.close();
	}
	
	
}
