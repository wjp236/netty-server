package com.tumbleweed.netty.core.exception;

public class ConfigException extends RuntimeException {
	static final long serialVersionUID = -7034897190745766949L;

	public ConfigException() {
		super();
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigException(Throwable cause) {
		super(cause);
	}
}
