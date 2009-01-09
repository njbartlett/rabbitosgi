package com.rabbitmq.client.osgi.commands;

import java.io.IOException;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.osgi.common.ServiceProperties;

public class RabbitCommands implements CommandProvider {
	
	public final String HELP_LIST_CONNS = "listConns - List connections.";
	public final String HELP_DECL_EXCHANGE = "declExchange <conn> <exchange> <type> - Declare an exchange on the specified connection.";
	public final String HELP_DECL_QUEUE = "declQ <conn> <queue> - Declare a queue.";
	public final String HELP_BIND_QUEUE = "bindQ <conn> <queue> <exchange> <routingKey> - Bind a queue to an exchange.";
	public final String HELP_PUBLISH = "publish <conn> <exchange> <routingKey> <message> - Publish a message to an exchange.";
	public final String HELP_RECEIVE = "receive <conn> <queue> - Retrieve a single message from the queue and print it.";
	
	private final BundleContext context;

	public RabbitCommands(BundleContext context) {
		this.context = context;
	}

	public String getHelp() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("---RabbitMQ Client Commands---\n");
		buf.append('\t').append(HELP_LIST_CONNS).append('\n');
		buf.append('\t').append(HELP_DECL_EXCHANGE).append('\n');
		buf.append('\t').append(HELP_DECL_QUEUE).append('\n');
		buf.append('\t').append(HELP_BIND_QUEUE).append('\n');
		buf.append('\t').append(HELP_PUBLISH).append('\n');
		buf.append('\t').append(HELP_RECEIVE).append('\n');
		
		return buf.toString();
	}
	
	public void _listConns(CommandInterpreter ci) throws InvalidSyntaxException {
		ServiceReference[] refs = context.getServiceReferences(Connection.class.getName(), null);
		if(refs != null) {
			for (ServiceReference ref : refs) {
				String name = (String) ref.getProperty(ServiceProperties.CONNECTION_NAME);
				String host = (String) ref.getProperty(ServiceProperties.CONNECTION_HOST);
				
				ci.println("{" + name + "}={" + ServiceProperties.CONNECTION_HOST + "=" + host + "}");
				ci.println("\t" + "Registered by bundle: " + ref.getBundle().getSymbolicName() + " " + ref.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION));
			}
		} else {
			ci.println("No open connections.");
		}
	}
	
	public void _declExchange(final CommandInterpreter ci) {
		String conn = ci.nextArgument();
		final String exchange = ci.nextArgument();
		final String type = ci.nextArgument();
		
		if(conn == null || exchange == null || type == null) {
			ci.println("Usage: " + HELP_DECL_EXCHANGE);
			return;
		}
		
		withConnection(conn, ci, new ChannelOp() {
			public void execute(Channel channel) {
				try {
					channel.exchangeDeclare(exchange, type);
					ci.println("Exchange Declared");
				} catch (IOException e) {
					ci.println("Error declaring exchange");
					ci.printStackTrace(e);
				}
			}
		});
	}
	
	public void _declQ(final CommandInterpreter ci) {
		String channelName = ci.nextArgument();
		final String queueName = ci.nextArgument();
		
		if(channelName == null || queueName == null) {
			ci.println("Usage: " + HELP_DECL_QUEUE);
			return;
		}

		withConnection(channelName, ci, new ChannelOp() {
			public void execute(Channel channel) {
				try {
					channel.queueDeclare(queueName);
					ci.println("Queue Declared");
				} catch (IOException e) {
					ci.println("Error declaring queue");
					ci.printStackTrace(e);
				}
			}
		});
	}
	
	public void _bindQ(final CommandInterpreter ci) {
		String channelName = ci.nextArgument();
		final String queueName = ci.nextArgument();
		final String exchangeName = ci.nextArgument();
		final String routingKey = ci.nextArgument();
		
		if(channelName == null || queueName == null || exchangeName == null || routingKey == null) {
			ci.println("Usage: " + HELP_BIND_QUEUE);
		}
		
		withConnection(channelName, ci, new ChannelOp() {
			public void execute(Channel channel) {
				try {
					channel.queueBind(queueName, exchangeName, routingKey);
					ci.println("Queue Bound");
				} catch (IOException e) {
					ci.println("Error binding queue");
					ci.printStackTrace(e);
				}
			}
		});
	}
	
	public void _publish(final CommandInterpreter ci) {
		String channelName = ci.nextArgument();
		final String exchange = ci.nextArgument();
		final String routingKey = ci.nextArgument();
		final String message = ci.nextArgument();
		
		if(channelName == null || exchange == null || routingKey == null || message == null) {
			ci.println("Usage: " + HELP_PUBLISH);
			return;
		}
		
		withConnection(channelName, ci, new ChannelOp() {
			public void execute(Channel channel) {
				try {
					channel.basicPublish(exchange, routingKey, null, message.getBytes());
					ci.println("Message Published");
				} catch (IOException e) {
					ci.println("Error publishing message");
					ci.printStackTrace(e);
				}
			}
		});
	}
	
	public void _receive(final CommandInterpreter ci) {
		String channelName = ci.nextArgument();
		final String queue = ci.nextArgument();
		
		if(channelName == null || queue == null) {
			ci.println("Usage: " + HELP_RECEIVE);
			return;
		}
		
		withConnection(channelName, ci, new ChannelOp() {
			public void execute(Channel channel) {
				try {
					GetResponse response = channel.basicGet(queue, true);
					if(response == null) {
						ci.println("---No message received---");
					} else {
						ci.println("---Message Received---");
						byte[] body = response.getBody();
						
						String message = new String(body);
						ci.println(message);
					}
				} catch (IOException e) {
					ci.println("Error Receiving Message");
					ci.printStackTrace(e);
				}
			}
		});
	}
	
	private ServiceReference getConnectionByName(String name) {
		ServiceReference result = null;
		try {
			ServiceReference[] refs = context.getServiceReferences(Connection.class.getName(), String.format("(%s=%s)", ServiceProperties.CONNECTION_NAME	, name));
			if(refs != null && refs.length > 0) {
				result = refs[0];
			}
		} catch (InvalidSyntaxException e) {
			// Shouldn't happen
			e.printStackTrace();
		}
		return result;
	}
	
	private void withConnection(String name, CommandInterpreter ci, ChannelOp op) {
		ServiceReference ref = getConnectionByName(name);
		if(ref == null) {
			ci.println("Specified connection does not exist");
			return;
		}
		
		Connection conn = (Connection) context.getService(ref);
		if(conn == null) {
			ci.println("Specified connection does not exist");
			return;
		}
		
		Channel channel = null;
		try {
			channel = conn.createChannel();
			op.execute(channel);
		} catch (IOException e) {
			ci.println("Error creating channel");
			ci.printStackTrace(e);
		} finally {
			context.ungetService(ref);
			if(channel != null) {
				try {
					channel.close();
				} catch (IOException e) {
					ci.println("Error closing channel");
					ci.printStackTrace(e);
				}
			}
		}
	}
	
	private interface ChannelOp {
		public void execute(Channel channel);
	}

}
