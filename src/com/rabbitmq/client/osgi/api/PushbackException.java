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
package com.rabbitmq.client.osgi.api;

/**
 * Represents a transient failure to process an inbound message. When thrown by
 * a {@link MessageReceiver} it indicates that it may be possible to process the
 * message if it is redelivered later.
 * 
 * @author Neil Bartlett
 */
public class PushbackException extends Exception {

	private static final long serialVersionUID = 1L;

	public PushbackException(String message) {
		super(message);
	}

	public PushbackException(Throwable cause) {
		super(cause);
	}

	public PushbackException(String message, Throwable cause) {
		super(message, cause);
	}

}
