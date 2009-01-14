package com.rabbitmq.client.osgi.api;

import java.io.IOException;

import com.rabbitmq.client.AMQP.BasicProperties;

public interface Exchange {
    /**
     * Publish a message with both "mandatory" and "immediate" flags set to false
     * @see com.rabbitmq.client.AMQP.Basic.Publish
     * @param routingKey the routing key
     * @param props other properties for the message - routing headers etc
     * @param body the message body
     * @throws java.io.IOException if an error is encountered
     */
    void basicPublish(String routingKey, BasicProperties props, byte[] body) throws IOException;

    /**
     * Publish a message
     * @see com.rabbitmq.client.AMQP.Basic.Publish
     * @param routingKey the routing key
     * @param mandatory true if we are requesting a mandatory publish
     * @param immediate true if we are requesting an immediate publish
     * @param props other properties for the message - routing headers etc
     * @param body the message body
     * @throws java.io.IOException if an error is encountered
     */
    void basicPublish(String routingKey, boolean mandatory, boolean immediate, BasicProperties props, byte[] body)
            throws IOException;
}
