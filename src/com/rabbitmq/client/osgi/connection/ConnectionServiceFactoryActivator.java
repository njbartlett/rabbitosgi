/*******************************************************************************
 * Copyright (c) 2009 Neil Bartlett.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Neil Bartlett - initial API and implementation
 ******************************************************************************/
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
