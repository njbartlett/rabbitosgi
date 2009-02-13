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
package com.rabbitmq.client.osgi.consumer;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ConsumerActivator implements BundleActivator {

	private ChannelConsumerTracker tracker;

	public void start(BundleContext context) throws Exception {
		tracker = new ChannelConsumerTracker(context, "FooQ");
		tracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		tracker.close();
	}

}
