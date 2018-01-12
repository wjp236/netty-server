package com.tumbleweed.netty.client.domain;

import com.tumbleweed.netty.core.util.StringUtils;
import com.tumbleweed.netty.core.domain.DomainBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HsmDomain extends DomainBase {

	private static final Logger logger = LoggerFactory.getLogger(HsmDomain.class);
	
	/**
	 * 报文体
	 */
	private String body;
	
	/**
	 * 命令码
	 */
	private String code;
	
	/**
	 * 错误码
	 */
	private String errorCode;
	
	/**
	 * 数据
	 */
	private String data;
	
	/**
	 * 是否启用备用通道
	 */
	private boolean hsmSpareFlag = false;
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
		
	public String toEncoderString(){
		String encoderStr = "";
		if(!StringUtils.isNullOrEmpty(this.code)){
			encoderStr += this.code;
		}else{
			logger.error(" code is null ");
			return null;
		}
		if(!StringUtils.isNullOrEmpty(this.data)){
			encoderStr += this.data;
		}else{
			logger.error(" data is null ");
			return null;
		}
		return encoderStr;
	}

	public boolean isHsmSpareFlag() {
		return hsmSpareFlag;
	}

	public void setHsmSpareFlag(boolean hsmSpareFlag) {
		this.hsmSpareFlag = hsmSpareFlag;
	}
}
