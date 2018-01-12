package com.tumbleweed.netty.core.util;


import com.tumbleweed.netty.client.bean.AppBeanBase;
import com.tumbleweed.netty.client.context.DomainCollectionContext;
import com.tumbleweed.netty.client.context.ClientContext;
import com.tumbleweed.netty.core.bean.BootstrapContextHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title: ContextUtil.java
 * @Description: Context工具，获取全局ContextMap | ClientContext | DomainCollectionContext
 * @version V1.0
 */
public class ContextUtil {
	private static Map<String, Object> contextMap = new ConcurrentHashMap<String, Object>();

	public static Map<String, Object> getContextMap() {
		return contextMap;
	}

	public static void setContextMap(Map<String, Object> contextMap) {
		ContextUtil.contextMap = contextMap;
	}

	public static ClientContext getClientContext(String appBeanName) {
		return ((AppBeanBase) BootstrapContextHelper.getBean(appBeanName)).getLcc();
	}

	public static DomainCollectionContext getDomainCollectionContextForUnion(
			String appBeanName) {
		return (DomainCollectionContext) BootstrapContextHelper
				.getBean(ChannelHandlerContextUtil.covertSuffix(appBeanName));
	}

	public static DomainCollectionContext getDomainCollectionContext(
			String appBeanName) {
		return ((AppBeanBase) BootstrapContextHelper.getBean(appBeanName))
				.getDhmc();
	}
}
