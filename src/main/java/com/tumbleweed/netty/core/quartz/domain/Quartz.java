package com.tumbleweed.netty.core.quartz.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * spring的quartz调度替换类需要使用的pojo
 */
public class Quartz implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6926173609511504336L;
	
	private String qid;
	private int islock;
	private String instance;
	private Timestamp updatetime;
	private String operid;
	private String bizid;
	
	public String getQid() {
		return qid;
	}
	public void setQid(String qid) {
		this.qid = qid;
	}
	public int getIslock() {
		return islock;
	}
	public void setIslock(int islock) {
		this.islock = islock;
	}
	public String getInstance() {
		return instance;
	}
	public void setInstance(String instance) {
		this.instance = instance;
	}
	public Timestamp getUpdatetime() {
		return updatetime;
	}
	public void setUpdatetime(Timestamp updatetime) {
		this.updatetime = updatetime;
	}
	public String getOperid() {
		return operid;
	}
	public void setOperid(String operid) {
		this.operid = operid;
	}
	public String getBizid() {
		return bizid;
	}
	public void setBizid(String bizid) {
		this.bizid = bizid;
	}
	
}
