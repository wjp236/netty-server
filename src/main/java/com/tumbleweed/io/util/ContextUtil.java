package com.tumbleweed.io.util;

import com.tumbleweed.io.bean.AppBeanBase;
import com.tumbleweed.io.bean.BootstrapContextHelper;
import com.tumbleweed.io.context.DomainCollectionContext;
import com.tumbleweed.io.context.LemonClientContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @Title: ContextUtil.java
 * @Package: com.lemon.io.util
 * @Description: Context工具，获取全局ContextMap | LemonClientContext | DomainCollectionContext
 * @author: jiangke
 * @date: 2014年1月2日
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

	public static LemonClientContext getLemonClientContext(String appBeanName) {
		return ((AppBeanBase) BootstrapContextHelper.getBean(appBeanName))
				.getLcc();
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
