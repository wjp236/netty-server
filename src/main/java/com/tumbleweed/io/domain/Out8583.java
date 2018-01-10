package com.tumbleweed.io.domain;

import io.netty.channel.ChannelHandlerContext;


/**
 * 
 * @Title: Out8583.java
 * @Package: com.lemon.io.domain
 * @Description: gatewayout domain
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class Out8583 extends DomainBase {
	
	/**
	 * 交易类型
	 */
//	private String type;

	/**
	 * 发至银行的报文
	 */
	private byte[] packets;
	
	/**
	 * ctx上下文
	 */
	private ChannelHandlerContext ctx;
	
	/**
	 * 是否启用备用通道
	 */
	private boolean hsmSpareFlag = false;
	
	public Out8583(){
	}
	
	public Out8583(byte[] packets){
		this.packets = packets;
	}
	
	public Out8583(String uuid, byte[] packets){
		this.uuid = uuid;
		this.packets = packets;
	}

	public byte[] getPackets() {
		return packets;
	}

	public void setPackets(byte[] packets) {
		this.packets = packets;
	}

	public ChannelHandlerContext getCtx() {
		return ctx;
	}

	public void setCtx(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	public boolean isHsmSpareFlag() {
		return hsmSpareFlag;
	}

	public void setHsmSpareFlag(boolean hsmSpareFlag) {
		this.hsmSpareFlag = hsmSpareFlag;
	}
}
