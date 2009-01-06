package com.rabbitmq.client.osgi.channel;

import java.io.IOException;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionParameters;
import com.rabbitmq.client.osgi.Constants;

public class ChannelServiceFactory implements ManagedServiceFactory {
	
	private static final String CHANNEL_NAME = "name";
	private static final String CONNECTION_HOST = "host";
	private static final String CONNECTION_PORT = "port";
	private static final String CONNECTION_USERNAME = "username";
	private static final String CONNECTION_PASSWORD = "password";
	private static final String CONNECTION_VHOST = "virtualHost";
	private static final String CONNECTION_REQUESTED_HEARTBEAT = "requestedHeartbeat";
	
	private final Map<String, Tuple3<Connection, Channel, ServiceRegistration>> map = new HashMap<String, Tuple3<Connection, Channel, ServiceRegistration>>();
	private final BundleContext context;
	
	public ChannelServiceFactory(BundleContext context) {
		this.context = context;
	}

	public void deleted(String pid) {
		Tuple3<Connection, Channel, ServiceRegistration> tuple = null;
		synchronized (map) {
			tuple = map.remove(pid);
		}
		if(tuple != null) {
			tuple.getC().unregister();
			try {
				tuple.getB().close();
				tuple.getA().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String getName() {
		return "Channel Service Factory";
	}

	public void updated(String pid, @SuppressWarnings("unchecked") Dictionary props)
			throws ConfigurationException {
		String channelName = getString(CHANNEL_NAME, props);
		String host = getMandatoryString(CONNECTION_HOST, props);
		Integer portObj = getInteger(CONNECTION_PORT, props);
	
		ConnectionParameters params = new ConnectionParameters();
		params.setUsername(getMandatoryString(CONNECTION_USERNAME, props));
		params.setPassword(getMandatoryString(CONNECTION_PASSWORD, props));
		params.setVirtualHost(getMandatoryString(CONNECTION_VHOST, props));
		
		Integer reqHeartbeat = getInteger(CONNECTION_REQUESTED_HEARTBEAT, props);
		if(reqHeartbeat != null) {
			params.setRequestedHeartbeat(reqHeartbeat.intValue());
		}
		
		// Create the new connection & service
		Tuple3<Connection, Channel, ServiceRegistration> tuple = null;
		try {
			ConnectionFactory connFactory = new ConnectionFactory(params);
			Connection conn = connFactory.newConnection(host, portObj == null ? -1 : portObj.intValue());
			Channel channel = conn.createChannel();
			
			Properties svcProps = new Properties();
			svcProps.put(CONNECTION_HOST, host);
			if(channelName == null) {
				svcProps.put(Constants.CHANNEL_NAME, params.getUserName() + "@" + host);
			} else {
				svcProps.put(Constants.CHANNEL_NAME, channelName);
			}
			ServiceRegistration reg = context.registerService(Channel.class.getName(), channel, svcProps);
			
			
			tuple = new Tuple3<Connection, Channel, ServiceRegistration>(conn, channel, reg);
		} catch (IOException e) {
			throw new ConfigurationException(null, "Error connecting to broker", e);
		}
		
		// Replace in the map
		Tuple3<Connection, Channel, ServiceRegistration> old = null;
		synchronized (map) {
			old = map.put(pid, tuple);
		}
		
		// Unregister and destroy the old connection with this PID (if any)
		if(old != null) {
			old.getC().unregister();
			try {
				old.getB().close();
				old.getA().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getString(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		Object result = props.get(name);
		if(result != null && !(result instanceof String)) {
			throw new ConfigurationException(name, "Property value must be of type String");
		}
		return (String) result;
	}
	
	private static String getMandatoryString(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		String result = getString(name, props);
		if(result == null) {
			throw new ConfigurationException(name, "Missing mandatory property");
		}
		return result;
	}
	
	private static Integer getInteger(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		Integer result = null;
		
		Object obj = props.get(name);
		if(obj != null) {
			if(obj instanceof String) {
				try {
					result = new Integer((String) obj);
				} catch (NumberFormatException e) {
					throw new ConfigurationException(name, "Invalid integer format");
				}
			} else if(obj instanceof Integer) {
				result = (Integer) obj;
			} else {
				throw new ConfigurationException(name, "Property value must of type Integer, or String in integer format");
			}
		}
		
		return result;
	}
	
	private static Integer getMandatoryInteger(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		Integer result = getInteger(name, props);
		if(result == null) {
			throw new ConfigurationException(name, "Missing mandatory property");
		}
		return result;
	}
}
