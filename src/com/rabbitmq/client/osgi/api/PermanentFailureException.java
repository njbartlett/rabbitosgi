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
 * Represents a permanent failure to process an inbound message. When thrown by
 * a {@link MessageReceiver} it indicates that the message cannot be processed
 * and can never be processed in its current form, so there is no use in
 * redelivering it.
 * 
 * @author Neil Bartlett
 */
public class PermanentFailureException extends Exception {

	private static final long serialVersionUID = 1L;

	public PermanentFailureException(String message) {
		super(message);
	}

	public PermanentFailureException(Throwable cause) {
		super(cause);
	}

	public PermanentFailureException(String message, Throwable cause) {
		super(message, cause);
	}

}
