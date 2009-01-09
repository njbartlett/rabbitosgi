package com.rabbitmq.client.osgi.commands;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class CommandsActivator implements BundleActivator {

	public void start(BundleContext context) throws Exception {
		context.registerService(CommandProvider.class.getName(), new RabbitCommands(context), null);
	}

	public void stop(BundleContext context) throws Exception {
	}

}
