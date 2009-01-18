package com.rabbitmq.client.osgi.common;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;

public class CMPropertyAccessor {
	
	@SuppressWarnings("unchecked")
	private final Dictionary props;

	public CMPropertyAccessor(@SuppressWarnings("unchecked") Dictionary props) {
		this.props = props;
	}
	
	public String getString(String name) throws ConfigurationException {
		Object result = props.get(name);
		if(result != null && !(result instanceof String)) {
			throw new ConfigurationException(name, "Property value must be of type String");
		}
		return (String) result;
	}
	
	public String getMandatoryString(String name) throws ConfigurationException {
		String result = getString(name);
		if(result == null) {
			throw new ConfigurationException(name, "Missing mandatory property");
		}
		return result;
	}
	
	public Integer getInteger(String name) throws ConfigurationException {
		Integer result = null;
		
		Object obj = props.get(name);
		if(obj != null) {
			if(obj instanceof String) {
				try {
					result = new Integer((String) obj);
				} catch (NumberFormatException e) {
					throw new ConfigurationException(name, "Invalid integer format");
				}
			} else if(obj instanceof Integer) {
				result = (Integer) obj;
			} else {
				throw new ConfigurationException(name, "Property value must of type Integer, or String in integer format");
			}
		}
		
		return result;
	}
	
	public Integer getMandatoryInteger(String name) throws ConfigurationException {
		Integer result = getInteger(name);
		if(result == null) {
			throw new ConfigurationException(name, "Missing mandatory property");
		}
		return result;
	}
	
	public Boolean getBoolean(String name) throws ConfigurationException {
		Boolean result = null;
		
		Object obj = props.get(name);
		if(obj != null) {
			if(obj instanceof String) {
				result = Boolean.parseBoolean((String) obj);
			} else if(obj instanceof Boolean) {
				result = (Boolean) obj;
			} else {
				throw new ConfigurationException(name, "Property value must of type Boolean, or String in boolean format");
			}
		}
		
		return result;
	}

	public boolean getBoolean(String name, boolean defaultValue) throws ConfigurationException {
		Boolean b = getBoolean(name);
		return (b != null) ? b.booleanValue() : defaultValue;
	}
}
