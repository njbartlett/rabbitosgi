package com.rabbitmq.client.osgi.common;

import java.util.Dictionary;

import org.osgi.service.cm.ConfigurationException;

public class CMUtils {
	
	private CMUtils() {
		// Prevent instantiation
	}
	
	public static String getString(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		Object result = props.get(name);
		if(result != null && !(result instanceof String)) {
			throw new ConfigurationException(name, "Property value must be of type String");
		}
		return (String) result;
	}
	
	public static String getMandatoryString(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		String result = getString(name, props);
		if(result == null) {
			throw new ConfigurationException(name, "Missing mandatory property");
		}
		return result;
	}
	
	public static Integer getInteger(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
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
	
	public static Integer getMandatoryInteger(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
		Integer result = getInteger(name, props);
		if(result == null) {
			throw new ConfigurationException(name, "Missing mandatory property");
		}
		return result;
	}
	
	public static Boolean getBoolean(String name, @SuppressWarnings("unchecked") Dictionary props) throws ConfigurationException {
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
}
