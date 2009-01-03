package com.rabbitmq.client.osgi.consumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ConsumerActivator implements BundleActivator {

	private ChannelConsumerTracker tracker;

	public void start(BundleContext context) throws Exception {
		tracker = new ChannelConsumerTracker(context, "fooq");
		tracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}

}
