package com.tumbleweed.netty.core.constants;

public enum LogType {

	PTS("PTS");//加密机服务

	private String type;
	
	LogType(String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
