package com.tumbleweed.netty.client.context;

import com.tumbleweed.netty.client.bean.AppBeanBase;

import java.util.HashMap;
import java.util.Map;

public class AppBeanContext {

	private Map<String, AppBeanBase> appBeanMap = new HashMap<String, AppBeanBase>();
	
	public void putAppBean(String appBeanName, AppBeanBase appBean){
		appBeanMap.put(appBeanName, appBean);
	}
	
	public void getAppBean(String appBeanName){
		appBeanMap.get(appBeanName);
	}
	
	public Map<String, AppBeanBase> getAppBeanMap(){
		return appBeanMap;
	}
}
