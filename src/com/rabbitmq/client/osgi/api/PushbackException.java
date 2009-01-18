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
