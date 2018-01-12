package com.tumbleweed.netty.client.bean;

import com.tumbleweed.netty.client.context.AppBeanContext;
import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.client.context.ClientContext;
import com.tumbleweed.netty.core.bean.BootstrapContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanNameAware;

import java.util.List;

public abstract class AppBeanBase implements BeanNameAware {

	private static final Logger logger = LoggerFactory.getLogger(AppBeanBase.class);

	protected String ip;

	protected int port;

	protected String suffix;

	protected ClientContext lcc = null;

	protected DomainCollectionContext dhmc = null;
	
	protected List<String> handlers;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ClientContext getLcc() {
		return lcc;
	}

	public void setLcc(ClientContext lcc) {
		this.lcc = lcc;
	}

	public DomainCollectionContext getDhmc() {
		return dhmc;
	}

	public void setDhmc(DomainCollectionContext dhmc) {
		this.dhmc = dhmc;
	}
	
	public List<String> getHandlers() {
		return handlers;
	}

	public void setHandlers(List<String> handlers) {
		this.handlers = handlers;
	}

	@Override
	public void setBeanName(String name) {
		this.suffix = name;
		try {
			((AppBeanContext) BootstrapContextHelper
                    .getBean("appBeanContext")).putAppBean(name, this);
		} catch (Exception e) {
			logger.warn("AppBeanBase 发生错误:"
                    + e.getMessage() + " 系统将无法使用appBeanMap，如不使用appBeanMap，此警告可忽略！");
		}
	}
	
	public abstract void startService();
	
	public abstract void stopService();
}
