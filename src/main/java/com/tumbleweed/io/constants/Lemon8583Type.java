package com.tumbleweed.io.constants;

public enum Lemon8583Type {
	/*
	 * 银联连接 TYPE_UC
	 * 短链接 TYPE_SC
	 * 长连接 TYPE_LC
	 * 原路返回 	TYPE_BC
	 */
	TYPE_UC("0"),
	TYPE_SC("1"),
	TYPE_LC("2"),
	TYPE_BC("8");

	private String type;
	
	Lemon8583Type(String type){
		this.type = type;
	}

	public String getType() {
		return type;
	}
}
