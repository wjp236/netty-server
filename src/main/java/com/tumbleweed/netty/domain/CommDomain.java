package com.tumbleweed.netty.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommDomain extends DomainBase {

	private static final Logger logger = LoggerFactory.getLogger(CommDomain.class);

	/**
	 * 错误码
	 */
	private String errorCode;
	
	/**
	 * 数据
	 */
	private byte[] packets;
	
	private Object object;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public byte[] getPackets() {
		return packets;
	}

	public void setPackets(byte[] packets) {
		this.packets = packets;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
