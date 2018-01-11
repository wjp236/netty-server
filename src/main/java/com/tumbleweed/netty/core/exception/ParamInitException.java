package com.tumbleweed.netty.core.exception;

public class ParamInitException extends RuntimeException {
	static final long serialVersionUID = -7034897190745766959L;

	public ParamInitException() {
		super();
	}

	public ParamInitException(String message) {
		super(message);
	}

	public ParamInitException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParamInitException(Throwable cause) {
		super(cause);
	}
}
