package com.rabbitmq.client.osgi.api;

public interface MessageReceiver {
	public void receive(Object message) throws PushbackException, PermanentFailureException;
}
