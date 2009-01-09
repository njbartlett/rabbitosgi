package com.rabbitmq.client.osgi.connection;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;

public class ConnectionServiceFactoryActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		Properties props = new Properties();
		props.put(Constants.SERVICE_PID, "rabbitmq.connections");

		context.registerService(ManagedServiceFactory.class.getName(),
				new ConnectionServiceFactory(context), props);
	}

	public void stop(BundleContext context) throws Exception {
	}

}
