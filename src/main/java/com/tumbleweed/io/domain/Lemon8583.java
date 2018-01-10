package com.tumbleweed.io.domain;

import com.google.gson.Gson;
import com.tumbleweed.io.constants.Lemon8583Type;
import com.mysql.jdbc.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @Title: Lemon8583.java
 * @Package: com.lemon.io.domain
 * @Description: jt 传输对象, pts <---> out
 * @author: jiangke
 * @date: 2014年1月2日
 * @version V1.0
 */
public class Lemon8583 extends DomainBase {
	static final Logger logger = LoggerFactory.getLogger(Lemon8583.class);

	/**
	 * 合作机构
	 */
	private String bankName;

	/**
	 * 发至银行的报文,hex编码
	 */
	private String packetsHex;

	/**
	 * 返回错误码
	 */
	private String errorCode;

	/**
	 * 上游机构类型
	 * Lemon8583Type枚举：
	 * 银联连接 0
	 * 短链接 1
	 * 长连接 2
	 * 原路返回 8
	 */
	private String type;

	/**
	 * 是否返回
	 */
	private boolean isBack = false;
	
	/**
	 * 非阻塞标记
	 */
	private boolean isNio;

	/**
	 * 上游机构是否主动推送消息
	 */
	private boolean isInitiative = false;

	/**
	 * 心跳报文
	 */
	private String heartbeatPacket;
	
	/**
	 * 报文格式是否xml
	 */
	private boolean isXml = false;

	public Lemon8583(Lemon8583Type lemon8583Type) {
		this.type = lemon8583Type.getType();
	}

	public Lemon8583(String heartbeatPacket) {
		this.heartbeatPacket = heartbeatPacket;
	}

	public Lemon8583(String uuid, String errorCode) {
		super.uuid = uuid;
		this.errorCode = errorCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPacketsHex() {
		return packetsHex;
	}

	public void setPacketsHex(String packetsHex) {
		this.packetsHex = packetsHex;
	}

	public boolean isBack() {
		return isBack;
	}

	public void setBack(boolean isBack) {
		this.isBack = isBack;
	}
	
	public boolean isNio() {
		return isNio;
	}

	public void setNio(boolean isNio) {
		this.isNio = isNio;
	}

	public boolean isInitiative() {
		return isInitiative;
	}

	public void setInitiative(boolean isInitiative) {
		this.isInitiative = isInitiative;
	}

	public String getHeartbeatPacket() {
		return heartbeatPacket;
	}

	public void setHeartbeatPacket(String heartbeatPacket) {
		this.heartbeatPacket = heartbeatPacket;
	}

	
	public boolean isXml() {
		return isXml;
	}

	public void setXml(boolean isXml) {
		this.isXml = isXml;
	}

	public boolean validate() {
		if (!StringUtils.isNullOrEmpty(heartbeatPacket)) {
			return true;
		}
		if (!StringUtils.isNullOrEmpty(errorCode)) {
			return true;
		}
		if (isBack) {
			return true;
		}
		if (StringUtils.isNullOrEmpty(uuid)) {
			return false;
		}
		if (StringUtils.isNullOrEmpty(bankName)) {
			return false;
		}
		if (StringUtils.isNullOrEmpty(packetsHex)) {
			return false;
		}
		if (StringUtils.isNullOrEmpty(type)) {
			return false;
		}
		return true;
	}

	public String toJson() {
		return new Gson().toJson(this);
	}
}
