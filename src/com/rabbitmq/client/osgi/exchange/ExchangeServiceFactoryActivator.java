package com.rabbitmq.client.osgi.exchange;

import java.util.Properties;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedServiceFactory;

import com.rabbitmq.client.osgi.common.LogTracker;

public class ExchangeServiceFactoryActivator implements BundleActivator {

	private LogTracker logTracker;
	private ServiceRegistration msfReg;

	public void start(BundleContext context) throws Exception {
		logTracker = new LogTracker(context);
		logTracker.open();
		
		ExchangeServiceFactory factory = new ExchangeServiceFactory(context, logTracker);
		Properties props = new Properties();
		props.put(Constants.SERVICE_PID, "rabbitmq.exchanges");
		msfReg = context.registerService(ManagedServiceFactory.class.getName(), factory, props);
	}

	public void stop(BundleContext context) throws Exception {
		msfReg.unregister();
		logTracker.close();
	}

}
