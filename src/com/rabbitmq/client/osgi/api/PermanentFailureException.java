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
