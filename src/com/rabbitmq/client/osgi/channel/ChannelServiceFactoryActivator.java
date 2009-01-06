package com.rabbitmq.client.osgi.channel;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;

public class ChannelServiceFactoryActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		ChannelServiceFactory factory = new ChannelServiceFactory(context);
		
		Properties props = new Properties();
		props.put(Constants.SERVICE_PID, "rabbitmq.channels");
		context.registerService(ManagedServiceFactory.class.getName(), factory, props);
	}

	public void stop(BundleContext context) throws Exception {
	}

}
