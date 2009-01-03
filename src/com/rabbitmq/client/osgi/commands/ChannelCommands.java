package com.rabbitmq.client.osgi.commands;

import java.io.IOException;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.osgi.Constants;

public class ChannelCommands implements CommandProvider {
	
	public final String HELP_LIST_CHANNELS = "listChannels - List connected channels.";
	public final String HELP_DECL_EXCHANGE = "declExchange <channel> <exchange> <type> - Declare an exchange on the specified channel.";
	public final String HELP_DECL_QUEUE = "declQ <channel> <queue> - Declare a queue.";
	public final String HELP_BIND_QUEUE = "bindQ <channel> <queue> <exchange> <routingKey> - Bind a queue to an exchange.";
	public final String HELP_PUBLISH = "publish <channel> <exchange> <routingKey> <message> - Publish a message to an exchange.";
	public final String HELP_RECEIVE = "receive <channel> <queue> - Retrieve a single message from the queue and print it.";
	
	private final BundleContext context;

	public ChannelCommands(BundleContext context) {
		this.context = context;
	}

	public String getHelp() {
		StringBuilder buf = new StringBuilder();
		
		buf.append("---RabbitMQ Client Commands---\n");
		buf.append('\t').append(HELP_LIST_CHANNELS).append('\n');
		buf.append('\t').append(HELP_DECL_EXCHANGE).append('\n');
		buf.append('\t').append(HELP_DECL_QUEUE).append('\n');
		buf.append('\t').append(HELP_BIND_QUEUE).append('\n');
		buf.append('\t').append(HELP_PUBLISH).append('\n');
		buf.append('\t').append(HELP_RECEIVE).append('\n');
		
		return buf.toString();
	}
	
	public void _listChannels(CommandInterpreter ci) throws InvalidSyntaxException {
		ServiceReference[] refs = context.getServiceReferences(Channel.class.getName(), null);
		if(refs != null) {
			for (ServiceReference ref : refs) {
				String name = (String) ref.getProperty(Constants.CHANNEL_NAME);
				String host = (String) ref.getProperty(Constants.CHANNEL_HOST);
				
				ci.println("{" + name + "}={" + Constants.CHANNEL_HOST + "=" + host + "}");
				ci.println("\t" + "Registered by bundle: " + ref.getBundle().getSymbolicName() + " " + ref.getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION));
			}
		} else {
			ci.println("No connected channels");
		}
	}
	
	public void _declExchange(final CommandInterpreter ci) {
		String channelName = ci.nextArgument();
		final String exchange = ci.nextArgument();
		final String type = ci.nextArgument();
		
		if(channelName == null || exchange == null || type == null) {
			ci.println("Usage: " + HELP_DECL_EXCHANGE);
			return;
		}
		
		withChannel(channelName, ci, new ChannelOp() {
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

		withChannel(channelName, ci, new ChannelOp() {
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
		
		withChannel(channelName, ci, new ChannelOp() {
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
		
		withChannel(channelName, ci, new ChannelOp() {
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
		
		withChannel(channelName, ci, new ChannelOp() {
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
	
	private ServiceReference getChannelByName(String name) {
		ServiceReference result = null;
		try {
			ServiceReference[] refs = context.getServiceReferences(Channel.class.getName(), String.format("(%s=%s)", Constants.CHANNEL_NAME, name));
			if(refs != null && refs.length > 0) {
				result = refs[0];
			}
		} catch (InvalidSyntaxException e) {
			// Shouldn't happen
		}
		return result;
	}
	
	private void withChannel(String name, CommandInterpreter ci, ChannelOp op) {
		ServiceReference ref = getChannelByName(name);
		if(ref == null) {
			ci.println("Specified channel does not exist");
			return;
		}
		
		Channel channel = (Channel) context.getService(ref);
		if(channel == null) {
			ci.println("Specified channel does not exist");
			return;
		}
		
		try {
			op.execute(channel);
		} finally {
			context.ungetService(ref);
		}
	}
	
	private interface ChannelOp {
		public void execute(Channel channel);
	}

}
