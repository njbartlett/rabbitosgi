package com.rabbitmq.client.osgi.channel;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ConnectionParameters;
import com.rabbitmq.client.osgi.Constants;

public class ChannelActivator implements BundleActivator {

	private Connection conn;
	private Channel chan;
	private ServiceRegistration reg;

	public void start(BundleContext context) throws Exception {
		ConnectionParameters params = new ConnectionParameters();
		params.setUsername("guest");
		params.setPassword("guest");
		params.setVirtualHost("/");
		params.setRequestedHeartbeat(0);
		ConnectionFactory factory = new ConnectionFactory(params);
		
		conn = factory.newConnection("localhost");
		chan = conn.createChannel();
		
		Properties svcProps = new Properties();
		svcProps.put(Constants.CHANNEL_HOST, "localhost");
		svcProps.put(Constants.CHANNEL_NAME, "localhost");
		
		reg = context.registerService(Channel.class.getName(), chan, svcProps);
	}

	public void stop(BundleContext context) throws Exception {
		reg.unregister();
		chan.close();
		conn.close();
	}

}
