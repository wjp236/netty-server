package com.tumbleweed.netty.domain;

 
public class Heartbeat extends DomainBase{

	private String packets;
	
	public Heartbeat(String packets){
			this.packets = packets;
	}

	public String getPackets() {
		return packets;
	}

	public void setPackets(String packets) {
		this.packets = packets;
	}
}
