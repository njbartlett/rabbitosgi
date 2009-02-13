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
package com.rabbitmq.client.osgi.common;

import java.io.PrintStream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class LogTracker extends ServiceTracker implements LogService {
	
	public LogTracker(BundleContext context) {
		super(context, LogService.class.getName(), null);
	}

	public void log(int level, String message) {
		log(null, level, message, null);
	}

	public void log(int level, String message, Throwable exception) {
		log(null, level, message, exception);
	}

	public void log(ServiceReference sr, int level, String message) {
		log(sr, level, message, null);
	}

	public void log(ServiceReference sr, int level, String message,
			Throwable exception) {
		LogService log = (LogService) getService();
		if(log != null) {
			log.log(sr, level, message, exception);
		} else {
			// Generate the log line
			StringBuffer buffer = new StringBuffer();
			buffer.append(getLvlString(level)).append(": ").append(message);
			if(sr != null) {
				buffer.append(" [").append(sr).append("]");
			}
			if(exception != null) {
				buffer.append(" -> ").append(exception.getMessage()).append(" (").append(exception.getClass().getName()).append(")");
			}
			
			// Print it
			PrintStream ps = (level == LogService.LOG_ERROR) ? System.err : System.out;
			ps.println(buffer.toString());
			if(exception != null) {
				exception.printStackTrace(ps);
			}
		}
	}
	private static String getLvlString(int level) {
		String result;
		switch(level) {
		case LogService.LOG_DEBUG:
			result = "DEBUG";
			break;
		case LogService.LOG_INFO:
			result = "INFO";
			break;
		case LogService.LOG_WARNING:
			result = "WARNING";
			break;
		case LogService.LOG_ERROR:
			result = "ERROR";
			break;
		default:
			result = "UNKNOWN";
		}
		return result;
	}

}
